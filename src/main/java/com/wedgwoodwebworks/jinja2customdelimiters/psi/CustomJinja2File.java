package com.wedgwoodwebworks.jinja2delimiters.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2FileType;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2Language;
import org.jetbrains.annotations.NotNull;

public class CustomJinja2File extends PsiFileBase {

    public CustomJinja2File(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CustomJinja2Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CustomJinja2FileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Jinja2 Custom Delimiters File";
    }
}
