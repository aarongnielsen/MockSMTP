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
        Assertions.assertTrue(output.contains("<h2>No emails in queue</h2>"));
        Assertions.assertFalse(output.contains("<table"));
    }

    @Test
    public void build_nonEmptyMailQueue() {
        MailListHtmlBuilder builder = new MailListHtmlBuilder();
        ArrayList<MockMail> mailQueue = new ArrayList<>();
        mailQueue.add(new MockMail());
        builder.setMailQueue(mailQueue);
        String output = builder.build();
        Assertions.assertFalse(output.contains("<h2>No emails in queue</h2>"));
        Assertions.assertTrue(output.contains("<table"));

    }
}
