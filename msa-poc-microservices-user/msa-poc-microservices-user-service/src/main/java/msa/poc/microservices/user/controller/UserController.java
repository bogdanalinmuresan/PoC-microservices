package msa.poc.microservices.user.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import msa.poc.microservices.common.Action;
import msa.poc.microservices.common.response.InternalStatus;
import msa.poc.microservices.communication.api.response.CommunicationResponse;
import msa.poc.microservices.user.api.dto.UserDto;
import msa.poc.microservices.user.api.request.UserRequest;
import msa.poc.microservices.user.api.response.UserListResponse;
import msa.poc.microservices.user.service.service.CommunicationUserService;
import msa.poc.microservices.user.service.service.UserService;
import msa.poc.microservices.user.stream.NotificationStreamProcessor;

@RefreshScope
@RestController
public class UserController {

	private Logger log = LoggerFactory.getLogger(UserController.class);
	

			
	@Autowired
	private CommunicationUserService communicationService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationStreamProcessor notificationStreamProcessor;

	@Value("${message:Hello default}")
	private String message;
	
	@RequestMapping("/message")
	String getMessage() {
		return this.message;
	}
	
	@RequestMapping(value="/user", method = RequestMethod.GET,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserListResponse> list() {
		
		ResponseEntity<UserListResponse> response;
		UserListResponse userListResponse = new UserListResponse();
		StringBuilder tracking = new StringBuilder();

		try {
			userListResponse.setUsers(this.userService.list());
			userListResponse.setResponse(InternalStatus.OK,"OK");
			response= ResponseEntity.status(HttpStatus.OK).body(userListResponse);
		}catch(Exception e) {
			userListResponse.setResponse(InternalStatus.ERROR,"ERROR");
			response= ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userListResponse);
		}
		
		tracking.append("USERS]:" ).append(userListResponse.getUsers().toString());
		tracking.append("[STATUS]:").append(userListResponse.getStatus());
		MDC.put("TRACKING", tracking.toString());
		
		log.info("Retrieving users");
		return response;

	}

	@GetMapping(path = "/user/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserListResponse> getUser(@PathVariable(value="id") Long id) {
		ResponseEntity<UserListResponse> response;
		UserListResponse userListResponse = new UserListResponse();
		List<UserDto> userDtoList = new ArrayList<UserDto>();
		StringBuilder tracking = new StringBuilder();

		try {
			userDtoList.add(this.userService.getUser(id));
			
			if(userDtoList.isEmpty()) {
				response= ResponseEntity.status(HttpStatus.NOT_FOUND).body(userListResponse);
				userListResponse.setResponse(InternalStatus.ERROR,"ERROR");
			}else {
				userListResponse.setResponse(InternalStatus.OK,"OK");
				userListResponse.setUsers(userDtoList);
				
				response= ResponseEntity.status(HttpStatus.OK).body(userListResponse);
			}
			
		}catch(Exception e){
			response= ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userListResponse);
			userListResponse.setResponse(InternalStatus.ERROR,"ERROR");
		}
		
		tracking.append("[RETRIEVE]: ").append(id);
		MDC.put("TRACKING", tracking.toString());
		
