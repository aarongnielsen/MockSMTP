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

public class MailDeleteHandlerTest {

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

        // handle a request
        Request jettyRequest = new Request(null, null);
        Response jettyResponse = Mockito.mock(Response.class);
        Mockito.doReturn(new PrintWriter(new StringWriter())).when(jettyResponse).getWriter();

        MailDeleteHandler mailDeleteHandler = new MailDeleteHandler(mailQueue);
        mailDeleteHandler.handle(urlPath, jettyRequest, jettyRequest, jettyResponse);

        // see if it was handled correctly
        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
            Assertions.assertTrue(mailQueue.getMailQueue().stream()
                    .anyMatch(mockMail -> mockMail.getSubject().equals("Email " + mailIndex))
            );
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());
            Assertions.assertEquals(numberOfMessagesToGenerate - 1, mailQueue.getMailQueue().size());
            Assertions.assertFalse(mailQueue.getMailQueue().stream()
                    .anyMatch(mockMail -> mockMail.getSubject().equals("Email " + mailIndex))
            );
        }
    }

    private static Stream<Arguments> handle_testByPathAndIndex_arguments() {
        return Stream.of(
                Arguments.of("/invalid/path", 1, false),
                Arguments.of("/delete/0", 1, false),
                Arguments.of("/delete/123", 1, false),
                Arguments.of("/delete/4", 4, true),
                Arguments.of("/delete/-4", 2, true)
        );
    }

    private static MockMail createMockMail(int index) {
        MockMail mockMail = new MockMail();
        mockMail.setId(UUID.randomUUID());
        mockMail.setFrom("sender@example.com");
        mockMail.setTo("recipient@example.com");
        mockMail.setSubject("Email " + index);
        mockMail.setBody("Body " + index);
        mockMail.setBodyHtml("<html><body>Email " + index + "</body></html>");
        mockMail.setReceivedTime(System.currentTimeMillis() + index);
        return mockMail;
    }
}
