package com.mockmock.server;

import com.mockmock.mail.MockSmtpMessageHandlerFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.subethamail.smtp.server.SMTPServer;

/**
 * A server that listens for SMTP connections from applications sending outgoing email messages.
 */
@Slf4j
public class SmtpServer extends AbstractServer {

    // instance fields

    /** The factory that generates handlers for incoming mail messages. **/
    @Setter
    private MockSmtpMessageHandlerFactory handlerFactory;

    /** The network server that handles incoming SMTP connections. **/
    private SMTPServer smtpServerImpl;

    // methods implemented for AbstractServer

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
