package com.wedgwoodwebworks.jinja2delimiters.lexer;

import com.intellij.psi.tree.IElementType;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2Language;

public class Jinja2TokenTypes {
    // Delimiters
    public static final IElementType BLOCK_START = new IElementType("BLOCK_START", CustomJinja2Language.INSTANCE);
    public static final IElementType BLOCK_END = new IElementType("BLOCK_END", CustomJinja2Language.INSTANCE);
    public static final IElementType VARIABLE_START = new IElementType("VARIABLE_START", CustomJinja2Language.INSTANCE);
    public static final IElementType VARIABLE_END = new IElementType("VARIABLE_END", CustomJinja2Language.INSTANCE);
    public static final IElementType COMMENT_START = new IElementType("COMMENT_START", CustomJinja2Language.INSTANCE);
    public static final IElementType COMMENT_END = new IElementType("COMMENT_END", CustomJinja2Language.INSTANCE);
    
    // Keywords - Control Flow
    public static final IElementType FOR = new IElementType("FOR", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDFOR = new IElementType("ENDFOR", CustomJinja2Language.INSTANCE);
    public static final IElementType IF = new IElementType("IF", CustomJinja2Language.INSTANCE);
    public static final IElementType ELIF = new IElementType("ELIF", CustomJinja2Language.INSTANCE);
    public static final IElementType ELSE = new IElementType("ELSE", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDIF = new IElementType("ENDIF", CustomJinja2Language.INSTANCE);
    public static final IElementType SET = new IElementType("SET", CustomJinja2Language.INSTANCE);
    public static final IElementType WITH = new IElementType("WITH", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDWITH = new IElementType("ENDWITH", CustomJinja2Language.INSTANCE);
    
    // Keywords - Template Structure
    public static final IElementType BLOCK = new IElementType("BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDBLOCK = new IElementType("ENDBLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType EXTENDS = new IElementType("EXTENDS", CustomJinja2Language.INSTANCE);
    public static final IElementType INCLUDE = new IElementType("INCLUDE", CustomJinja2Language.INSTANCE);
    public static final IElementType IMPORT = new IElementType("IMPORT", CustomJinja2Language.INSTANCE);
    public static final IElementType FROM = new IElementType("FROM", CustomJinja2Language.INSTANCE);
    public static final IElementType AS = new IElementType("AS", CustomJinja2Language.INSTANCE);
    
    // Keywords - Macros and Functions
    public static final IElementType MACRO = new IElementType("MACRO", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDMACRO = new IElementType("ENDMACRO", CustomJinja2Language.INSTANCE);
    public static final IElementType CALL = new IElementType("CALL", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDCALL = new IElementType("ENDCALL", CustomJinja2Language.INSTANCE);
    
    // Keywords - Advanced Features
    public static final IElementType FILTER = new IElementType("FILTER", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDFILTER = new IElementType("ENDFILTER", CustomJinja2Language.INSTANCE);
    public static final IElementType RAW = new IElementType("RAW", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDRAW = new IElementType("ENDRAW", CustomJinja2Language.INSTANCE);
    public static final IElementType AUTOESCAPE = new IElementType("AUTOESCAPE", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDAUTOESCAPE = new IElementType("ENDAUTOESCAPE", CustomJinja2Language.INSTANCE);
    public static final IElementType TRANS = new IElementType("TRANS", CustomJinja2Language.INSTANCE);
    public static final IElementType ENDTRANS = new IElementType("ENDTRANS", CustomJinja2Language.INSTANCE);
    public static final IElementType PLURALIZE = new IElementType("PLURALIZE", CustomJinja2Language.INSTANCE);
    
    // Keywords - Logic and Flow
    public static final IElementType IN = new IElementType("IN", CustomJinja2Language.INSTANCE);
    public static final IElementType IS = new IElementType("IS", CustomJinja2Language.INSTANCE);
    public static final IElementType NOT = new IElementType("NOT", CustomJinja2Language.INSTANCE);
    public static final IElementType AND = new IElementType("AND", CustomJinja2Language.INSTANCE);
    public static final IElementType OR = new IElementType("OR", CustomJinja2Language.INSTANCE);
    
    // Keywords - Loop Special
    public static final IElementType RECURSIVE = new IElementType("RECURSIVE", CustomJinja2Language.INSTANCE);
    public static final IElementType SCOPED = new IElementType("SCOPED", CustomJinja2Language.INSTANCE);
    public static final IElementType IGNORE = new IElementType("IGNORE", CustomJinja2Language.INSTANCE);
    public static final IElementType MISSING = new IElementType("MISSING", CustomJinja2Language.INSTANCE);
    public static final IElementType WITHOUT = new IElementType("WITHOUT", CustomJinja2Language.INSTANCE);
    public static final IElementType CONTEXT = new IElementType("CONTEXT", CustomJinja2Language.INSTANCE);
    
    // Operators - Assignment
    public static final IElementType ASSIGN = new IElementType("ASSIGN", CustomJinja2Language.INSTANCE); // =
    
    // Operators - Arithmetic
    public static final IElementType PLUS = new IElementType("PLUS", CustomJinja2Language.INSTANCE); // +
    public static final IElementType MINUS = new IElementType("MINUS", CustomJinja2Language.INSTANCE); // -
    public static final IElementType MULTIPLY = new IElementType("MULTIPLY", CustomJinja2Language.INSTANCE); // *
    public static final IElementType DIVIDE = new IElementType("DIVIDE", CustomJinja2Language.INSTANCE); // /
    public static final IElementType FLOOR_DIVIDE = new IElementType("FLOOR_DIVIDE", CustomJinja2Language.INSTANCE); // //
    public static final IElementType MODULO = new IElementType("MODULO", CustomJinja2Language.INSTANCE); // %
    public static final IElementType POWER = new IElementType("POWER", CustomJinja2Language.INSTANCE); // **
    
    // Operators - Comparison
    public static final IElementType EQ = new IElementType("EQ", CustomJinja2Language.INSTANCE); // ==
    public static final IElementType NE = new IElementType("NE", CustomJinja2Language.INSTANCE); // !=
    public static final IElementType LT = new IElementType("LT", CustomJinja2Language.INSTANCE); // <
    public static final IElementType GT = new IElementType("GT", CustomJinja2Language.INSTANCE); // >
    public static final IElementType LE = new IElementType("LE", CustomJinja2Language.INSTANCE); // <=
    public static final IElementType GE = new IElementType("GE", CustomJinja2Language.INSTANCE); // >=
    
    // Operators - Special
    public static final IElementType PIPE = new IElementType("PIPE", CustomJinja2Language.INSTANCE); // |
    public static final IElementType TILDE = new IElementType("TILDE", CustomJinja2Language.INSTANCE); // ~ (string concatenation)
    
    // Punctuation
    public static final IElementType LPAREN = new IElementType("LPAREN", CustomJinja2Language.INSTANCE); // (
    public static final IElementType RPAREN = new IElementType("RPAREN", CustomJinja2Language.INSTANCE); // )
    public static final IElementType LBRACKET = new IElementType("LBRACKET", CustomJinja2Language.INSTANCE); // [
    public static final IElementType RBRACKET = new IElementType("RBRACKET", CustomJinja2Language.INSTANCE); // ]
    public static final IElementType LBRACE = new IElementType("LBRACE", CustomJinja2Language.INSTANCE); // {
    public static final IElementType RBRACE = new IElementType("RBRACE", CustomJinja2Language.INSTANCE); // }
    public static final IElementType DOT = new IElementType("DOT", CustomJinja2Language.INSTANCE); // .
    public static final IElementType COMMA = new IElementType("COMMA", CustomJinja2Language.INSTANCE); // ,
    public static final IElementType COLON = new IElementType("COLON", CustomJinja2Language.INSTANCE); // :
    public static final IElementType SEMICOLON = new IElementType("SEMICOLON", CustomJinja2Language.INSTANCE); // ;
    public static final IElementType QUESTION = new IElementType("QUESTION", CustomJinja2Language.INSTANCE); // ?
    
    // Literals
    public static final IElementType STRING = new IElementType("STRING", CustomJinja2Language.INSTANCE);
    public static final IElementType INTEGER = new IElementType("INTEGER", CustomJinja2Language.INSTANCE);
    public static final IElementType FLOAT = new IElementType("FLOAT", CustomJinja2Language.INSTANCE);
    public static final IElementType NUMBER = new IElementType("NUMBER", CustomJinja2Language.INSTANCE); // Generic number
    public static final IElementType BOOLEAN_TRUE = new IElementType("BOOLEAN_TRUE", CustomJinja2Language.INSTANCE);
    public static final IElementType BOOLEAN_FALSE = new IElementType("BOOLEAN_FALSE", CustomJinja2Language.INSTANCE);
    public static final IElementType NONE = new IElementType("NONE", CustomJinja2Language.INSTANCE);
    
    // Identifiers and Names
    public static final IElementType IDENTIFIER = new IElementType("IDENTIFIER", CustomJinja2Language.INSTANCE);
    public static final IElementType KEYWORD = new IElementType("KEYWORD", CustomJinja2Language.INSTANCE); // Generic keyword fallback
    
    // Content
    public static final IElementType TEXT = new IElementType("TEXT", CustomJinja2Language.INSTANCE);
    public static final IElementType COMMENT_TEXT = new IElementType("COMMENT_TEXT", CustomJinja2Language.INSTANCE);
    public static final IElementType WHITESPACE = new IElementType("WHITESPACE", CustomJinja2Language.INSTANCE);
    public static final IElementType NEWLINE = new IElementType("NEWLINE", CustomJinja2Language.INSTANCE);
    
    // Special
    public static final IElementType EOF = new IElementType("EOF", CustomJinja2Language.INSTANCE);
    public static final IElementType BAD_CHARACTER = new IElementType("BAD_CHARACTER", CustomJinja2Language.INSTANCE);
}
