package com.mockmock.http;

import com.mockmock.Settings;
import com.mockmock.htmlbuilder.FooterHtmlBuilder;
import com.mockmock.htmlbuilder.HeaderHtmlBuilder;
import com.mockmock.htmlbuilder.MailViewHtmlBuilder;
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

public class MailDetailHandlerTest {

    @ParameterizedTest
    @MethodSource("handle_testByPathAndIndex_arguments")
    public void handle_testByPathAndIndex(String urlPath, boolean expectedHandled) throws ServletException, IOException {
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

        MailDetailHandler mailDetailHandler = new MailDetailHandler();
        mailDetailHandler.setMailQueue(mailQueue);
        HeaderHtmlBuilder mockHeaderHtmlBuilder = Mockito.mock(HeaderHtmlBuilder.class);
        Mockito.doReturn("").when(mockHeaderHtmlBuilder).build();
        mailDetailHandler.setHeaderHtmlBuilder(mockHeaderHtmlBuilder);
        FooterHtmlBuilder mockFooterHtmlBuilder = Mockito.mock(FooterHtmlBuilder.class);
        Mockito.doReturn("").when(mockFooterHtmlBuilder).build();
        mailDetailHandler.setFooterHtmlBuilder(mockFooterHtmlBuilder);
        MailViewHtmlBuilder mockMailViewHtmlBuilder = Mockito.mock(MailViewHtmlBuilder.class);
        Mockito.doReturn("Body").when(mockMailViewHtmlBuilder).build();
        mailDetailHandler.setMailViewHtmlBuilder(mockMailViewHtmlBuilder);

        mailDetailHandler.handle(urlPath, jettyRequest, jettyRequest, jettyResponse);

        // see if it was handled correctly
        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
            Assertions.assertEquals("",  responseStringWriter.toString());
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());
            Assertions.assertEquals("Body", responseStringWriter.toString());
        }
    }

    private static Stream<Arguments> handle_testByPathAndIndex_arguments() {
        return Stream.of(
                Arguments.of("/invalid/path", false),
                Arguments.of("/view/0", false),   // indexes start at 1
                Arguments.of("/view/4", true),    // 4th from the start
                Arguments.of("/view/-4", true),   // 4th from the end
                Arguments.of("/view/6", false),   // 6th from the start (out of bounds)
                Arguments.of("/view/-6", false)   // 6th from the end (out of bounds)
        );
    }

    private static MockMail createMockMail(int index) {
        MockMail mockMail = new MockMail();
        mockMail.setId(UUID.randomUUID());
        mockMail.setFrom("sender@example.com");
        mockMail.setTo("recipient@example.com");
        mockMail.setSubject("Email " + index);
        mockMail.setBody("Body " + index);
        mockMail.setReceivedTime(System.currentTimeMillis() + index);
        return mockMail;
    }
}
