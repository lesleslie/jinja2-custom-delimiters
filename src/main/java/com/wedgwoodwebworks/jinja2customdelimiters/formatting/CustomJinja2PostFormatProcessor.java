package com.wedgwoodwebworks.jinja2customdelimiters.formatting;

import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor;
import com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Post-format processor that converts standard Jinja2 delimiters back to custom delimiters
 * after PyCharm's built-in Jinja2 formatter has processed the file.
 *
 * This completes the round-trip conversion, restoring the user's custom delimiter preferences.
 */
public class CustomJinja2PostFormatProcessor implements PostFormatProcessor {

    private static final Logger LOG = Logger.getInstance(CustomJinja2PostFormatProcessor.class);

    @NotNull
    @Override
    public PsiElement processElement(@NotNull PsiElement source, @NotNull CodeStyleSettings settings) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PostFormatProcessor: Processing element");
            }

            PsiFile file = source.getContainingFile();
            if (file == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PostFormatProcessor: File is null, skipping");
                }
                return source;
            }

            // Check if this is a Jinja2 file
            Language fileLanguage = file.getLanguage();
            if (!"Jinja2".equals(fileLanguage.getID()) && !fileLanguage.getID().contains("Jinja")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PostFormatProcessor: Not a Jinja2 file (language: " + fileLanguage.getID() + "), skipping");
                }
                return source;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("PostFormatProcessor: Processing Jinja2 file: " + file.getName());
            }

            Document document = PsiDocumentManager.getInstance(source.getProject()).getDocument(file);
            if (document != null) {
                convertDelimiters(document, source.getTextRange(), file);
            } else {
                LOG.warn("PostFormatProcessor: Document is null for file: " + file.getName());
            }

            return source;

        } catch (Exception e) {
            LOG.error("PostFormatProcessor: Failed to process element", e);
            return source; // Return original element on error
        }
    }

    @NotNull
    @Override
    public TextRange processText(@NotNull PsiFile source,
                                  @NotNull TextRange rangeToReformat,
                                  @NotNull CodeStyleSettings settings) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PostFormatProcessor: Processing text range " + rangeToReformat);
            }

            // Check if this is a Jinja2 file
            Language fileLanguage = source.getLanguage();
            if (!"Jinja2".equals(fileLanguage.getID()) && !fileLanguage.getID().contains("Jinja")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PostFormatProcessor: Not a Jinja2 file (language: " + fileLanguage.getID() + "), skipping");
                }
                return rangeToReformat;
            }

            Document document = PsiDocumentManager.getInstance(source.getProject()).getDocument(source);
            if (document == null) {
                LOG.warn("PostFormatProcessor: Document is null for file: " + source.getName());
                return rangeToReformat;
            }

            return convertDelimiters(document, rangeToReformat, source);

        } catch (Exception e) {
            LOG.error("PostFormatProcessor: Failed to process text", e);
            return rangeToReformat; // Return original range on error
        }
    }

    private TextRange convertDelimiters(Document document, TextRange range, PsiFile file) {
        // Get custom delimiter settings
        Jinja2DelimitersSettings delimSettings = Jinja2DelimitersSettings.getInstance();

        String blockStart = delimSettings.getBlockStartString();
        String blockEnd = delimSettings.getBlockEndString();
        String variableStart = delimSettings.getVariableStartString();
        String variableEnd = delimSettings.getVariableEndString();
        String commentStart = delimSettings.getCommentStartString();
        String commentEnd = delimSettings.getCommentEndString();

        if (LOG.isDebugEnabled()) {
            LOG.debug("PostFormatProcessor: Using delimiters - block: " + blockStart + "/" + blockEnd +
                     ", variable: " + variableStart + "/" + variableEnd +
                     ", comment: " + commentStart + "/" + commentEnd);
        }

        // Get the text in the range
        String text = document.getText(range);

        // Convert standard Jinja2 delimiters back to custom delimiters
        String converted = text;

        // Block delimiters with whitespace control
        converted = replaceWithWhitespaceControl(converted, "{%", blockStart);
        converted = replaceWithWhitespaceControl(converted, "%}", blockEnd);

        // Variable delimiters with whitespace control
        converted = replaceWithWhitespaceControl(converted, "{{", variableStart);
        converted = replaceWithWhitespaceControl(converted, "}}", variableEnd);

        // Comment delimiters with whitespace control
        converted = replaceWithWhitespaceControl(converted, "{#", commentStart);
        converted = replaceWithWhitespaceControl(converted, "#}", commentEnd);

        // If text changed, update the document
        if (!text.equals(converted)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PostFormatProcessor: Converting standard delimiters back to custom in range " + range);
            }

            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(file.getProject());

            document.replaceString(range.getStartOffset(), range.getEndOffset(), converted);

            // Commit the document changes to PSI
            psiDocumentManager.commitDocument(document);

            // Return adjusted range if length changed
            int lengthDiff = converted.length() - text.length();
            TextRange newRange = new TextRange(range.getStartOffset(), range.getEndOffset() + lengthDiff);

            if (LOG.isDebugEnabled()) {
                LOG.debug("PostFormatProcessor: Conversion complete, new range: " + newRange);
            }

            return newRange;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PostFormatProcessor: No conversion needed (using standard delimiters)");
            }
        }

        return range;
    }

    /**
     * Replace standard delimiter with custom delimiter, preserving whitespace control characters.
     *
     * Examples:
     * - "{%" → "[%"
     * - "{%-" → "[%-"
     * - "{%+" → "[%+"
     * - "-%}" → "-%]"
     * - "+%}" → "+%]"
     */
    private String replaceWithWhitespaceControl(String text, String standardDelim, String customDelim) {
        if (customDelim.equals(standardDelim)) {
            return text; // No conversion needed
        }

        String result = text;

        // For opening delimiters (e.g., "{%" → "[%")
        if (standardDelim.equals("{%") || standardDelim.equals("{{") || standardDelim.equals("{#")) {
            // Handle: {%-, {%+, {%
            result = result.replace(standardDelim + "-", customDelim + "-");
            result = result.replace(standardDelim + "+", customDelim + "+");
            result = result.replace(standardDelim, customDelim);
        }
        // For closing delimiters (e.g., "%}" → "%]")
        else if (standardDelim.equals("%}") || standardDelim.equals("}}") || standardDelim.equals("#}")) {
            // Handle: -%}, +%}, %}
            result = result.replace("-" + standardDelim, "-" + customDelim);
            result = result.replace("+" + standardDelim, "+" + customDelim);
            result = result.replace(standardDelim, customDelim);
        }

        return result;
    }
}
