package com.wedgwoodwebworks.jinja2delimiters.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.wedgwoodwebworks.jinja2delimiters.lexer.Jinja2TokenTypes;
import org.jetbrains.annotations.NotNull;

public class CustomJinja2Parser implements PsiParser {

    private static final Logger LOG = Logger.getInstance(CustomJinja2Parser.class);

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        LOG.debug("Starting Jinja2 template parsing");
        PsiBuilder.Marker rootMarker = builder.mark();

        while (!builder.eof()) {
            try {
                IElementType tokenType = builder.getTokenType();

                if (tokenType == Jinja2TokenTypes.BLOCK_START) {
                    parseBlock(builder);
                } else if (tokenType == Jinja2TokenTypes.VARIABLE_START) {
                    parseVariable(builder);
                } else if (tokenType == Jinja2TokenTypes.COMMENT_START) {
                    parseComment(builder);
                } else {
                    builder.advanceLexer(); // Skip text content and other tokens
                }
            } catch (Exception e) {
                LOG.error("Error parsing Jinja2 template", e);
                recoverFromError(builder);
            }
        }

        rootMarker.done(root);
        LOG.debug("Completed Jinja2 template parsing");
        return builder.getTreeBuilt();
    }

    private void parseBlock(PsiBuilder builder) {
        PsiBuilder.Marker blockMarker = builder.mark();
        builder.advanceLexer(); // consume BLOCK_START

        skipWhitespace(builder);

        IElementType tokenType = builder.getTokenType();
        
        try {
            if (tokenType == Jinja2TokenTypes.FOR) {
                parseForBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.IF) {
                parseIfBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.ELIF) {
                parseElifBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.ELSE) {
                parseElseBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.SET) {
                parseSetBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.WITH) {
                parseWithBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.BLOCK) {
                parseBlockDefBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.EXTENDS) {
                parseExtendsBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.INCLUDE) {
                parseIncludeBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.IMPORT) {
                parseImportBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.FROM) {
                parseFromImportBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.MACRO) {
                parseMacroBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.CALL) {
                parseCallBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.FILTER) {
                parseFilterBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.RAW) {
                parseRawBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.AUTOESCAPE) {
                parseAutoescapeBlock(builder, blockMarker);
            } else if (tokenType == Jinja2TokenTypes.TRANS) {
                parseTransBlock(builder, blockMarker);
            } else {
                parseGenericBlock(builder, blockMarker);
            }
        } catch (Exception e) {
            LOG.warn("Error parsing block, falling back to generic parsing", e);
            parseGenericBlock(builder, blockMarker);
        }
    }

    private void parseForBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'for'
        skipWhitespace(builder);

        // Parse: for variable in iterable [if condition] [recursive]
        if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
            parseTargetList(builder); // loop variables
            skipWhitespace(builder);

            if (builder.getTokenType() == Jinja2TokenTypes.IN) {
                builder.advanceLexer(); // consume 'in'
                skipWhitespace(builder);
                parseExpression(builder); // iterable
                skipWhitespace(builder);
                
                // Optional filter condition
                if (builder.getTokenType() == Jinja2TokenTypes.IF) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                    parseExpression(builder);
                    skipWhitespace(builder);
                }
                
                // Optional recursive
                if (builder.getTokenType() == Jinja2TokenTypes.RECURSIVE) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                }
            }
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.FOR_BLOCK);
    }

    private void parseIfBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'if'
        skipWhitespace(builder);

        parseExpression(builder); // condition

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.IF_BLOCK);
    }

    private void parseElifBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'elif'
        skipWhitespace(builder);

        parseExpression(builder); // condition

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.ELIF_BLOCK);
    }

    private void parseElseBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'else'
        skipWhitespace(builder);

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.ELSE_BLOCK);
    }

    private void parseSetBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'set'
        skipWhitespace(builder);

        // Parse: set target = expression or set target %} block content {% endset
        parseTargetList(builder); // assignment targets
        skipWhitespace(builder);

        if (builder.getTokenType() == Jinja2TokenTypes.ASSIGN) {
            builder.advanceLexer(); // consume '='
            skipWhitespace(builder);
            parseExpression(builder); // value expression
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.SET_BLOCK);
    }

    private void parseWithBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'with'
        skipWhitespace(builder);

        // Parse with context assignments
        parseWithContextList(builder);

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.WITH_BLOCK);
    }

    private void parseBlockDefBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'block'
        skipWhitespace(builder);

        // Parse block name
        if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
            builder.advanceLexer();
            skipWhitespace(builder);
            
            // Optional scoped
            if (builder.getTokenType() == Jinja2TokenTypes.SCOPED) {
                builder.advanceLexer();
                skipWhitespace(builder);
            }
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.BLOCK_DEF);
    }

    private void parseExtendsBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'extends'
        skipWhitespace(builder);

        parseExpression(builder); // template name

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.EXTENDS_BLOCK);
    }

    private void parseIncludeBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'include'
        skipWhitespace(builder);

        parseExpression(builder); // template name
        skipWhitespace(builder);
        
        // Optional ignore missing
        if (builder.getTokenType() == Jinja2TokenTypes.IGNORE) {
            builder.advanceLexer();
            skipWhitespace(builder);
            if (builder.getTokenType() == Jinja2TokenTypes.MISSING) {
                builder.advanceLexer();
                skipWhitespace(builder);
            }
        }
        
        // Optional without context
        if (builder.getTokenType() == Jinja2TokenTypes.WITHOUT) {
            builder.advanceLexer();
            skipWhitespace(builder);
            if (builder.getTokenType() == Jinja2TokenTypes.CONTEXT) {
                builder.advanceLexer();
                skipWhitespace(builder);
            }
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.INCLUDE_BLOCK);
    }

    private void parseImportBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'import'
        skipWhitespace(builder);

        parseExpression(builder); // template name
        skipWhitespace(builder);
        
        if (builder.getTokenType() == Jinja2TokenTypes.AS) {
            builder.advanceLexer();
            skipWhitespace(builder);
            
            if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                builder.advanceLexer(); // alias name
                skipWhitespace(builder);
            }
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.IMPORT_BLOCK);
    }

    private void parseFromImportBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'from'
        skipWhitespace(builder);

        parseExpression(builder); // template name
        skipWhitespace(builder);
        
        if (builder.getTokenType() == Jinja2TokenTypes.IMPORT) {
            builder.advanceLexer();
            skipWhitespace(builder);
            
            parseImportNameList(builder);
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.FROM_IMPORT_BLOCK);
    }

    private void parseMacroBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'macro'
        skipWhitespace(builder);

        // Parse macro name and parameters
        if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
            builder.advanceLexer(); // macro name
            skipWhitespace(builder);

            if (builder.getTokenType() == Jinja2TokenTypes.LPAREN) {
                parseParameterList(builder);
            }
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.MACRO_BLOCK);
    }

    private void parseCallBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'call'
        skipWhitespace(builder);

        // Parse call expression
        parseExpression(builder);

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.CALL_BLOCK);
    }

    private void parseFilterBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'filter'
        skipWhitespace(builder);

        // Parse filter expression
        parseExpression(builder);

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.FILTER_BLOCK);
    }

    private void parseRawBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'raw'
        skipWhitespace(builder);

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.RAW_BLOCK);
    }

    private void parseAutoescapeBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'autoescape'
        skipWhitespace(builder);

        // Parse autoescape setting
        parseExpression(builder);

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.AUTOESCAPE_BLOCK);
    }

    private void parseTransBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        builder.advanceLexer(); // consume 'trans'
        skipWhitespace(builder);

        // Optional variable list for pluralization
        while (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
            builder.advanceLexer();
            skipWhitespace(builder);
            
            if (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                builder.advanceLexer();
                skipWhitespace(builder);
            } else {
                break;
            }
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.TRANS_BLOCK);
    }

    private void parseGenericBlock(PsiBuilder builder, PsiBuilder.Marker blockMarker) {
        // Parse generic block content until BLOCK_END
        while (!builder.eof() && builder.getTokenType() != Jinja2TokenTypes.BLOCK_END) {
            if (builder.getTokenType() == Jinja2TokenTypes.WHITESPACE) {
                builder.advanceLexer();
            } else {
                parseExpression(builder);
            }
        }

        consumeBlockEnd(builder);
        blockMarker.done(Jinja2ElementTypes.BLOCK);
    }

    private void parseVariable(PsiBuilder builder) {
        PsiBuilder.Marker variableMarker = builder.mark();
        builder.advanceLexer(); // consume VARIABLE_START

        skipWhitespace(builder);
        parseExpression(builder); // Parse expression inside variable
        skipWhitespace(builder);

        if (builder.getTokenType() == Jinja2TokenTypes.VARIABLE_END) {
            builder.advanceLexer();
        } else {
            builder.error("Expected variable end delimiter");
        }

        variableMarker.done(Jinja2ElementTypes.VARIABLE);
    }

    private void parseComment(PsiBuilder builder) {
        PsiBuilder.Marker commentMarker = builder.mark();
        builder.advanceLexer(); // consume COMMENT_START

        // Parse comment content until COMMENT_END
        while (!builder.eof() && builder.getTokenType() != Jinja2TokenTypes.COMMENT_END) {
            builder.advanceLexer();
        }

        if (builder.getTokenType() == Jinja2TokenTypes.COMMENT_END) {
            builder.advanceLexer();
        } else {
            builder.error("Expected comment end delimiter");
        }

        commentMarker.done(Jinja2ElementTypes.COMMENT);
    }

    // Expression parsing with proper precedence
    private void parseExpression(PsiBuilder builder) {
        parseConditionalExpression(builder);
    }

    private void parseConditionalExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parseOrExpression(builder);

        if (builder.getTokenType() == Jinja2TokenTypes.IF) {
            builder.advanceLexer(); // consume 'if'
            skipWhitespace(builder);
            parseOrExpression(builder); // condition
            skipWhitespace(builder);
            
            if (builder.getTokenType() == Jinja2TokenTypes.ELSE) {
                builder.advanceLexer(); // consume 'else'
                skipWhitespace(builder);
                parseOrExpression(builder); // else value
            }
            
            marker.done(Jinja2ElementTypes.CONDITIONAL_EXPRESSION);
        } else {
            marker.drop();
        }
    }

    private void parseOrExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parseAndExpression(builder);

        while (builder.getTokenType() == Jinja2TokenTypes.OR) {
            builder.advanceLexer();
            skipWhitespace(builder);
            parseAndExpression(builder);
            marker.done(Jinja2ElementTypes.OR_EXPRESSION);
            marker = marker.precede();
        }

        marker.drop();
    }

    private void parseAndExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parseNotExpression(builder);

        while (builder.getTokenType() == Jinja2TokenTypes.AND) {
            builder.advanceLexer();
            skipWhitespace(builder);
            parseNotExpression(builder);
            marker.done(Jinja2ElementTypes.AND_EXPRESSION);
            marker = marker.precede();
        }

        marker.drop();
    }

    private void parseNotExpression(PsiBuilder builder) {
        if (builder.getTokenType() == Jinja2TokenTypes.NOT) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            skipWhitespace(builder);
            parseComparisonExpression(builder);
            marker.done(Jinja2ElementTypes.NOT_EXPRESSION);
        } else {
            parseComparisonExpression(builder);
        }
    }

    private void parseComparisonExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parseArithmeticExpression(builder);

        IElementType tokenType = builder.getTokenType();
        if (tokenType == Jinja2TokenTypes.EQ || tokenType == Jinja2TokenTypes.NE ||
            tokenType == Jinja2TokenTypes.LT || tokenType == Jinja2TokenTypes.LE ||
            tokenType == Jinja2TokenTypes.GT || tokenType == Jinja2TokenTypes.GE) {

            builder.advanceLexer();
            skipWhitespace(builder);
            parseArithmeticExpression(builder);
            marker.done(Jinja2ElementTypes.COMPARISON_EXPRESSION);
        } else if (tokenType == Jinja2TokenTypes.IN) {
            builder.advanceLexer();
            skipWhitespace(builder);
            parseArithmeticExpression(builder);
            marker.done(Jinja2ElementTypes.COMPARISON_EXPRESSION);
        } else if (tokenType == Jinja2TokenTypes.IS) {
            builder.advanceLexer();
            skipWhitespace(builder);
            
            // Optional 'not' after 'is'
            if (builder.getTokenType() == Jinja2TokenTypes.NOT) {
                builder.advanceLexer();
                skipWhitespace(builder);
            }
            
            parseArithmeticExpression(builder); // test name or expression
            marker.done(Jinja2ElementTypes.IS_EXPRESSION);
        } else {
            marker.drop();
        }
    }

    private void parseArithmeticExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parseTermExpression(builder);

        IElementType tokenType = builder.getTokenType();
        while (tokenType == Jinja2TokenTypes.PLUS || tokenType == Jinja2TokenTypes.MINUS || 
               tokenType == Jinja2TokenTypes.TILDE) {
            builder.advanceLexer();
            skipWhitespace(builder);
            parseTermExpression(builder);
            marker.done(Jinja2ElementTypes.ARITHMETIC_EXPRESSION);
            marker = marker.precede();
            tokenType = builder.getTokenType();
        }

        marker.drop();
    }

    private void parseTermExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parseFactorExpression(builder);

        IElementType tokenType = builder.getTokenType();
        while (tokenType == Jinja2TokenTypes.MULTIPLY || tokenType == Jinja2TokenTypes.DIVIDE ||
               tokenType == Jinja2TokenTypes.FLOOR_DIVIDE || tokenType == Jinja2TokenTypes.MODULO) {
            builder.advanceLexer();
            skipWhitespace(builder);
            parseFactorExpression(builder);
            marker.done(Jinja2ElementTypes.TERM_EXPRESSION);
            marker = marker.precede();
            tokenType = builder.getTokenType();
        }

        marker.drop();
    }

    private void parseFactorExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parsePowerExpression(builder);

        if (builder.getTokenType() == Jinja2TokenTypes.POWER) {
            builder.advanceLexer();
            skipWhitespace(builder);
            parseFactorExpression(builder); // Right associative
            marker.done(Jinja2ElementTypes.POWER_EXPRESSION);
        } else {
            marker.drop();
        }
    }

    private void parsePowerExpression(PsiBuilder builder) {
        parseFilterExpression(builder);
    }

    private void parseFilterExpression(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        parseAtomicExpression(builder);

        while (builder.getTokenType() == Jinja2TokenTypes.PIPE) {
            builder.advanceLexer(); // consume |
            skipWhitespace(builder);

            if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                builder.advanceLexer(); // filter name
                skipWhitespace(builder);

                // Parse filter arguments
                if (builder.getTokenType() == Jinja2TokenTypes.LPAREN) {
                    parseArgumentList(builder);
                }
            } else {
                builder.error("Expected filter name");
            }

            marker.done(Jinja2ElementTypes.FILTER_EXPRESSION);
            marker = marker.precede();
        }

        marker.drop();
    }

    private void parseAtomicExpression(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        if (tokenType == Jinja2TokenTypes.IDENTIFIER) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();

            // Handle postfix operations
            while (true) {
                skipWhitespace(builder);
                if (builder.getTokenType() == Jinja2TokenTypes.DOT) {
                    // Attribute access
                    builder.advanceLexer();
                    skipWhitespace(builder);
                    if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                        builder.advanceLexer();
                        marker.done(Jinja2ElementTypes.ATTRIBUTE_ACCESS);
                        marker = marker.precede();
                    } else {
                        builder.error("Expected attribute name");
                        break;
                    }
                } else if (builder.getTokenType() == Jinja2TokenTypes.LBRACKET) {
                    // Subscript access
                    builder.advanceLexer();
                    skipWhitespace(builder);
                    parseExpression(builder); // index or slice
                    skipWhitespace(builder);
                    if (builder.getTokenType() == Jinja2TokenTypes.RBRACKET) {
                        builder.advanceLexer();
                    } else {
                        builder.error("Expected ']'");
                    }
                    marker.done(Jinja2ElementTypes.SUBSCRIPT_ACCESS);
                    marker = marker.precede();
                } else if (builder.getTokenType() == Jinja2TokenTypes.LPAREN) {
                    // Function call
                    parseArgumentList(builder);
                    marker.done(Jinja2ElementTypes.FUNCTION_CALL);
                    marker = marker.precede();
                } else {
                    break;
                }
            }

            marker.drop();
        } else if (tokenType == Jinja2TokenTypes.STRING) {
            builder.advanceLexer();
        } else if (tokenType == Jinja2TokenTypes.INTEGER || tokenType == Jinja2TokenTypes.FLOAT) {
            builder.advanceLexer();
        } else if (tokenType == Jinja2TokenTypes.BOOLEAN_TRUE || tokenType == Jinja2TokenTypes.BOOLEAN_FALSE) {
            builder.advanceLexer();
        } else if (tokenType == Jinja2TokenTypes.NONE) {
            builder.advanceLexer();
        } else if (tokenType == Jinja2TokenTypes.LPAREN) {
            // Parenthesized expression or tuple
            parseTupleOrParenthesized(builder);
        } else if (tokenType == Jinja2TokenTypes.LBRACKET) {
            // List literal
            parseListLiteral(builder);
        } else if (tokenType == Jinja2TokenTypes.LBRACE) {
            // Dictionary literal
            parseDictLiteral(builder);
        } else {
            builder.error("Expected expression");
            builder.advanceLexer(); // Skip unknown token
        }
    }

    // Helper methods for parsing complex constructs
    private void parseTargetList(PsiBuilder builder) {
        // Parse comma-separated list of identifiers (assignment targets)
        if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
            builder.advanceLexer();
            skipWhitespace(builder);
            
            while (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                builder.advanceLexer();
                skipWhitespace(builder);
                
                if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                } else {
                    break;
                }
            }
        }
    }

    private void parseWithContextList(PsiBuilder builder) {
        // Parse with context assignments: var = expr, var2 = expr2
        parseExpression(builder);
        skipWhitespace(builder);
        
        while (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
            builder.advanceLexer();
            skipWhitespace(builder);
            parseExpression(builder);
            skipWhitespace(builder);
        }
    }

    private void parseImportNameList(PsiBuilder builder) {
        // Parse import list: name1, name2 as alias2, name3
        if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
            builder.advanceLexer();
            skipWhitespace(builder);
            
            if (builder.getTokenType() == Jinja2TokenTypes.AS) {
                builder.advanceLexer();
                skipWhitespace(builder);
                if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                }
            }
            
            while (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                builder.advanceLexer();
                skipWhitespace(builder);
                
                if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                    
                    if (builder.getTokenType() == Jinja2TokenTypes.AS) {
                        builder.advanceLexer();
                        skipWhitespace(builder);
                        if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                            builder.advanceLexer();
                            skipWhitespace(builder);
                        }
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void parseParameterList(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // consume (
        skipWhitespace(builder);

        // Parse parameters: name, name=default, *args, **kwargs
        while (!builder.eof() && builder.getTokenType() != Jinja2TokenTypes.RPAREN) {
            if (builder.getTokenType() == Jinja2TokenTypes.IDENTIFIER) {
                builder.advanceLexer();
                skipWhitespace(builder);
                
                // Check for default value
                if (builder.getTokenType() == Jinja2TokenTypes.ASSIGN) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                    parseExpression(builder);
                    skipWhitespace(builder);
                }
            }

            if (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                builder.advanceLexer();
                skipWhitespace(builder);
            } else if (builder.getTokenType() != Jinja2TokenTypes.RPAREN) {
                builder.error("Expected ',' or ')'");
                break;
            }
        }

        if (builder.getTokenType() == Jinja2TokenTypes.RPAREN) {
            builder.advanceLexer();
        } else {
            builder.error("Expected ')'");
        }
        
        marker.done(Jinja2ElementTypes.ARGUMENT_LIST);
    }

    private void parseArgumentList(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // consume (
        skipWhitespace(builder);

        // Parse arguments: expr, name=expr, *expr, **expr
        while (!builder.eof() && builder.getTokenType() != Jinja2TokenTypes.RPAREN) {
            parseExpression(builder);
            skipWhitespace(builder);

            if (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                builder.advanceLexer();
                skipWhitespace(builder);
            } else if (builder.getTokenType() != Jinja2TokenTypes.RPAREN) {
                builder.error("Expected ',' or ')'");
                break;
            }
        }

        if (builder.getTokenType() == Jinja2TokenTypes.RPAREN) {
            builder.advanceLexer();
        } else {
            builder.error("Expected ')'");
        }
        
        marker.done(Jinja2ElementTypes.ARGUMENT_LIST);
    }

    private void parseTupleOrParenthesized(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // consume (
        skipWhitespace(builder);

        if (builder.getTokenType() == Jinja2TokenTypes.RPAREN) {
            // Empty tuple
            builder.advanceLexer();
            marker.done(Jinja2ElementTypes.TUPLE_LITERAL);
            return;
        }

        parseExpression(builder);
        skipWhitespace(builder);

        if (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
            // Tuple with multiple elements
            builder.advanceLexer();
            skipWhitespace(builder);
            
            while (builder.getTokenType() != Jinja2TokenTypes.RPAREN && !builder.eof()) {
                parseExpression(builder);
                skipWhitespace(builder);
                
                if (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                } else {
                    break;
                }
            }
            
            if (builder.getTokenType() == Jinja2TokenTypes.RPAREN) {
                builder.advanceLexer();
            } else {
                builder.error("Expected ')'");
            }
            
            marker.done(Jinja2ElementTypes.TUPLE_LITERAL);
        } else {
            // Single parenthesized expression
            if (builder.getTokenType() == Jinja2TokenTypes.RPAREN) {
                builder.advanceLexer();
            } else {
                builder.error("Expected ')'");
            }
            
            marker.drop();
        }
    }

    private void parseListLiteral(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // consume [
        skipWhitespace(builder);

        // Parse list elements
        while (!builder.eof() && builder.getTokenType() != Jinja2TokenTypes.RBRACKET) {
            parseExpression(builder);
            skipWhitespace(builder);

            if (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                builder.advanceLexer();
                skipWhitespace(builder);
            } else if (builder.getTokenType() != Jinja2TokenTypes.RBRACKET) {
                builder.error("Expected ',' or ']'");
                break;
            }
        }

        if (builder.getTokenType() == Jinja2TokenTypes.RBRACKET) {
            builder.advanceLexer();
        } else {
            builder.error("Expected ']'");
        }

        marker.done(Jinja2ElementTypes.LIST_LITERAL);
    }

    private void parseDictLiteral(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // consume {
        skipWhitespace(builder);

        // Parse dictionary entries
        while (!builder.eof() && builder.getTokenType() != Jinja2TokenTypes.RBRACE) {
            parseExpression(builder); // key
            skipWhitespace(builder);

            if (builder.getTokenType() == Jinja2TokenTypes.COLON) {
                builder.advanceLexer();
                skipWhitespace(builder);
                parseExpression(builder); // value
                skipWhitespace(builder);

                if (builder.getTokenType() == Jinja2TokenTypes.COMMA) {
                    builder.advanceLexer();
                    skipWhitespace(builder);
                } else if (builder.getTokenType() != Jinja2TokenTypes.RBRACE) {
                    builder.error("Expected ',' or '}'");
                    break;
                }
            } else {
                builder.error("Expected ':'");
                break;
            }
        }

        if (builder.getTokenType() == Jinja2TokenTypes.RBRACE) {
            builder.advanceLexer();
        } else {
            builder.error("Expected '}'");
        }

        marker.done(Jinja2ElementTypes.DICT_LITERAL);
    }

    private void skipWhitespace(PsiBuilder builder) {
        while (builder.getTokenType() == Jinja2TokenTypes.WHITESPACE) {
            builder.advanceLexer();
        }
    }

    private void consumeBlockEnd(PsiBuilder builder) {
        skipWhitespace(builder);
        if (builder.getTokenType() == Jinja2TokenTypes.BLOCK_END) {
            builder.advanceLexer();
        } else {
            builder.error("Expected block end delimiter");
            // Error recovery: consume tokens until block end or EOF
            while (!builder.eof() && builder.getTokenType() != Jinja2TokenTypes.BLOCK_END) {
                builder.advanceLexer();
            }
            if (builder.getTokenType() == Jinja2TokenTypes.BLOCK_END) {
                builder.advanceLexer();
            }
        }
    }

    private void recoverFromError(PsiBuilder builder) {
        // Skip tokens until we find a delimiter or EOF
        while (!builder.eof()) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == Jinja2TokenTypes.BLOCK_START ||
                tokenType == Jinja2TokenTypes.VARIABLE_START ||
                tokenType == Jinja2TokenTypes.COMMENT_START) {
                break;
            }
            builder.advanceLexer();
        }
    }
}