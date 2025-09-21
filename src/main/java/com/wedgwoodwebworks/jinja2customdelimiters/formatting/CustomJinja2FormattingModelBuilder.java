package com.wedgwoodwebworks.jinja2delimiters.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CustomJinja2FormattingModelBuilder implements FormattingModelBuilder {

    @NotNull
    @Override
    public FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        PsiElement element = formattingContext.getPsiElement();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();

        return FormattingModelProvider.createFormattingModelForPsiFile(
            element.getContainingFile(),
            new CustomJinja2Block(element.getNode(), null, null, settings),
            settings
        );
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }

    private static class CustomJinja2Block implements Block {
        private final ASTNode node;
        private final Alignment alignment;
        private final Indent indent;
        private final CodeStyleSettings settings;

        public CustomJinja2Block(ASTNode node, Alignment alignment, Indent indent, CodeStyleSettings settings) {
            this.node = node;
            this.alignment = alignment;
            this.indent = indent != null ? indent : Indent.getNoneIndent();
            this.settings = settings;
        }

        @Nullable
        public ASTNode getNode() {
            return node;
        }

        @NotNull
        @Override
        public TextRange getTextRange() {
            return node.getTextRange();
        }

        @NotNull
        @Override
        public List<Block> getSubBlocks() {
            // Simplified - would implement proper block structure for formatting
            return Collections.emptyList();
        }

        @Nullable
        @Override
        public Wrap getWrap() {
            return null;
        }

        @Nullable
        @Override
        public Indent getIndent() {
            return indent;
        }

        @Nullable
        @Override
        public Alignment getAlignment() {
            return alignment;
        }

        @Nullable
        @Override
        public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
            return null;
        }

        @NotNull
        @Override
        public ChildAttributes getChildAttributes(int newChildIndex) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }

        @Override
        public boolean isIncomplete() {
            return false;
        }

        @Override
        public boolean isLeaf() {
            return node.getFirstChildNode() == null;
        }
    }
}
