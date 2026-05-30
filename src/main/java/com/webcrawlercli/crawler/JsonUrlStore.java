package com.webcrawlercli.crawler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUrlStore {

    private static final Pattern JSON_STRING_PATTERN = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");

    public static Set<String> readUrlsFromJson(Path filePath) throws IOException {
        Set<String> urls = new LinkedHashSet<>();

        if (!Files.exists(filePath)) {
            return urls;
        }

        String json = Files.readString(filePath, StandardCharsets.UTF_8);
        Matcher matcher = JSON_STRING_PATTERN.matcher(json);

        while (matcher.find()) {
            String url = matcher.group(1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
            urls.add(url);
        }

        return urls;
    }

    public static void saveUrlsToJson(Set<String> urls, Path filePath) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        int index = 0;
        for (String url : urls) {
            if (index > 0) {
                json.append(",\n");
            }
            json.append("  \"")
                    .append(url.replace("\\", "\\\\").replace("\"", "\\\""))
                    .append("\"");
            index++;
        }

        json.append("\n]");
        Files.writeString(filePath, json.toString(), StandardCharsets.UTF_8);
    }
}
