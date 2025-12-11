package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import lombok.Setter;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ViewHeadersHandler extends BaseHandler {

    private final String pattern = "^/view/headers/(-?[0-9]+)/?$";

    @Autowired
    @Setter
    private MailQueue mailQueue;

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException
    {
        if (!isMatch(target)) {
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
            httpServletResponse.getWriter().write(headerText);
            request.setHandled(true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if this handler should be used for the given target
     * @param target String
     * @return boolean
     */
    private boolean isMatch(String target) {
        return target.matches(pattern);
    }

    /**
     * Returns the mail id if it is part of the target
     * @param target String
     * @return long
     */
    private int getMailIndex(String target) {
        Pattern compiledPattern = Pattern.compile(pattern);

        Matcher matcher = compiledPattern.matcher(target);
        if(matcher.find()) {
            String result = matcher.group(1);
            try {
                return Integer.parseInt(result);
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }

        return 0;
    }

}
