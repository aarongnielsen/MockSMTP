package com.mockmock.mail;

import com.mockmock.Settings;
import com.mockmock.Util;
import lombok.extern.slf4j.Slf4j;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;

@Slf4j
public class MockSmtpMessageHandlerFactory implements MessageHandlerFactory {

    private final MailQueue mailQueue;
	private final Settings settings;

    public MockSmtpMessageHandlerFactory(MailQueue mailQueue, Settings settings) {
        this.mailQueue = mailQueue;
        this.settings = settings;
    }

	@Override
    public MessageHandler create(MessageContext messageContext)
    {
        return new MockMockHandler(messageContext);
    }

    class MockMockHandler implements MessageHandler {
        MessageContext context;
        MockMail mockMail;

        /**
         * Constructor
         *
         * @param context MessageContext
         */
        public MockMockHandler(MessageContext context) {
            this.context = context;
            this.mockMail = new MockMail();

            // give the mockmail a unique id (previously just a timestamp in ms)
            this.mockMail.setId(UUID.randomUUID());
        }

        /**
         * Called to set the sender's address, upon receiving the {@code MAIL FROM} command in an SMTP exchange.
         * This is typically called before the {@link #recipient(String) recipients} are set.
         */
        @Override
        public void from(String from) throws RejectException {
            this.mockMail.setFrom(from);
        }

        /**
         * Called to set the recipient's address, upon receiving the {@code RCPT} command in an SMTP exchange.
         * This is typically called just after the {@link #from(String) sender} is set.
         * <p>
         * Note that this may be called multiple times for different recipient types (e.g. CC/BCC).
         * However, this application only stores one recipient, presumed to be in the {@code To:} field.
         */
        @Override
        public void recipient(String recipient) throws RejectException {
            this.mockMail.setTo(recipient);
        }

        /** Called to store the message data, upon receiving the {@code DATA} command in an SMTP exchange. **/
        @Override
        public void data(InputStream data) throws RejectException, IOException {
            String rawMail = Util.getStreamContentsAsString(data);
            mockMail.setRawMail(rawMail);

            Session session = Session.getDefaultInstance(new Properties());
            InputStream is = new ByteArrayInputStream(rawMail.getBytes());

            try {
                MimeMessage message = new MimeMessage(session, is);
                mockMail.setSubject(message.getSubject());
                mockMail.setMimeMessage(message);

                Object messageContent = message.getContent();
                if (messageContent instanceof Multipart) {
                    Multipart multipart = (Multipart) messageContent;
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        String contentType = bodyPart.getContentType();
                        if (contentType.matches("text/plain.*")) {
                            mockMail.setBody(Util.getStreamContentsAsString(bodyPart.getInputStream()));
                        } else if (contentType.matches("text/html.*")) {
                            mockMail.setBodyHtml(Util.getStreamContentsAsString(bodyPart.getInputStream()));
                        } else if (bodyPart.getHeader("Content-Disposition") != null) {
                            String attachmentContentType = bodyPart.getContentType(); //.getHeader("Content-Type")[0];
                            int indexOfSemicolon = attachmentContentType.indexOf(';');
                            if (indexOfSemicolon != -1) {
                                attachmentContentType = attachmentContentType.substring(0, indexOfSemicolon);
                            }

                            String contentDispositionFilename = bodyPart.getHeader("Content-Disposition")[0];
                            int indexOfFilename = contentDispositionFilename.indexOf("filename=\"");
                            if (indexOfFilename != -1) {
                                contentDispositionFilename = contentDispositionFilename.substring(indexOfFilename + 10, contentDispositionFilename.length() - 1);
                            }

                            MockMail.Attachment attachment = new MockMail.Attachment();
                            attachment.setContentType(attachmentContentType);
                            attachment.setFilename(contentDispositionFilename);
                            attachment.setContents(Util.getStreamContentsAsByteArray(bodyPart.getInputStream()));
                            mockMail.getAttachments().add(attachment);
                        }
                    }
                } else if (messageContent instanceof InputStream) {
                    InputStream mailContent = (InputStream) messageContent;
                    mockMail.setBody(Util.getStreamContentsAsString(mailContent));
                } else if (messageContent instanceof String) {
                    String contentType = message.getContentType();

                    if (contentType.matches("text/plain.*")) {
                        mockMail.setBody(messageContent.toString());
                    } else if (contentType.matches("text/html.*")) {
                        mockMail.setBodyHtml(messageContent.toString());
                    }
                }
            } catch (MessagingException msgX) {
                log.error("Error processing message data", msgX);
            }
        }

        @Override
        public void done() {
            // check if this email's "from" address matches one in the filtered list
            if (settings.getFilterFromEmailAddresses().contains(mockMail.getFrom())) {
                log.warn("Skipping email because From address matches filter: {}", mockMail.getFrom());
                return;
            }

            // check if this email's "to" address matches one in the filtered list
            if (settings.getFilterToEmailAddresses().contains(mockMail.getTo())) {
                log.warn("Skipping email because To address matches filter: {}", mockMail.getTo());
                return;
            }

            // set the received date
            mockMail.setReceivedTime(Instant.now().toEpochMilli());

            mailQueue.add(mockMail);
            log.info("Email received from {}", mockMail.getFrom());
        }
    }

}
