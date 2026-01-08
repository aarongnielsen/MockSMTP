package com.mockmock.htmlbuilder;

import com.mockmock.Main;
import org.springframework.stereotype.Service;

@Service
public class HeaderHtmlBuilder implements HtmlBuilder {

    @Override
    public String build() {
        String output =
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <title>MockMock - SMTP Mock Server version " + Main.VERSION_NUMBER + "</title>\n" +
                "    <link href=\"/web-static/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "    <link href=\"/web-static/css/mockmock.css\" rel=\"stylesheet\">\n" +
                "  </head>\n" +
                "  <body>\n" +
                "  <div class=\"navbar navbar-inverse navbar-fixed-top\">\n" +
                "    <div class=\"navbar-inner\">\n" +
                "      <div class=\"container\">\n" +
                "        <a class=\"brand\" href=\"/\">MockMock</a>\n" +
                "        <div class=\"nav-collapse collapse\">\n" +
                "          <ul class=\"nav\">\n" +
                "            <li class=\"active\"><a href=\"/\">Home</a></li>\n" +
                "            <li><a href=\"https://github.com/tweakers-dev/MockMock\">MockMock on Github</a></li>\n" +
                "          </ul>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n";
        return output;
    }

}
