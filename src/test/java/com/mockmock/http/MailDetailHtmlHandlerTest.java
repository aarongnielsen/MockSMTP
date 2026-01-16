package com.mockmock.http;

import com.mockmock.Settings;
import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
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
import java.util.UUID;
import java.util.stream.Stream;

public class MailDetailHtmlHandlerTest {

    @ParameterizedTest
    @MethodSource("handle_testByPathAndIndex_arguments")
    public void handle_testByPathAndIndex(String urlPath, int mailIndex, boolean expectedHandled) throws ServletException, IOException {
        // make up a mock mail queue
        int numberOfMessagesToGenerate = 5;
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSettings(Mockito.mock(Settings.class));
        for (int i = 1; i <= numberOfMessagesToGenerate; i++) {
            mailQueue.add(createMockMail(i));
        }

        // make sure one of the messages has neither a plain-text nor a HTML body
        MockMail mockMail = createMockMail(6);
        mockMail.setBody(null);
        mockMail.setBodyHtml(null);

        // handle a request
        Request jettyRequest = new Request(null, null);
        Response jettyResponse = Mockito.mock(Response.class);
        StringWriter responseStringWriter = new StringWriter();
        Mockito.doReturn(new PrintWriter(responseStringWriter)).when(jettyResponse).getWriter();
        ViewMailBodyHandler mailDetailHtmlHandler = new ViewMailBodyHandler(mailQueue);
        mailDetailHtmlHandler.setMailQueue(mailQueue);
        mailDetailHtmlHandler.handle(urlPath, jettyRequest, jettyRequest, jettyResponse);

        // see if it was handled correctly
        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
            Assertions.assertEquals("",  responseStringWriter.toString());
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());
            if (mailIndex <= 3) {
                Mockito.verify(jettyResponse, Mockito.times(1)).setContentType(Mockito.startsWith("text/plain"));
                Assertions.assertTrue(responseStringWriter.toString().contains("Body " + mailIndex));
            } else {
                Mockito.verify(jettyResponse, Mockito.times(1)).setContentType(Mockito.startsWith("text/html"));
                Assertions.assertTrue(responseStringWriter.toString().contains("<html><body>Body " + mailIndex + "</body></html>"));
            }
        }
    }

    private static Stream<Arguments> handle_testByPathAndIndex_arguments() {
        return Stream.of(
                Arguments.of("/invalid/path", 1, false),
                Arguments.of("/view/0/body", 1, false),   // indexes start at 1
                Arguments.of("/view/4/body", 4, true),    // 4th from the start
                Arguments.of("/view/-4/body", 2, true),   // 4th from the end
                Arguments.of("/view/5/body", 5, false),   // no body as either text or HTML
                Arguments.of("/view/6/body", 6, false),   // 6th from the start (out of bounds)
                Arguments.of("/view/-6/body", 1, false)   // 6th from the end (out of bounds)
        );
    }

    private static MockMail createMockMail(int index) {
        MockMail mockMail = new MockMail();
        mockMail.setId(UUID.randomUUID());
        mockMail.setFrom("sender@example.com");
        mockMail.setTo("recipient@example.com");
        mockMail.setSubject("Email " + index);
        if (index <= 2) {
            mockMail.setBody("Body " + index);
        } else if (index <= 4) {
            mockMail.setBodyHtml("<html><body>Body " + index + "</body></html>");
        }
        mockMail.setReceivedTime(System.currentTimeMillis() + index);
        return mockMail;
    }
}
