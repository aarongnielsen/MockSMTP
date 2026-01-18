package com.mockmock.htmlbuilder;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MailListViewHtmlBuilderTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void build_emptyMailQueue(boolean isMailQueueConstructed) {
        MailListViewHtmlBuilder builder = new MailListViewHtmlBuilder();
        String output = builder.buildMailListView(isMailQueueConstructed ? new MailQueue() : null);
        Assertions.assertTrue(output.contains("<h1>No emails in queue</h1>"));
        Assertions.assertFalse(output.contains("<table"));
    }

    @Test
    public void build_mailQueueHasOneEntry() {
        MailListViewHtmlBuilder builder = new MailListViewHtmlBuilder();
        MailQueue mailQueue = new MailQueue();
        mailQueue.add(new MockMail());
        String output = builder.buildMailListView(mailQueue);
        Assertions.assertTrue(output.contains("<h1>You have 1 email! "));
        Assertions.assertTrue(output.contains("<table"));
    }

    @Test
    public void build_mailQueueHasMoreThanOneEntry() {
        MailListViewHtmlBuilder builder = new MailListViewHtmlBuilder();
        MailQueue mailQueue = new MailQueue();
        mailQueue.add(new MockMail());
        mailQueue.add(new MockMail());
        String output = builder.buildMailListView(mailQueue);
        Assertions.assertTrue(output.contains("<h1>You have 2 emails! "));
        Assertions.assertTrue(output.contains("<table"));
    }

    @Test
    public void build_mailMessageHasNoSubject() {
        MailListViewHtmlBuilder builder = new MailListViewHtmlBuilder();
        MailQueue mailQueue = new MailQueue();

        MockMail mockMail = new MockMail();
        mockMail.setSubject(null);
        mailQueue.add(mockMail);

        String output = builder.buildMailListView(mailQueue);
        Assertions.assertTrue(output.contains("<em>No subject given</em>"));
    }

    @Test
    public void build_mailMessageHasHtmlBody() {
        MailListViewHtmlBuilder builder = new MailListViewHtmlBuilder();
        MailQueue mailQueue = new MailQueue();

        MockMail mail = new MockMail();
        mail.setSubject("message1");
        mail.setBodyHtml("<html><body>body1</body></html>");
        mail.setBody(null);
        mailQueue.add(mail);

        String output = builder.buildMailListView(mailQueue);
        Assertions.assertTrue(output.contains("<a href=\"/view/1/body\">Body (HTML)</a>"));
    }

    @Test
    public void build_mailMessageHasPlainTextBody() {
        MailListViewHtmlBuilder builder = new MailListViewHtmlBuilder();
        MailQueue mailQueue = new MailQueue();

        MockMail mail = new MockMail();
        mail.setSubject("message1");
        mail.setBodyHtml(null);
        mail.setBody("body1");
        mailQueue.add(mail);

        String output = builder.buildMailListView(mailQueue);
        Assertions.assertTrue(output.contains("<a href=\"/view/1/body\">Body (text)</a>"));
    }

    @Test
    public void build_mailMessageHasAttachments() {
        MailListViewHtmlBuilder builder = new MailListViewHtmlBuilder();
        MailQueue mailQueue = new MailQueue();

        MockMail mail = new MockMail();
        mail.setSubject("message1");
        mail.setBodyHtml(null);
        mail.setBody("body1");
        MockMail.Attachment attachment1 = new MockMail.Attachment();
        attachment1.setFilename(null);
        mail.getAttachments().add(attachment1);
        MockMail.Attachment attachment2 = new MockMail.Attachment();
        attachment2.setFilename("attachment2.bin");
        mail.getAttachments().add(attachment2);
        mailQueue.add(mail);

        String output = builder.buildMailListView(mailQueue);
        Assertions.assertTrue(output.contains("<a href=\"/view/1/attachment/1\">Attachment 1</a>"));
        Assertions.assertTrue(output.contains("<a href=\"/view/1/attachment/2\">Attachment 2: attachment2.bin</a>"));
    }

}
