# Jinja2 Custom Delimiters

A PyCharm/IntelliJ IDEA plugin that allows you to configure custom Jinja2 template delimiters while maintaining full IDE language support including syntax highlighting, code completion, formatting, and refactoring capabilities.

## Why This Plugin?

Jinja2's default delimiters (`{{`, `{%`, `{#`) can conflict with other template engines or frontend frameworks. This plugin lets you use custom delimiters (like `<<`, `<#`, `/*`) while preserving all of PyCharm's powerful Jinja2 features.

## Features

### 🎨 **Custom Delimiter Configuration**
- **Variable delimiters**: `{{ variable }}` → `<< variable >>`
- **Block delimiters**: `{% for %}` → `<# for #>`
- **Comment delimiters**: `{# comment #}` → `/* comment */`
- **Line prefixes**: Enable line-based syntax (`# for item in items`)

### 🧠 **Full IDE Language Support**
- **Syntax highlighting** with proper color coding for delimiters, keywords, strings, and expressions
- **Code completion** for Jinja2 keywords, filters, and functions
- **Error detection** and syntax validation
- **Code formatting** with proper indentation and structure
- **Find usages** and navigation for variables and macros
- **Refactoring support** including variable renaming

### ⚡ **Advanced Features**
- **Extract macro intention**: Convert selected Jinja2 code into reusable macros
- **Live templates** for common Jinja2 patterns
- **Performance optimization** with intelligent caching
- **Hot reload**: Refresh delimiters without restarting IDE

<!-- Plugin description -->
Allows configuration of custom Jinja2 template delimiters while maintaining
PyCharm's full Jinja2 language support including syntax highlighting,
code completion, and formatting.
<!-- Plugin description end -->

## Installation

### From JetBrains Marketplace (Recommended)
1. Open PyCharm/IntelliJ IDEA
2. Go to <kbd>File</kbd> → <kbd>Settings</kbd> → <kbd>Plugins</kbd>
3. Click <kbd>Marketplace</kbd> tab
4. Search for "Jinja2 Custom Delimiters"
5. Click <kbd>Install</kbd>

### Manual Installation
1. Download the latest plugin ZIP from [Releases](https://github.com/lesleslie/jinja2-custom-delimiters/releases)
2. In PyCharm: <kbd>File</kbd> → <kbd>Settings</kbd> → <kbd>Plugins</kbd> → <kbd>⚙️</kbd> → <kbd>Install Plugin from Disk...</kbd>
3. Select the downloaded ZIP file

## Quick Start

### 1. Configure Delimiters
After installation, configure your custom delimiters:

1. Go to <kbd>File</kbd> → <kbd>Settings</kbd> → <kbd>Languages & Frameworks</kbd> → <kbd>Jinja2 Custom Delimiters</kbd>
2. Set your preferred delimiters:
   ```
   Variable Start: <<        Variable End: >>
   Block Start:    <%        Block End:    %>
   Comment Start:  /*        Comment End:  */
   ```
3. Click <kbd>Apply</kbd>

### 2. Create Template Files
Create files with supported extensions (`.j2`, `.jinja2`, `.html.j2`):

```html
<!-- Instead of default Jinja2 -->
{{ user.name }}
{% for item in items %}
  {# This is a comment #}
{% endfor %}

<!-- Use your custom delimiters -->
<< user.name >>
<% for item in items %>
  /* This is a comment */
<% endfor %>
```

### 3. Refresh (If Needed)
If delimiters don't update immediately:
- Use <kbd>Tools</kbd> → <kbd>Refresh Jinja2 Delimiters</kbd>
- Or restart the IDE

## Advanced Usage

### Line-Based Syntax
Enable line-based delimiters for cleaner templates:

```
Line Statement Prefix: #
Line Comment Prefix:   ##
```

Then use:
```html
# for item in items
  << item.name >>
  ## This is a line comment
# endfor
```

### Extract Macro Refactoring
1. Select Jinja2 code in your template
2. Press <kbd>Alt</kbd> + <kbd>Enter</kbd>
3. Choose "Extract Jinja2 macro"
4. The plugin will create a macro and replace the selection with a macro call

### Supported File Types
The plugin recognizes these file extensions:
- `.j2` - Standard Jinja2 templates
- `.jinja2` - Explicit Jinja2 templates
- `.html.j2` - HTML templates with Jinja2

## Use Cases

### 🔧 **Avoiding Conflicts**
When using Jinja2 with:
- **Vue.js/Angular**: Use `<< >>` instead of `{{ }}` to avoid frontend framework conflicts
- **Liquid templates**: Use different delimiters to distinguish template engines
- **Custom build systems**: Match your organization's template conventions

### 🎯 **Framework Integration**
Perfect for:
- **Django projects** with custom template configurations
- **Flask applications** with non-standard Jinja2 setups
- **Static site generators** with custom delimiter requirements
- **Multi-template environments** where different delimiters clarify intent

## Development

### Building from Source
```bash
git clone https://github.com/lesleslie/jinja2-custom-delimiters.git
cd jinja2-custom-delimiters
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Development IDE Setup
```bash
./gradlew runIde
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## Support

- **Issues**: [GitHub Issues](https://github.com/lesleslie/jinja2-custom-delimiters/issues)
- **Feature Requests**: [GitHub Discussions](https://github.com/lesleslie/jinja2-custom-delimiters/discussions)
- **Email**: les@wedgwoodwebworks.com

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Developed by** [Wedgwood Web Works](https://wedgwoodwebworks.com)

Built with the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
