package com.wedgwoodwebworks.jinja2customdelimiters.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wedgwoodwebworks.jinja2delimiters.settings.Jinja2DelimitersSettings;
import com.wedgwoodwebworks.jinja2delimiters.lexer.CustomJinja2Lexer;
import com.wedgwoodwebworks.jinja2delimiters.lexer.Jinja2TokenTypes;

import java.util.ArrayList;
import java.util.List;

public class CustomJinja2LexerTest extends BasePlatformTestCase {

    private CustomJinja2Lexer lexer;
    private Jinja2DelimitersSettings settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        lexer = new CustomJinja2Lexer();
        settings = Jinja2DelimitersSettings.getInstance();
        // Reset to default settings
        settings.blockStartString = "{%";
        settings.blockEndString = "%}";
        settings.variableStartString = "{{";
        settings.variableEndString = "}}";
        settings.commentStartString = "{#";
        settings.commentEndString = "#}";
        settings.lineStatementPrefix = "";
        settings.lineCommentPrefix = "";
    }

    public void testBasicDelimiters() {
        String input = "{% if true %}{{ name }}{# comment #}";
        List<IElementType> tokens = tokenize(input);
        
        assertContainsInOrder(tokens,
            Jinja2TokenTypes.BLOCK_START,
            Jinja2TokenTypes.IF,
            Jinja2TokenTypes.BOOLEAN_TRUE,
            Jinja2TokenTypes.BLOCK_END,
            Jinja2TokenTypes.VARIABLE_START,
            Jinja2TokenTypes.IDENTIFIER,
            Jinja2TokenTypes.VARIABLE_END,
            Jinja2TokenTypes.COMMENT_START,
            Jinja2TokenTypes.COMMENT_END
        );
    }

    public void testCustomDelimiters() {
        // Change to custom delimiters
        settings.blockStartString = "<%";
        settings.blockEndString = "%>";
        settings.variableStartString = "[[";
        settings.variableEndString = "]]";
        settings.commentStartString = "<#";
        settings.commentEndString = "#>";
        
        lexer = new CustomJinja2Lexer(); // Recreate to pick up new settings
        
        String input = "<% if true %>[[ name ]]<# comment #>";
        List<IElementType> tokens = tokenize(input);
        
        assertContainsInOrder(tokens,
            Jinja2TokenTypes.BLOCK_START,
            Jinja2TokenTypes.IF,
            Jinja2TokenTypes.BOOLEAN_TRUE,
            Jinja2TokenTypes.BLOCK_END,
            Jinja2TokenTypes.VARIABLE_START,
            Jinja2TokenTypes.IDENTIFIER,
            Jinja2TokenTypes.VARIABLE_END,
            Jinja2TokenTypes.COMMENT_START,
            Jinja2TokenTypes.COMMENT_END
        );
    }

    public void testKeywordRecognition() {
        String input = "{% for item in items if item.active recursive %}";
        List<IElementType> tokens = tokenize(input);
        
        assertContainsInOrder(tokens,
            Jinja2TokenTypes.BLOCK_START,
            Jinja2TokenTypes.FOR,
            Jinja2TokenTypes.IDENTIFIER, // item
            Jinja2TokenTypes.IN,
            Jinja2TokenTypes.IDENTIFIER, // items
            Jinja2TokenTypes.IF,
            Jinja2TokenTypes.IDENTIFIER, // item
            Jinja2TokenTypes.DOT,
            Jinja2TokenTypes.IDENTIFIER, // active
            Jinja2TokenTypes.RECURSIVE,
            Jinja2TokenTypes.BLOCK_END
        );
    }

    public void testOperators() {
        String input = "{{ 1 + 2 * 3 // 4 ** 5 == 6 != 7 <= 8 >= 9 }}";
        List<IElementType> tokens = tokenize(input);
        
        assertContains(tokens, Jinja2TokenTypes.VARIABLE_START);
        assertContains(tokens, Jinja2TokenTypes.INTEGER);
        assertContains(tokens, Jinja2TokenTypes.PLUS);
        assertContains(tokens, Jinja2TokenTypes.MULTIPLY);
        assertContains(tokens, Jinja2TokenTypes.FLOOR_DIVIDE);
        assertContains(tokens, Jinja2TokenTypes.POWER);
        assertContains(tokens, Jinja2TokenTypes.EQ);
        assertContains(tokens, Jinja2TokenTypes.NE);
        assertContains(tokens, Jinja2TokenTypes.LE);
        assertContains(tokens, Jinja2TokenTypes.GE);
        assertContains(tokens, Jinja2TokenTypes.VARIABLE_END);
    }

    public void testLiterals() {
        String input = "{{ 'string' \"double\" 123 45.67 true false none }}";
        List<IElementType> tokens = tokenize(input);
        
        assertContains(tokens, Jinja2TokenTypes.VARIABLE_START);
        assertContains(tokens, Jinja2TokenTypes.STRING);
        assertContains(tokens, Jinja2TokenTypes.INTEGER);
        assertContains(tokens, Jinja2TokenTypes.FLOAT);
        assertContains(tokens, Jinja2TokenTypes.BOOLEAN_TRUE);
        assertContains(tokens, Jinja2TokenTypes.BOOLEAN_FALSE);
        assertContains(tokens, Jinja2TokenTypes.NONE);
        assertContains(tokens, Jinja2TokenTypes.VARIABLE_END);
    }

    public void testComplexExpressions() {
        String input = "{{ user.name | upper | truncate(20) if user.active else 'N/A' }}";
        List<IElementType> tokens = tokenize(input);

        // Test that key tokens are present in the right order, accounting for whitespace
        assertContainsInOrder(tokens,
            Jinja2TokenTypes.VARIABLE_START,
            Jinja2TokenTypes.IDENTIFIER, // user
            Jinja2TokenTypes.DOT,
            Jinja2TokenTypes.IDENTIFIER, // name
            Jinja2TokenTypes.PIPE,
            Jinja2TokenTypes.IDENTIFIER, // upper
            Jinja2TokenTypes.PIPE,
            Jinja2TokenTypes.IDENTIFIER, // truncate
            Jinja2TokenTypes.LPAREN,
            Jinja2TokenTypes.INTEGER, // 20
            Jinja2TokenTypes.RPAREN,
            Jinja2TokenTypes.IF,
            Jinja2TokenTypes.IDENTIFIER, // user
            Jinja2TokenTypes.DOT,
            Jinja2TokenTypes.IDENTIFIER, // active
            Jinja2TokenTypes.ELSE,
            Jinja2TokenTypes.STRING, // 'N/A'
            Jinja2TokenTypes.VARIABLE_END
        );
    }

    public void testLineBasedSyntax() {
        settings.lineStatementPrefix = "%";
        settings.lineCommentPrefix = "##";
        
        lexer = new CustomJinja2Lexer(); // Recreate to pick up new settings
        
        String input = "% if condition\n## This is a comment\nText content";
        List<IElementType> tokens = tokenize(input);
        
        assertContains(tokens, Jinja2TokenTypes.BLOCK_START); // Line statement
        assertContains(tokens, Jinja2TokenTypes.IF);
        assertContains(tokens, Jinja2TokenTypes.IDENTIFIER); // condition
        assertContains(tokens, Jinja2TokenTypes.NEWLINE);
        assertContains(tokens, Jinja2TokenTypes.COMMENT_START); // Line comment
        assertContains(tokens, Jinja2TokenTypes.NEWLINE);
        assertContains(tokens, Jinja2TokenTypes.TEXT);
    }

    public void testStringEscaping() {
        String input = "{{ 'hello\\'world' \"quote\\\"test\" }}";
        List<IElementType> tokens = tokenize(input);
        
        assertContains(tokens, Jinja2TokenTypes.VARIABLE_START);
        assertContains(tokens, Jinja2TokenTypes.STRING); // 'hello\'world'
        assertContains(tokens, Jinja2TokenTypes.STRING); // "quote\"test"
        assertContains(tokens, Jinja2TokenTypes.VARIABLE_END);
    }

    public void testRawBlocks() {
        String input = "{% raw %}{{ not_a_variable }}{% endraw %}";
        List<IElementType> tokens = tokenize(input);

        // Raw blocks work differently - once RAW is encountered, everything until endraw is TEXT
        assertContainsInOrder(tokens,
            Jinja2TokenTypes.BLOCK_START,
            Jinja2TokenTypes.RAW,
            Jinja2TokenTypes.TEXT, // raw content including the %}{{ not_a_variable }}{%
            Jinja2TokenTypes.ENDRAW,
            Jinja2TokenTypes.BLOCK_END
        );
    }

    public void testWhitespaceHandling() {
        String input = "{{   variable   }}";
        List<IElementType> tokens = tokenize(input);
        
        assertContainsInOrder(tokens,
            Jinja2TokenTypes.VARIABLE_START,
            Jinja2TokenTypes.WHITESPACE,
            Jinja2TokenTypes.IDENTIFIER,
            Jinja2TokenTypes.WHITESPACE,
            Jinja2TokenTypes.VARIABLE_END
        );
    }

    public void testComplexStructures() {
        String input = "{{ items[0].name['key'] }}";
        List<IElementType> tokens = tokenize(input);
        
        assertContainsInOrder(tokens,
            Jinja2TokenTypes.VARIABLE_START,
            Jinja2TokenTypes.IDENTIFIER, // items
            Jinja2TokenTypes.LBRACKET,
            Jinja2TokenTypes.INTEGER, // 0
            Jinja2TokenTypes.RBRACKET,
            Jinja2TokenTypes.DOT,
            Jinja2TokenTypes.IDENTIFIER, // name
            Jinja2TokenTypes.LBRACKET,
            Jinja2TokenTypes.STRING, // 'key'
            Jinja2TokenTypes.RBRACKET,
            Jinja2TokenTypes.VARIABLE_END
        );
    }

    public void testAllBlockTypes() {
        String[] blockTypes = {
            "{% for item in items %}",
            "{% if condition %}",
            "{% elif other %}",
            "{% else %}",
            "{% set var = value %}",
            "{% with context %}",
            "{% block content %}",
            "{% extends 'base.html' %}",
            "{% include 'partial.html' %}",
            "{% import 'macros.html' as m %}",
            "{% from 'lib.html' import helper %}",
            "{% macro test() %}",
            "{% call helper() %}",
            "{% filter upper %}",
            "{% autoescape true %}",
            "{% trans %}"
        };

        for (String blockType : blockTypes) {
            List<IElementType> tokens = tokenize(blockType);
            assertContains(tokens, Jinja2TokenTypes.BLOCK_START);
            assertContains(tokens, Jinja2TokenTypes.BLOCK_END);
        }

        // Test raw block separately since it has different behavior
        String rawBlock = "{% raw %}";
        List<IElementType> rawTokens = tokenize(rawBlock);
        assertContains(rawTokens, Jinja2TokenTypes.BLOCK_START);
        assertContains(rawTokens, Jinja2TokenTypes.RAW);
        // Raw blocks don't have BLOCK_END token because the %} is consumed entering RAW mode
    }

    // Helper methods
    private List<IElementType> tokenize(String input) {
        lexer.start(input, 0, input.length(), 0);
        List<IElementType> tokens = new ArrayList<>();
        
        while (lexer.getTokenType() != null) {
            tokens.add(lexer.getTokenType());
            lexer.advance();
        }
        
        return tokens;
    }

    private void assertContains(List<IElementType> tokens, IElementType expectedToken) {
        assertTrue("Expected token " + expectedToken + " not found in: " + tokens,
                   tokens.contains(expectedToken));
    }

    private void assertContainsInOrder(List<IElementType> tokens, IElementType... expectedTokens) {
        int lastIndex = -1;
        for (IElementType expectedToken : expectedTokens) {
            int index = -1;
            // Find the next occurrence of the token after lastIndex
            for (int i = lastIndex + 1; i < tokens.size(); i++) {
                if (tokens.get(i).equals(expectedToken)) {
                    index = i;
                    break;
                }
            }
            assertTrue("Expected token " + expectedToken + " not found after index " + lastIndex + " in: " + tokens,
                      index != -1);
            lastIndex = index;
        }
    }
}