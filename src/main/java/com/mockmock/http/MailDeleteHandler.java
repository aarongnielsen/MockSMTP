package com.mockmock.http;

import com.mockmock.mail.MockMail;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class MailDeleteHandler extends BaseHandler {

	@Override
	protected String getUrlPathPattern() {
		return "^/delete/(-?[0-9]+)/?$";
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

		this.mailQueue.deleteById(mockMail.getId());

		response.setHeader(HttpHeader.LOCATION.asString(), "/");
		response.setStatus(HttpStatus.FOUND_302);
		response.getWriter().println("Mail message " + mockMail.getId() + " has been deleted");
		request.setHandled(true);
	}

	private int getMailIndex(String target) {
		return getRegexMatchedGroup(target, 1);
	}

}
