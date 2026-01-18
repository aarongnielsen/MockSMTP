package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.stream.Stream;

public class WebUiHtmlBuilderTest {

    // tests - buildSenderAddress(MockMail)

    @Test
    public void buildSenderAddress_nullMailMessage() {
        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        String output = webUiHtmlBuilder.buildSenderAddress(new MockMail());
        Assertions.assertEquals("", output);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void buildSenderAddress_mimeMessageFromAddresses(boolean hasFromAddresses) throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        if (hasFromAddresses) {
            mimeMessage.addFrom(new Address[] {
                    new InternetAddress("sender01@example.com"),
                    new InternetAddress("sender02@example.com")
            });
        }
        MockMail mockMail = new MockMail();
        mockMail.setFrom("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        String output = webUiHtmlBuilder.buildSenderAddress(mockMail);
        String expectedLongAddresses = (hasFromAddresses ?
                "title=\"sender01@example.com, sender02@example.com\"" :
                mockMail.getFrom()
        );
        Assertions.assertTrue(output.contains(expectedLongAddresses));
    }

    @ParameterizedTest
    @MethodSource("buildSenderAddress_maxLength_parameters")
    public void buildSenderAddress_maxLength(int maxLength, boolean isLongAddressTruncated) throws MessagingException {
        // one sender, address is 20 characters long
        String senderAddress = "sender01@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        mimeMessage.addFrom(new Address[] { new InternetAddress(senderAddress) });

        MockMail mockMail = new MockMail();
        mockMail.setFrom("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        // build the HTML and see if the builder has truncated the text
        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        webUiHtmlBuilder.setMaxAddressLength(maxLength);
        String output = webUiHtmlBuilder.buildSenderAddress(mockMail);
        String expectedShortAddresses = (isLongAddressTruncated ?
                senderAddress.substring(0, maxLength) + "..." :
                senderAddress
        );
        Assertions.assertTrue(output.contains(expectedShortAddresses));
    }

    private static Stream<Arguments> buildSenderAddress_maxLength_parameters() {
        return Stream.of(
                Arguments.of(0, false),
                Arguments.of(20, false),
                Arguments.of(10, true)
        );
    }

    @Test
    public void buildSenderAddress_caughtMessagingException() throws MessagingException {
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doThrow(new MessagingException()).when(mimeMessage).getFrom();

        MockMail mockMail = new MockMail();
        mockMail.setMimeMessage(mimeMessage);

        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        String output = webUiHtmlBuilder.buildSenderAddress(mockMail);
        Assertions.assertNull(output);
    }


    // tests - buildRecipientAddress(MockMail)

    @Test
    public void buildRecipientAddress_nullMailMessage() {
        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        String output = webUiHtmlBuilder.buildRecipientAddress(new MockMail(), Message.RecipientType.TO);
        Assertions.assertEquals("", output);
    }

    @ParameterizedTest
    @MethodSource("buildRecipientAddress_mimeMessageHasNoRecipientAddresses_arguments")
    public void build_mimeMessageHasNoRecipientAddresses(Message.RecipientType recipientType, String expectedOutput) {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        MockMail mockMail = new MockMail();
        mockMail.setTo("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        String output = webUiHtmlBuilder.buildRecipientAddress(mockMail, recipientType);
        Assertions.assertEquals(expectedOutput, output);
    }

    private static Stream<Arguments> buildRecipientAddress_mimeMessageHasNoRecipientAddresses_arguments() {
        return Stream.of(
                Arguments.of(Message.RecipientType.TO, "unittest.mockmail@example.com"),
                Arguments.of(Message.RecipientType.CC, "")
        );
    }

    @ParameterizedTest
    @MethodSource("buildRecipientAddress_mimeMessageHasRecipientAddresses_arguments")
    public void buildRecipientAddress_mimeMessageHasRecipientAddresses(Message.RecipientType recipientType, String expectedLongAddresses) throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        mimeMessage.addRecipients(Message.RecipientType.TO, new Address[] {
                new InternetAddress("torecip1@example.com"),
                new InternetAddress("torecip2@example.com")
        });
        mimeMessage.addRecipients(Message.RecipientType.CC, new Address[] {
                new InternetAddress("ccrecip1@example.com"),
                new InternetAddress("ccrecip2@example.com")
        });
        MockMail mockMail = new MockMail();
        mockMail.setTo("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        String output = webUiHtmlBuilder.buildRecipientAddress(mockMail, recipientType);
        Assertions.assertTrue(output.contains(expectedLongAddresses));
    }

    private static Stream<Arguments> buildRecipientAddress_mimeMessageHasRecipientAddresses_arguments() {
        return Stream.of(
                Arguments.of(null, "torecip1@example.com, torecip2@example.com, ccrecip1@example.com, ccrecip2@example.com"),
                Arguments.of(Message.RecipientType.TO, "torecip1@example.com, torecip2@example.com"),
                Arguments.of(Message.RecipientType.CC, "ccrecip1@example.com, ccrecip2@example.com")
        );
    }

    @ParameterizedTest
    @MethodSource("buildRecipientAddress_maxLength_parameters")
    public void buildRecipientAddress_maxLength(int maxLength, boolean isLongAddressTruncated) throws MessagingException {
        // one recipient, address is 20 characters long
        String recipientAddress = "torecip1@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("torecip1@example.com"));

        MockMail mockMail = new MockMail();
        mockMail.setFrom("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        // build the HTML and see if the builder has truncated the text
        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        webUiHtmlBuilder.setMaxAddressLength(maxLength);
        String output = webUiHtmlBuilder.buildRecipientAddress(mockMail, Message.RecipientType.TO);
        String expectedShortAddresses = (isLongAddressTruncated ?
                recipientAddress.substring(0, maxLength) + "..." :
                recipientAddress
        );
        Assertions.assertTrue(output.contains(expectedShortAddresses));
    }

    private static Stream<Arguments> buildRecipientAddress_maxLength_parameters() {
        return Stream.of(
                Arguments.of(0, false),
                Arguments.of(20, false),
                Arguments.of(10, true)
        );
    }

    @Test
    public void buildRecipientAddress_caughtMessagingException() throws MessagingException {
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doThrow(new MessagingException()).when(mimeMessage).getRecipients(Mockito.any());

        MockMail mockMail = new MockMail();
        mockMail.setMimeMessage(mimeMessage);

        WebUiHtmlBuilder webUiHtmlBuilder = new WebUiHtmlBuilder() {};
        String output = webUiHtmlBuilder.buildRecipientAddress(mockMail, Message.RecipientType.TO);
        Assertions.assertNull(output);
    }

}
