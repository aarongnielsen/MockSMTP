package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import lombok.Setter;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The base for all classes that handle HTTP requests and generate web UI pages in response.
 */
public abstract class BaseHandler extends AbstractHandler {

    // instance fields

    @Setter
    protected MailQueue mailQueue;

    // methods to be implemented

    /** Returns the regular expression used to match the URL paths handled by this handler. **/
    protected abstract String getUrlPathPattern();

    /**
     * Checks if this handler should be used for the given target URL path.
     * This is usually done by a {@link #getUrlPathPattern() regular expression match}.
     * In a given request, any handler that does not match via this method will be ignored.
     *
     * @param urlPath the path segment of the URL in the request.
     */
    protected boolean isUrlPathMatch(String urlPath) {
        return urlPath.matches(getUrlPathPattern());
    }

    // protected methods

    /**
     * Sets the basic fields in the given HTTP response.
     * This is a shortcut method to set the response content type to be HTML in UTF-8
     * and the HTTP response code to be {@code 200 OK}.
     */
    protected void setDefaultResponseOptions(HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * A convenience method to extract grouped substrings from a regular expression matcher.
     * As per {@link java.util.regex.Matcher}, the groups are indexed starting from 1.
     *
     * @param urlPath the path of a URL.
     * @param groupNumber the grouped substring to extract.
     * @return an integer representation of the group substring at the given index in the regular expression matcher,
     *     or 0 if the group does not exist.
     */
    protected int getRegexMatchedGroup(String urlPath, int groupNumber) {
        Pattern compiledPattern = Pattern.compile(getUrlPathPattern());

        Matcher matcher = compiledPattern.matcher(urlPath);
        if(matcher.find()) {
            String result = matcher.group(groupNumber);
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
