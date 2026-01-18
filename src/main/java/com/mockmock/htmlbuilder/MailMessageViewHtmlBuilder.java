package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Builds the web UI page displaying the details of a single received mail message.
 */
@Slf4j
public class MailMessageViewHtmlBuilder extends WebUiHtmlBuilder {

    /** Builds a web page, including the header and footer, to display the details of a single message. **/
    public String buildMailMessageView(MockMail mockMail, int mailIndex) {
        // build the output here
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(buildHeader());

        String subjectOutput;
        if (mockMail.getSubject() == null) {
            subjectOutput = "<em>No subject given</em>";
        } else {
            subjectOutput = StringEscapeUtils.escapeHtml4(mockMail.getSubject());
        }
        subjectOutput += " <a class=\"heading-action\" href=\"/delete/" + mailIndex + "\">Delete</a>";

        stringBuilder
                .append("<div class=\"container\">\n")
                .append("  <h1>").append(subjectOutput).append("</h1>\n")
                .append("  <div class=\"row\">\n")
                .append("    <div class=\"span10\" name=\"addresses\">\n")
                .append("      <h3>Headers <a class=\"heading-action\" href=\"/view/").append(mailIndex).append("/headers\">Show All</a></h3>\n")
                .append("      <pre class=\"well\" style=\"width: 100%;\">\n").append(buildHeaderSummaryLines(mockMail)).append("</pre>\n")
                .append("    </div>\n")
                ;

        // display body in an iframe
        String bodyHeading = (mockMail.getBodyHtml() != null ? "Body (HTML)" : "Body (plain text)");
        bodyHeading += " <a class=\"heading-action\" href=\"/view/" + mailIndex + "/body\">Open</a>";
        stringBuilder
                .append("    <div class=\"span10\" name=\"bodyHtmlFormatted\">\n")
                .append("      <h3>").append(bodyHeading).append("</h3>\n")
                .append("      <iframe class=\"well\" src=\"/view/").append(mailIndex).append("/body\" style=\"width: 100%; height: 500px; overflow: scroll;\" name=\"bodyHTML_iFrame\"></iframe>\n")
                .append("    </div>")
                ;

        if (!mockMail.getAttachments().isEmpty()) {
            StringBuilder attachmentListItems = new StringBuilder();
            for (int i = 0; i < mockMail.getAttachments().size(); i++) {
                int attachmentIndex = i + 1;
                MockMail.Attachment attachment = mockMail.getAttachments().get(i);
                attachmentListItems
                        .append("        <li> ")
                        .append("<a href=\"/view/").append(mailIndex).append("/attachment/").append(attachmentIndex).append("\">")
                        .append(attachment.getFilename())
                        .append("</a>")
                        .append(" <em>(").append(attachment.getContentType()).append(", ").append(attachment.getContents().length).append(" bytes)</em>")
                        .append(" </li>\n");
            }
            stringBuilder
                    .append("    <div class=\"span10\" name=\"attachmentsList\">\n")
                    .append("      <h3>Attachments</h3>\n")
                    .append("      <ol style=\"margin-bottom: 20px;\">\n")
                    .append(attachmentListItems)
                    .append("      </ol>\n")
                    .append("    </div>\n")
                    ;

        }

        stringBuilder
                .append("  </div>\n")
                .append("</div>\n")
                ;

        stringBuilder.append(buildFooter());

        return stringBuilder.toString();
    }


    /**
     * A list of user-facing SMTP headers to include in the summary, if they are present in the message.
     * Note that these are case-insensitive.
     */
    private static final String[] SMTP_HEADERS = {
            "subject", "date", "from", "to", "cc", "bcc", "reply-to"
    };

    /**
     * Builds a summary list of the email's headers for display on this page.
     * The headers are displayed simply as a list of strings, separated by line feed characters.
     * <p>
     * While only a summary of the headers are displayed by this method,
     * the web UI allows the user to load the entire set of headers for a given message.
     */
    public String buildHeaderSummaryLines(MockMail mockMail) {
        MimeMessage mimeMessage = mockMail.getMimeMessage();
        List<String> headerLines = new ArrayList<>();
        try {
            for (Enumeration<?> e = mimeMessage.getMatchingHeaderLines(SMTP_HEADERS); e.hasMoreElements(); ) {
                headerLines.add(StringEscapeUtils.escapeHtml4((String) e.nextElement()));
            }
        } catch (MessagingException msgX) {
            log.error("error reading mail headers", msgX);
        }

        return String.join("\n", headerLines);
    }

}
