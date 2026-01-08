package com.mockmock.http;

import com.mockmock.mail.MockMail;
import org.eclipse.jetty.server.Request;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AttachmentHandler extends BaseHandler {

    @Override
    protected String getUrlPathPattern() {
        return "^/view/(-?[0-9]+)/attachment/([0-9]+)/?$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
    {
        if(!isUrlPathMatch(target)) {
            return;
        }

        int mailIndex = getMailIndex(target);
        if(mailIndex == 0) {
            return;
        }

        MockMail mockMail = this.mailQueue.getByIndex(mailIndex);
        if(mockMail == null) {
            return;
        }

        int attachmentIndex = getAttachmentIndex(target);
        if (attachmentIndex <= 0 || attachmentIndex > mockMail.getAttachments().size()) {
            return;
        }

        MockMail.Attachment attachment = mockMail.getAttachments().get(attachmentIndex - 1);
        response.setContentType(attachment.getContentType());
        response.setHeader("Content-Disposition", "filename=\"email_" + mailIndex + "_" + attachment.getFilename() + "\"");
        response.setStatus(HttpServletResponse.SC_OK);

        response.getOutputStream().write(attachment.getContents());

        request.setHandled(true);
    }

    /**
     * Returns the mail id if it is part of the target
     * @param target String
     * @return int
     */
    private int getMailIndex(String target) {
        return getRegexMatchedGroup(target, 1);
    }

    private int getAttachmentIndex(String target) {
        return getRegexMatchedGroup(target, 2);
    }

}
