package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import lombok.Setter;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ViewRawMessageHandler extends BaseHandler {

    private final String pattern = "^/view/raw/(-?[0-9]+)/?$";

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

        httpServletResponse.setContentType("text/plain;charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.getWriter().write(mockMail.getRawMail());
        request.setHandled(true);
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
