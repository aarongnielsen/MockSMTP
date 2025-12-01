package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
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
            output += "      <th>Action</th>\n";
            output += "    </thead>\n";
            output += "    <tbody>\n";
            for (int index = 0; index < mailQueue.size(); index++) {
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
        fromBuilder.setMockMail(mail);
        String fromOutput = fromBuilder.build();

        StringRecipientHtmlBuilder recipientBuilder = new StringRecipientHtmlBuilder();
        recipientBuilder.setMaxLength(27);
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
            subjectOutput = StringEscapeUtils.escapeHtml(mail.getSubject());
        }

        StringBuilder attachmentStringBuilder = new StringBuilder();
        if (mail.getBodyHtml() != null) {
            attachmentStringBuilder.append("    <a href=\"/view/html/" + index + "\"><em>Body HTML</em></a><br>\n");
        }
        for (int i = 0; i < mail.getAttachments().size(); i++) {
            MockMail.Attachment attachment = mail.getAttachments().get(i);
            attachmentStringBuilder.append("    <a href=\"/view/" + index + "/attachment/" + (i + 1) + "\"><em>Attachment " + (i + 1) + "</em></a><br>\n");
        }

        return
            "<tr>\n" +
            "  <td>" + fromOutput + "</td>\n" +
            "  <td>" + toOutput + "</td>\n" +
            "  <td><a title=\"" + StringEscapeUtils.escapeHtml(mail.getSubject()) + "\" href=\"/view/" + index + "\">" + subjectOutput + "</a></td>\n" +
            "  <td>\n" +
                 attachmentStringBuilder +
            "    <a title=\"Delete this mail\" href=\"/delete/" + index + "\"><em>Delete</em></a>" +
            "  </td>\n" +
            "</tr>";
    }
}
