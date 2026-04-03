package com.wedgwoodwebworks.jinja2customdelimiters.formatting;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseGate;
import com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Pre-format processor that converts custom Jinja2 delimiters to standard delimiters
 * before PyCharm's built-in Jinja2 formatter processes the file.
 *
 * This allows us to leverage PyCharm Professional's excellent Jinja2 formatting
 * while supporting custom delimiter configurations.
 */
public class CustomJinja2PreFormatProcessor implements PreFormatProcessor {

    private static final Logger LOG = Logger.getInstance(CustomJinja2PreFormatProcessor.class);

    @NotNull
    @Override
    public TextRange process(@NotNull ASTNode element, @NotNull TextRange range) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PreFormatProcessor: Processing range " + range);
            }

            PsiElement psiElement = element.getPsi();
            if (psiElement == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreFormatProcessor: PSI element is null, skipping");
                }
                return range;
            }

            PsiFile file = psiElement.getContainingFile();
            if (file == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreFormatProcessor: File is null, skipping");
                }
                return range;
            }

            // Only process Jinja2 files
            Language fileLanguage = file.getLanguage();
            if (!"Jinja2".equals(fileLanguage.getID()) && !fileLanguage.getID().contains("Jinja")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreFormatProcessor: Not a Jinja2 file (language: " + fileLanguage.getID() + "), skipping");
                }
                return range;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("PreFormatProcessor: Processing Jinja2 file: " + file.getName());
            }

            if (!LicenseGate.ensureLicensed("custom Jinja2 delimiter formatting")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreFormatProcessor: Skipping formatting because the plugin is not licensed");
                }
                return range;
            }

            Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
            if (document == null) {
                LOG.warn("PreFormatProcessor: Document is null for file: " + file.getName());
                return range;
            }

            Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();

            if (LOG.isDebugEnabled()) {
                LOG.debug("PreFormatProcessor: Using delimiters - block: " + settings.getBlockStartString() + "/" + settings.getBlockEndString() +
                         ", variable: " + settings.getVariableStartString() + "/" + settings.getVariableEndString() +
                         ", comment: " + settings.getCommentStartString() + "/" + settings.getCommentEndString());
            }

            // Get the text in the range
            String text = document.getText(range);
            String converted = DelimiterConversionUtil.convertCustomToStandard(text, settings);

            // If text changed, update the document
            if (!text.equals(converted)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreFormatProcessor: Converting custom delimiters to standard in range " + range);
                }

                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(file.getProject());

                document.replaceString(range.getStartOffset(), range.getEndOffset(), converted);

                // Commit the document changes to PSI
                psiDocumentManager.commitDocument(document);

                // Return adjusted range if length changed
                int lengthDiff = converted.length() - text.length();
                TextRange newRange = new TextRange(range.getStartOffset(), range.getEndOffset() + lengthDiff);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreFormatProcessor: Conversion complete, new range: " + newRange);
                }

                return newRange;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreFormatProcessor: No conversion needed (using standard delimiters)");
                }
            }

            return range;

        } catch (Exception e) {
            // Log error but don't crash the formatter
            LOG.error("PreFormatProcessor: Failed to convert custom delimiters", e);
            return range; // Return original range on error
        }
    }

}
