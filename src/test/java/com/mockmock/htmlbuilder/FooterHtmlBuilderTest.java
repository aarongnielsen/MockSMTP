package com.mockmock.htmlbuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FooterHtmlBuilderTest {

    @Test
    public void build_containsHtmlEndTags() {
        FooterHtmlBuilder footerHtmlBuilder = new FooterHtmlBuilder();
        Assertions.assertTrue(footerHtmlBuilder.build().contains("</body>"));
        Assertions.assertTrue(footerHtmlBuilder.build().contains("</html>"));
    }

}