		return response;
	}

	@PostMapping(path="/user",consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserListResponse> add(@RequestBody UserDto userDto) {

		ResponseEntity<UserListResponse> response;
		UserListResponse userListResponse = new UserListResponse();
		List<UserDto> userDtoList = new ArrayList<UserDto>();
		UserRequest userRequest = new UserRequest();
		StringBuilder tracking = new StringBuilder();

		// add user to DB
		try {
			userDtoList.add(this.userService.add(userDto));
			userListResponse.setUsers(userDtoList);
			
			userListResponse.setStatus(InternalStatus.OK);
			response = ResponseEntity.status(HttpStatus.CREATED).body(userListResponse);
			
		}catch(Exception e) {
			userListResponse.setStatus(InternalStatus.ERROR);
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userListResponse);
			log.info("Exception trying to persist the user " +e.getStackTrace());
		}
		
		//send data to communication service
		userRequest.setAction(Action.ADD);
		userRequest.setUserDto(userDto);

		//sent notification to communication
		try {
			CommunicationResponse communicationResponse = communicationService.sendNotification(userRequest);
			userListResponse.setResponse(communicationResponse.getStatus(),communicationResponse.getInfo());
			log.info("[ADD] Notification sent for user: "+ userDto.getName());
		}catch(Exception e) {
			userListResponse.setResponse(InternalStatus.ERROR, "Error when sent notification to communication microservice");
			log.info("[ADD] Notification NOT sent for user: "+ userDto.getName());
			log.info("Exception trying communicate with communication  " +e.getStackTrace());
		}

		tracking.append("[EMAIL]:" ).append(userDto.getEmail());
		tracking.append("[NAME]:").append(userDto.getName());
		MDC.put("TRACKING", tracking.toString());
		
		return response;
	}

	@PatchMapping(path = "/user/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE } )
	public ResponseEntity<UserListResponse> update(@PathVariable Long id, @RequestBody final UserDto userDto) {

		ResponseEntity<UserListResponse> response;
		UserListResponse userListResponse = new UserListResponse();
		CommunicationResponse communicationResponse=new CommunicationResponse();
		List<UserDto> userDtoList = new ArrayList<>();
		UserRequest userRequest = new UserRequest();
		StringBuilder tracking = new StringBuilder();
	
		try {
			userDtoList.add(this.userService.update(id, userDto));
			userListResponse.setUsers(userDtoList);
			userListResponse.setStatus(InternalStatus.OK);
			response = ResponseEntity.status(HttpStatus.OK).body(userListResponse);
			
		}catch(Exception e) {
			userListResponse.setStatus(InternalStatus.ERROR);
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userListResponse);
		}
		
		userRequest.setAction(Action.MODIFY);
		userRequest.setUserDto(userDto);
		
		try {
			
			communicationResponse = communicationService.sendNotification(userRequest);
			userListResponse.setResponse(communicationResponse.getStatus(),communicationResponse.getInfo());
			log.info("[UPDATE] Notification sent for user: "+ userDto.getName());
			
		}catch(Exception e) {
			communicationResponse.setResponse(InternalStatus.ERROR, "Error");
			userListResponse.setResponse(InternalStatus.ERROR, "Error when try to send modify notification to communication microservice");
			log.info("[UPDATE] Notification NOT sent for user: "+ userDto.getName());
		}

		tracking.append("[UPDATE_ID]:").append(id);
		tracking.append("[USER]:").append(userDto.toString());
		MDC.put("TRACKING", tracking.toString());
		
		return response;
	}

	@DeleteMapping(path = "/users/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserListResponse> delete(@PathVariable Long id) {

		ResponseEntity<UserListResponse> response;
		UserListResponse userListResponse = new UserListResponse();
		List<UserDto> userDtoList = new ArrayList<UserDto>();
		
		UserRequest userRequest = new UserRequest();
		StringBuilder tracking = new StringBuilder();
		
		try {
			userDtoList.add(this.userService.delete(id));
			userListResponse.setUsers(userDtoList);
			userListResponse.setStatus(InternalStatus.OK);
			response = ResponseEntity.status(HttpStatus.OK).body(userListResponse);
			
		}catch(Exception e) {
			userListResponse.setResponse(InternalStatus.ERROR,"Error when try to send delete notification to communication");
			response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(userListResponse);
		}
		
		tracking.append("[ID]:").append(id);
		MDC.put("TRACKING", tracking.toString());

		userRequest.setAction(Action.DELETE);
		userRequest.setUserDto(userDtoList.get(0));

		try {
			notificationStreamProcessor.sendNotification().send(MessageBuilder.withPayload(userRequest).build());
			log.info("[DELETE] Notification sent for user: "+ userDtoList.get(0).getName());
		}catch(Exception e) {
			log.info("[DELETE] Notification NOT sent for user: "+ userDtoList.get(0).getName());
		}
		
		return response;
	}

}
