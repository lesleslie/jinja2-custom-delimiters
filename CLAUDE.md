# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IntelliJ IDEA/PyCharm plugin that enables custom Jinja2 template delimiters while maintaining code formatting support. The plugin uses a simplified architecture with Pre/PostFormatProcessors that convert custom delimiters before and after PyCharm Professional's built-in Jinja2 formatter runs.

**Current Status:** Plugin builds successfully and is functional (v0.3.0). Major architectural simplification completed - reduced from dozens of files to just 4 Java source files. Formatting works correctly with custom delimiters preserved.

**Trade-offs:** This simplified approach sacrifices custom syntax highlighting in favor of reliable code formatting. Files must be recognized as "Jinja 2 Template" language for formatting to work.

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
- **Java Version**: 21

## Architecture

### Simplified Design (v0.3.0)

The plugin uses a minimal architecture with just 4 Java source files:

#### 1. Settings System (`com.wedgwoodwebworks.jinja2customdelimiters.settings`)

**Thread-Safe Design:**
- **`Jinja2DelimitersSettings`**: Application-level persistent state component with thread-safe getters/setters
  - Uses `volatile` fields for thread-safe publication
  - Synchronized getters/setters prevent race conditions
  - Provides `@NotNull` defaults for all delimiter strings
  - All field access MUST use getters/setters, never direct field access

- **`Jinja2DelimitersConfigurable`**: Settings UI panel (Settings → Languages → Jinja2 Custom Delimiters)
  - Uses getters/setters to interact with settings
  - Provides input validation and UI components

**Important:** When accessing settings, always use:
```java
settings.getBlockStartString()  // ✓ Correct
settings.blockStartString       // ✗ Wrong - not thread-safe
```

#### 2. Format Processors (`com.wedgwoodwebworks.jinja2customdelimiters.formatting`)

- **`CustomJinja2PreFormatProcessor`**: Converts custom delimiters to standard Jinja2 delimiters before formatting
  - Checks if file language is "Jinja2"
  - Converts `[%` → `{%`, `[[` → `{{`, `[#` → `{#` (or whatever custom delimiters are configured)
  - Preserves whitespace control characters (`-`, `+`)
  - Returns adjusted TextRange after conversion

- **`CustomJinja2PostFormatProcessor`**: Converts standard delimiters back to custom after formatting
  - Runs after PyCharm's Jinja2 formatter completes
  - Converts `{%` → `[%`, `{{` → `[[`, `{#` → `[#` (or configured delimiters)
  - Ensures custom delimiters are preserved in final output

### How It Works

1. User has file with custom delimiters (e.g., `[%` instead of `{%`)
2. File must be recognized as **Jinja2** language (not a custom language)
3. When user formats (Cmd/Ctrl+Alt+L):
   - **PreFormatProcessor** converts custom delimiters to standard in document
   - **PyCharm's Jinja2 formatter** formats the code (sees standard delimiters)
   - **PostFormatProcessor** converts standard delimiters back to custom
4. Result: Perfect formatting with custom delimiters preserved

### What Was Removed (v0.3.0)

To achieve reliable formatting, the following were removed:
- ❌ Custom language implementation (CustomJinja2Language)
- ❌ Custom lexer and parser
- ❌ Custom PSI elements
- ❌ Custom syntax highlighter
- ❌ Custom FormattingModelBuilder
- ❌ Find usages, refactoring, live templates, intentions
- ❌ Caching service
- ❌ Refresh delimiters action

**Trade-off:** No custom syntax highlighting, but reliable code formatting that automatically benefits from PyCharm's Jinja2 formatter improvements.

## Extension Point Registration

The plugin registers components in `plugin.xml` (src/main/resources/META-INF/plugin.xml):

```xml
<extensions defaultExtensionNs="com.intellij">
    <!-- Settings -->
    <applicationConfigurable
        parentId="language"
        instance="com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersConfigurable"
        displayName="Jinja2 Custom Delimiters"/>

    <applicationService
        serviceImplementation="com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings"/>

    <!-- Code Formatting -->
    <preFormatProcessor
        implementation="com.wedgwoodwebworks.jinja2customdelimiters.formatting.CustomJinja2PreFormatProcessor"/>
    <postFormatProcessor
        implementation="com.wedgwoodwebworks.jinja2customdelimiters.formatting.CustomJinja2PostFormatProcessor"/>
</extensions>
```

**That's it!** The simplified plugin only needs these 4 extensions.

## Package Structure (Simplified)

```
com.wedgwoodwebworks.jinja2customdelimiters/
├── formatting/        # Pre/PostFormatProcessors (2 files)
└── settings/          # Configuration management (2 files)
```

**Total: 4 Java source files**

## Key Implementation Details

### Thread-Safety Requirements

**Settings Access Pattern:**
```java
// Always use getters
Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
String blockStart = settings.getBlockStartString();  // ✓ Thread-safe

// Never access fields directly
String blockStart = settings.blockStartString;  // ✗ Not thread-safe
```

**Why this matters:**
- Settings can be modified from UI thread (settings dialog)
- Settings can be read from background threads (lexing, parsing)
- Direct field access creates race conditions
- Getters/setters provide synchronization

### Delimiter Conversion Logic

The processors use a simple string replacement approach with whitespace control preservation:

