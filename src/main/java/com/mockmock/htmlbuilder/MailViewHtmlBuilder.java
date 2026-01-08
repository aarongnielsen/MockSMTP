package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailViewHtmlBuilder implements HtmlBuilder {

    @Autowired
    @Setter
    private MailHeaderSummaryHtmlBuilder mailHeaderSummaryHtmlBuilder;

    @Setter
    private MockMail mockMail;

    @Setter
    private int mailIndex;

    public String build() {
        mailHeaderSummaryHtmlBuilder.setMockMail(mockMail);

        String subjectOutput;
        if (mockMail.getSubject() == null) {
            subjectOutput = "<em>No subject given</em>";
        } else {
            subjectOutput = StringEscapeUtils.escapeHtml4(mockMail.getSubject());
        }
		subjectOutput += " <a class=\"heading-action\" href=\"/delete/" + mailIndex + "\">Delete</a>";

        String output = "<div class=\"container\">\n";

        output +=
                "<h1>" + subjectOutput + "</h1>\n" +
                "  <div class=\"row\">\n";

        output +=
                "    <div class=\"span10\" name=\"addresses\">\n" +
                "      <h3>Headers <a class=\"heading-action\" href=\"/view/headers/" + mailIndex + "\">Show All</a></h3>\n" +
                "      <pre class=\"well\" style=\"width: 100%;\">\n" + mailHeaderSummaryHtmlBuilder.build() + "</pre>\n" +
                "    </div>\n";

        // display body in an iframe
        String bodyHeading = (mockMail.getBodyHtml() != null ? "Body (HTML)" : "Body (plain text)");
        bodyHeading += " <a class=\"heading-action\" href=\"/view/body/" + mailIndex + "\">Open</a>";
        output += "    <div class=\"span10\" name=\"bodyHtmlFormatted\">\n" +
                  "      <h3>" + bodyHeading + "</h3>\n" +
                  "      <iframe class=\"well\" src=\"/view/body/" + mailIndex + "\" style=\"width: 100%; height: 500px; overflow: scroll;\" name=\"bodyHTML_iFrame\"></iframe>\n" +
                  "    </div>";

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
            output += "    <div class=\"span10\" name=\"attachmentsList\">\n" +
                      "      <h3>Attachments</h3>\n" +
                      "      <ol style=\"margin-bottom: 20px;\">\n" +
                      attachmentListItems +
                      "      </ol>\n" +
                      "    </div>\n";

        }

        output += "  </div>\n" +
                  "</div>\n";

        return output;
    }

}