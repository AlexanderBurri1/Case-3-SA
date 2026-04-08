package ch.fhnw.digi.mockups.case3.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import ch.fhnw.digi.mockups.case3.JobMessage;
import ch.fhnw.digi.mockups.case3.JobRequestMessage;

@Component
public class MessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    void requestJobFromDispo(JobMessage job) {

        // Create request message
        JobRequestMessage request = new JobRequestMessage();
        request.setJobnumber(job.getJobnumber());
        request.setRequestingEmployee("Employee X"); // can be any name

        // IMPORTANT: Queue (not topic)
        jmsTemplate.setPubSubDomain(false);

        // Send to correct channel
        jmsTemplate.convertAndSend("dispo.jobs.requestAssignment", request);
    }
}