package com.wedgwoodwebworks.jinja2delimiters.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "Jinja2DelimitersSettings",
    storages = @Storage("jinja2_delimiters.xml")
)
public class Jinja2DelimitersSettings implements PersistentStateComponent<Jinja2DelimitersSettings> {

    // Default Jinja2 delimiters
    public String blockStartString = "{%";
    public String blockEndString = "%}";
    public String variableStartString = "{{";
    public String variableEndString = "}}";
    public String commentStartString = "{#";
    public String commentEndString = "#}";
    public String lineStatementPrefix = "";
    public String lineCommentPrefix = "";

    public static Jinja2DelimitersSettings getInstance() {
        return ApplicationManager.getApplication().getService(Jinja2DelimitersSettings.class);
    }

    @Nullable
    @Override
    public Jinja2DelimitersSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Jinja2DelimitersSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean isUsingCustomDelimiters() {
        return !safeEquals("{%", blockStartString) ||
               !safeEquals("%}", blockEndString) ||
               !safeEquals("{{", variableStartString) ||
               !safeEquals("}}", variableEndString) ||
               !safeEquals("{#", commentStartString) ||
               !safeEquals("#}", commentEndString) ||
               !safeStringEmpty(lineStatementPrefix) ||
               !safeStringEmpty(lineCommentPrefix);
    }

    private boolean safeEquals(String expected, String actual) {
        if (actual == null) {
            // Treat null as equivalent to the default value
            return true;
        }
        return expected.equals(actual);
    }

    private boolean safeStringEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
