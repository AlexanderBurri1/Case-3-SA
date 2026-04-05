package ch.fhnw.digi.mockups.case3.client;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;
import ch.fhnw.digi.mockups.case3.JobMessage;
import ch.fhnw.digi.mockups.case3.JobAssignmentMessage;

@Component
public class MessageReceiver {

	@Autowired
	private UI ui;


	// FIXME: Nachrichten Empfangen und an das GUI weitergeben
    // Fixed
    @JmsListener(destination = "dispo.jobs.new", containerFactory = "myFactory")
    public void receiveJob(JobMessage job) {
        System.out.println("Received job: " + job.getJobnumber());

        if (job.getType() == JobMessage.JobType.Maintanence) {
            ui.addJobToList(job);
        }
    }

    @JmsListener(destination = "dispo.jobs.assignments", containerFactory = "myFactory")
    public void receiveAssignment(JobAssignmentMessage assignment) {
        System.out.println("Received assignment: " + assignment.getJobnumber());
        ui.assignJob(assignment);
    }
	// @see ui.addJobToList()
	// @see ui.assignJob()
	
	
	
	
	
	
	@Bean
	public DefaultJmsListenerContainerFactory myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

		configurer.configure(factory, connectionFactory);
		factory.setPubSubDomain(true);
		factory.setMessageConverter(jacksonJmsMessageConverter());

		return factory;
	}

	@Bean // Serialize message content to json/from using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

}
