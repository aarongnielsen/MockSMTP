package com.mockmock.http;

import com.mockmock.Settings;
import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

public class DeleteHandlerTest {

    @ParameterizedTest
    @MethodSource("handleTestArguments")
    public void _handleTestImpl(String targetUrlPath, boolean expectedHandled) throws IOException, ServletException {
        // make up a mock mail queue
        int numberOfMessagesToGenerate = 5;
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSettings(Mockito.mock(Settings.class));
        for (int i = 0; i < numberOfMessagesToGenerate; i++) {
            mailQueue.add(Mockito.mock(MockMail.class));
        }

        Request jettyRequest = new Request(null, null);
        Response jettyResponse = Mockito.mock(Response.class);
        Mockito.doReturn(new PrintWriter(new StringWriter())).when(jettyResponse).getWriter();
        DeleteHandler deleteHandler = new DeleteHandler();
        deleteHandler.setMailQueue(mailQueue);
        deleteHandler.handle(targetUrlPath, jettyRequest, jettyRequest, jettyResponse);

        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());
            Mockito.verify(jettyResponse, Mockito.times(1)).setStatus(Mockito.eq(HttpStatus.FOUND_302));
            Mockito.verify(jettyResponse, Mockito.times(1)).setHeader(Mockito.eq("Location"), Mockito.eq("/"));
        }
    }

    private static Stream<Arguments> handleTestArguments() {
        return Stream.of(
                Arguments.of("/incorrect/path", false),
                Arguments.of("/mail/delete/all", true)
        );
    }

}