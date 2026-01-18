package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.eclipse.jetty.server.Request;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * The HTTP handler used to display the headers of a given message in the mail queue.
 * <p>
 * The user invokes this handler by requesting a URL of the form: {@code /view/:mailIndex/headers}.
 */
public class ViewHeadersHandler extends BaseHandler {

    // constructors

    public ViewHeadersHandler(MailQueue mailQueue) {
        setMailQueue(mailQueue);
    }

    // methods implemented for BaseHandler

    @Override
    protected String getUrlPathPattern() {
        return "^/view/(-?[0-9]+)/headers/?$";
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException
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

        MimeMessage mimeMessage = mockMail.getMimeMessage();
        if (mimeMessage == null) {
            return;
        }

        try {
            List<String> headerLines = new ArrayList<>();
            for (Enumeration<?> e = mimeMessage.getAllHeaderLines(); e.hasMoreElements(); ) {
                headerLines.add((String) e.nextElement());
            }
            String headerText = String.join("\n", headerLines);

            httpServletResponse.setContentType("text/plain;charset=utf-8");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletResponse.setHeader("Content-Disposition", "filename=\"email_" + mailIndex + "_headers.txt\"");
            httpServletResponse.getWriter().write(headerText);
            request.setHandled(true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private int getMailIndex(String target) {
        return getRegexMatchedGroup(target, 1);
    }

}
