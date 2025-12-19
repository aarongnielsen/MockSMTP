package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AddressesHtmlBuilderTest {

    @ParameterizedTest
    @CsvSource({ "true,true", "false,true", "true,false", "false,false" })
    public void build_allRecipientTypes(boolean includeCC, boolean includeBCC) throws MessagingException {
        MockMail mockMail = new MockMail();
        mockMail.setMimeMessage(Mockito.mock(MimeMessage.class));
        mockMail.setFrom("sender@example.com");
        mockMail.setTo("to.recipient@example.com");
        if (includeCC) {
            Mockito.doReturn(new Address[] { new InternetAddress("cc.recipient@example.com") } )
                    .when(mockMail.getMimeMessage()).getRecipients(Mockito.eq(Message.RecipientType.CC));
        }
        if (includeBCC) {
            Mockito.doReturn(new Address[] { new InternetAddress("bcc.recipient@example.com") } )
                    .when(mockMail.getMimeMessage()).getRecipients(Mockito.eq(Message.RecipientType.BCC));
        }

        AddressesHtmlBuilder addressesHtmlBuilder = new AddressesHtmlBuilder();
        addressesHtmlBuilder.setMockMail(mockMail);
        String builderOutput = addressesHtmlBuilder.build();

        String[] lines = builderOutput.split("\\n");
        Assertions.assertTrue(lines[0].startsWith("From: ") && lines[0].contains(mockMail.getFrom()));
        Assertions.assertTrue(lines[1].startsWith("To: ") && lines[1].contains(mockMail.getTo()));
        if (includeCC && includeBCC) {
            Assertions.assertEquals(4, lines.length);
            Assertions.assertTrue(lines[2].startsWith("CC: ") && lines[2].contains("cc.recipient@example.com"));
            Assertions.assertTrue(lines[3].startsWith("BCC: ") && lines[3].contains("bcc.recipient@example.com"));
        }
        else if (includeCC) {
            Assertions.assertEquals(3, lines.length);
            Assertions.assertTrue(lines[2].startsWith("CC: ") && lines[2].contains("cc.recipient@example.com"));
        }
        else if (includeBCC) {
            Assertions.assertEquals(3, lines.length);
            Assertions.assertTrue(lines[2].startsWith("BCC: ") && lines[2].contains("bcc.recipient@example.com"));
        }
        else {
            Assertions.assertEquals(2, lines.length);
        }
    }

}
