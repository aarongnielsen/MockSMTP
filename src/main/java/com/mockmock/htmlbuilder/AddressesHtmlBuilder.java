package com.mockmock.htmlbuilder;

import com.mockmock.mail.MockMail;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@Setter
public class AddressesHtmlBuilder implements HtmlBuilder {

    private MockMail mockMail;

    public String build() {
        String output = "";

        StringFromHtmlBuilder fromHtmlBuilder = new StringFromHtmlBuilder();
        fromHtmlBuilder.setMockMail(mockMail);

        StringRecipientHtmlBuilder recipientHtmlBuilder = new StringRecipientHtmlBuilder();
        recipientHtmlBuilder.setMockMail(mockMail);

        output += "From: " + fromHtmlBuilder.build() + "<br />\n";

        recipientHtmlBuilder.setRecipientType(MimeMessage.RecipientType.TO);
        output += "To: " + recipientHtmlBuilder.build() + "<br />\n";

        recipientHtmlBuilder.setRecipientType(MimeMessage.RecipientType.CC);
        String ccOutput = recipientHtmlBuilder.build();
        if(!ccOutput.isEmpty()) {
            output += "CC: " + ccOutput + "<br />\n";
        }

        recipientHtmlBuilder.setRecipientType(MimeMessage.RecipientType.BCC);
        String bccOutput = recipientHtmlBuilder.build();
        if(!bccOutput.isEmpty()) {
            output += "BCC: " + bccOutput + "<br />\n";
        }

        return output;
    }

}
