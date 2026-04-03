package com.wedgwoodwebworks.jinja2customdelimiters.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(
    name = "Jinja2DelimitersSettings",
    storages = @Storage("jinja2_delimiters.xml")
)
public class Jinja2DelimitersSettings implements PersistentStateComponent<Jinja2DelimitersSettings> {

    // Volatile fields for thread-safe publication
    // These are public for XML serialization but should be accessed via getters
    public volatile String blockStartString = "{%";
    public volatile String blockEndString = "%}";
    public volatile String variableStartString = "{{";
    public volatile String variableEndString = "}}";
    public volatile String commentStartString = "{#";
    public volatile String commentEndString = "#}";
    public volatile String lineStatementPrefix = "";
    public volatile String lineCommentPrefix = "";

    public static Jinja2DelimitersSettings getInstance() {
        return ApplicationManager.getApplication().getService(Jinja2DelimitersSettings.class);
    }

    @Nullable
    @Override
    public synchronized Jinja2DelimitersSettings getState() {
        return this;
    }

    @Override
    public synchronized void loadState(@NotNull Jinja2DelimitersSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    // Thread-safe getters
    @NotNull
    public String getBlockStartString() {
        return blockStartString != null ? blockStartString : "{%";
    }

    @NotNull
    public String getBlockEndString() {
        return blockEndString != null ? blockEndString : "%}";
    }

    @NotNull
    public String getVariableStartString() {
        return variableStartString != null ? variableStartString : "{{";
    }

    @NotNull
    public String getVariableEndString() {
        return variableEndString != null ? variableEndString : "}}";
    }

    @NotNull
    public String getCommentStartString() {
        return commentStartString != null ? commentStartString : "{#";
    }

    @NotNull
    public String getCommentEndString() {
        return commentEndString != null ? commentEndString : "#}";
    }

    @NotNull
    public String getLineStatementPrefix() {
        return lineStatementPrefix != null ? lineStatementPrefix : "";
    }

    @NotNull
    public String getLineCommentPrefix() {
        return lineCommentPrefix != null ? lineCommentPrefix : "";
    }

    // Thread-safe setters
    public synchronized void setBlockStartString(@NotNull String value) {
        this.blockStartString = requireNonNull(value, "blockStartString");
    }

    public synchronized void setBlockEndString(@NotNull String value) {
        this.blockEndString = requireNonNull(value, "blockEndString");
    }

    public synchronized void setVariableStartString(@NotNull String value) {
        this.variableStartString = requireNonNull(value, "variableStartString");
    }

    public synchronized void setVariableEndString(@NotNull String value) {
        this.variableEndString = requireNonNull(value, "variableEndString");
    }

    public synchronized void setCommentStartString(@NotNull String value) {
        this.commentStartString = requireNonNull(value, "commentStartString");
    }

    public synchronized void setCommentEndString(@NotNull String value) {
        this.commentEndString = requireNonNull(value, "commentEndString");
    }

    public synchronized void setLineStatementPrefix(@NotNull String value) {
        this.lineStatementPrefix = requireNonNull(value, "lineStatementPrefix");
    }

    public synchronized void setLineCommentPrefix(@NotNull String value) {
        this.lineCommentPrefix = requireNonNull(value, "lineCommentPrefix");
    }

    public synchronized boolean isUsingCustomDelimiters() {
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
            return true;
        }
        return expected.equals(actual);
    }

    private boolean safeStringEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private String requireNonNull(String value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        return value;
    }
}
