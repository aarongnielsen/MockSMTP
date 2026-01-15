package com.mockmock.htmlbuilder;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.apache.commons.text.StringEscapeUtils;

import javax.mail.Message;

public class MailListViewHtmlBuilder extends WebUiHtmlBuilder {

    public String buildMailListView(MailQueue mailQueue) {
        // build the output here
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(buildHeader());

        stringBuilder.append("<div class=\"container\">\n");
        if (mailQueue == null || mailQueue.size() == 0) {
            stringBuilder.append("  <h1>No emails in queue</h1>\n");
        } else {
            String mailText = mailQueue.size() == 1 ? "email" : "emails";
            stringBuilder
                    .append("  <h1>")
                    .append("You have ").append(mailQueue.size()).append(" ").append(mailText).append("! ")
                    .append("<a class=\"heading-action\" href=\"/delete/all\">Delete all</a>")
                    .append("</h1>\n")
                    .append("  <table class=\"messages-list table table-striped\">\n")
                    .append("    <thead>\n")
                    .append("      <th>From</th>\n")
                    .append("      <th>To</th>\n")
                    .append("      <th>Subject</th>\n")
                    .append("      <th>Sections</th>\n")
                    .append("      <th>Actions</th>\n")
                    .append("    </thead>\n")
                    .append("    <tbody>\n")
                    ;

            // display the mail queue in reverse order of timestamp (i.e. most recent messages first)
            for (int index = mailQueue.size(); index >= 1; index--) {
                MockMail mockMail = mailQueue.getByIndex(index);
                stringBuilder.append(buildMailRow(mockMail, index));
            }

            stringBuilder
                    .append("    </tbody>\n")
                    .append("  </table>\n")
                    ;
        }
        stringBuilder.append("</div>");

        stringBuilder.append(buildFooter());

        return stringBuilder.toString();
    }

    private String buildMailRow(MockMail mail, int index) {
        String fromOutput = buildSenderAddress(mail);
        String toOutput = buildRecipientAddress(mail, Message.RecipientType.TO);

        String subjectOutput = (mail.getSubject() != null ?
                StringEscapeUtils.escapeHtml4(mail.getSubject()) :
                "<em>No subject given</em>"
        );

        StringBuilder sectionsStringBuilder = new StringBuilder();
        sectionsStringBuilder.append("    <a href=\"/view/").append(index).append("/headers\">")
                .append("Headers")
                .append("</a>")
                .append("<br>\n");
        sectionsStringBuilder.append("    <a href=\"/view/").append(index).append("/body\">")
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
            sectionsStringBuilder.append("    <a href=\"/view/").append(index).append("/attachment/").append(attachmentIndex).append("\">")
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
                "    <a href=\"/view/" + index + "/raw\">View raw text</a><br>\n" +
                "    <a href=\"/delete/" + index + "\">Delete</a>" +
                "  </td>\n" +
                "</tr>";
    }

}
