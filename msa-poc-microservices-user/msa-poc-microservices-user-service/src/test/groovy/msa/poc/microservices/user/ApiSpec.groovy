package msa.poc.microservices.user

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import com.fasterxml.jackson.databind.ObjectMapper

import msa.poc.microservices.common.Action
import msa.poc.microservices.common.response.InternalStatus
import msa.poc.microservices.communication.api.response.CommunicationResponse
import msa.poc.microservices.user.api.dto.UserDto
import msa.poc.microservices.user.api.request.UserRequest
import msa.poc.microservices.user.api.response.UserListResponse
import msa.poc.microservices.user.controller.UserController
import msa.poc.microservices.user.service.service.CommunicationUserService
import msa.poc.microservices.user.service.service.UserService
import msa.poc.microservices.user.stream.NotificationStreamProcessor
import spock.lang.Specification
import spock.lang.Stepwise



class ApiSpec extends Specification {

	UserService userService = Mock(UserService)
	CommunicationUserService communicationService = Mock(CommunicationUserService)
	NotificationStreamProcessor notificationStreamProcessor = Mock(NotificationStreamProcessor)
	
	UserController api=new UserController();
	
	MockMvc mockMvc = MockMvcBuilders.standaloneSetup(api).build()
	
	def setup() {
		api.communicationService=communicationService
		api.userService=userService
		api.notificationStreamProcessor=notificationStreamProcessor
	}
	
	def 'get all users endpoint'(){
		given:"a user "
			UserDto userDto=new UserDto("nameTest","surname1Test","surname2Test","emailTest")
			List<UserDto>listaUsers=new ArrayList<UserDto>()
			listaUsers.add(userDto)
			ObjectMapper objectMapper = new ObjectMapper()
		
		when: "all user should be retrieve"
			MockHttpServletResponse usersResponse = mockMvc.perform(get("/user/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn().response
	
		then: "user list should contain the user"
			1 * userService.list() >> listaUsers
			
			usersResponse.getStatus()==200
			
			//deserialize
			UserListResponse usersReturned = objectMapper
			.readValue(usersResponse.getContentAsString(), UserListResponse.class)
			
			usersReturned.getUsers().get(0)==userDto
	}
	
	
	def 'get user by id endpoint'(){
		given:"a user"
			Long id = 1
			ObjectMapper objectMapper = new ObjectMapper()
			UserDto userDto=new UserDto("nameTest","surname1Test","surname2Test","emailTest")
			
		when: "get an user by id "
			MockHttpServletResponse userResponse=mockMvc.perform(get("/user/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.param("id","1")).andReturn().response
			
		then:"user with id equal to given id"
			1 * userService.getUser(id) >> userDto
		
			userResponse.getStatus()==200
			
			//deserialize 
			UserListResponse usersReturned = objectMapper
			.readValue(userResponse.getContentAsString(), UserListResponse.class)
			
			usersReturned.getUsers().get(0)==userDto
			
	}

	
	def 'adding user endpoint'(){
		given:
			UserDto userDto=new UserDto("nameTest","surname1Test","surname2Test","emailTest")
			ObjectMapper objectMapper = new ObjectMapper()
			String userJson=objectMapper.writeValueAsString(userDto)
			
			CommunicationResponse communicationResponse= new CommunicationResponse()
			communicationResponse.setStatus(InternalStatus.OK)  
			
		when: "user should be created"
			MockHttpServletResponse userResponse=mockMvc.perform(post("/user/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.content(userJson)).andReturn().response
			
		then: "methods should have been invocated"
			1 * userService.add(_)>> userDto
			1 * communicationService.sendNotification(_) >> communicationResponse
	
			 userResponse.contentType.contains('application/json')
			 userResponse.contentType == 'application/json;charset=UTF-8'
			 
			 userResponse.getStatus()==201
			 
			 //deserialize
			 UserListResponse usersReturned = objectMapper
			 .readValue(userResponse.getContentAsString(), UserListResponse.class)
			 
			 usersReturned.getUsers().get(0)==userDto
			 communicationResponse.getStatus()==InternalStatus.OK
			 
	}
		
	def "update user endpoint"() {
		given:"a user"
			Long id=1;
			UserDto userDto=new UserDto("nameTest","surname1Test","surname2Test","emailTest")
			ObjectMapper objectMapper = new ObjectMapper()
			String userJson=objectMapper.writeValueAsString(userDto)
			
			CommunicationResponse communicationResponse= new CommunicationResponse()
			communicationResponse.setStatus(InternalStatus.OK)
			
		when: "user should be created"
			MockHttpServletResponse userResponse=mockMvc.perform(patch("/user/"+ id)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.content(userJson)).andReturn().response
	 
		then: "methods should have been invocated"  
			1 * userService.update(id,_) >> userDto
			1 * communicationService.sendNotification(_) >> communicationResponse
			
			userResponse.getStatus()==200
			
			UserListResponse updatedUserDto = objectMapper
				.readValue(userResponse.getContentAsString(), UserListResponse.class)
			
			 updatedUserDto.getUsers().get(0)==userDto	
			 communicationResponse.getStatus()==InternalStatus.OK
			 
	}
	
	def "delete user endpoint"(){
		given:
			Long id=1;
			boolean requestStatus=true;
			
			UserDto userDto=new UserDto("nameTest","surname1Test","surname2Test","emailTest")
			UserListResponse userListResponse = new UserListResponse()
			userListResponse.getUsers().add(userDto)
			
			UserRequest userRequest = new UserRequest()
			userRequest.setAction(Action.DELETE)
			
			MessageChannel messageChannel = Mock{
	           send(_) >> true
	
	       }
			
			NotificationStreamProcessor notificationStreamProcessor = Mock{
				sendNotification() >> messageChannel
			
			}
			
			notificationStreamProcessor.sendNotification() >> messageChannel
			messageChannel.send(_)>> true
			
		   
		when: "make the request"
			MockHttpServletResponse userResponse = mockMvc.perform(delete("/user/"+ id)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.param("id","1")).andReturn().response
		
		then:
			1 * userService.delete(_) >> userDto
			 notificationStreamProcessor.sendNotification().send(MessageBuilder.withPayload(_).build())>>true

	
			userResponse.getStatus()==200
			
			
	}
}
