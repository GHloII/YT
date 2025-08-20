package ytdownloader.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class UrlValidator {

    private static final List<String> TRUSTED_DOMAINS = Arrays.asList(
            "youtube.com",
            "youtu.be",
            "soundcloud.com"
    );

    public static boolean isTrusted(String url) {
        try {
            URI parsedUrl = new URI(url);
            String host = parsedUrl.getHost();

            // Remove "www." if present for consistent checking
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            for (String domain : TRUSTED_DOMAINS) {
                if (host.equals(domain) || host.endsWith("." + domain)) {
                    return true;
                }
            }
        } catch (URISyntaxException e) {
            // Log the error or handle it as appropriate
            System.err.println("Invalid URL format: " + url + " - " + e.getMessage());
        }
        return false;
    }
}
