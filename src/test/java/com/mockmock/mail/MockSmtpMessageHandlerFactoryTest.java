package com.mockmock.mail;

import com.mockmock.Settings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.subethamail.smtp.MessageContext;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class MockSmtpMessageHandlerFactoryTest {

    @Test
    public void create_valid() {
        MockSmtpMessageHandlerFactory factory = new MockSmtpMessageHandlerFactory(new MailQueue(), new Settings());
        Assertions.assertInstanceOf(MockSmtpMessageHandlerFactory.MockMockHandler.class, factory.create(Mockito.mock(MessageContext.class)));
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class MockMockHandlerTest {

        @Test
        public void from_valid() {
            MockSmtpMessageHandlerFactory factory = new MockSmtpMessageHandlerFactory(new MailQueue(), new Settings());
            MockSmtpMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockSmtpMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

            String fromAddress = "sender@example.com";
            mockMockHandler.from(fromAddress);
            Assertions.assertEquals(fromAddress, mockMockHandler.mockMail.getFrom());
        }

        @Test
        public void to_valid() {
            MockSmtpMessageHandlerFactory factory = new MockSmtpMessageHandlerFactory(new MailQueue(), new Settings());
            MockSmtpMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockSmtpMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

            String toAddress = "recipient@example.com";
            mockMockHandler.recipient(toAddress);
            Assertions.assertEquals(toAddress, mockMockHandler.mockMail.getTo());
        }

        @ParameterizedTest
        @MethodSource("done_valid_arguments")
        public void done_valid(String fromAddress, String toAddress, boolean expectedSent) {
            // exclude certain from/to addresses
            Settings settings = new Settings();
            settings.getFilterFromEmailAddresses().add("excluded.sender@example.com");
            settings.getFilterToEmailAddresses().add("excluded.recipient@example.com");

            // check whether mail messages get created
            MailQueue mailQueue = new MailQueue();
            mailQueue.setSettings(settings);

            MockSmtpMessageHandlerFactory factory = new MockSmtpMessageHandlerFactory(mailQueue, settings);
            MockSmtpMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockSmtpMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

            mockMockHandler.from(fromAddress);
            mockMockHandler.recipient(toAddress);
            mockMockHandler.done();

            Assertions.assertEquals(expectedSent ? 1 : 0, mailQueue.getMailQueue().size());
        }

        private Stream<Arguments> done_valid_arguments() {
            return Stream.of(
                    Arguments.of("excluded.sender@example.com", "normal.recipient@example.com", false),
                    Arguments.of("normal.sender@example.com", "excluded.recipient@example.com", false),
                    Arguments.of("normal.sender@example.com", "normal.recipient@example.com", true)
            );
        }

        @Test
        public void data_treatInputStreamContentAsPlainTextOnly() throws IOException {
            MockSmtpMessageHandlerFactory factory = new MockSmtpMessageHandlerFactory(new MailQueue(), new Settings());
            MockSmtpMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockSmtpMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

            // test the message handler:
            String expectedMessageBodyText = "message body";
            try (MockedConstruction<MimeMessage> mockMimeMessageService = Mockito.mockConstruction(MimeMessage.class, (mockObject, context) -> {
                //  - intercept the construction of new MimeMessage objects so we get valid test cases
                Mockito.doReturn(new ByteArrayInputStream(expectedMessageBodyText.getBytes(StandardCharsets.UTF_8))).when(mockObject).getContent();
            })) {
                //  - run the handler and ensure the message body is set appropriately
                mockMockHandler.data(new ByteArrayInputStream(new byte[0]));
                Assertions.assertEquals(expectedMessageBodyText, mockMockHandler.mockMail.getBody());
                Assertions.assertNull(mockMockHandler.mockMail.getBodyHtml());
            }
        }

        @ParameterizedTest
        @MethodSource({"data_treatStringContentBasedOnContentType_arguments"})
        public void data_treatStringContentBasedOnContentType(
                String contentType,
                boolean expectedBodyTextSet,
                boolean expectedBodyHtmlSet
        ) throws IOException {
            MockSmtpMessageHandlerFactory factory = new MockSmtpMessageHandlerFactory(new MailQueue(), new Settings());
            MockSmtpMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockSmtpMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

            // test the message handler:
            String expectedMessageBodyText = "message body";
            try (MockedConstruction<MimeMessage> mockMimeMessageService = Mockito.mockConstruction(MimeMessage.class, (mockObject, context) -> {
                //  - intercept the construction of new MimeMessage objects so we get valid test cases
                Mockito.doReturn(expectedMessageBodyText).when(mockObject).getContent();
                Mockito.doReturn(contentType).when(mockObject).getContentType();
            })) {
                //  - run the handler and ensure the message body is set appropriately
                mockMockHandler.data(new ByteArrayInputStream(new byte[0]));
                Assertions.assertEquals(expectedBodyTextSet ? expectedMessageBodyText : null, mockMockHandler.mockMail.getBody());
                Assertions.assertEquals(expectedBodyHtmlSet ? expectedMessageBodyText : null, mockMockHandler.mockMail.getBodyHtml());
            }
        }

        private Stream<Arguments> data_treatStringContentBasedOnContentType_arguments() {
            return Stream.of(
                    Arguments.of("text/plain", true, false),
                    Arguments.of("text/html", false, true),
                    Arguments.of("text/somethingelse", false, false)
            );
        }

        @ParameterizedTest
        @MethodSource("data_handleMultipartMessages_arguments")
        public void data_handleMultipartMessages(
                String contentType,
                String contentDisposition,
                String contents,
                boolean expectedBodyTextSet,
                boolean expectedBodyHtmlSet,
                String expectedAttachmentContentType,
                String expectedAttachmentFilename
        ) throws IOException {
            MockSmtpMessageHandlerFactory factory = new MockSmtpMessageHandlerFactory(new MailQueue(), new Settings());
            MockSmtpMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockSmtpMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

            try (MockedConstruction<MimeMessage> mockMimeMessageService = Mockito.mockConstruction(MimeMessage.class, (mockObject, context) -> {
                //  - intercept the construction of new MimeMessage objects so we get valid test cases
                Multipart multipart = Mockito.mock(Multipart.class);
                Mockito.doReturn(1).when(multipart).getCount();
                Mockito.doReturn(createMimeMessageBodyPart(contentType, contentDisposition, contents)).when(multipart).getBodyPart(Mockito.anyInt());
                Mockito.doReturn(multipart).when(mockObject).getContent();
            })) {
                //  - run the handler and ensure the message body is set appropriately
                mockMockHandler.data(new ByteArrayInputStream(new byte[0]));
                Assertions.assertEquals(expectedBodyTextSet ? contents : null, mockMockHandler.mockMail.getBody());
                Assertions.assertEquals(expectedBodyHtmlSet ? contents : null, mockMockHandler.mockMail.getBodyHtml());
                if (expectedAttachmentContentType != null) {
                    Assertions.assertEquals(expectedAttachmentContentType, mockMockHandler.mockMail.getAttachments().get(0).getContentType());
                    Assertions.assertEquals(expectedAttachmentFilename, mockMockHandler.mockMail.getAttachments().get(0).getFilename());
                }
            }
        }

        private Stream<Arguments> data_handleMultipartMessages_arguments() {
            return Stream.of(
                    Arguments.of("text/plain", "", "bodytext", true, false, null, null),
                    Arguments.of("text/html", "", "bodytext", false, true, null, null),
                    Arguments.of("image/png", "attachment", "bodytext", false, false, "image/png", "attachment"),
                    Arguments.of("image/png; encoding=random", "attachment; filename=\"icon.png\"", "bodytext", false, false, "image/png", "icon.png")
            );
        }

        private BodyPart createMimeMessageBodyPart(String contentType, String contentDisposition, String contents) throws MessagingException, IOException {
            BodyPart bodyPart = Mockito.mock(BodyPart.class);
            Mockito.doReturn(contentType).when(bodyPart).getContentType();
            Mockito.doReturn(new String[] { contentDisposition }).when(bodyPart).getHeader(Mockito.eq("Content-Disposition"));
            Mockito.doReturn(new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8))).when(bodyPart).getInputStream();
            return bodyPart;
        }
    }
}
