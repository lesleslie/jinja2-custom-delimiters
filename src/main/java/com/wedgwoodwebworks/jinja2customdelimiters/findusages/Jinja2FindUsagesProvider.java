package com.wedgwoodwebworks.jinja2delimiters.findusages;

import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import com.wedgwoodwebworks.jinja2delimiters.lexer.CustomJinja2Lexer;
import com.wedgwoodwebworks.jinja2delimiters.lexer.Jinja2TokenTypes;
import com.wedgwoodwebworks.jinja2delimiters.parser.Jinja2ElementTypes;
import com.wedgwoodwebworks.jinja2delimiters.psi.CustomJinja2PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides find usages functionality for Jinja2 template elements
 */
public class Jinja2FindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
            new CustomJinja2Lexer(),
            TokenSet.create(Jinja2TokenTypes.IDENTIFIER),
            TokenSet.create(
                Jinja2TokenTypes.COMMENT_START,
                Jinja2TokenTypes.COMMENT_END,
                Jinja2TokenTypes.COMMENT_TEXT
            ),
            TokenSet.create(
                Jinja2TokenTypes.STRING,
                Jinja2TokenTypes.INTEGER,
                Jinja2TokenTypes.FLOAT
            )
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof CustomJinja2PsiElement && 
               isSearchableElement((CustomJinja2PsiElement) psiElement);
    }

    private boolean isSearchableElement(CustomJinja2PsiElement element) {
        // Can search for variables, functions, macros, blocks
        return element.getNode().getElementType() == Jinja2TokenTypes.IDENTIFIER ||
               element.getNode().getElementType() == Jinja2ElementTypes.MACRO_BLOCK ||
               element.getNode().getElementType() == Jinja2ElementTypes.BLOCK_DEF ||
               element.getNode().getElementType() == Jinja2ElementTypes.FUNCTION_CALL;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "reference.dialogs.findUsages.other";
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (!(element instanceof CustomJinja2PsiElement)) {
            return "unknown";
        }

        CustomJinja2PsiElement jinja2Element = (CustomJinja2PsiElement) element;

        if (jinja2Element.getNode().getElementType() == Jinja2TokenTypes.IDENTIFIER) {
            // Determine if it's a variable, function, etc. based on context
            if (isInVariableContext(jinja2Element)) {
                return "variable";
            } else if (isInFunctionContext(jinja2Element)) {
                return "function";
            } else if (isInFilterContext(jinja2Element)) {
                return "filter";
            } else {
                return "identifier";
            }
        } else if (jinja2Element.getNode().getElementType() == Jinja2ElementTypes.MACRO_BLOCK) {
            return "macro";
        } else if (jinja2Element.getNode().getElementType() == Jinja2ElementTypes.BLOCK_DEF) {
            return "block";
        } else if (jinja2Element.getNode().getElementType() == Jinja2ElementTypes.FUNCTION_CALL) {
            return "function call";
        }

        return "template element";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }

        String text = element.getText();
        if (text.length() > 50) {
            return text.substring(0, 47) + "...";
        }
        return text;
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (useFullName && element instanceof CustomJinja2PsiElement) {
            // Provide context for full name display
            String type = getType(element);
            String name = getDescriptiveName(element);
            String fileName = element.getContainingFile().getName();
            return String.format("%s '%s' in %s", type, name, fileName);
        }

        return getDescriptiveName(element);
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

    private boolean isInFunctionContext(CustomJinja2PsiElement element) {
        PsiElement next = element.getNextSibling();
        while (next != null && next.getText().trim().isEmpty()) {
            next = next.getNextSibling();
        }

        return next != null && next instanceof CustomJinja2PsiElement &&
               ((CustomJinja2PsiElement) next).getNode().getElementType() == Jinja2TokenTypes.LPAREN;
    }

    private boolean isInFilterContext(CustomJinja2PsiElement element) {
        PsiElement prev = element.getPrevSibling();
        while (prev != null && prev.getText().trim().isEmpty()) {
            prev = prev.getPrevSibling();
        }

        return prev != null && prev instanceof CustomJinja2PsiElement &&
               ((CustomJinja2PsiElement) prev).getNode().getElementType() == Jinja2TokenTypes.PIPE;
    }
}
