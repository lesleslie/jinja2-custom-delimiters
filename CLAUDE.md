# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IntelliJ IDEA/PyCharm plugin that enables custom Jinja2 template delimiters while maintaining full IDE language support. The plugin creates a custom language implementation that dynamically adapts to user-configured delimiters for blocks (`{%`), variables (`{{`), and comments (`{#`).

## Build System

This project uses Gradle with the IntelliJ Platform Plugin development framework.

### Core Commands

```bash
# Build the plugin
./gradlew build

# Run plugin in IDE sandbox for testing
./gradlew runIde

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests "CustomJinja2LexerTest"

# Verify plugin compatibility
./gradlew verifyPlugin

# Build distribution ZIP
./gradlew buildPlugin

# Generate changelog patch
./gradlew patchChangelog
```

### Platform Configuration

- **Target Platform**: PyCharm (`PY`)
- **Platform Version**: 2025.2.1.1
- **Minimum Build**: 252
- **Testing Framework**: JUnit 4.13.2

## Architecture

### Core Components

The plugin follows IntelliJ's language implementation pattern with these key components:

#### 1. Settings System (`com.wedgwoodwebworks.jinja2customdelimiters.settings`)
- **`Jinja2DelimitersSettings`**: Persistent state component storing custom delimiter configurations
- **`Jinja2DelimitersConfigurable`**: UI configuration panel accessible via Settings → Languages → Jinja2 Custom Delimiters

#### 2. Language Definition (`com.wedgwoodwebworks.jinja2customdelimiters.lang`)
- **`CustomJinja2Language`**: Language instance registered as "Jinja2Custom"
- **`CustomJinja2FileType`**: File type for `.j2`, `.jinja2`, `.html.j2` extensions
- **`CustomJinja2ParserDefinition`**: Parser configuration linking lexer and PSI elements

#### 3. Lexical Analysis (`com.wedgwoodwebworks.jinja2customdelimiters.lexer`)
- **`CustomJinja2Lexer`**: Dynamic lexer that adapts to user-configured delimiters
- **`Jinja2TokenTypes`**: Complete token type definitions for Jinja2 syntax

#### 4. Parsing & PSI (`com.wedgwoodwebworks.jinja2customdelimiters.parser`)
- **`CustomJinja2Parser`**: AST builder for Jinja2 syntax
- **`Jinja2ElementTypes`**: PSI element type definitions
- **`CustomJinja2PsiElement`**: Base PSI element implementation

#### 5. IDE Features
- **Syntax Highlighting**: `CustomJinja2SyntaxHighlighter`
- **Code Formatting**: `CustomJinja2FormattingModelBuilder`
- **Find Usages**: `Jinja2FindUsagesProvider`
- **Refactoring**: `Jinja2RenameVariableProcessor`
- **Live Templates**: `Jinja2TemplateContextType`

### Dynamic Delimiter System

The lexer (`CustomJinja2Lexer`) implements state-based parsing with these states:
- `TEMPLATE_STATE`: HTML/text content
- `BLOCK_STATE`: Jinja2 statements (`{% ... %}`)
- `VARIABLE_STATE`: Variable expressions (`{{ ... }}`)
- `COMMENT_STATE`: Comments (`{# ... #}`)
- `RAW_STATE`: Raw blocks (no processing)
- `LINE_STATEMENT_STATE`: Line-based statements
- `LINE_COMMENT_STATE`: Line-based comments

The lexer reads delimiter configurations from `Jinja2DelimitersSettings.getInstance()` and dynamically switches parsing behavior.

## Development Workflow

### Plugin Extension Points

The plugin registers these extensions in `plugin.xml`:
- `applicationConfigurable`: Settings UI
- `applicationService`: Settings persistence
- `fileType`: File type registration
- `lang.parserDefinition`: Parser binding
- `lang.syntaxHighlighterFactory`: Syntax highlighting
- `lang.formatter`: Code formatting

### Testing Strategy

Tests are organized by component:
- `CustomJinja2LexerTest`: Lexer tokenization verification
- `CustomJinja2ParserTest`: Parser AST generation tests
- `Jinja2DelimitersSettingsTest`: Settings persistence tests
- `PluginIntegrationTest`: End-to-end plugin functionality

### Performance Considerations

- **`Jinja2CachingService`**: Caches parsed results to improve performance
- Lexer uses efficient string matching for delimiter detection
- Parser builds minimal AST for syntax highlighting performance

## Package Structure

```
com.wedgwoodwebworks.jinja2customdelimiters/
├── actions/           # User actions (refresh delimiters)
├── findusages/        # Find usages support
├── formatting/        # Code formatting
├── highlighting/      # Syntax highlighting
├── intentions/        # Code intentions
├── lang/              # Core language definition
├── lexer/             # Tokenization
├── liveTemplates/     # Live template support
├── parser/            # AST parsing
├── performance/       # Caching services
├── psi/               # PSI elements
├── refactoring/       # Refactoring support
└── settings/          # Configuration management
```

## Key Implementation Details

### Custom Delimiter Detection
The lexer dynamically reads from settings and adjusts its delimiter matching logic. When settings change, the `RefreshDelimitersAction` can be triggered to reparse all files.

### PSI Element Structure
PSI elements inherit from `CustomJinja2PsiElement` and implement IntelliJ's PSI interfaces for navigation, find usages, and refactoring support.

### Integration with PyCharm
The plugin extends PyCharm's existing Jinja2 support rather than replacing it, allowing seamless integration with Python web frameworks.