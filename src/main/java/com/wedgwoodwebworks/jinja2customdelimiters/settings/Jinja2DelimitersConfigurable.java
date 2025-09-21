package com.wedgwoodwebworks.jinja2delimiters.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

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
        JTextArea helpText = new JTextArea(
            "Configure custom delimiters for Jinja2 templates.\n" +
            "Leave line prefixes empty to disable line-based syntax.\n" +
            "Changes will take effect after restarting the IDE or refreshing files."
        );
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
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
        return !blockStartField.getText().equals(settings.blockStartString) ||
               !blockEndField.getText().equals(settings.blockEndString) ||
               !variableStartField.getText().equals(settings.variableStartString) ||
               !variableEndField.getText().equals(settings.variableEndString) ||
               !commentStartField.getText().equals(settings.commentStartString) ||
               !commentEndField.getText().equals(settings.commentEndString) ||
               !lineStatementPrefixField.getText().equals(settings.lineStatementPrefix) ||
               !lineCommentPrefixField.getText().equals(settings.lineCommentPrefix);
    }

    @Override
    public void apply() throws ConfigurationException {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        settings.blockStartString = blockStartField.getText();
        settings.blockEndString = blockEndField.getText();
        settings.variableStartString = variableStartField.getText();
        settings.variableEndString = variableEndField.getText();
        settings.commentStartString = commentStartField.getText();
        settings.commentEndString = commentEndField.getText();
        settings.lineStatementPrefix = lineStatementPrefixField.getText();
        settings.lineCommentPrefix = lineCommentPrefixField.getText();
    }

    @Override
    public void reset() {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        blockStartField.setText(settings.blockStartString);
        blockEndField.setText(settings.blockEndString);
        variableStartField.setText(settings.variableStartString);
        variableEndField.setText(settings.variableEndString);
        commentStartField.setText(settings.commentStartString);
        commentEndField.setText(settings.commentEndString);
        lineStatementPrefixField.setText(settings.lineStatementPrefix);
        lineCommentPrefixField.setText(settings.lineCommentPrefix);
    }
}
