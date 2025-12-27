package com.wedgwoodwebworks.jinja2customdelimiters.settings;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class Jinja2DelimitersSettingsTest extends BasePlatformTestCase {

    private Jinja2DelimitersSettings settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Get the service instance (it should be registered via plugin.xml in test environment)
        settings = Jinja2DelimitersSettings.getInstance();

        // If service is null, create a new instance for testing
        if (settings == null) {
            settings = new Jinja2DelimitersSettings();
        }

        // Reset to defaults before each test
        settings.setBlockStartString("{%");
        settings.setBlockEndString("%}");
        settings.setVariableStartString("{{");
        settings.setVariableEndString("}}");
        settings.setCommentStartString("{#");
        settings.setCommentEndString("#}");
        settings.setLineStatementPrefix("");
        settings.setLineCommentPrefix("");
    }

    public void testDefaultSettings() {
        // Test default Jinja2 delimiters using thread-safe getters
        assertEquals("{%", settings.getBlockStartString());
        assertEquals("%}", settings.getBlockEndString());
        assertEquals("{{", settings.getVariableStartString());
        assertEquals("}}", settings.getVariableEndString());
        assertEquals("{#", settings.getCommentStartString());
        assertEquals("#}", settings.getCommentEndString());
        assertEquals("", settings.getLineStatementPrefix());
        assertEquals("", settings.getLineCommentPrefix());
    }

    public void testIsUsingCustomDelimitersDefault() {
        // Should return false with default delimiters
        assertFalse(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithCustomBlock() {
        settings.setBlockStartString("<%");
        settings.setBlockEndString("%>");

        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithCustomVariable() {
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");

        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithCustomComment() {
        settings.setCommentStartString("<#");
        settings.setCommentEndString("#>");

        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithLineStatement() {
        settings.lineStatementPrefix = "%";

        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testIsUsingCustomDelimitersWithLineComment() {
        settings.setLineCommentPrefix("##");

        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testSettingsPersistence() {
        // Modify settings using thread-safe setters
        settings.setBlockStartString("<%");
        settings.setBlockEndString("%>");
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");
        settings.setCommentStartString("<#");
        settings.setCommentEndString("#>");
        settings.setLineStatementPrefix("%");
        settings.setLineCommentPrefix("##");
        
        // Verify state using thread-safe getters
        Jinja2DelimitersSettings state = settings.getState();
        assertNotNull(state);
        assertEquals("<%", state.getBlockStartString());
        assertEquals("%>", state.getBlockEndString());
        assertEquals("[[", state.getVariableStartString());
        assertEquals("]]", state.getVariableEndString());
        assertEquals("<#", state.getCommentStartString());
        assertEquals("#>", state.getCommentEndString());
        assertEquals("%", state.getLineStatementPrefix());
        assertEquals("##", state.getLineCommentPrefix());
    }

    public void testLoadState() {
        // Create a new state with custom values using thread-safe setters
        Jinja2DelimitersSettings newState = new Jinja2DelimitersSettings();
        newState.setBlockStartString("<%");
        newState.setBlockEndString("%>");
        newState.setVariableStartString("[[");
        newState.setVariableEndString("]]");
        newState.setCommentStartString("<#");
        newState.setCommentEndString("#>");
        newState.setLineStatementPrefix("%");
        newState.setLineCommentPrefix("##");
        
        // Load the new state
        settings.loadState(newState);
        
        // Verify the settings were updated using thread-safe getters
        assertEquals("<%", settings.getBlockStartString());
        assertEquals("%>", settings.getBlockEndString());
        assertEquals("[[", settings.getVariableStartString());
        assertEquals("]]", settings.getVariableEndString());
        assertEquals("<#", settings.getCommentStartString());
        assertEquals("#>", settings.getCommentEndString());
        assertEquals("%", settings.getLineStatementPrefix());
        assertEquals("##", settings.getLineCommentPrefix());
    }

    public void testCommonCustomDelimiterConfigurations() {
        // Test Django-style (which should be default)
        settings.setBlockStartString("{%");
        settings.setBlockEndString("%}");
        settings.setVariableStartString("{{");
        settings.setVariableEndString("}}");
        settings.setCommentStartString("{#");
        settings.setCommentEndString("#}");
        
        assertFalse(settings.isUsingCustomDelimiters());
        
        // Test JSP-style delimiters
        settings.setBlockStartString("<%");
        settings.setBlockEndString("%>");
        settings.setVariableStartString("<%=");
        settings.setVariableEndString("%>");
        
        assertTrue(settings.isUsingCustomDelimiters());
        
        // Test square bracket style
        settings.setBlockStartString("[%");
        settings.setBlockEndString("%]");
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");
        settings.setCommentStartString("[#");
        settings.setCommentEndString("#]");
        
        assertTrue(settings.isUsingCustomDelimiters());
        
        // Test angle bracket style
        settings.setBlockStartString("<@");
        settings.setBlockEndString("@>");
        settings.setVariableStartString("${");
        settings.setVariableEndString("}");
        settings.setCommentStartString("<#--");
        settings.setCommentEndString("-->");
        
        assertTrue(settings.isUsingCustomDelimiters());
    }

    public void testEmptyDelimiters() {
        // Test with empty delimiters (edge case)
        settings.setBlockStartString("");
        settings.setBlockEndString("");
        
        assertTrue(settings.isUsingCustomDelimiters());

        // Reset to defaults
        settings.setBlockStartString("{%");
        settings.setBlockEndString("%}");
        settings.setVariableStartString("{{");
        settings.setVariableEndString("}}");

        // With all default delimiters, should return false
        assertFalse(settings.isUsingCustomDelimiters());

        // Test that null values are rejected (should throw IllegalArgumentException)
        try {
            settings.setVariableStartString(null);
            fail("Expected IllegalArgumentException when setting null delimiter");
        } catch (IllegalArgumentException e) {
            // Expected - null values are not allowed
        }
    }

    @Override
    protected void tearDown() throws Exception {
        // Settings are reset in setUp() before each test
        super.tearDown();
    }
}