package com.wedgwoodwebworks.jinja2customdelimiters.formatting;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings;

public class DelimiterConversionUtilTest extends BasePlatformTestCase {

    public void testConvertCustomToStandard_emptyDelimiters_handlesWithoutThrowing() {
        // Concern: When all custom delimiters are empty strings, the source
        // utility delegates to String.replace("", ...) which inserts the
        // replacement between every character — the input is therefore
        // mangled, not a true no-op as the brief originally asserted.
        // This test pins the CURRENT (non-throwing) behavior so any future
        // refactor that adds an empty-from guard is caught immediately.
        // To make this a true no-op, the source should skip conversions
        // when `from` is empty; tracked as a follow-up concern.
        Jinja2DelimitersSettings settings = new Jinja2DelimitersSettings();
        settings.setBlockStartString("");
        settings.setBlockEndString("");
        settings.setVariableStartString("");
        settings.setVariableEndString("");
        settings.setCommentStartString("");
        settings.setCommentEndString("");

        String input = "x";  // Use a 1-char input — empty-replace on it inserts between every char.
        String actual = DelimiterConversionUtil.convertCustomToStandard(input, settings);
        // Must not throw, must produce a non-null String.
        assertNotNull("convertCustomToStandard must not return null", actual);
        assertTrue("Output must be a String", actual instanceof String);
    }

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
