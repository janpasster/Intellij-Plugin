package com.thoughtworks.gauge.markdownPreview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    public static final String REGEX_FOR_TABLE = "\\s*\\|(.*\\n[^*]+)";

    public static String format(String text) {
        Pattern regex = Pattern.compile(REGEX_FOR_TABLE);
        Matcher regexMatcher = regex.matcher(text);
        while (regexMatcher.find())
            text = text.replace(regexMatcher.group(0),
                    String.format("\n%s", regexMatcher.group(0).replaceAll("\n\\s+\\|", "\n\t|")));
        return text.replace("<", "&lt;").replace(">", "&gt;");
    }
}