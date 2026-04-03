package com.wedgwoodwebworks.jinja2customdelimiters.formatting;

import com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class DelimiterConversionUtil {

    private DelimiterConversionUtil() {}

    static String convertCustomToStandard(String text, Jinja2DelimitersSettings settings) {
        return applyConversions(text, List.of(
            opening(settings.getVariableStartString(), "{{"),
            opening(settings.getBlockStartString(), "{%"),
            opening(settings.getCommentStartString(), "{#"),
            closing(settings.getVariableEndString(), "}}"),
            closing(settings.getBlockEndString(), "%}"),
            closing(settings.getCommentEndString(), "#}")
        ));
    }

    static String convertStandardToCustom(String text, Jinja2DelimitersSettings settings) {
        return applyConversions(text, List.of(
            opening("{{", settings.getVariableStartString()),
            opening("{%", settings.getBlockStartString()),
            opening("{#", settings.getCommentStartString()),
            closing("}}", settings.getVariableEndString()),
            closing("%}", settings.getBlockEndString()),
            closing("#}", settings.getCommentEndString())
        ));
    }

    private static String applyConversions(String text, List<DelimiterConversion> conversions) {
        String result = text;
        List<DelimiterConversion> ordered = new ArrayList<>(conversions);
        ordered.sort(Comparator.comparingInt((DelimiterConversion conversion) -> conversion.from().length()).reversed());

        for (DelimiterConversion conversion : ordered) {
            if (conversion.from().equals(conversion.to())) {
                continue;
            }

            if (conversion.opening()) {
                result = result.replace(conversion.from() + "-", conversion.to() + "-");
                result = result.replace(conversion.from() + "+", conversion.to() + "+");
            } else {
                result = result.replace("-" + conversion.from(), "-" + conversion.to());
                result = result.replace("+" + conversion.from(), "+" + conversion.to());
            }

            result = result.replace(conversion.from(), conversion.to());
        }

        return result;
    }

    private static DelimiterConversion opening(String from, String to) {
        return new DelimiterConversion(from, to, true);
    }

    private static DelimiterConversion closing(String from, String to) {
        return new DelimiterConversion(from, to, false);
    }

    private record DelimiterConversion(String from, String to, boolean opening) {}
}
