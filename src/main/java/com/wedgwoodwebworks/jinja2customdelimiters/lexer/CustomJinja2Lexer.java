package com.wedgwoodwebworks.jinja2delimiters.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.wedgwoodwebworks.jinja2delimiters.settings.Jinja2DelimitersSettings;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CustomJinja2Lexer extends LexerBase {

    // Lexer states
    private static final int TEMPLATE_STATE = 0;
    private static final int BLOCK_STATE = 1;
    private static final int VARIABLE_STATE = 2;
    private static final int COMMENT_STATE = 3;
    private static final int RAW_STATE = 4;
    private static final int LINE_STATEMENT_STATE = 5;
    private static final int LINE_COMMENT_STATE = 6;

    private final Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
    private final Map<String, IElementType> keywords = new HashMap<>();
    
    private CharSequence buffer;
    private int bufferEnd;
    private int currentOffset;
    private int tokenStart;
    private int tokenEnd;
    private IElementType currentTokenType;
    private int state = TEMPLATE_STATE;
    private boolean isLineStart = true;

    public CustomJinja2Lexer() {
        initKeywords();
    }

    private void initKeywords() {
        // Control flow keywords
        keywords.put("for", Jinja2TokenTypes.FOR);
        keywords.put("endfor", Jinja2TokenTypes.ENDFOR);
        keywords.put("if", Jinja2TokenTypes.IF);
        keywords.put("elif", Jinja2TokenTypes.ELIF);
        keywords.put("else", Jinja2TokenTypes.ELSE);
        keywords.put("endif", Jinja2TokenTypes.ENDIF);
        keywords.put("set", Jinja2TokenTypes.SET);
        keywords.put("with", Jinja2TokenTypes.WITH);
        keywords.put("endwith", Jinja2TokenTypes.ENDWITH);
        
        // Template structure
        keywords.put("block", Jinja2TokenTypes.BLOCK);
        keywords.put("endblock", Jinja2TokenTypes.ENDBLOCK);
        keywords.put("extends", Jinja2TokenTypes.EXTENDS);
        keywords.put("include", Jinja2TokenTypes.INCLUDE);
        keywords.put("import", Jinja2TokenTypes.IMPORT);
        keywords.put("from", Jinja2TokenTypes.FROM);
        keywords.put("as", Jinja2TokenTypes.AS);
        
        // Macros and functions
        keywords.put("macro", Jinja2TokenTypes.MACRO);
        keywords.put("endmacro", Jinja2TokenTypes.ENDMACRO);
        keywords.put("call", Jinja2TokenTypes.CALL);
        keywords.put("endcall", Jinja2TokenTypes.ENDCALL);
        
        // Advanced features
        keywords.put("filter", Jinja2TokenTypes.FILTER);
        keywords.put("endfilter", Jinja2TokenTypes.ENDFILTER);
        keywords.put("raw", Jinja2TokenTypes.RAW);
        keywords.put("endraw", Jinja2TokenTypes.ENDRAW);
        keywords.put("autoescape", Jinja2TokenTypes.AUTOESCAPE);
        keywords.put("endautoescape", Jinja2TokenTypes.ENDAUTOESCAPE);
        keywords.put("trans", Jinja2TokenTypes.TRANS);
        keywords.put("endtrans", Jinja2TokenTypes.ENDTRANS);
        keywords.put("pluralize", Jinja2TokenTypes.PLURALIZE);
        
        // Logic keywords
        keywords.put("in", Jinja2TokenTypes.IN);
        keywords.put("is", Jinja2TokenTypes.IS);
        keywords.put("not", Jinja2TokenTypes.NOT);
        keywords.put("and", Jinja2TokenTypes.AND);
        keywords.put("or", Jinja2TokenTypes.OR);
        
        // Special keywords
        keywords.put("recursive", Jinja2TokenTypes.RECURSIVE);
        keywords.put("scoped", Jinja2TokenTypes.SCOPED);
        keywords.put("ignore", Jinja2TokenTypes.IGNORE);
        keywords.put("missing", Jinja2TokenTypes.MISSING);
        keywords.put("without", Jinja2TokenTypes.WITHOUT);
        keywords.put("context", Jinja2TokenTypes.CONTEXT);
        
        // Literals
        keywords.put("true", Jinja2TokenTypes.BOOLEAN_TRUE);
        keywords.put("false", Jinja2TokenTypes.BOOLEAN_FALSE);
        keywords.put("none", Jinja2TokenTypes.NONE);
    }

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.currentOffset = startOffset;
        this.bufferEnd = endOffset;
        this.tokenStart = startOffset;
        this.tokenEnd = startOffset;
        this.currentTokenType = null;
        this.state = initialState;
        this.isLineStart = startOffset == 0 || (startOffset > 0 && buffer.charAt(startOffset - 1) == '\n');
        advance();
    }

    @Override
    public int getState() {
        return state;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return tokenStart;
    }

    @Override
    public int getTokenEnd() {
        return tokenEnd;
    }

    @Override
    public void advance() {
        if (currentOffset >= bufferEnd) {
            currentTokenType = null;
            return;
        }

        tokenStart = currentOffset;
        
        switch (state) {
            case TEMPLATE_STATE:
                advanceInTemplate();
                break;
            case BLOCK_STATE:
                advanceInBlock();
                break;
            case VARIABLE_STATE:
                advanceInVariable();
                break;
            case COMMENT_STATE:
                advanceInComment();
                break;
            case RAW_STATE:
                advanceInRaw();
                break;
            case LINE_STATEMENT_STATE:
                advanceInLineStatement();
                break;
            case LINE_COMMENT_STATE:
                advanceInLineComment();
                break;
            default:
                advanceInTemplate();
        }
        
        // Update line start status
        if (currentTokenType == Jinja2TokenTypes.NEWLINE) {
            isLineStart = true;
        } else if (currentTokenType != Jinja2TokenTypes.WHITESPACE) {
            isLineStart = false;
        }
    }

    private void advanceInTemplate() {
        // Check for line-based syntax at start of line
        if (isLineStart) {
            if (settings.lineStatementPrefix != null && !settings.lineStatementPrefix.isEmpty() &&
                matchesString(settings.lineStatementPrefix)) {
                currentTokenType = Jinja2TokenTypes.BLOCK_START;
                state = LINE_STATEMENT_STATE;
                return;
            }
            if (settings.lineCommentPrefix != null && !settings.lineCommentPrefix.isEmpty() &&
                matchesString(settings.lineCommentPrefix)) {
                currentTokenType = Jinja2TokenTypes.COMMENT_START;
                state = LINE_COMMENT_STATE;
                return;
            }
        }
        
        // Check for block delimiters
        if (matchesDelimiter(settings.blockStartString)) {
            currentTokenType = Jinja2TokenTypes.BLOCK_START;
            state = BLOCK_STATE;
            return;
        }
        
        // Check for variable delimiters
        if (matchesDelimiter(settings.variableStartString)) {
            currentTokenType = Jinja2TokenTypes.VARIABLE_START;
            state = VARIABLE_STATE;
            return;
        }
        
        // Check for comment delimiters
        if (matchesDelimiter(settings.commentStartString)) {
            currentTokenType = Jinja2TokenTypes.COMMENT_START;
            state = COMMENT_STATE;
            return;
        }
        
        // Handle newlines
        if (peek() == '\n') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.NEWLINE;
            return;
        }
        
        // Consume template text until next delimiter or newline
        while (currentOffset < bufferEnd) {
            char c = peek();
            if (c == '\n') break;
            
            // Check if we're about to hit a delimiter
            if (matchesDelimiter(settings.blockStartString, false) ||
                matchesDelimiter(settings.variableStartString, false) ||
                matchesDelimiter(settings.commentStartString, false)) {
                break;
            }
            
            consumeChar();
        }
        
        currentTokenType = Jinja2TokenTypes.TEXT;
    }

    private void advanceInBlock() {
        // Check for end delimiter
        if (matchesDelimiter(settings.blockEndString)) {
            currentTokenType = Jinja2TokenTypes.BLOCK_END;
            state = TEMPLATE_STATE;
            return;
        }
        
        skipWhitespace();
        if (tokenStart != currentOffset) {
            currentTokenType = Jinja2TokenTypes.WHITESPACE;
            return;
        }
        
        // Handle specific tokens
        char c = peek();
        
        // Operators (multi-character first)
        if (matchesString("==")) {
            currentTokenType = Jinja2TokenTypes.EQ;
        } else if (matchesString("!=")) {
            currentTokenType = Jinja2TokenTypes.NE;
        } else if (matchesString("<=")) {
            currentTokenType = Jinja2TokenTypes.LE;
        } else if (matchesString(">=")) {
            currentTokenType = Jinja2TokenTypes.GE;
        } else if (matchesString("//")) {
            currentTokenType = Jinja2TokenTypes.FLOOR_DIVIDE;
        } else if (matchesString("**")) {
            currentTokenType = Jinja2TokenTypes.POWER;
        } else if (c == '=') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.ASSIGN;
        } else if (c == '+') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.PLUS;
        } else if (c == '-') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.MINUS;
        } else if (c == '*') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.MULTIPLY;
        } else if (c == '/') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.DIVIDE;
        } else if (c == '%') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.MODULO;
        } else if (c == '<') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.LT;
        } else if (c == '>') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.GT;
        } else if (c == '|') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.PIPE;
        } else if (c == '~') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.TILDE;
        } else if (c == '(') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.LPAREN;
        } else if (c == ')') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.RPAREN;
        } else if (c == '[') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.LBRACKET;
        } else if (c == ']') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.RBRACKET;
        } else if (c == '{') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.LBRACE;
        } else if (c == '}') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.RBRACE;
        } else if (c == '.') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.DOT;
        } else if (c == ',') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.COMMA;
        } else if (c == ':') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.COLON;
        } else if (c == ';') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.SEMICOLON;
        } else if (c == '?') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.QUESTION;
        } else if (c == '"' || c == '\'') {
            scanString();
        } else if (Character.isDigit(c)) {
            scanNumber();
        } else if (Character.isLetter(c) || c == '_') {
            scanKeywordOrIdentifier();
        } else {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.BAD_CHARACTER;
        }
    }

    private void advanceInVariable() {
        // Check for end delimiter
        if (matchesDelimiter(settings.variableEndString)) {
            currentTokenType = Jinja2TokenTypes.VARIABLE_END;
            state = TEMPLATE_STATE;
            return;
        }
        
        // Use same logic as block state for expressions
        advanceInBlock();
    }

    private void advanceInComment() {
        // Check for end delimiter
        if (matchesDelimiter(settings.commentEndString)) {
            currentTokenType = Jinja2TokenTypes.COMMENT_END;
            state = TEMPLATE_STATE;
            return;
        }
        
        // Consume comment content until end delimiter
        while (currentOffset < bufferEnd) {
            if (matchesDelimiter(settings.commentEndString, false)) {
                break;
            }
            consumeChar();
        }
        
        currentTokenType = Jinja2TokenTypes.TEXT;
    }

    private void advanceInRaw() {
        // Look for endraw block
        if (matchesDelimiter(settings.blockStartString, false)) {
            int saveOffset = currentOffset;
            int saveTokenStart = tokenStart;
            
            // Temporarily advance to check for endraw
            if (matchesDelimiter(settings.blockStartString)) {
                skipWhitespace();
                if (matchesString("endraw")) {
                    // Found endraw, emit text up to this point
                    currentOffset = saveOffset;
                    tokenEnd = currentOffset;
                    if (tokenStart < tokenEnd) {
                        currentTokenType = Jinja2TokenTypes.TEXT;
                        return;
                    } else {
                        // No text, process the endraw block
                        tokenStart = currentOffset;
                        matchesDelimiter(settings.blockStartString);
                        currentTokenType = Jinja2TokenTypes.BLOCK_START;
                        state = BLOCK_STATE;
                        return;
                    }
                }
            }
            
            // Not endraw, restore and continue
            currentOffset = saveOffset;
            tokenStart = saveTokenStart;
        }
        
        // Consume raw content
        consumeChar();
        currentTokenType = Jinja2TokenTypes.TEXT;
    }

    private void advanceInLineStatement() {
        // Line statements end at newline
        if (peek() == '\n') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.NEWLINE;
            state = TEMPLATE_STATE;
            return;
        }
        
        // Use block parsing logic
        advanceInBlock();
    }

    private void advanceInLineComment() {
        // Line comments end at newline
        if (peek() == '\n') {
            consumeChar();
            currentTokenType = Jinja2TokenTypes.NEWLINE;
            state = TEMPLATE_STATE;
            return;
        }
        
        // Consume comment content
        while (currentOffset < bufferEnd && peek() != '\n') {
            consumeChar();
        }
        
        currentTokenType = Jinja2TokenTypes.TEXT;
    }

    private void scanString() {
        char quote = consumeChar(); // Consume opening quote
        
        while (currentOffset < bufferEnd) {
            char c = peek();
            if (c == quote) {
                consumeChar(); // Consume closing quote
                break;
            } else if (c == '\\') {
                consumeChar(); // Consume backslash
                if (currentOffset < bufferEnd) {
                    consumeChar(); // Consume escaped character
                }
            } else {
                consumeChar();
            }
        }
        
        currentTokenType = Jinja2TokenTypes.STRING;
    }

    private void scanNumber() {
        // Consume digits
        while (currentOffset < bufferEnd && Character.isDigit(peek())) {
            consumeChar();
        }
        
        // Check for float
        if (currentOffset < bufferEnd && peek() == '.') {
            if (currentOffset + 1 < bufferEnd && Character.isDigit(buffer.charAt(currentOffset + 1))) {
                consumeChar(); // Consume '.'
                while (currentOffset < bufferEnd && Character.isDigit(peek())) {
                    consumeChar();
                }
                currentTokenType = Jinja2TokenTypes.FLOAT;
                return;
            }
        }
        
        currentTokenType = Jinja2TokenTypes.INTEGER;
    }

    private void scanKeywordOrIdentifier() {
        while (currentOffset < bufferEnd) {
            char c = peek();
            if (Character.isLetterOrDigit(c) || c == '_') {
                consumeChar();
            } else {
                break;
            }
        }
        
        String text = buffer.subSequence(tokenStart, currentOffset).toString();
        currentTokenType = keywords.getOrDefault(text, Jinja2TokenTypes.IDENTIFIER);
        
        // Special handling for raw blocks
        if (currentTokenType == Jinja2TokenTypes.RAW && state == BLOCK_STATE) {
            state = RAW_STATE;
        }
    }

    private void skipWhitespace() {
        while (currentOffset < bufferEnd) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r') {
                consumeChar();
            } else {
                break;
            }
        }
    }

    private boolean matchesDelimiter(String delimiter) {
        return matchesDelimiter(delimiter, true);
    }

    private boolean matchesDelimiter(String delimiter, boolean consume) {
        if (delimiter == null || delimiter.isEmpty()) return false;

        if (currentOffset + delimiter.length() <= bufferEnd) {
            String substr = buffer.subSequence(currentOffset, currentOffset + delimiter.length()).toString();
            if (substr.equals(delimiter)) {
                if (consume) {
                    currentOffset += delimiter.length();
                    tokenEnd = currentOffset;
                }
                return true;
            }
        }
        return false;
    }

    private boolean matchesString(String str) {
        if (currentOffset + str.length() <= bufferEnd) {
            String substr = buffer.subSequence(currentOffset, currentOffset + str.length()).toString();
            if (substr.equals(str)) {
                currentOffset += str.length();
                tokenEnd = currentOffset;
                return true;
            }
        }
        return false;
    }

    private char peek() {
        return currentOffset < bufferEnd ? buffer.charAt(currentOffset) : '\0';
    }

    private char consumeChar() {
        if (currentOffset < bufferEnd) {
            char c = buffer.charAt(currentOffset);
            currentOffset++;
            tokenEnd = currentOffset;
            return c;
        }
        return '\0';
    }

    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return bufferEnd;
    }
}
