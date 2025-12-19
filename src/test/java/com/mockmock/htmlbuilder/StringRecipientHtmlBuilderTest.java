package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.stream.Stream;

public class StringRecipientHtmlBuilderTest {

    @Test
    public void build_nullMailMessage() {
        StringRecipientHtmlBuilder builder = new StringRecipientHtmlBuilder();
        builder.setMockMail(new MockMail());
        Assertions.assertEquals("", builder.build());
    }

    @ParameterizedTest
    @MethodSource("build_mimeMessageHasNoRecipientAddresses_arguments")
    public void build_mimeMessageHasNoRecipientAddresses(Message.RecipientType recipientType, String expectedOutput) {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        MockMail mockMail = new MockMail();
        mockMail.setTo("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        StringRecipientHtmlBuilder builder = new StringRecipientHtmlBuilder();
        builder.setMockMail(mockMail);
        builder.setRecipientType(recipientType);
        String builderResult = builder.build();
        Assertions.assertEquals(expectedOutput, builderResult);
    }

    private static Stream<Arguments> build_mimeMessageHasNoRecipientAddresses_arguments() {
        return Stream.of(
                Arguments.of(Message.RecipientType.TO, "unittest.mockmail@example.com"),
                Arguments.of(Message.RecipientType.CC, "")
        );
    }

    @ParameterizedTest
    @MethodSource("build_mimeMessageHasRecipientAddresses_arguments")
    public void build_mimeMessageHasRecipientAddresses(Message.RecipientType recipientType, String expectedLongAddresses) throws MessagingException {
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

        StringRecipientHtmlBuilder builder = new StringRecipientHtmlBuilder();
        builder.setMockMail(mockMail);
        builder.setRecipientType(recipientType);
        String builderResult = builder.build();
        Assertions.assertTrue(builderResult.contains(expectedLongAddresses));
    }

    private static Stream<Arguments> build_mimeMessageHasRecipientAddresses_arguments() {
        return Stream.of(
                Arguments.of(null, "torecip1@example.com, torecip2@example.com, ccrecip1@example.com, ccrecip2@example.com"),
                Arguments.of(Message.RecipientType.TO, "torecip1@example.com, torecip2@example.com"),
                Arguments.of(Message.RecipientType.CC, "ccrecip1@example.com, ccrecip2@example.com")
        );
    }

    @ParameterizedTest
    @MethodSource("build_maxLength_parameters")
    public void build_maxLength(int maxLength, boolean isLongAddressTruncated) throws MessagingException {
        // one recipient, address is 20 characters long
        String recipientAddress = "torecip1@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("torecip1@example.com"));

        MockMail mockMail = new MockMail();
        mockMail.setFrom("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        // build the HTML and see if the builder has truncated the text
        StringRecipientHtmlBuilder builder = new StringRecipientHtmlBuilder();
        builder.setMockMail(mockMail);
        builder.setMaxLength(maxLength);
        String builderResult =  builder.build();
        String expectedShortAddresses = (isLongAddressTruncated ?
                recipientAddress.substring(0, maxLength) + "..." :
                recipientAddress
        );
        Assertions.assertTrue(builderResult.contains(expectedShortAddresses));
    }

    private static Stream<Arguments> build_maxLength_parameters() {
        return Stream.of(
                Arguments.of(0, false),
                Arguments.of(20, false),
                Arguments.of(10, true)
        );
    }

    @Test
    public void build_caughtMessagingException() throws MessagingException {
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doThrow(new MessagingException()).when(mimeMessage).getAllRecipients();

        MockMail mockMail = new MockMail();
        mockMail.setMimeMessage(mimeMessage);

        StringRecipientHtmlBuilder builder = new StringRecipientHtmlBuilder();
        builder.setMockMail(mockMail);
        Assertions.assertNull(builder.build());
    }

}
