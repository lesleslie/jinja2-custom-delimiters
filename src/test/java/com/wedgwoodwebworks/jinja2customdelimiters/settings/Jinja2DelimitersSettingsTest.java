package com.wedgwoodwebworks.jinja2delimiters.settings;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class Jinja2DelimitersSettingsTest extends BasePlatformTestCase {

    private Jinja2DelimitersSettings settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        settings = Jinja2DelimitersSettings.getInstance();
    }

    public void testDefaultSettings() {
        // Test default Jinja2 delimiters
        assertEquals("{%", settings.blockStartString);
        assertEquals("%}", settings.blockEndString);
        assertEquals("{{", settings.variableStartString);
        assertEquals("}}", settings.variableEndString);
        assertEquals("{#", settings.commentStartString);
        assertEquals("#}", settings.commentEndString);
        assertEquals("", settings.lineStatementPrefix);
        assertEquals("", settings.lineCommentPrefix);
    }

    public void testIsUsingCustomDelimitersDefault() {
        // Should return false with default delimiters
        assertFalse(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithCustomBlock() {
        settings.blockStartString = "<%";
        settings.blockEndString = "%>";
        
        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithCustomVariable() {
        settings.variableStartString = "[[";
        settings.variableEndString = "]]";
        
        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithCustomComment() {
        settings.commentStartString = "<#";
        settings.commentEndString = "#>";
        
        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithLineStatement() {
        settings.lineStatementPrefix = "%";
        
        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithLineComment() {
        settings.lineCommentPrefix = "##";
        
        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testSettingsPersistence() {
        // Modify settings
        settings.blockStartString = "<%";
        settings.blockEndString = "%>";
        settings.variableStartString = "[[";
        settings.variableEndString = "]]";
        settings.commentStartString = "<#";
        settings.commentEndString = "#>";
        settings.lineStatementPrefix = "%";
        settings.lineCommentPrefix = "##";
        
        // Verify state
        Jinja2DelimitersSettings state = settings.getState();
        assertNotNull(state);
        assertEquals("<%", state.blockStartString);
        assertEquals("%>", state.blockEndString);
        assertEquals("[[", state.variableStartString);
        assertEquals("]]", state.variableEndString);
        assertEquals("<#", state.commentStartString);
        assertEquals("#>", state.commentEndString);
        assertEquals("%", state.lineStatementPrefix);
        assertEquals("##", state.lineCommentPrefix);
    }

    public void testLoadState() {
        // Create a new state with custom values
        Jinja2DelimitersSettings newState = new Jinja2DelimitersSettings();
        newState.blockStartString = "<%";
        newState.blockEndString = "%>";
        newState.variableStartString = "[[";
        newState.variableEndString = "]]";
        newState.commentStartString = "<#";
        newState.commentEndString = "#>";
        newState.lineStatementPrefix = "%";
        newState.lineCommentPrefix = "##";
        
        // Load the new state
        settings.loadState(newState);
        
        // Verify the settings were updated
        assertEquals("<%", settings.blockStartString);
        assertEquals("%>", settings.blockEndString);
        assertEquals("[[", settings.variableStartString);
        assertEquals("]]", settings.variableEndString);
        assertEquals("<#", settings.commentStartString);
        assertEquals("#>", settings.commentEndString);
        assertEquals("%", settings.lineStatementPrefix);
        assertEquals("##", settings.lineCommentPrefix);
    }

    public void testCommonCustomDelimiterConfigurations() {
        // Test Django-style (which should be default)
        settings.blockStartString = "{%";
        settings.blockEndString = "%}";
        settings.variableStartString = "{{";
        settings.variableEndString = "}}";
        settings.commentStartString = "{#";
        settings.commentEndString = "#}";
        
        assertFalse(settings.isUsingCustomDelimiters());
        
        // Test JSP-style delimiters
        settings.blockStartString = "<%";
        settings.blockEndString = "%>";
        settings.variableStartString = "<%=";
        settings.variableEndString = "%>";
        
        assertTrue(settings.isUsingCustomDelimiters());
        
        // Test square bracket style
        settings.blockStartString = "[%";
        settings.blockEndString = "%]";
        settings.variableStartString = "[[";
        settings.variableEndString = "]]";
        settings.commentStartString = "[#";
        settings.commentEndString = "#]";
        
        assertTrue(settings.isUsingCustomDelimiters());
        
        // Test angle bracket style
        settings.blockStartString = "<@";
        settings.blockEndString = "@>";
        settings.variableStartString = "${";
        settings.variableEndString = "}";
        settings.commentStartString = "<#--";
        settings.commentEndString = "-->";
        
        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testEmptyDelimiters() {
        // Test with empty delimiters (edge case)
        settings.blockStartString = "";
        settings.blockEndString = "";
        
        assertTrue(settings.isUsingCustomDelimiters());
        
        // Reset and test null delimiters (edge case)
        settings.blockStartString = "{%";
        settings.blockEndString = "%}";
        settings.variableStartString = null;
        
        // Should handle null gracefully
        assertFalse(settings.isUsingCustomDelimiters());
    }

    @Override
    protected void tearDown() throws Exception {
        // Reset to defaults after each test
        if (settings != null) {
            settings.blockStartString = "{%";
            settings.blockEndString = "%}";
            settings.variableStartString = "{{";
            settings.variableEndString = "}}";
            settings.commentStartString = "{#";
            settings.commentEndString = "#}";
            settings.lineStatementPrefix = "";
            settings.lineCommentPrefix = "";
        }
        super.tearDown();
    }
}