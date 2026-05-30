package com.webcrawlercli.crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class WebCrawler {

    public static Set<String> discoverUrlsRecursively(String startUrl, Path jsonFilePath) throws IOException {
        Set<String> allDiscoveredUrls = new LinkedHashSet<>();
        Set<String> currentBatch = UrlExtractor.extractValidUrls(startUrl);

        while (!currentBatch.isEmpty()) {
            Set<String> newUrls = new LinkedHashSet<>();

            for (String url : currentBatch) {
                if (allDiscoveredUrls.add(url)) {
                    try {
                        Set<String> extractedUrls = UrlExtractor.extractValidUrls(url);
                        for (String extractedUrl : extractedUrls) {
                            if (!allDiscoveredUrls.contains(extractedUrl)) {
                                newUrls.add(extractedUrl);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Skipping: " + url + " because " + e.getMessage());
                    }
                }
            }

            JsonUrlStore.saveUrlsToJson(allDiscoveredUrls, jsonFilePath);
            currentBatch = newUrls;
        }

        return allDiscoveredUrls;
    }

    public static void main(String[] args) {
        Path jsonFile = Path.of("discovered-urls.json");

        if (args.length != 1) {
            System.out.println("Usage: java WebCrawler \"<url>\"");
            System.exit(1);
        }

        String input = args[0];
        try {
            Set<String> urls = discoverUrlsRecursively(input, jsonFile);
            System.out.println(urls);
        } catch (IOException e) {
            System.out.println("Error occured when reading webpage " + e.getMessage());
        }
    }
}
