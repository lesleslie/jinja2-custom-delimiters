package com.wedgwoodwebworks.jinja2delimiters.intentions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2FileType;
import com.wedgwoodwebworks.jinja2delimiters.parser.Jinja2ElementTypes;
import com.wedgwoodwebworks.jinja2delimiters.psi.CustomJinja2PsiElement;
import com.wedgwoodwebworks.jinja2delimiters.settings.Jinja2DelimitersSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Intention action to extract selected Jinja2 code into a macro
 */
public class ExtractJinja2MacroIntention extends PsiElementBaseIntentionAction implements IntentionAction {

    private static final Logger LOG = Logger.getInstance(ExtractJinja2MacroIntention.class);

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element)
            throws IncorrectOperationException {

        PsiFile file = element.getContainingFile();
        if (!(file.getFileType() instanceof CustomJinja2FileType)) {
            return;
        }

        // Get selected text or current element
        String selectedText = getSelectedText(editor);
        if (selectedText == null || selectedText.trim().isEmpty()) {
            selectedText = element.getText();
        }

        // Generate macro name suggestion
        String macroName = generateMacroName(selectedText);
        
        // Make variables effectively final for lambda
        final String finalSelectedText = selectedText;
        final String finalMacroName = macroName;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                // Create the macro definition
                String macroDefinition = createMacroDefinition(finalMacroName, finalSelectedText);

                // Replace selected text with macro call
                String macroCall = createMacroCall(finalMacroName);

                // Insert macro definition at the beginning of the file
                insertMacroDefinition(editor, file, macroDefinition);

                // Replace selected text with macro call
                replaceSelectedText(editor, finalSelectedText, macroCall);

                LOG.info("Successfully extracted macro: " + finalMacroName);

            } catch (Exception e) {
                LOG.error("Error extracting macro", e);
                throw new IncorrectOperationException("Failed to extract macro: " + e.getMessage());
            }
        });
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiFile file = element.getContainingFile();
        return file.getFileType() instanceof CustomJinja2FileType && 
               (hasSelection(editor) || isExtractableElement(element));
    }

    private boolean hasSelection(Editor editor) {
        return editor.getSelectionModel().hasSelection();
    }

    private boolean isExtractableElement(PsiElement element) {
        // Check if element is in a block that can be extracted
        CustomJinja2PsiElement jinja2Element = PsiTreeUtil.getParentOfType(element, CustomJinja2PsiElement.class);
        return jinja2Element != null && 
               (jinja2Element.getNode().getElementType() == Jinja2ElementTypes.VARIABLE ||
                jinja2Element.getNode().getElementType() == Jinja2ElementTypes.BLOCK);
    }

    @Nullable
    private String getSelectedText(Editor editor) {
        return editor.getSelectionModel().getSelectedText();
    }

    private String generateMacroName(String content) {
        // Generate a meaningful macro name from content
        String name = content.replaceAll("[^a-zA-Z0-9_]", "_")
                           .replaceAll("_{2,}", "_")
                           .replaceAll("^_|_$", "");

        if (name.isEmpty() || Character.isDigit(name.charAt(0))) {
            name = "extracted_macro";
        }

        // Ensure name is not too long
        if (name.length() > 30) {
            name = name.substring(0, 27) + "_fn";
        }

        return name;
    }

    private String createMacroDefinition(String macroName, String content) {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        String blockStart = settings.blockStartString;
        String blockEnd = settings.blockEndString;

        return String.format("%s macro %s() %s\n%s\n%s endmacro %s\n\n",
            blockStart, macroName, blockEnd,
            content.trim(),
            blockStart, blockEnd);
    }

    private String createMacroCall(String macroName) {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        String varStart = settings.variableStartString;
        String varEnd = settings.variableEndString;

        return String.format("%s %s() %s", varStart, macroName, varEnd);
    }

    private void insertMacroDefinition(Editor editor, PsiFile file, String macroDefinition) {
        // Insert at the beginning of the file
        editor.getDocument().insertString(0, macroDefinition);
    }

    private void replaceSelectedText(Editor editor, String selectedText, String replacement) {
        if (editor.getSelectionModel().hasSelection()) {
            int start = editor.getSelectionModel().getSelectionStart();
            int end = editor.getSelectionModel().getSelectionEnd();
            editor.getDocument().replaceString(start, end, replacement);
        } else {
            // Find and replace the text in current position
            int caretOffset = editor.getCaretModel().getOffset();
            String documentText = editor.getDocument().getText();
            int startIndex = documentText.lastIndexOf(selectedText, caretOffset);
            if (startIndex != -1) {
                editor.getDocument().replaceString(startIndex, startIndex + selectedText.length(), replacement);
            }
        }
    }

    @NotNull
    @Override
    public String getText() {
        return "Extract Jinja2 macro";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Jinja2 refactoring";
    }
}
