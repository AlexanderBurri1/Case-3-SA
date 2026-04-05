package ch.fhnw.digi.mockups.case3.client;

import ch.fhnw.digi.mockups.case3.JobRequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import ch.fhnw.digi.mockups.case3.JobMessage;

@Component
public class MessageSender {


	@Autowired
	private JmsTemplate jmsTemplate;

	void requestJobFromDispo(JobMessage job) {
        JobRequestMessage request = new JobRequestMessage();
        request.setJobnumber(job.getJobnumber());
        jmsTemplate.convertAndSend("dispo.jobs.requestAssignment", request);
		
		// FIXME: JobRequestMessage erzeugen und an Broker schicken
        // Fixed

	}
}
