// Algorithm:
// 1. Initialize WebCrawler:
//    - Create an instance of WebCrawler with numThreads and maxDepth.
//    - Set up thread pool with numThreads and initialize urlQueue and visitedUrls.
// 2. Start Crawling:
//    - Add the starting URL to the urlQueue and mark it as visited.
//    - Start multiple worker threads to process the crawling task.
// 3. Worker Thread Execution (CrawlerTask):
//    - Each thread continuously retrieves URLs from the queue and calls the crawl(url, depth) method.
// 4. Crawl a Web Page:
//    - For each URL, attempt to connect and fetch content using HttpURLConnection.
//    - If the response code is 200, proceed to extract links.
// 5. Extract Links:
//    - Simulate extracting links by generating new URLs.
//    - If a link has not been visited, add it to the queue.
// 6. Shutdown and Await Termination:
//    - After submitting crawling tasks, shut down the executor service.
//    - Await thread termination with a timeout, forcefully shut down if not finished.

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

// WebCrawler class that manages multithreaded crawling
public class WebCrawler {
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet(); // Store visited URLs to avoid duplicates
    private final ConcurrentLinkedQueue<String> urlQueue = new ConcurrentLinkedQueue<>(); // Queue for URLs to be crawled
    private final ExecutorService executorService; // Thread pool for crawling tasks
    private final int maxDepth; // Maximum depth to crawl

    // Constructor initializes the thread pool and max depth
    public WebCrawler(int numThreads, int maxDepth) {
        this.executorService = Executors.newFixedThreadPool(numThreads); // Create thread pool
        this.maxDepth = maxDepth;
    }

    // Start the crawling process with an initial URL
    public void startCrawling(String startUrl) {
        urlQueue.add(startUrl); // Add the initial URL to queue
        visitedUrls.add(startUrl); // Mark it as visited

        for (int i = 0; i < 5; i++) { // Start multiple worker threads
            executorService.execute(new CrawlerTask());
        }

        executorService.shutdown(); // Shutdown after tasks are submitted
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    // Worker task that crawls web pages
    private class CrawlerTask implements Runnable {
        @Override
        public void run() {
            while (!urlQueue.isEmpty()) {
                String url = urlQueue.poll(); // Get next URL
                if (url != null) {
                    crawl(url, 0); // Start crawling at depth 0
                }
            }
        }
    }

    // Method to crawl a single web page
    private void crawl(String url, int depth) {
        if (depth > maxDepth) return; // Stop crawling if max depth is reached

        try {
            System.out.println("Crawling: " + url);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // Set timeout to 5 seconds
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // If successful response
                extractLinks(url, depth); // Simulated function to extract links
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch: " + url + " | Error: " + e.getMessage());
        }
    }

    // Simulated function to extract links from a page (replace with real HTML parsing)
    private void extractLinks(String pageUrl, int depth) {
        Set<String> newUrls = new HashSet<>();
        newUrls.add(pageUrl + "/next1"); // Simulating link extraction
        newUrls.add(pageUrl + "/next2");

        for (String newUrl : newUrls) {
            if (visitedUrls.add(newUrl)) { // Avoid duplicates
                urlQueue.add(newUrl); // Add new URLs to the queue
            }
        }
    }

    // Main method to run the crawler
    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler(5, 2); // 5 threads, max depth 2
        crawler.startCrawling("https://example.com"); // Start crawling from this URL
    }
}
