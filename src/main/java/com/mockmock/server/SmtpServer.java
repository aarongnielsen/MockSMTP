package com.mockmock.server;

import com.mockmock.Settings;
import com.mockmock.mail.MockMockMessageHandlerFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.subethamail.smtp.server.SMTPServer;

@Slf4j
public class SmtpServer implements Server {

    @Setter
    private Settings settings;

    @Setter
    private MockMockMessageHandlerFactory handlerFactory;

    private SMTPServer smtpServerImpl;

    @Override
    public void start() {
        // start the smtp server!
        smtpServerImpl = new SMTPServer(handlerFactory);
        smtpServerImpl.setSoftwareName("MockSMTP");
        smtpServerImpl.setPort(settings.getSmtpPort());

        try {
            smtpServerImpl.start();
            log.info("MockSMTP is listening for SMTP on port {}", settings.getSmtpPort());
        } catch (Exception x) {
            log.error("Could not start SMTP server. Maybe port {} is already in use? {}", settings.getSmtpPort(), x.getMessage());
            log.debug("Stacktrace:", x);
            throw new RuntimeException(x);
        }
    }

    @Override
    public void stop() {
        smtpServerImpl.stop();
    }

}
