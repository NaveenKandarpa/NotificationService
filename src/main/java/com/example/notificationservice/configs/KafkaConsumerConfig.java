package com.example.notificationservice.configs;

import com.example.notificationservice.dtos.SendEmailDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
public class KafkaConsumerConfig {
    // Code to listen an event from Kafka
    private ObjectMapper objectMapper;
    private EmailUtil emailUtil;

    public KafkaConsumerConfig(ObjectMapper objectMapper,
                               EmailUtil emailUtil) {
        this.objectMapper = objectMapper;
        this.emailUtil = emailUtil;
    }

    @KafkaListener(topics = "sendEmail", groupId = "notificationGroup")
    public void handleEmailEvent(String message) throws JsonProcessingException {
        // code to email the user
        SendEmailDto emailDto = objectMapper.readValue(message, SendEmailDto.class);

        System.out.println("Event has been picked up by the consumer");

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {

            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailDto.getSenderEmail(), "hiymxnvwvmexmerg");
            }
        };
        Session session = Session.getInstance(props, auth);

        emailUtil.sendEmail(session, emailDto.getReceiverEmail(), emailDto.getSubject(), emailDto.getBody());
    }
}