```java
// In CustomJinja2PreFormatProcessor
private String replaceWithWhitespaceControl(String text, String customDelim, String standardDelim) {
    if (customDelim.equals(standardDelim)) {
        return text; // No conversion needed
    }

    String result = text;

    // For opening delimiters (e.g., "[%" → "{%")
    if (standardDelim.equals("{%") || standardDelim.equals("{{") || standardDelim.equals("{#")) {
        // Handle: [%-, [%+, [%
        result = result.replace(customDelim + "-", standardDelim + "-");
        result = result.replace(customDelim + "+", standardDelim + "+");
        result = result.replace(customDelim, standardDelim);
    }
    // For closing delimiters (e.g., "%]" → "%}")
    else if (standardDelim.equals("%}") || standardDelim.equals("}}") || standardDelim.equals("#}")) {
        // Handle: -%], +%], %]
        result = result.replace("-" + customDelim, "-" + standardDelim);
        result = result.replace("+" + customDelim, "+" + standardDelim);
        result = result.replace(customDelim, standardDelim);
    }

    return result;
}
```

**Process:**
1. User updates settings in UI
2. Settings are persisted
3. Next time user formats, processors read current settings
4. Custom delimiters converted to standard, then back after formatting

### Integration with PyCharm

The plugin leverages PyCharm's existing Jinja2 support:
- Depends on `com.intellij.modules.python` and `Pythonid` modules
- Files must be recognized as "Jinja2" language (not custom language)
- Pre/PostFormatProcessors intercept PyCharm's formatting workflow
- No custom PSI elements - uses PyCharm's Jinja2 PSI directly

## Testing Strategy

### Manual Testing (Primary Method)

**Running Plugin in Sandbox:**
```bash
./gradlew runIde  # Opens IDE sandbox with plugin installed
```

**Testing Steps:**
1. **Configure Settings**: Settings → Languages & Frameworks → Jinja2 Custom Delimiters
   - Set block delimiters: `[%` and `%]`
   - Set variable delimiters: `[[` and `]]`
   - Set comment delimiters: `[#` and `#]`

2. **Create Test File**: Use `.j2` or `.jinja2` extension
   - Or configure `.html` files to be recognized as "Jinja 2 Template" in File Types

3. **Test Formatting**:
   - Write unformatted Jinja2 code with custom delimiters
   - Press Cmd/Ctrl+Alt+L
   - Verify code is formatted correctly
   - Verify custom delimiters are preserved

**Unit Tests:**
Most tests from the previous custom language implementation have been removed. The simplified plugin has minimal testable surface area:
- Settings persistence (still works)
- Delimiter conversion logic (could add unit tests)

**Manual Testing Recommended** because:
- Pre/PostFormatProcessors require full IDE context
- Formatter integration is best tested in live IDE
- Only 4 source files to maintain

## Common Development Patterns

### Adding a New Delimiter Type

1. Add fields to `Jinja2DelimitersSettings`:
```java
public volatile String newDelimiterStart = "{*";
public volatile String newDelimiterEnd = "*}";

@NotNull
public String getNewDelimiterStart() {
    return newDelimiterStart != null ? newDelimiterStart : "{*";
}

public synchronized void setNewDelimiterStart(@NotNull String value) {
    this.newDelimiterStart = value;
}
```

2. Add UI fields to `Jinja2DelimitersConfigurable`

3. Update conversion logic in both `CustomJinja2PreFormatProcessor` and `CustomJinja2PostFormatProcessor`

### Modifying Settings

Always follow this pattern:
```java
Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
settings.setBlockStartString("<%");  // Use setter
settings.setBlockEndString("%>");

// Settings automatically picked up on next format operation
```

### Debugging Format Processors

Enable IDE logging to see processor activity:
1. Help → Diagnostic Tools → Debug Log Settings
2. Add: `#com.wedgwoodwebworks.jinja2customdelimiters`
3. Check idea.log for processor execution

## Known Limitations

1. **Syntax Highlighting:** Files with custom delimiters won't have full syntax highlighting until formatted at least once, as PyCharm's lexer expects standard Jinja2 delimiters. This is an acceptable trade-off for reliable formatting.

2. **File Type Recognition:** Files must be recognized as "Jinja2" language for formatting to work:
   - Use `.j2`, `.jinja2`, `.jinja` extensions for automatic recognition
   - OR manually configure file patterns in Settings → Editor → File Types → Jinja 2 Template

3. **Settings Scope:** Settings are application-level only (no per-project settings)

4. **PyCharm Professional Required:** Code formatting only works in PyCharm Professional or IntelliJ IDEA Ultimate, as Community editions don't include the Jinja2 formatter

## Architecture Evolution

### Version 0.3.0 (Current) - Simplified Approach
- ✅ **Removed custom language implementation** (dozens of files)
- ✅ **Just 4 Java source files** remaining
- ✅ **Reliable formatting** using PyCharm's built-in Jinja2 formatter
- ✅ **Zero maintenance burden** for language features
- ❌ **No custom syntax highlighting** (trade-off accepted)

### Version 0.2.0 (Previous) - Custom Language Approach
- ❌ Custom language, lexer, parser, PSI elements
- ❌ Custom syntax highlighter
- ❌ Custom formatter (buggy, 620 lines)
- ❌ Many edge cases and maintenance burden

### Why We Simplified
The custom language approach was complex and unreliable. The simplified approach:
- Works with PyCharm's existing Jinja2 support
- Automatically benefits from PyCharm's formatter improvements
- Much easier to maintain (4 files vs dozens)
- Reliable formatting is more important than syntax highlighting for most users

## Resources

- **README.md**: User documentation and installation instructions
- **CRITICAL-AUDIT-REPORT.md**: Detailed audit findings and recommendations
- **IntelliJ Platform SDK**: https://plugins.jetbrains.com/docs/intellij/
- **Plugin Template**: https://github.com/JetBrains/intellij-platform-plugin-template
