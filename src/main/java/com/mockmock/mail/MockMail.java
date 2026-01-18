package com.mockmock.mail;

import lombok.Getter;
import lombok.Setter;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** This class represents a single message received by the application. **/
@Getter
@Setter
public class MockMail implements Comparable<MockMail> {

    private UUID id;
    private String from;
    private String to;
    private String subject;
    private String body;
    private String bodyHtml;
    private String rawMail;
    private MimeMessage mimeMessage;
    private long receivedTime;
    private final List<Attachment> attachments = new ArrayList<>();

    /** Compares two mock mail messages in ascending order of the times at which they were received. **/
    @Override
    public int compareTo(MockMail o) {
        long receivedTime = this.getReceivedTime();
        long receivedTime2 = o.getReceivedTime();

        long diff = receivedTime - receivedTime2;
        return (int) diff;
    }

    @Getter
    @Setter
    public static class Attachment {
        private String contentType;
        private String filename;
        private byte[] contents;
    }

}
