package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.mail.MockMockMessageHandlerFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.server.SMTPServer;

@Service
@Slf4j
public class SmtpServer implements Server {

    @Autowired
    @Setter
    private Settings settings;

    @Autowired
    @Setter
    private MockMockMessageHandlerFactory handlerFactory;

    @Override
    public void start() {
        // start the smtp server!
        SMTPServer server = new SMTPServer(handlerFactory);
        server.setSoftwareName("MockSMTP");
        server.setPort(settings.getSmtpPort());

        try {
            server.start();
            log.info("MockSMTP is listening for SMTP on port {}", settings.getSmtpPort());
        } catch (Exception x) {
            log.error("Could not start SMTP server. Maybe port {} is already in use? {}", settings.getSmtpPort(), x.getMessage());
            log.debug("Stacktrace:", x);
            throw new RuntimeException(x);
        }
    }

}
