package com.wedgwoodwebworks.jinja2delimiters.refactoring;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.wedgwoodwebworks.jinja2delimiters.lexer.Jinja2TokenTypes;
import com.wedgwoodwebworks.jinja2delimiters.parser.Jinja2ElementTypes;
import com.wedgwoodwebworks.jinja2delimiters.psi.CustomJinja2PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Processor for renaming Jinja2 variables across templates
 */
public class Jinja2RenameVariableProcessor extends RenamePsiElementProcessor {

    private static final Logger LOG = Logger.getInstance(Jinja2RenameVariableProcessor.class);
    private static final Key<String> ORIGINAL_NAME_KEY = Key.create("JINJA2_ORIGINAL_NAME");

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof CustomJinja2PsiElement && 
               isJinja2Variable((CustomJinja2PsiElement) element);
    }

    private boolean isJinja2Variable(CustomJinja2PsiElement element) {
        return element.getNode().getElementType() == Jinja2TokenTypes.IDENTIFIER &&
               isInVariableContext(element);
    }

    private boolean isInVariableContext(CustomJinja2PsiElement element) {
        PsiElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof CustomJinja2PsiElement) {
                CustomJinja2PsiElement parentElement = (CustomJinja2PsiElement) parent;
                if (parentElement.getNode().getElementType() == Jinja2ElementTypes.VARIABLE ||
                    parentElement.getNode().getElementType() == Jinja2ElementTypes.SET_BLOCK ||
                    parentElement.getNode().getElementType() == Jinja2ElementTypes.FOR_BLOCK) {
                    return true;
                }
            }
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public void prepareRenaming(@NotNull PsiElement element, 
                               @NotNull String newName, 
                               @NotNull Map<PsiElement, String> allRenames,
                               @NotNull SearchScope scope) {
        LOG.info("Preparing rename of Jinja2 variable: " + element.getText() + " -> " + newName);

        // Store original name for rollback if needed
        element.putUserData(ORIGINAL_NAME_KEY, element.getText());

        // Find all related variables in the same template and related templates
        Collection<PsiReference> references = ReferencesSearch.search(element, scope).findAll();

        for (PsiReference reference : references) {
            PsiElement refElement = reference.getElement();
            if (refElement instanceof CustomJinja2PsiElement && 
                isJinja2Variable((CustomJinja2PsiElement) refElement)) {

                allRenames.put(refElement, newName);
                LOG.debug("Added variable reference for rename: " + refElement.getText());
            }
        }

        // Also find variable definitions and usages in the same template
        PsiFile containingFile = element.getContainingFile();
        Collection<CustomJinja2PsiElement> variables = 
            PsiTreeUtil.findChildrenOfType(containingFile, CustomJinja2PsiElement.class);

        String originalName = element.getText();
        for (CustomJinja2PsiElement variable : variables) {
            if (variable.getText().equals(originalName) && 
                isJinja2Variable(variable) && 
                !allRenames.containsKey(variable)) {

                allRenames.put(variable, newName);
                LOG.debug("Added same-template variable for rename: " + variable.getText());
            }
        }
    }

    @Override
    public void renameElement(@NotNull PsiElement element,
                            @NotNull String newName,
                            @NotNull UsageInfo[] usages,
                            @Nullable RefactoringElementListener listener) 
            throws IncorrectOperationException {

        LOG.info("Executing rename of Jinja2 variable: " + element.getText() + " -> " + newName);

        try {
            // Validate the new name
            if (!isValidJinja2VariableName(newName)) {
                throw new IncorrectOperationException("Invalid variable name: " + newName);
            }

            // Perform the actual rename
            super.renameElement(element, newName, usages, listener);

            // Update any dependent elements
            updateDependentElements(element, newName);

            LOG.info("Successfully renamed Jinja2 variable to: " + newName);

        } catch (Exception e) {
            LOG.error("Error renaming Jinja2 variable", e);
            throw new IncorrectOperationException("Failed to rename variable: " + e.getMessage());
        }
    }

    private boolean isValidJinja2VariableName(String name) {
        // Check if name follows Python identifier rules
        if (name == null || name.isEmpty()) {
            return false;
        }

        if (!Character.isLetter(name.charAt(0)) && name.charAt(0) != '_') {
            return false;
        }

        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }

        // Check against Jinja2 reserved keywords
        return !isJinja2ReservedKeyword(name);
    }

    private boolean isJinja2ReservedKeyword(String name) {
        return name.equals("for") || name.equals("if") || name.equals("else") || 
               name.equals("elif") || name.equals("endif") || name.equals("endfor") ||
               name.equals("set") || name.equals("endset") || name.equals("block") ||
               name.equals("endblock") || name.equals("extends") || name.equals("include") ||
               name.equals("import") || name.equals("from") || name.equals("macro") ||
               name.equals("endmacro") || name.equals("and") || name.equals("or") ||
               name.equals("not") || name.equals("in") || name.equals("is") ||
               name.equals("true") || name.equals("false") || name.equals("none");
    }

    private void updateDependentElements(PsiElement element, String newName) {
        // Update any elements that depend on this variable
        // This could include updating documentation, comments, etc.
        LOG.debug("Updating dependent elements for renamed variable: " + newName);
    }

    @Nullable
    public String getQualifiedName(@NotNull PsiElement element) {
        if (element instanceof CustomJinja2PsiElement) {
            PsiFile file = element.getContainingFile();
            String fileName = file != null ? file.getName() : "unknown";
            return fileName + ":" + element.getText();
        }
        return element.getText();
    }
}
