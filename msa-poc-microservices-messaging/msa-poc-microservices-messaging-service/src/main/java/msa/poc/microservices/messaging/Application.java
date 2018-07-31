package msa.poc.microservices.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.jaegertracing.Configuration;

@SpringBootApplication
public class Application {
    public static void main(String [] args) {
        SpringApplication.run(Application.class);
    }
    
    @Bean
    public io.opentracing.Tracer jaegerTracing(){
    	Configuration config = Configuration.fromEnv();
    	
    	System.out.println("Config_Host: " + config.getReporter().getSenderConfiguration().getAgentHost());
		System.out.println("Config_Port: " + config.getReporter().getSenderConfiguration().getAgentPort());
		System.out.println("Sampler_Host_Port: " + config.getSampler().getManagerHostPort());

		return config.getTracer();
    }
    
}