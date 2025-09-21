package com.wedgwoodwebworks.jinja2delimiters.lang;

import com.intellij.lang.Language;

public class CustomJinja2Language extends Language {
    public static final CustomJinja2Language INSTANCE = new CustomJinja2Language();

    private CustomJinja2Language() {
        super("Jinja2Custom");
    }
}
