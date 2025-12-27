package com.wedgwoodwebworks.jinja2customdelimiters.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.Insets;

public class Jinja2DelimitersConfigurable implements Configurable {

    private JPanel mainPanel;
    private JTextField blockStartField;
    private JTextField blockEndField;
    private JTextField variableStartField;
    private JTextField variableEndField;
    private JTextField commentStartField;
    private JTextField commentEndField;
    private JTextField lineStatementPrefixField;
    private JTextField lineCommentPrefixField;
    private JButton resetDefaultsButton;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Jinja2 Custom Delimiters";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (mainPanel == null) {
            createUI();
        }
        return mainPanel;
    }

    private void createUI() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Block delimiters
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Block Start:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        blockStartField = new JTextField(10);
        mainPanel.add(blockStartField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Block End:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        blockEndField = new JTextField(10);
        mainPanel.add(blockEndField, gbc);

        // Variable delimiters
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Variable Start:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        variableStartField = new JTextField(10);
        mainPanel.add(variableStartField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Variable End:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        variableEndField = new JTextField(10);
        mainPanel.add(variableEndField, gbc);

        // Comment delimiters
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Comment Start:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        commentStartField = new JTextField(10);
        mainPanel.add(commentStartField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Comment End:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        commentEndField = new JTextField(10);
        mainPanel.add(commentEndField, gbc);

        // Line prefixes
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Line Statement Prefix:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lineStatementPrefixField = new JTextField(10);
        mainPanel.add(lineStatementPrefixField, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Line Comment Prefix:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lineCommentPrefixField = new JTextField(10);
        mainPanel.add(lineCommentPrefixField, gbc);

        // Reset button
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        resetDefaultsButton = new JButton("Reset to Defaults");
        resetDefaultsButton.addActionListener(e -> resetToDefaults());
        mainPanel.add(resetDefaultsButton, gbc);

        // Help text
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 5, 0); // Add top spacing
        JTextArea helpText = new JTextArea(
            "Configure custom delimiters for Jinja2 templates.\n" +
            "Leave line prefixes empty to disable line-based syntax.\n" +
            "Code formatting (Cmd/Ctrl+Alt+L) requires PyCharm Professional or IntelliJ IDEA Ultimate.\n" +
            "Changes will take effect after restarting the IDE or refreshing files."
        );
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setFont(helpText.getFont().deriveFont(Font.PLAIN, helpText.getFont().getSize() - 1));
        helpText.setForeground(UIManager.getColor("Label.disabledForeground"));
        mainPanel.add(helpText, gbc);
    }

    private void resetToDefaults() {
        blockStartField.setText("{%");
        blockEndField.setText("%}");
        variableStartField.setText("{{");
        variableEndField.setText("}}");
        commentStartField.setText("{#");
        commentEndField.setText("#}");
        lineStatementPrefixField.setText("");
        lineCommentPrefixField.setText("");
    }

    @Override
    public boolean isModified() {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        return !blockStartField.getText().equals(settings.getBlockStartString()) ||
               !blockEndField.getText().equals(settings.getBlockEndString()) ||
               !variableStartField.getText().equals(settings.getVariableStartString()) ||
               !variableEndField.getText().equals(settings.getVariableEndString()) ||
               !commentStartField.getText().equals(settings.getCommentStartString()) ||
               !commentEndField.getText().equals(settings.getCommentEndString()) ||
               !lineStatementPrefixField.getText().equals(settings.getLineStatementPrefix()) ||
               !lineCommentPrefixField.getText().equals(settings.getLineCommentPrefix());
    }

    @Override
    public void apply() throws ConfigurationException {
        // Validate all delimiter inputs before applying
        validateDelimiter(blockStartField.getText(), "Block Start");
        validateDelimiter(blockEndField.getText(), "Block End");
        validateDelimiter(variableStartField.getText(), "Variable Start");
        validateDelimiter(variableEndField.getText(), "Variable End");
        validateDelimiter(commentStartField.getText(), "Comment Start");
        validateDelimiter(commentEndField.getText(), "Comment End");

        // Validate line prefixes (can be empty)
        validateLinePrefix(lineStatementPrefixField.getText(), "Line Statement Prefix");
        validateLinePrefix(lineCommentPrefixField.getText(), "Line Comment Prefix");

        // Check for overlapping delimiters
        checkOverlappingDelimiters();

        // Apply settings if all validations pass
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        settings.setBlockStartString(blockStartField.getText().trim());
        settings.setBlockEndString(blockEndField.getText().trim());
        settings.setVariableStartString(variableStartField.getText().trim());
        settings.setVariableEndString(variableEndField.getText().trim());
        settings.setCommentStartString(commentStartField.getText().trim());
        settings.setCommentEndString(commentEndField.getText().trim());
        settings.setLineStatementPrefix(lineStatementPrefixField.getText().trim());
        settings.setLineCommentPrefix(lineCommentPrefixField.getText().trim());
    }

    /**
     * Validates a delimiter field to ensure it meets requirements.
     *
     * @param delimiter The delimiter string to validate
     * @param fieldName The name of the field for error messages
     * @throws ConfigurationException if validation fails
     */
    private void validateDelimiter(String delimiter, String fieldName) throws ConfigurationException {
        if (delimiter == null || delimiter.trim().isEmpty()) {
            throw new ConfigurationException(fieldName + " cannot be empty");
        }

        String trimmed = delimiter.trim();

        if (trimmed.length() > 10) {
            throw new ConfigurationException(fieldName + " is too long (max 10 characters)");
        }

        // Check for whitespace-only delimiters
        if (trimmed.isEmpty()) {
            throw new ConfigurationException(fieldName + " cannot be whitespace only");
        }

        // Warn about potentially problematic characters
        if (trimmed.contains("<") || trimmed.contains(">")) {
            // Allow but could warn - angle brackets might conflict with HTML
        }
    }

    /**
     * Validates a line prefix field (can be empty).
     *
     * @param prefix The prefix string to validate
     * @param fieldName The name of the field for error messages
     * @throws ConfigurationException if validation fails
     */
    private void validateLinePrefix(String prefix, String fieldName) throws ConfigurationException {
        if (prefix == null) {
            return; // Null is acceptable for line prefixes
        }

        String trimmed = prefix.trim();

        if (trimmed.length() > 10) {
            throw new ConfigurationException(fieldName + " is too long (max 10 characters)");
        }
    }

    /**
     * Checks for overlapping delimiters that could cause parsing issues.
     *
     * @throws ConfigurationException if delimiters overlap
     */
    private void checkOverlappingDelimiters() throws ConfigurationException {
        String[] delimiters = {
            blockStartField.getText().trim(),
            blockEndField.getText().trim(),
            variableStartField.getText().trim(),
            variableEndField.getText().trim(),
            commentStartField.getText().trim(),
            commentEndField.getText().trim()
        };

        String[] names = {
            "Block Start",
            "Block End",
            "Variable Start",
            "Variable End",
            "Comment Start",
            "Comment End"
        };

        // Check for exact duplicates
        for (int i = 0; i < delimiters.length; i++) {
            for (int j = i + 1; j < delimiters.length; j++) {
                if (delimiters[i].equals(delimiters[j])) {
                    throw new ConfigurationException(
                        names[i] + " and " + names[j] + " cannot be the same: \"" + delimiters[i] + "\""
                    );
                }
            }
        }

        // Check for substring overlaps (one delimiter contains another)
        for (int i = 0; i < delimiters.length; i++) {
            for (int j = i + 1; j < delimiters.length; j++) {
                if (delimiters[i].contains(delimiters[j]) && !delimiters[j].isEmpty()) {
                    throw new ConfigurationException(
                        names[i] + " (\"" + delimiters[i] + "\") contains " +
                        names[j] + " (\"" + delimiters[j] + "\")"
                    );
                }
                if (delimiters[j].contains(delimiters[i]) && !delimiters[i].isEmpty()) {
                    throw new ConfigurationException(
                        names[j] + " (\"" + delimiters[j] + "\") contains " +
                        names[i] + " (\"" + delimiters[i] + "\")"
                    );
                }
            }
        }
    }

    @Override
    public void reset() {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        blockStartField.setText(settings.getBlockStartString());
        blockEndField.setText(settings.getBlockEndString());
        variableStartField.setText(settings.getVariableStartString());
        variableEndField.setText(settings.getVariableEndString());
        commentStartField.setText(settings.getCommentStartString());
        commentEndField.setText(settings.getCommentEndString());
        lineStatementPrefixField.setText(settings.getLineStatementPrefix());
        lineCommentPrefixField.setText(settings.getLineCommentPrefix());
    }
}
