package com.wedgwoodwebworks.jinja2customdelimiters.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

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
    public Jinja2DelimitersSettings getState() {
        // IDE calls this on the EDT before serializing. The IDE's contract
        // guarantees single-threaded access here; no synchronization needed.
        return this;
    }

    @Override
    public void loadState(@NotNull Jinja2DelimitersSettings state) {
        // XML tolerance: a single bad field (hand-edit, schema downgrade,
        // XmlSerializer edge case) must NOT lose all settings. Reflective
        // copyBean silently propagated nulls; explicit setters would throw
        // IllegalArgumentException and lose the whole component state.
        // Use null-tolerant guards so a null input preserves the default
        // for THAT field while the other 7 still load correctly.
        applyIfNonNull(state.blockStartString, this::setBlockStartString);
        applyIfNonNull(state.blockEndString, this::setBlockEndString);
        applyIfNonNull(state.variableStartString, this::setVariableStartString);
        applyIfNonNull(state.variableEndString, this::setVariableEndString);
        applyIfNonNull(state.commentStartString, this::setCommentStartString);
        applyIfNonNull(state.commentEndString, this::setCommentEndString);
        applyIfNonNull(state.lineStatementPrefix, this::setLineStatementPrefix);
        applyIfNonNull(state.lineCommentPrefix, this::setLineCommentPrefix);
    }

    private static void applyIfNonNull(String value, Consumer<String> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    // Thread-safe getters
    @NotNull
    public String getBlockStartString() {
        return blockStartString;
    }

    @NotNull
    public String getBlockEndString() {
        return blockEndString;
    }

    @NotNull
    public String getVariableStartString() {
        return variableStartString;
    }

    @NotNull
    public String getVariableEndString() {
        return variableEndString;
    }

    @NotNull
    public String getCommentStartString() {
        return commentStartString;
    }

    @NotNull
    public String getCommentEndString() {
        return commentEndString;
    }

    @NotNull
    public String getLineStatementPrefix() {
        return lineStatementPrefix;
    }

    @NotNull
    public String getLineCommentPrefix() {
        return lineCommentPrefix;
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
