<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# jinja2-custom-delimiters Changelog

## [Unreleased]

## [0.3.0] - 2025-10-10

### Changed

- **MAJOR ARCHITECTURAL SIMPLIFICATION: Removed custom language implementation**
  - Removed entire custom language infrastructure (lang/, lexer/, parser/, psi/, highlighting/, etc.)
  - Plugin now works directly with PyCharm's built-in Jinja2 language
  - Reduced from dozens of files to just 4 Java source files
  - Simplified approach: Pre/PostFormatProcessors handle delimiter conversion
  - Zero maintenance burden - automatically benefits from PyCharm's Jinja2 updates

- **Replaced custom formatter with PyCharm Professional's Jinja2 formatter delegation**
  - Removed 620 lines of buggy custom formatting code
  - Perfect formatting now leverages PyCharm Pro's battle-tested Jinja2 formatter
  - Resolves all spacing and indentation issues

### Added

- File type icons (16x16 SVG) for `.j2`, `.jinja2`, and `.html.j2` files
- Improved plugin icon (40x40 SVG) meeting IntelliJ Platform specifications
- Documentation clarifying PyCharm Professional requirement for formatting feature
- Troubleshooting guide for common issues

### Fixed

- Plugin icon dimensions (was 16x16, now correct 40x40)
- File type icon missing in Settings → File Types
- Documentation accuracy regarding feature availability by edition
- Formatter now works correctly with Jinja2 language files

### Removed

- Custom language implementation (CustomJinja2Language)
- Custom lexer, parser, and PSI elements
- Custom syntax highlighter (trade-off for reliable formatting)
- Find usages, refactoring, live templates, intentions, and caching features
- Refresh delimiters action (no longer needed)

## [0.2.0] - 2025-10-09

### Added

- Complete plugin.xml extension registrations (settings, language, highlighting, formatting, etc.)
- Thread-safe settings system with synchronized getters/setters
- LRU-based caching service with automatic memory management (1000 entry limit per cache)
- Intention action descriptions for Extract Jinja2 Macro refactoring
- Improved settings UI with better spacing and visual hierarchy

### Fixed

- Package naming consistency across all Java files
- Memory leak in caching service (unbounded cache growth)
- Thread-safety issues in settings access
- All critical blockers preventing plugin functionality

## [0.1.1] - 2025-10-09

### Added

- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

[Unreleased]: https://github.com/lesleslie/jinja2-custom-delimiters/compare/v0.3.0...HEAD
[0.3.0]: https://github.com/lesleslie/jinja2-custom-delimiters/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/lesleslie/jinja2-custom-delimiters/compare/v0.1.1...v0.2.0
[0.1.1]: https://github.com/lesleslie/jinja2-custom-delimiters/commits/v0.1.1
