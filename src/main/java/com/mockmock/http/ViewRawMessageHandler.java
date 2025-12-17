package com.mockmock.http;

import com.mockmock.mail.MockMail;
import org.eclipse.jetty.server.Request;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class ViewRawMessageHandler extends BaseHandler {

    @Override
    protected String getUrlPathPattern() {
        return "^/view/raw/(-?[0-9]+)/?$";
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
        httpServletResponse.getWriter().write(mockMail.getRawMail());
        request.setHandled(true);
    }

    private int getMailIndex(String target) {
        return getRegexMatchedGroup(target, 1);
    }

}
