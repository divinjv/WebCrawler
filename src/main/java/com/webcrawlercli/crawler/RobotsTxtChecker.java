package com.webcrawlercli.crawler;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotsTxtChecker {

    private final Map<String, List<String>> robotsCache = new HashMap<>();

    public boolean isAllowed(String url) {
        try {
            URI uri = new URI(url);
            String origin = uri.getScheme() + "://" + uri.getHost();
            String path = uri.getPath();

            if (path == null || path.isBlank()) {
                path = "/";
            }

            List<String> disallowedPaths = robotsCache.computeIfAbsent(origin, RobotsTxtChecker::fetchDisallowedPaths);

            for (String disallowed : disallowedPaths) {
                if ("/".equals(disallowed)) {
                    return false;
                }
                if (!disallowed.isBlank() && path.startsWith(disallowed)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static List<String> fetchDisallowedPaths(String origin) {
        List<String> disallowedPaths = new ArrayList<>();

        try {
            String robotsUrl = origin + "/robots.txt";
            String content = Jsoup.connect(robotsUrl)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute()
                    .body();

            boolean inGlobalSection = false;

            for (String rawLine : content.split("\\R")) {
                String line = rawLine.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String lower = line.toLowerCase();

                if (lower.startsWith("user-agent:")) {
                    String agent = line.substring("user-agent:".length()).trim();
                    inGlobalSection = "*".equals(agent);
                    continue;
                }

                if (inGlobalSection && lower.startsWith("disallow:")) {
                    String path = line.substring("disallow:".length()).trim();
                    if (!path.isEmpty()) {
                        disallowedPaths.add(path);
                    }
                }
            }
        } catch (IOException ignored) {
            // If robots.txt cannot be read, treat as allowed for this basic implementation.
        }

        return disallowedPaths;
    }
}
