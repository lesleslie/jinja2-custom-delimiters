package com.wedgwoodwebworks.jinja2customdelimiters.formatting;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings;

public class DelimiterConversionUtilTest extends BasePlatformTestCase {

    public void testOverlappingOpeningDelimitersRoundTrip() {
        Jinja2DelimitersSettings settings = new Jinja2DelimitersSettings();
        settings.setBlockStartString("[[");
        settings.setBlockEndString("]]");
        settings.setVariableStartString("[[=");
        settings.setVariableEndString("=]]");
        settings.setCommentStartString("<#");
        settings.setCommentEndString("#>");

        String custom = "[[ if user ]][[= user.name =]][[ endif ]]";
        String standard = DelimiterConversionUtil.convertCustomToStandard(custom, settings);

        assertEquals("{% if user %}{{ user.name }}{% endif %}", standard);
        assertEquals(custom, DelimiterConversionUtil.convertStandardToCustom(standard, settings));
    }

    public void testWhitespaceControlRoundTrip() {
        Jinja2DelimitersSettings settings = new Jinja2DelimitersSettings();
        settings.setBlockStartString("[%");
        settings.setBlockEndString("%]");
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");
        settings.setCommentStartString("[#");
        settings.setCommentEndString("#]");

        String custom = "[%- for item in items -%]\n[[+ item +]]\n[#- note -#]";
        String standard = DelimiterConversionUtil.convertCustomToStandard(custom, settings);

        assertEquals("{%- for item in items -%}\n{{+ item +}}\n{#- note -#}", standard);
        assertEquals(custom, DelimiterConversionUtil.convertStandardToCustom(standard, settings));
    }
}
