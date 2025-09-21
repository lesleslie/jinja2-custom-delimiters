package com.wedgwoodwebworks.jinja2delimiters.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CustomJinja2FileType extends LanguageFileType {
    public static final CustomJinja2FileType INSTANCE = new CustomJinja2FileType();

    private CustomJinja2FileType() {
        super(CustomJinja2Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Jinja2Custom";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Jinja2 template with custom delimiters";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "j2";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/jinja2.png", CustomJinja2FileType.class);
    }
}
