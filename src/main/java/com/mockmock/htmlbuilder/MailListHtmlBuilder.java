package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;

@Setter
@Service
public class MailListHtmlBuilder implements HtmlBuilder
{
    private ArrayList<MockMail> mailQueue;

    public String build()
    {
        String output =
                    "<div class=\"container\">\n";

        if(mailQueue == null || mailQueue.isEmpty())
        {
            output += "  <h2>No emails in queue</h2>\n";
        }
        else
        {
            String mailText = mailQueue.size() == 1 ? "email" : "emails";
            output += "  <h1>You have "  + mailQueue.size() + " " + mailText + "! <small class=\"deleteLink\"><a class=\"delete\" href=\"/mail/delete/all\">Delete all</a></small></h1>\n";
            output += "  <table class=\"table table-striped\">\n";
            output += "    <thead>\n";
            output += "      <th>From</th>\n";
            output += "      <th>To</th>\n";
            output += "      <th>Subject</th>\n";
            output += "      <th>Sections</th>\n";
            output += "      <th>Action</th>\n";
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

    private String buildMailRow(MockMail mail, int index)
    {
        StringFromHtmlBuilder fromBuilder = new StringFromHtmlBuilder();
        fromBuilder.setMaxLength(30);
        fromBuilder.setMockMail(mail);
        String fromOutput = fromBuilder.build();

        StringRecipientHtmlBuilder recipientBuilder = new StringRecipientHtmlBuilder();
        recipientBuilder.setMaxLength(30);
        recipientBuilder.setMockMail(mail);
        recipientBuilder.setRecipientType(MimeMessage.RecipientType.TO);
        String toOutput = recipientBuilder.build();

        String subjectOutput;
        if(mail.getSubject() == null)
        {
            subjectOutput = "<em>No subject given</em>";
        }
        else
        {
            subjectOutput = StringEscapeUtils.escapeHtml4(mail.getSubject());
        }

        StringBuilder attachmentStringBuilder = new StringBuilder();
        attachmentStringBuilder.append("    <a href=\"/view/headers/").append(index).append("\">")
                .append("<em>").append("Headers").append("</em>")
                .append("</a>")
                .append("<br>\n");
        attachmentStringBuilder.append("    <a href=\"/view/body/").append(index).append("\">")
                .append("<em>").append(mail.getBodyHtml() != null ? "Body HTML" : "Body text").append("</em>")
                .append("</a>")
                .append("<br>\n");
        for (int i = 0; i < mail.getAttachments().size(); i++) {
            MockMail.Attachment attachment = mail.getAttachments().get(i);
            attachmentStringBuilder.append("    <a href=\"/view/" + index + "/attachment/" + (i + 1) + "\"><em>Attachment " + (i + 1) + "</em></a><br>\n");
        }

        return
            "<tr>\n" +
            "  <td>" + fromOutput + "</td>\n" +
            "  <td>" + toOutput + "</td>\n" +
            "  <td><a title=\"" + StringEscapeUtils.escapeHtml4(mail.getSubject()) + "\" href=\"/view/" + index + "\">" + subjectOutput + "</a></td>\n" +
            "  <td>\n" +
                 attachmentStringBuilder +
            "  </td>\n" +
            "  <td>\n" +
            "    <a href=\"/view/raw/" + index + "\"><em>View raw text</em></a><br>\n" +
            "    <a href=\"/delete/" + index + "\"><em>Delete</em></a>" +
            "  </td>\n" +
            "</tr>";
    }
}
