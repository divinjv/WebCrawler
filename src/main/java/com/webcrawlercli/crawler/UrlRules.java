package com.webcrawlercli.crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class UrlRules {

    private static final Pattern URL_PATTERN =
            Pattern.compile("https?://[a-zA-Z0-9./?=_#&%-]+");
    private static final Pattern HTTPS_URL_REGEX =
            Pattern.compile("^https://([a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}(:\\d{1,5})?(/[a-zA-Z0-9._~:/?#!$&'()*+,;=%\\-\\[\\]]*)?$");

    public static Pattern urlPattern() {
        return URL_PATTERN;
    }

    public static String extractDomain(String inputUrl) {
        try {
            URI uri = new URI(inputUrl);
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("Invalid input URL: " + inputUrl);
            }
            return host.toLowerCase();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid input URL: " + inputUrl, e);
        }
    }

    public static Pattern buildDomainPattern(String allowedDomain) {
        return Pattern.compile(
                "^https://([a-zA-Z0-9-]+\\.)*" + Pattern.quote(allowedDomain) + "(:\\d{1,5})?(/.*)?$",
                Pattern.CASE_INSENSITIVE
        );
    }

    public static boolean isValidDomainUrl(String url, Pattern domainPattern) {
        if (url == null || url.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(url);
            return "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null
                    && domainPattern.matcher(url).matches();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isValidHttpsUrl(String url) {
        try {
            if (url == null || url.isBlank()) {
                return false;
            }

            URI uri = new URI(url);
            return "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null
                    && HTTPS_URL_REGEX.matcher(url).matches();
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
