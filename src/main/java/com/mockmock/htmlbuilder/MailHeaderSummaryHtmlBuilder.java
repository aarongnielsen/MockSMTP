package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
@Setter
@Slf4j
public class MailHeaderSummaryHtmlBuilder implements HtmlBuilder {

    /**
     * A list of user-facing SMTP headers to include in the summary, if they are present in the message.
     * Note that these are case-insensitive.
     */
    private static final String[] SMTP_HEADERS = {
            "subject", "date", "from", "to", "cc", "bcc", "reply-to"
    };

    private MockMail mockMail;

    public String build() {
        String output = "";

        MimeMessage mimeMessage = mockMail.getMimeMessage();
        List<String> headerLines = new ArrayList<>();
        try {
            for (Enumeration<?> e = mimeMessage.getMatchingHeaderLines(SMTP_HEADERS); e.hasMoreElements(); ) {
                headerLines.add(StringEscapeUtils.escapeHtml4((String) e.nextElement()));
            }
        } catch (MessagingException msgX) {
            log.error("error reading mail headers", msgX);
        }
        output += String.join("\n", headerLines);

        return output;
    }

}
