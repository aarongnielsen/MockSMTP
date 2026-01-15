package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Vector;

public class MailMessageViewHtmlBuilderTest {

    @Test
    public void buildMailMessageView_messageHasNoSubject() throws MessagingException {
        MockMail mockMail = createMockMailMessage(null, null, null);
        MailMessageViewHtmlBuilder messageViewBuilder = new MailMessageViewHtmlBuilder();
        String output = messageViewBuilder.buildMailMessageView(mockMail, 42);
        Assertions.assertTrue(output.contains("<h1><em>No subject given</em> "));
    }

    @Test
    public void buildMailMessageView_messageHasSubject() throws MessagingException {
        MockMail mockMail = createMockMailMessage("Subject1 <-", null, null);
        MailMessageViewHtmlBuilder messageViewBuilder = new MailMessageViewHtmlBuilder();
        String output = messageViewBuilder.buildMailMessageView(mockMail, 42);
        Assertions.assertTrue(output.contains("<h1>Subject1 &lt;-"));
    }

    @Test
    public void buildMailMessageView_messageHasHtmlBody() throws MessagingException {
        MockMail mockMail = createMockMailMessage("Subject1", null, "<html><body>Body1</body></html>");
        MailMessageViewHtmlBuilder messageViewBuilder = new MailMessageViewHtmlBuilder();
        String output = messageViewBuilder.buildMailMessageView(mockMail, 42);
        Assertions.assertTrue(output.contains("<h3>Body (HTML) "));
    }

    @Test
    public void buildMailMessageView_messageHasPlainTextBody() throws MessagingException {
        MockMail mockMail = createMockMailMessage("Subject1", "Body1", null);
        MailMessageViewHtmlBuilder messageViewBuilder = new MailMessageViewHtmlBuilder();
        String output = messageViewBuilder.buildMailMessageView(mockMail, 42);
        Assertions.assertTrue(output.contains("<h3>Body (plain text) "));
    }

    @Test
    public void buildMailMessageView_messageHasAttachments() throws MessagingException {
        MockMail mockMail = createMockMailMessage("Subject1", "Body1", null);

        MockMail.Attachment attachment1 = new MockMail.Attachment();
        attachment1.setFilename("attachment1.png");
        attachment1.setContentType("image/png");
        attachment1.setContents(new byte[45]);
        mockMail.getAttachments().add(attachment1);

        MockMail.Attachment attachment2 = new MockMail.Attachment();
        attachment2.setFilename("attachment2.pdf");
        attachment2.setContentType("application/pdf");
        attachment2.setContents(new byte[54]);
        mockMail.getAttachments().add(attachment2);

        MailMessageViewHtmlBuilder messageViewBuilder = new MailMessageViewHtmlBuilder();
        String output = messageViewBuilder.buildMailMessageView(mockMail, 42);
        Assertions.assertTrue(output.contains("<li> <a href=\"/view/42/attachment/1\">attachment1.png</a> <em>(image/png, 45 bytes)</em> </li>"));
        Assertions.assertTrue(output.contains("<li> <a href=\"/view/42/attachment/2\">attachment2.pdf</a> <em>(application/pdf, 54 bytes)</em> </li>"));
    }

    @Test
    public void buildHeaderSummaryLines_valid() throws MessagingException {
        MockMail mockMail = createMockMailMessage("Subject1", "Body1", null);

        Vector<String> headers = new Vector<>();
        headers.add("Header1: Value1");
        headers.add("Header2: Value2");
        headers.add("Header3: Value3");
        Mockito.doReturn(headers.elements()).when(mockMail.getMimeMessage()).getMatchingHeaderLines(Mockito.any());

        MailMessageViewHtmlBuilder messageViewBuilder = new MailMessageViewHtmlBuilder();
        String output = messageViewBuilder.buildHeaderSummaryLines(mockMail);
        Assertions.assertEquals(String.join("\n", headers), output);
    }

    @Test
    public void buildHeaderSummaryLines_returnsEmptyStringOnException() throws MessagingException {
        MockMail mockMail = createMockMailMessage("Subject1", "Body1", null);
        Mockito.doThrow(new MessagingException()).when(mockMail.getMimeMessage()).getMatchingHeaderLines(Mockito.any());

        MailMessageViewHtmlBuilder messageViewBuilder = new MailMessageViewHtmlBuilder();
        String output = messageViewBuilder.buildHeaderSummaryLines(mockMail);
        Assertions.assertEquals("", output);
    }


    private static MockMail createMockMailMessage(String subject, String bodyText, String bodyHtml) throws MessagingException {
        MockMail mockMail = new MockMail();
        mockMail.setSubject(subject);
        mockMail.setBody(bodyText);
        mockMail.setBodyHtml(bodyHtml);
        MimeMessage mockMimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.doReturn(new Vector<String>().elements()).when(mockMimeMessage).getMatchingHeaderLines(Mockito.any());
        mockMail.setMimeMessage(mockMimeMessage);
        return mockMail;
    }

}
