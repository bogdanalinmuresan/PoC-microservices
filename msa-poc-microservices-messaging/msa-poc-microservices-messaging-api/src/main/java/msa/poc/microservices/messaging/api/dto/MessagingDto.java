package msa.poc.microservices.messaging.api.dto;

public class MessagingDto{
	
	private String from;
	private String to;
	private String body;
	private String subject;

	public MessagingDto(String from, String to, String body, String subject) {
		super();
		this.from = from;
		this.to = to;
		this.body = body;
		this.subject = subject;
	}

	public MessagingDto() {

	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "MessagingDto [from=" + from + ", to=" + to + ", body=" + body + ", subject=" + subject + "]";
	}
	
}