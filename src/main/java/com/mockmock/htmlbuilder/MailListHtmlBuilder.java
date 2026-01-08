package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;

@Service
public class MailListHtmlBuilder implements HtmlBuilder {

    @Setter
    private ArrayList<MockMail> mailQueue;

    public String build() {
        // build the output here
        String output = "<div class=\"container\">\n";

        if(mailQueue == null || mailQueue.isEmpty()) {
            output += "  <h1>No emails in queue</h1>\n";
        } else {
            String mailText = mailQueue.size() == 1 ? "email" : "emails";
            output += "  <h1>You have "  + mailQueue.size() + " " + mailText + "! <a class=\"heading-action\" href=\"/mail/delete/all\">Delete all</a></h1>\n";
            output += "  <table class=\"messages-list table table-striped\">\n";
            output += "    <thead>\n";
            output += "      <th>From</th>\n";
            output += "      <th>To</th>\n";
            output += "      <th>Subject</th>\n";
            output += "      <th>Sections</th>\n";
            output += "      <th>Actions</th>\n";
            output += "    </thead>\n";
            output += "    <tbody>\n";

            // display the mail queue in reverse order of timestamp (i.e. most recent messages first)
            for (int index = mailQueue.size() - 1; index >= 0; index--) {
                output += buildMailRow(mailQueue.get(index), index + 1);
            }

            output += "    </tbody>\n";
            output += "  </table>\n";
        }

        output += "</div>\n";

        return output;
    }

    private String buildMailRow(MockMail mail, int index) {
        StringFromHtmlBuilder fromBuilder = new StringFromHtmlBuilder();
        fromBuilder.setMaxLength(30);
        fromBuilder.setMockMail(mail);
        String fromOutput = fromBuilder.build();

        StringRecipientHtmlBuilder recipientBuilder = new StringRecipientHtmlBuilder();
        recipientBuilder.setMaxLength(30);
        recipientBuilder.setMockMail(mail);
        recipientBuilder.setRecipientType(MimeMessage.RecipientType.TO);
        String toOutput = recipientBuilder.build();

        String subjectOutput = (mail.getSubject() != null ?
                StringEscapeUtils.escapeHtml4(mail.getSubject()) :
                "<em>No subject given</em>"
        );

        StringBuilder sectionsStringBuilder = new StringBuilder();
        sectionsStringBuilder.append("    <a href=\"/view/headers/").append(index).append("\">")
                .append("Headers")
                .append("</a>")
                .append("<br>\n");
        sectionsStringBuilder.append("    <a href=\"/view/body/").append(index).append("\">")
                .append(mail.getBodyHtml() != null ? "Body (HTML)" : "Body (text)")
                .append("</a>")
                .append("<br>\n");
        for (int i = 0; i < mail.getAttachments().size(); i++) {
            MockMail.Attachment attachment = mail.getAttachments().get(i);
            int attachmentIndex = i + 1;
            String attachmentText = "Attachment " + attachmentIndex;
            if (attachment.getFilename() != null) {
                attachmentText += ": " + attachment.getFilename();
            }
            sectionsStringBuilder.append("    <a href=\"/view/" + index + "/attachment/" + attachmentIndex + "\">")
                    .append(attachmentText)
                    .append("</a><br>\n");
        }

        return
            "<tr>\n" +
            "  <td>" + fromOutput + "</td>\n" +
            "  <td>" + toOutput + "</td>\n" +
            "  <td><a title=\"" + StringEscapeUtils.escapeHtml4(mail.getSubject()) + "\" href=\"/view/" + index + "\">" + subjectOutput + "</a></td>\n" +
            "  <td class=\"table-column-sections action-link\">\n" +
                 sectionsStringBuilder +
            "  </td>\n" +
            "  <td class=\"table-column-actions action-link\">\n" +
            "    <a href=\"/view/raw/" + index + "\">View raw text</a><br>\n" +
            "    <a href=\"/delete/" + index + "\">Delete</a>" +
            "  </td>\n" +
            "</tr>";
    }

}
