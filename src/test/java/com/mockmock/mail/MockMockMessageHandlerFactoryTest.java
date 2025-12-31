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

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class MockMockMessageHandlerFactoryTest {

    @Test
    public void create_valid() {
        MockMockMessageHandlerFactory factory = new MockMockMessageHandlerFactory(new MailQueue(), new Settings());
        Assertions.assertInstanceOf(MockMockMessageHandlerFactory.MockMockHandler.class, factory.create(Mockito.mock(MessageContext.class)));
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class MockMockHandlerTest {

        @Test
        public void from_valid() {
            MockMockMessageHandlerFactory factory = new MockMockMessageHandlerFactory(new MailQueue(), new Settings());
            MockMockMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockMockMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

            String fromAddress = "sender@example.com";
            mockMockHandler.from(fromAddress);
            Assertions.assertEquals(fromAddress, mockMockHandler.mockMail.getFrom());
        }

        @Test
        public void to_valid() {
            MockMockMessageHandlerFactory factory = new MockMockMessageHandlerFactory(new MailQueue(), new Settings());
            MockMockMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockMockMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

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

            MockMockMessageHandlerFactory factory = new MockMockMessageHandlerFactory(mailQueue, settings);
            MockMockMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockMockMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

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
            MockMockMessageHandlerFactory factory = new MockMockMessageHandlerFactory(new MailQueue(), new Settings());
            MockMockMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockMockMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

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
            MockMockMessageHandlerFactory factory = new MockMockMessageHandlerFactory(new MailQueue(), new Settings());
            MockMockMessageHandlerFactory.MockMockHandler mockMockHandler
                    = (MockMockMessageHandlerFactory.MockMockHandler) factory.create(Mockito.mock(MessageContext.class));

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
    }
}
