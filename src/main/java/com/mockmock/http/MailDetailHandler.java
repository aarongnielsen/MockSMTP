package com.mockmock.http;

import com.mockmock.htmlbuilder.MailMessageViewHtmlBuilder;
import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The HTTP handler used to display the details of a given message in the mail queue.
 * <p>
 * The user invokes this handler by requesting a URL of the form: {@code /view/:mailIndex}.
 */
public class MailDetailHandler extends BaseHandler {

    // instance fields

    private final MailMessageViewHtmlBuilder mailMessageViewHtmlBuilder = new MailMessageViewHtmlBuilder();

    // constructors

    public MailDetailHandler(MailQueue mailQueue) {
        setMailQueue(mailQueue);
    }

    // methods implemented for BaseHandler

    @Override
    protected String getUrlPathPattern() {
        return "^/view/(-?[0-9]+)/?$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
    {
        if (!isUrlPathMatch(target)) {
            return;
        }

        int mailIndex = getMailIndex(target);
        if (mailIndex == 0) {
            return;
        }

        MockMail mockMail = this.mailQueue.getByIndex(mailIndex);
        if (mockMail == null) {
            return;
        }

        setDefaultResponseOptions(response);

        String mailDetailHTML = mailMessageViewHtmlBuilder.buildMailMessageView(mockMail, mailIndex);
        response.getWriter().print(mailDetailHTML);

        request.setHandled(true);
    }

    private int getMailIndex(String target) {
        return getRegexMatchedGroup(target, 1);
    }

}
