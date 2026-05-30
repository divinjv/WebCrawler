package com.webcrawlercli.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlExtractor {

    public static Set<String> extractValidUrls(String pageUrl) throws IOException {
        String allowedDomain = UrlRules.extractDomain(pageUrl);
        Pattern domainPattern = UrlRules.buildDomainPattern(allowedDomain);
        RobotsTxtChecker robotsChecker = new RobotsTxtChecker();

        Set<String> validUrls = new LinkedHashSet<>();
        Document document = Jsoup.connect(pageUrl).get();

        collectValidUrls(document.html(), validUrls, domainPattern, robotsChecker);

        for (Element element : document.getAllElements()) {
            collectValidUrls(element.attr("href"), validUrls, domainPattern, robotsChecker);
            collectValidUrls(element.attr("src"), validUrls, domainPattern, robotsChecker);

            String absHref = element.absUrl("href");
            if (UrlRules.isValidDomainUrl(absHref, domainPattern) && robotsChecker.isAllowed(absHref)) {
                validUrls.add(absHref);
            }

            String absSrc = element.absUrl("src");
            if (UrlRules.isValidDomainUrl(absSrc, domainPattern) && robotsChecker.isAllowed(absSrc)) {
                validUrls.add(absSrc);
            }
        }

        return validUrls;
    }

    private static void collectValidUrls(
            String text,
            Set<String> validUrls,
            Pattern domainPattern,
            RobotsTxtChecker robotsChecker
    ) {
        if (text == null || text.isBlank()) {
            return;
        }

        Matcher matcher = UrlRules.urlPattern().matcher(text);
        while (matcher.find()) {
            String candidate = matcher.group();
            if (UrlRules.isValidDomainUrl(candidate, domainPattern) && robotsChecker.isAllowed(candidate)) {
                validUrls.add(candidate);
            }
        }
    }
}
