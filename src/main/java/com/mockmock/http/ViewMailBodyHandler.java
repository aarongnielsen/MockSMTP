package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The HTTP handler used to display the body of a given message in the mail queue.
 * Note that this can be used to display either plain-text or HTML contents.
 * <p>
 * The user invokes this handler by requesting a URL of the form: {@code /view/:mailIndex/body}.
 */
public class ViewMailBodyHandler extends BaseHandler {

    // constructors

    public ViewMailBodyHandler(MailQueue mailQueue) {
        setMailQueue(mailQueue);
    }

    // methods implemented for BaseHandler

    @Override
    protected String getUrlPathPattern() {
        return "^/view/(-?[0-9]+)/body/?$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
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

        setDefaultResponseOptions(response);
		if (mockMail.getBodyHtml() != null) {
            response.getWriter().print(mockMail.getBodyHtml());
            response.setHeader("Content-Disposition", "inline; filename=\"email_" + mailIndex + "_body.html\"");
            request.setHandled(true);
		}
        else if (mockMail.getBody() != null) {
            response.setContentType("text/plain;charset=utf-8");
            response.setHeader("Content-Disposition", "inline; filename=\"email_" + mailIndex + "_body.txt\"");
            response.getWriter().print(mockMail.getBody());
            request.setHandled(true);
        }
    }

    private int getMailIndex(String target) {
        return getRegexMatchedGroup(target, 1);
    }

}
