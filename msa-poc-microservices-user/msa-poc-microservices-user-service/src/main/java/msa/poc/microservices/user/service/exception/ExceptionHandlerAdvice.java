package msa.poc.microservices.user.service.exception;

import msa.poc.microservices.common.response.InternalStatus;
import msa.poc.microservices.user.api.response.UserListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private MessageSource messageSource;
   
    private UserListResponse getUserListReponse(String prop) {
    	UserListResponse userListResponse = new UserListResponse();
    	userListResponse.setResponse(InternalStatus.ERROR, messageSource.getMessage("user.exception."+prop,null,LocaleContextHolder.getLocale()));
    	
    	return userListResponse;
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ResponseEntity<UserListResponse> entityNotFound(Exception ex) {

        log.info(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.getUserListReponse("entitynotfound"));
    }

    @ExceptionHandler(EmailNotAvailableException.class)
    @ResponseBody
    public ResponseEntity<UserListResponse> emailNotAvailable(Exception ex) {

        log.info(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.getUserListReponse("emailnotavailable"));
    }

}