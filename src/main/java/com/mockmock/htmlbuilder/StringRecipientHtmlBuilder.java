package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.stream.Collectors;

@Setter
@Slf4j
public class StringRecipientHtmlBuilder implements HtmlBuilder {

    private MockMail mockMail;
    private int maxLength = 0;
    Message.RecipientType recipientType;

    public String build() {
        MimeMessage mimeMessage = mockMail.getMimeMessage();
		if(mimeMessage == null) {
			return "";
		}

        try {
            Address[] addresses = (recipientType == null ? mimeMessage.getAllRecipients() : mimeMessage.getRecipients(recipientType));
            if (addresses == null) {
                return (recipientType == Message.RecipientType.TO ? mockMail.getTo() : "");
            }

            String longAddresses = Arrays.stream(addresses)
                    .map(address -> StringEscapeUtils.escapeHtml4(address.toString()))
                    .collect(Collectors.joining(", "));

            String shortAddresses = longAddresses;
            if (maxLength > 0) {
                if (longAddresses.length() > maxLength) {
                    shortAddresses = longAddresses.substring(0, maxLength) + "...";
                }
            }

            return "<span title=\"" + longAddresses + "\">" + shortAddresses + "</title>";
        }
        catch (MessagingException msgX) {
            log.error("error reading recipient details from email", msgX);
        }

        // should never happen
        return null;
    }

}
