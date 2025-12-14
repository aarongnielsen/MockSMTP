package com.mockmock.htmlbuilder;

import org.springframework.stereotype.Service;

@Service
public class FooterHtmlBuilder implements HtmlBuilder {

	@Override
    public String build() {
    	String output =
				"  <script src=\"/web-static/js/jquery-1.8.1.min.js\"></script>\n" +
				"  <script src=\"/web-static/js/bootstrap.min.js\"></script>\n" +
                "  </body>\n" +
                "</html>\n";
    	return output;
    }

}
