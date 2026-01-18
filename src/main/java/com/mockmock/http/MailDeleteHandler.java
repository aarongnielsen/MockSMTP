package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The HTTP handler used to delete a given message from the mail queue.
 * <p>
 * The user invokes this handler by requesting a URL of the form: {@code /delete/:mailIndex}.
 */
public class MailDeleteHandler extends BaseHandler {

	// constructors

	public MailDeleteHandler(MailQueue mailQueue) {
		setMailQueue(mailQueue);
	}

	// methods implemented for BaseHandler

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
        if (mailIndex == 0) {
            return;
        }

		MockMail mockMail = this.mailQueue.getByIndex(mailIndex);
		if (mockMail == null) {
			return;
		}

		boolean isDeleted = this.mailQueue.deleteByIndex(mailIndex);
		String responseMessage = (isDeleted ?
				"Mail message " + mockMail.getId() + " has been deleted" :
				"no message to delete at index " + mailIndex
		);

		response.setHeader(HttpHeader.LOCATION.asString(), "/");
		response.setStatus(HttpStatus.FOUND_302);
		response.getWriter().println(responseMessage);
		request.setHandled(true);
	}

	private int getMailIndex(String target) {
		return getRegexMatchedGroup(target, 1);
	}

}
