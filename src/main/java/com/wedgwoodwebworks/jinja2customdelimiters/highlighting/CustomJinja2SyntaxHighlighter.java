package com.wedgwoodwebworks.jinja2delimiters.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.wedgwoodwebworks.jinja2delimiters.lexer.CustomJinja2Lexer;
import com.wedgwoodwebworks.jinja2delimiters.lexer.Jinja2TokenTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class CustomJinja2SyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey BLOCK_DELIMITER = createTextAttributesKey("JINJA2_BLOCK_DELIMITER", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey VARIABLE_DELIMITER = createTextAttributesKey("JINJA2_VARIABLE_DELIMITER", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMENT_DELIMITER = createTextAttributesKey("JINJA2_COMMENT_DELIMITER", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey KEYWORD = createTextAttributesKey("JINJA2_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING = createTextAttributesKey("JINJA2_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER = createTextAttributesKey("JINJA2_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("JINJA2_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);

    private static final TextAttributesKey[] BLOCK_DELIMITER_KEYS = new TextAttributesKey[]{BLOCK_DELIMITER};
    private static final TextAttributesKey[] VARIABLE_DELIMITER_KEYS = new TextAttributesKey[]{VARIABLE_DELIMITER};
    private static final TextAttributesKey[] COMMENT_DELIMITER_KEYS = new TextAttributesKey[]{COMMENT_DELIMITER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CustomJinja2Lexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(Jinja2TokenTypes.BLOCK_START) || tokenType.equals(Jinja2TokenTypes.BLOCK_END)) {
            return BLOCK_DELIMITER_KEYS;
        } else if (tokenType.equals(Jinja2TokenTypes.VARIABLE_START) || tokenType.equals(Jinja2TokenTypes.VARIABLE_END)) {
            return VARIABLE_DELIMITER_KEYS;
        } else if (tokenType.equals(Jinja2TokenTypes.COMMENT_START) || tokenType.equals(Jinja2TokenTypes.COMMENT_END)) {
            return COMMENT_DELIMITER_KEYS;
        } else if (tokenType.equals(Jinja2TokenTypes.KEYWORD)) {
            return KEYWORD_KEYS;
        } else if (tokenType.equals(Jinja2TokenTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(Jinja2TokenTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(Jinja2TokenTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
