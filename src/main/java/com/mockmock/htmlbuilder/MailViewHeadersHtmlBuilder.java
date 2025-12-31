package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Enumeration;

@Service
@Slf4j
public class MailViewHeadersHtmlBuilder implements HtmlBuilder {

    @Setter
    private MockMail mockMail;

    public String build() {
        String output = "";

        if (mockMail != null) {
            MimeMessage mimeMessage = mockMail.getMimeMessage();
            try {
                output += "<pre>\n";
                Enumeration<?> headers = mimeMessage.getAllHeaderLines();
                while (headers.hasMoreElements()) {
                    String header = (String) headers.nextElement();
                    output += StringEscapeUtils.escapeHtml4(header) + "<br/>";
                }
                output += "</pre>";
            }
            catch (MessagingException msgX) {
                log.error("error reading email headers", msgX);
            }
        }

        return output;
    }

}
