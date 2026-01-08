package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Vector;

public class MailHeaderSummaryHtmlBuilderTest {

    @Test
    public void build_valid() throws MessagingException {
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Vector<String> allHeaders = new Vector<>(Arrays.asList(
                "To: recipient@example.com",
                "Cc: cc.recipient@example.com",
                "Bcc: bcc.recipient@example.com",
                "From: sender@example.com",
                "Subject: test message",
                "Date: 2026-01-02"
        ));
        Mockito.doReturn(allHeaders.elements()).when(mimeMessage).getMatchingHeaderLines(Mockito.any());

        MockMail mockMail = new MockMail();
        mockMail.setMimeMessage(mimeMessage);

        MailHeaderSummaryHtmlBuilder builder = new MailHeaderSummaryHtmlBuilder();
        builder.setMockMail(mockMail);
        String builderResult = builder.build();
        String expectedResult = String.join("\n", allHeaders);
        Assertions.assertEquals(expectedResult, builderResult);
    }

    @Test
    public void build_mimeMessageThrowsException() throws MessagingException {
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doThrow(new MessagingException()).when(mimeMessage).getMatchingHeaderLines(Mockito.any());

        MockMail mockMail = new MockMail();
        mockMail.setMimeMessage(mimeMessage);

        MailHeaderSummaryHtmlBuilder builder = new MailHeaderSummaryHtmlBuilder();
        builder.setMockMail(mockMail);
        String builderResult = builder.build();
        Assertions.assertEquals("", builderResult);
    }

}
