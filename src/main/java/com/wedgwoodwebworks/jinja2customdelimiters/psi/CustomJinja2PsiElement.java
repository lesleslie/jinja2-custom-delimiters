package com.wedgwoodwebworks.jinja2delimiters.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class CustomJinja2PsiElement extends ASTWrapperPsiElement {

    public CustomJinja2PsiElement(@NotNull ASTNode node) {
        super(node);
    }
}
