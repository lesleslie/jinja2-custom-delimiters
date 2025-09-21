package com.wedgwoodwebworks.jinja2delimiters.liveTemplates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2FileType;
import org.jetbrains.annotations.NotNull;

/**
 * Context type for Jinja2 live templates
 */
public class Jinja2TemplateContextType extends TemplateContextType {

    public static final String JINJA2_CONTEXT_ID = "JINJA2_CUSTOM";

    protected Jinja2TemplateContextType() {
        super(JINJA2_CONTEXT_ID, "Jinja2 Custom Delimiters");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        PsiFile file = templateActionContext.getFile();
        return file.getFileType() instanceof CustomJinja2FileType;
    }
}
