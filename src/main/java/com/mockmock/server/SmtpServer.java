package com.mockmock.server;

import com.mockmock.AppStarter;
import com.mockmock.mail.MockMockMessageHandlerFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.server.SMTPServer;

@Service
@Slf4j
public class SmtpServer implements Server {

    @Setter
    private int port;

    @Autowired
    @Setter
    private MockMockMessageHandlerFactory handlerFactory;

    public void start() {
        // start the smtp server!
        SMTPServer server = new SMTPServer(handlerFactory);
        server.setSoftwareName("MockMock SMTP Server version " + AppStarter.VERSION_NUMBER);
        server.setPort(port);

        try {
            server.start();
            log.info("MockSMTP is listening for SMTP on port {}", port);
        } catch (Exception x) {
            log.error("Could not start SMTP server. Maybe port {} is already in use? {}", port, x.getMessage());
            log.debug("Stacktrace:", x);
        }
    }

}
