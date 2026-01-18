package com.mockmock.mail;

import com.mockmock.Settings;
import com.mockmock.server.DemoDataLoader;
import com.mockmock.server.SmtpServer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.subethamail.smtp.MessageContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class DemoDataLoaderTest {

    @Test
    public void load_allMessagesAreLoaded() throws IOException {
        loadTestImpl(0, 3);
    }

    @Test
    public void load_allThreeMessagesAreAttemptedIfServerPortIsClosed() throws IOException {
        loadTestImpl(1, 0);
    }

    private void loadTestImpl(int portOffset, int expectedNumberOfMessages) throws IOException {
        int port = 20000 + new Random().nextInt(10000);
        Settings settings = new Settings();
        settings.setSmtpPort(port);

        SmtpServer smtpServer = new SmtpServer();
        smtpServer.setSettings(settings);
        MockSmtpMessageHandlerFactory messageHandlerFactory = Mockito.mock(MockSmtpMessageHandlerFactory.class);
        MockSmtpMessageHandlerFactory.MockMockHandler messageHandler = Mockito.mock(MockSmtpMessageHandlerFactory.MockMockHandler.class);
        Mockito.doReturn(messageHandler).when(messageHandlerFactory).create(Mockito.any(MessageContext.class));
        smtpServer.setHandlerFactory(messageHandlerFactory);
        smtpServer.start();

        settings.setSmtpPort(port + portOffset);
        DemoDataLoader demoDataLoader = new DemoDataLoader(settings);
        demoDataLoader.load();

        Mockito.verify(messageHandler, Mockito.times(expectedNumberOfMessages)).from(Mockito.anyString());
        Mockito.verify(messageHandler, Mockito.times(expectedNumberOfMessages)).recipient(Mockito.anyString());
        Mockito.verify(messageHandler, Mockito.times(expectedNumberOfMessages)).data(Mockito.any(InputStream.class));
        Mockito.verify(messageHandler, Mockito.times(expectedNumberOfMessages)).done();

        smtpServer.stop();
    }

}
