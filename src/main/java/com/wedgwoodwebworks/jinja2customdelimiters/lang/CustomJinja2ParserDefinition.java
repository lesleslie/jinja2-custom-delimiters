package com.wedgwoodwebworks.jinja2delimiters.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.wedgwoodwebworks.jinja2delimiters.lexer.CustomJinja2Lexer;
import com.wedgwoodwebworks.jinja2delimiters.lexer.Jinja2TokenTypes;
import com.wedgwoodwebworks.jinja2delimiters.parser.CustomJinja2Parser;
import com.wedgwoodwebworks.jinja2delimiters.psi.CustomJinja2File;
import com.wedgwoodwebworks.jinja2delimiters.psi.CustomJinja2PsiElement;
import org.jetbrains.annotations.NotNull;

public class CustomJinja2ParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType("JINJA2_CUSTOM_FILE", CustomJinja2Language.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CustomJinja2Lexer();
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        return new CustomJinja2Parser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.create(Jinja2TokenTypes.COMMENT_START, Jinja2TokenTypes.COMMENT_END);
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.create(Jinja2TokenTypes.STRING);
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return new CustomJinja2PsiElement(node);
    }

    @NotNull
    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CustomJinja2File(viewProvider);
    }
}
