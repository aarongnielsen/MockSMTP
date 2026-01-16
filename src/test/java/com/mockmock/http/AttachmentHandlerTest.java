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
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Stream;

public class AttachmentHandlerTest {

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
        ServletOutputStream servletOutputStream = Mockito.mock(ServletOutputStream.class);
        Mockito.doReturn(servletOutputStream).when(jettyResponse).getOutputStream();

        AttachmentHandler attachmentHandler = new AttachmentHandler(mailQueue);
        attachmentHandler.handle(urlPath, jettyRequest, jettyRequest, jettyResponse);

        // see if it was handled correctly
        if (!expectedHandled) {
            Assertions.assertFalse(jettyRequest.isHandled());
            Mockito.verify(jettyResponse, Mockito.never()).getOutputStream();
        } else {
            Assertions.assertTrue(jettyRequest.isHandled());
            Mockito.verify(jettyResponse, Mockito.times(1)).setContentType(Mockito.eq("image/png"));
            Mockito.verify(servletOutputStream, Mockito.times(1)).write(Mockito.any(byte[].class));
        }
    }

    private static Stream<Arguments> handle_testByPathAndIndex_arguments() {
        return Stream.of(
                Arguments.of("/invalid/path", false),
                Arguments.of("/view/0/attachment/0", false),   // indexes start at 1
                Arguments.of("/view/123/attachment/0", false), // out of bounds
                Arguments.of("/view/1/attachment/0", false),   // indexes start at 1
                Arguments.of("/view/1/attachment/123", false), // out of bounds
                Arguments.of("/view/4/attachment/1", true),    // 4th from the start
                Arguments.of("/view/-2/attachment/2", true)    // 2nd from the end
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

        int attachmentsToAdd = index - 2;
        for (int i = 0; i < attachmentsToAdd; i++) {
            MockMail.Attachment attachment = new MockMail.Attachment();
            attachment.setFilename("image" + (i + 1) + ".png");
            attachment.setContentType("image/png");
            attachment.setContents(attachment.getFilename().getBytes(StandardCharsets.UTF_8));
            mockMail.getAttachments().add(attachment);
        }

        return mockMail;
    }
}
