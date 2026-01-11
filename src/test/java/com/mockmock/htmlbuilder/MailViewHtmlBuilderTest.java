package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MailViewHtmlBuilderTest {

    @Test
    public void build_messageHasNoSubject() {
        MockMail mockMail = new MockMail();
        mockMail.setSubject(null);

        MailHeaderSummaryHtmlBuilder headerBuilder = Mockito.mock(MailHeaderSummaryHtmlBuilder.class);
        Mockito.doReturn("Header1: Value1").when(headerBuilder).build();

        MailViewHtmlBuilder builder = new MailViewHtmlBuilder();
        builder.setMailIndex(42);
        builder.setMockMail(mockMail);
        builder.setMailHeaderSummaryHtmlBuilder(headerBuilder);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<h1><em>No subject given</em> "));
    }

    @Test
    public void build_messageHasSubject() {
        MockMail mockMail = new MockMail();
        mockMail.setSubject("Subject1 <-");

        MailHeaderSummaryHtmlBuilder headerBuilder = Mockito.mock(MailHeaderSummaryHtmlBuilder.class);
        Mockito.doReturn("Header1: Value1").when(headerBuilder).build();

        MailViewHtmlBuilder builder = new MailViewHtmlBuilder();
        builder.setMailIndex(42);
        builder.setMockMail(mockMail);
        builder.setMailHeaderSummaryHtmlBuilder(headerBuilder);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<h1>Subject1 &lt;-"));
    }

    @Test
    public void build_messageHasHtmlBody() {
        MockMail mockMail = new MockMail();
        mockMail.setSubject("Subject1");
        mockMail.setBodyHtml("<html><body>Body1</body></html>");

        MailHeaderSummaryHtmlBuilder headerBuilder = Mockito.mock(MailHeaderSummaryHtmlBuilder.class);
        Mockito.doReturn("Header1: Value1").when(headerBuilder).build();

        MailViewHtmlBuilder builder = new MailViewHtmlBuilder();
        builder.setMailIndex(42);
        builder.setMockMail(mockMail);
        builder.setMailHeaderSummaryHtmlBuilder(headerBuilder);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<h3>Body (HTML) "));
    }

    @Test
    public void build_messageHasPlainTextBody() {
        MockMail mockMail = new MockMail();
        mockMail.setSubject("Subject1");
        mockMail.setBody("Body1");

        MailHeaderSummaryHtmlBuilder headerBuilder = Mockito.mock(MailHeaderSummaryHtmlBuilder.class);
        Mockito.doReturn("Header1: Value1").when(headerBuilder).build();

        MailViewHtmlBuilder builder = new MailViewHtmlBuilder();
        builder.setMailIndex(42);
        builder.setMockMail(mockMail);
        builder.setMailHeaderSummaryHtmlBuilder(headerBuilder);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<h3>Body (plain text) "));
    }

    @Test
    public void build_messageHasAttachments() {
        MockMail mockMail = new MockMail();
        mockMail.setSubject("Subject1");
        mockMail.setBody("Body1");

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

        MailHeaderSummaryHtmlBuilder headerBuilder = Mockito.mock(MailHeaderSummaryHtmlBuilder.class);
        Mockito.doReturn("Header1: Value1").when(headerBuilder).build();

        MailViewHtmlBuilder builder = new MailViewHtmlBuilder();
        builder.setMailIndex(42);
        builder.setMockMail(mockMail);
        builder.setMailHeaderSummaryHtmlBuilder(headerBuilder);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<li> <a href=\"/view/42/attachment/1\">attachment1.png</a> <em>(image/png, 45 bytes)</em> </li>"));
        Assertions.assertTrue(output.contains("<li> <a href=\"/view/42/attachment/2\">attachment2.pdf</a> <em>(application/pdf, 54 bytes)</em> </li>"));
    }

}
