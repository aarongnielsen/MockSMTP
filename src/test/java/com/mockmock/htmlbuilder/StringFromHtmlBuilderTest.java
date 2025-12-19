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
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.stream.Stream;

public class StringFromHtmlBuilderTest {

    @Test
    public void build_nullMailMessage() {
        StringFromHtmlBuilder builder = new StringFromHtmlBuilder();
        builder.setMockMail(new MockMail());
        Assertions.assertEquals("", builder.build());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void build_mimeMessageFromAddresses(boolean hasFromAddresses) throws MessagingException {
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

        StringFromHtmlBuilder builder = new StringFromHtmlBuilder();
        builder.setMockMail(mockMail);
        String builderResult =  builder.build();
        String expectedLongAddresses = (hasFromAddresses ?
                "title=\"sender01@example.com, sender02@example.com\"" :
                mockMail.getFrom()
        );
        Assertions.assertTrue(builderResult.contains(expectedLongAddresses));
    }

    @ParameterizedTest
    @MethodSource("build_maxLength_parameters")
    public void build_maxLength(int maxLength, boolean isLongAddressTruncated) throws MessagingException {
        // one sender, address is 20 characters long
        String senderAddress = "sender01@example.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        mimeMessage.addFrom(new Address[] { new InternetAddress(senderAddress) });

        MockMail mockMail = new MockMail();
        mockMail.setFrom("unittest.mockmail@example.com");
        mockMail.setMimeMessage(mimeMessage);

        // build the HTML and see if the builder has truncated the text
        StringFromHtmlBuilder builder = new StringFromHtmlBuilder();
        builder.setMockMail(mockMail);
        builder.setMaxLength(maxLength);
        String builderResult =  builder.build();
        String expectedShortAddresses = (isLongAddressTruncated ?
                senderAddress.substring(0, maxLength) + "..." :
                senderAddress
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
        Mockito.doThrow(new MessagingException()).when(mimeMessage).getFrom();

        MockMail mockMail = new MockMail();
        mockMail.setMimeMessage(mimeMessage);

        StringFromHtmlBuilder builder = new StringFromHtmlBuilder();
        builder.setMockMail(mockMail);
        Assertions.assertNull(builder.build());
    }

}
