package msa.poc.microservices.user;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import msa.poc.microservices.user.Application;
import io.jaegertracing.Configuration;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class Application {
	
    public static void main(String [] args) {
        SpringApplication.run(Application.class);
    }
    
    @Bean
    public ModelMapper modelMapper() {
    	return new ModelMapper();
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