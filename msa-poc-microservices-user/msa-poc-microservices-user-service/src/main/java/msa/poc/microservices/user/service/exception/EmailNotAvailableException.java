package msa.poc.microservices.user.service.exception;

public class EmailNotAvailableException extends RuntimeException {
	
	public EmailNotAvailableException() {
		
	}

    public EmailNotAvailableException(String message) {
        super(message);
    }
}