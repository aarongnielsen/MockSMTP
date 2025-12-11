package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.stream.Collectors;

@Setter
@Slf4j
public class StringFromHtmlBuilder implements HtmlBuilder {

    private MockMail mockMail;
    private int maxLength = 0;

    @Override
    public String build() {
        MimeMessage mimeMessage = mockMail.getMimeMessage();
		if (mimeMessage == null) {
			return "";
		}

        String longAddresses;
        try {
            Address[] addresses = mimeMessage.getFrom();
            if (addresses != null) {
                longAddresses = Arrays.stream(addresses)
                        .map(address -> StringEscapeUtils.escapeHtml4(address.toString()))
                        .collect(Collectors.joining(", "));
            } else {
                longAddresses = StringEscapeUtils.escapeHtml4(mockMail.getFrom());
            }

            String shortAddresses = longAddresses;
            if (maxLength > 0 && longAddresses.length() > maxLength) {
                shortAddresses = longAddresses.substring(0, maxLength) + "...";
            }

            return "<span title=\"" + longAddresses + "\">" + shortAddresses + "</title>";
        }
        catch (MessagingException msgX) {
            log.error("error reading sender details from email", msgX);
        }

        // should never happen
        return null;
    }

}
