package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The HTTP handler used to display a message from the mail queue as raw text, including all headers and attachments.
 * <p>
 * The user invokes this handler by requesting a URL of the form: {@code /view/:mailIndex/raw}.
 */
public class ViewRawMessageHandler extends BaseHandler {

    // constructors

    public ViewRawMessageHandler(MailQueue mailQueue) {
        setMailQueue(mailQueue);
    }

    // methods implemented for BaseHandler

    @Override
    protected String getUrlPathPattern() {
        return "^/view/(-?[0-9]+)/raw/?$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException
    {
        if (!isUrlPathMatch(target)) {
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

        httpServletResponse.setContentType("text/plain;charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setHeader("Content-Disposition", "inline; filename=\"email_" + mailIndex + "_raw.msg\"");
        httpServletResponse.getWriter().write(mockMail.getRawMail());
        request.setHandled(true);
    }

    private int getMailIndex(String target) {
        return getRegexMatchedGroup(target, 1);
    }

}
