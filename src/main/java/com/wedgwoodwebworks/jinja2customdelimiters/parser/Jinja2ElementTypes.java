package com.wedgwoodwebworks.jinja2delimiters.parser;

import com.intellij.psi.tree.IElementType;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2Language;

public class Jinja2ElementTypes {
    // Basic elements
    public static final IElementType BLOCK = new IElementType("BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType VARIABLE = new IElementType("VARIABLE", CustomJinja2Language.INSTANCE);
    public static final IElementType COMMENT = new IElementType("COMMENT", CustomJinja2Language.INSTANCE);
    public static final IElementType TEXT = new IElementType("TEXT", CustomJinja2Language.INSTANCE);

    // Control structure blocks
    public static final IElementType FOR_BLOCK = new IElementType("FOR_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType IF_BLOCK = new IElementType("IF_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType ELIF_BLOCK = new IElementType("ELIF_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType ELSE_BLOCK = new IElementType("ELSE_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType SET_BLOCK = new IElementType("SET_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType WITH_BLOCK = new IElementType("WITH_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType BLOCK_DEF = new IElementType("BLOCK_DEF", CustomJinja2Language.INSTANCE);
    public static final IElementType EXTENDS_BLOCK = new IElementType("EXTENDS_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType INCLUDE_BLOCK = new IElementType("INCLUDE_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType IMPORT_BLOCK = new IElementType("IMPORT_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType FROM_IMPORT_BLOCK = new IElementType("FROM_IMPORT_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType MACRO_BLOCK = new IElementType("MACRO_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType CALL_BLOCK = new IElementType("CALL_BLOCK", CustomJinja2Language.INSTANCE);

    // Advanced blocks
    public static final IElementType FILTER_BLOCK = new IElementType("FILTER_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType RAW_BLOCK = new IElementType("RAW_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType AUTOESCAPE_BLOCK = new IElementType("AUTOESCAPE_BLOCK", CustomJinja2Language.INSTANCE);
    public static final IElementType TRANS_BLOCK = new IElementType("TRANS_BLOCK", CustomJinja2Language.INSTANCE);

    // Expression elements
    public static final IElementType EXPRESSION = new IElementType("EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType OR_EXPRESSION = new IElementType("OR_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType AND_EXPRESSION = new IElementType("AND_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType NOT_EXPRESSION = new IElementType("NOT_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType COMPARISON_EXPRESSION = new IElementType("COMPARISON_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType ARITHMETIC_EXPRESSION = new IElementType("ARITHMETIC_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType TERM_EXPRESSION = new IElementType("TERM_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType POWER_EXPRESSION = new IElementType("POWER_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType FILTER_EXPRESSION = new IElementType("FILTER_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType CONDITIONAL_EXPRESSION = new IElementType("CONDITIONAL_EXPRESSION", CustomJinja2Language.INSTANCE);

    // Access expressions
    public static final IElementType ATTRIBUTE_ACCESS = new IElementType("ATTRIBUTE_ACCESS", CustomJinja2Language.INSTANCE);
    public static final IElementType SUBSCRIPT_ACCESS = new IElementType("SUBSCRIPT_ACCESS", CustomJinja2Language.INSTANCE);
    public static final IElementType FUNCTION_CALL = new IElementType("FUNCTION_CALL", CustomJinja2Language.INSTANCE);

    // Literals
    public static final IElementType LIST_LITERAL = new IElementType("LIST_LITERAL", CustomJinja2Language.INSTANCE);
    public static final IElementType DICT_LITERAL = new IElementType("DICT_LITERAL", CustomJinja2Language.INSTANCE);
    public static final IElementType TUPLE_LITERAL = new IElementType("TUPLE_LITERAL", CustomJinja2Language.INSTANCE);
    public static final IElementType STRING_LITERAL = new IElementType("STRING_LITERAL", CustomJinja2Language.INSTANCE);
    public static final IElementType NUMBER_LITERAL = new IElementType("NUMBER_LITERAL", CustomJinja2Language.INSTANCE);
    public static final IElementType BOOLEAN_LITERAL = new IElementType("BOOLEAN_LITERAL", CustomJinja2Language.INSTANCE);

    // Identifier and references
    public static final IElementType IDENTIFIER = new IElementType("IDENTIFIER", CustomJinja2Language.INSTANCE);
    public static final IElementType VARIABLE_REFERENCE = new IElementType("VARIABLE_REFERENCE", CustomJinja2Language.INSTANCE);
    public static final IElementType FILTER_REFERENCE = new IElementType("FILTER_REFERENCE", CustomJinja2Language.INSTANCE);

    // Statements
    public static final IElementType ASSIGNMENT_STATEMENT = new IElementType("ASSIGNMENT_STATEMENT", CustomJinja2Language.INSTANCE);
    public static final IElementType EXPRESSION_STATEMENT = new IElementType("EXPRESSION_STATEMENT", CustomJinja2Language.INSTANCE);

    // Argument lists
    public static final IElementType ARGUMENT_LIST = new IElementType("ARGUMENT_LIST", CustomJinja2Language.INSTANCE);
    public static final IElementType KEYWORD_ARGUMENT = new IElementType("KEYWORD_ARGUMENT", CustomJinja2Language.INSTANCE);

    // Loop constructs
    public static final IElementType FOR_LOOP = new IElementType("FOR_LOOP", CustomJinja2Language.INSTANCE);
    public static final IElementType LOOP_CONDITION = new IElementType("LOOP_CONDITION", CustomJinja2Language.INSTANCE);

    // Test expressions
    public static final IElementType TEST_EXPRESSION = new IElementType("TEST_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType IS_EXPRESSION = new IElementType("IS_EXPRESSION", CustomJinja2Language.INSTANCE);

    // Special constructs  
    public static final IElementType SLICE_EXPRESSION = new IElementType("SLICE_EXPRESSION", CustomJinja2Language.INSTANCE);
    public static final IElementType COMPREHENSION = new IElementType("COMPREHENSION", CustomJinja2Language.INSTANCE);
}
