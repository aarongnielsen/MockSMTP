package com.mockmock.htmlbuilder;

import com.mockmock.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeaderHtmlBuilderTest {

    @Test
    public void build_containsAppVersionNumber() {
        HeaderHtmlBuilder headerHtmlBuilder = new HeaderHtmlBuilder();
        Assertions.assertTrue(headerHtmlBuilder.build().contains(Main.VERSION_NUMBER));
    }

}
