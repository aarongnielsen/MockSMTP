package com.mockmock.server;

import com.mockmock.Settings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class SmtpServerTest {

    @Test
    public void start_portAlreadyInUse() {
        int port = 20000 + new Random().nextInt(10000);
        Settings settings = new Settings();
        settings.setSmtpPort(port);

        SmtpServer smtpServer1 = new SmtpServer();
        smtpServer1.setSettings(settings);
        smtpServer1.start();

        SmtpServer smtpServer2 = new SmtpServer();
        smtpServer2.setSettings(settings);
        Assertions.assertThrows(RuntimeException.class, smtpServer2::start);

        smtpServer2.stop();
        smtpServer1.stop();
    }
}
