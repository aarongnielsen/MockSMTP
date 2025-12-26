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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Stream;

public class ViewHeadersHandlerTest {

    @ParameterizedTest
    @MethodSource("handle_testByPathAndIndex_arguments")
    public void handle_testByPathAndIndex(String urlPath, int mailIndex, boolean expectedHandled) throws ServletException, IOException, MessagingException {
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
        StringWriter responseStringWriter = new StringWriter();
        Mockito.doReturn(new PrintWriter(responseStringWriter)).when(jettyResponse).getWriter();

        ViewHeadersHandler viewHeadersHandler = new ViewHeadersHandler();
        viewHeadersHandler.setMailQueue(mailQueue);

        viewHeadersHandler.handle(urlPath, jettyRequest, jettyRequest, jettyResponse);

        // see if it was handled correctly
        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
            Assertions.assertEquals("",  responseStringWriter.toString());
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());
            Assertions.assertEquals("Message Index: " + mailIndex + "\nSome Header: Some Value", responseStringWriter.toString());
        }
    }

    private static Stream<Arguments> handle_testByPathAndIndex_arguments() {
        return Stream.of(
                Arguments.of("/invalid/path", 1, false),
                Arguments.of("/view/headers/0", 1, false),   // indexes start at 1
                Arguments.of("/view/headers/1", 2, false),   // MockMail with no MIME message
                Arguments.of("/view/headers/4", 4, true),     // 4th from the start
                Arguments.of("/view/headers/-4", 2, true),    // 4th from the end
                Arguments.of("/view/headers/6", 1, false),   // 6th from the start (out of bounds)
                Arguments.of("/view/headers/-6", 1, false)   // 6th from the end (out of bounds)
        );
    }

    private static MockMail createMockMail(int index) throws MessagingException {
        MockMail mockMail = new MockMail();
        mockMail.setId(UUID.randomUUID());
        mockMail.setFrom("sender@example.com");
        mockMail.setTo("recipient@example.com");
        mockMail.setSubject("Email " + index);
        mockMail.setBody("Body " + index);
        mockMail.setReceivedTime(System.currentTimeMillis() + index);
        if (index != 1) {
            MimeMessage mockMimeMessage = Mockito.mock(MimeMessage.class);
            Vector<String> headers = new Vector<>(Arrays.asList(
                    "Message Index: " + index,
                    "Some Header: Some Value"
            ));
            Mockito.doReturn(headers.elements()).when(mockMimeMessage).getAllHeaderLines();
            mockMail.setMimeMessage(mockMimeMessage);
        }
        return mockMail;
    }
}
