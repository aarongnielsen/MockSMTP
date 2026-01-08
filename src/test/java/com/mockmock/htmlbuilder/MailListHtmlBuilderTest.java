package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;

public class MailListHtmlBuilderTest {

    @ParameterizedTest
    @NullAndEmptySource
    public void build_emptyMailQueue(ArrayList<MockMail> mailQueue) {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        builder.setMailQueue(mailQueue);
        String output = builder.build();
        Assertions.assertTrue(output.contains("<h1>No emails in queue</h1>"));
        Assertions.assertFalse(output.contains("<table"));
    }

    @Test
    public void build_mailQueueHasOneEntry() {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        ArrayList<MockMail> mailQueue = new ArrayList<>();
        mailQueue.add(new MockMail());
        builder.setMailQueue(mailQueue);
        String output = builder.build();
        Assertions.assertTrue(output.contains("<h1>You have 1 email! "));
        Assertions.assertTrue(output.contains("<table"));

    }

    @Test
    public void build_mailQueueHasMoreThanOneEntry() {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        ArrayList<MockMail> mailQueue = new ArrayList<>();
        mailQueue.add(new MockMail());
        mailQueue.add(new MockMail());
        builder.setMailQueue(mailQueue);
        String output = builder.build();
        Assertions.assertTrue(output.contains("<h1>You have 2 emails! "));
        Assertions.assertTrue(output.contains("<table"));
    }

    @Test
    public void build_mailMessageHasNoSubject() {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        ArrayList<MockMail> mailQueue = new ArrayList<>();
        builder.setMailQueue(mailQueue);

        MockMail mail = new MockMail();
        mail.setSubject(null);
        mailQueue.add(mail);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<em>No subject given</em>"));
    }

    @Test
    public void build_mailMessageHasHtmlBody() {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        ArrayList<MockMail> mailQueue = new ArrayList<>();
        builder.setMailQueue(mailQueue);

        MockMail mail = new MockMail();
        mail.setSubject("message1");
        mail.setBodyHtml("<html><body>body1</body></html>");
        mail.setBody(null);
        mailQueue.add(mail);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<a href=\"/view/body/1\">Body (HTML)</a>"));
    }

    @Test
    public void build_mailMessageHasPlainTextBody() {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        ArrayList<MockMail> mailQueue = new ArrayList<>();
        builder.setMailQueue(mailQueue);

        MockMail mail = new MockMail();
        mail.setSubject("message1");
        mail.setBodyHtml(null);
        mail.setBody("body1");
        mailQueue.add(mail);

        String output = builder.build();
        Assertions.assertTrue(output.contains("<a href=\"/view/body/1\">Body (text)</a>"));
    }

    @Test
    public void build_mailMessageHasAttachments() {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        ArrayList<MockMail> mailQueue = new ArrayList<>();
        builder.setMailQueue(mailQueue);

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

        String output = builder.build();
        Assertions.assertTrue(output.contains("<a href=\"/view/1/attachment/1\">Attachment 1</a>"));
        Assertions.assertTrue(output.contains("<a href=\"/view/1/attachment/2\">Attachment 2: attachment2.bin</a>"));
    }

}
