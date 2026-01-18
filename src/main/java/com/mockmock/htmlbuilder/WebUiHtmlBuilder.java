package com.mockmock.htmlbuilder;

import com.mockmock.Main;
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

/**
 * The base for classes that create view pages in the Web UI.
 * It provides some default behaviour for generating HTML headers and footers,
 * as well as formatting email addresses and their display names.
 */
@Slf4j
public abstract class WebUiHtmlBuilder {

    /** The maximum length (in characters) that can be displayed for an email address and its display name. **/
    @Setter
    protected int maxAddressLength = 30;

    // public methods

    /** Builds the common HTML header for the page. **/
    protected String buildHeader() {
        String output =
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <title>MockSMTP " + Main.VERSION_NUMBER + "</title>\n" +
                "    <link href=\"/web-static/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "    <link href=\"/web-static/css/mockmock.css\" rel=\"stylesheet\">\n" +
                "  </head>\n" +
                "  <body>\n" +
                "  <div class=\"navbar navbar-inverse navbar-fixed-top\">\n" +
                "    <div class=\"navbar-inner\">\n" +
                "      <div class=\"container\">\n" +
                "        <a class=\"brand\" href=\"/\">MockSMTP</a>\n" +
                "        <div class=\"nav-collapse collapse\">\n" +
                "          <ul class=\"nav\">\n" +
                "            <li class=\"active\"><a href=\"/\">Home</a></li>\n" +
                "            <li><a href=\"https://github.com/aarongnielsen/MockSMTP\">MockSMTP on Github</a></li>\n" +
                "          </ul>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n";
        return output;
    }

    /** Builds the common HTML footer for the page. **/
    protected String buildFooter() {
        String output =
                "  <script src=\"/web-static/js/jquery-1.8.1.min.js\"></script>\n" +
                "  <script src=\"/web-static/js/bootstrap.min.js\"></script>\n" +
                "  </body>\n" +
                "</html>\n";
        return output;
    }

    /**
     * Builds a HTML string for the sender of the given email message.
     * <p>
     * If the sender has a display name, it will be displayed first, with the email address surrounded by angle brackets.
     * If not, the email address is displayed on its own.
     */
    protected String buildSenderAddress(MockMail mockMail) {
        MimeMessage mimeMessage = mockMail.getMimeMessage();
        if (mimeMessage == null) {
            return "";
        }

        try {
            String longAddresses;
            Address[] addresses = mimeMessage.getFrom();
            if (addresses != null) {
                longAddresses = Arrays.stream(addresses)
                        .map(address -> StringEscapeUtils.escapeHtml4(address.toString()))
                        .collect(Collectors.joining(", "));
            } else {
                longAddresses = StringEscapeUtils.escapeHtml4(mockMail.getFrom());
            }

            String shortAddresses = longAddresses;
            if (maxAddressLength > 0) {
                if (longAddresses.length() > maxAddressLength) {
                    shortAddresses = longAddresses.substring(0, maxAddressLength) + "...";
                }
            }

            return "<span title=\"" + longAddresses + "\">" + shortAddresses + "</title>";
        }
        catch (MessagingException msgX) {
            log.error("error reading sender details from email", msgX);
        }

        // should never happen
        return null;
    }

    /**
     * Builds a HTML string for the sender of the given email message.
     * <p>
     * If the sender has a display name, it will be displayed first, with the email address surrounded by angle brackets.
     * If not, the email address is displayed on its own.
     * <p>
     * Multiple recipients of the same type are displayed as a comma-separated list.
     * If no recipient type is specified, all recipients are listed together.
     */
    protected String buildRecipientAddress(MockMail mockMail, Message.RecipientType recipientType) {
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
            if (maxAddressLength > 0) {
                if (longAddresses.length() > maxAddressLength) {
                    shortAddresses = longAddresses.substring(0, maxAddressLength) + "...";
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
