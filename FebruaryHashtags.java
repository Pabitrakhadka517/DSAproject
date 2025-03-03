import java.util.*;
import java.util.regex.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class TrendingHashtags {
    
    /*
    Algorithm to find the top 3 trending hashtags in February 2024:
    
    1. **Initialize Data Structures:**
       - Create a HashMap `hashtagCount` to store the frequency of each hashtag.
       - Use a DateTimeFormatter to parse tweet dates.

    2. **Process Each Tweet:**
       - Convert the `tweet_date` string to a LocalDate object.
       - Check if the tweet belongs to **February 2024**.
       - If yes, extract all hashtags from the tweet text using a **regex pattern**.

    3. **Update Hashtag Counts:**
       - If a hashtag is found, update its count in the `hashtagCount` HashMap.

    4. **Sort Hashtags:**
       - Convert the HashMap entries into a list.
       - Sort the list based on:
         - **Hashtag frequency** in descending order.
         - **Hashtag name** in descending order (if counts are the same).

    5. **Extract Top 3 Hashtags:**
       - Select up to **three** most frequent hashtags from the sorted list.

    6. **Display Output:**
       - Print the results in a tabular format.
    */

    // Function to extract top 3 trending hashtags in February 2024
    public static List<Map.Entry<String, Integer>> getTopHashtags(List<Tweet> tweets) {
        // Step 1: Initialize data structures
        Map<String, Integer> hashtagCount = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Step 2: Process each tweet
        for (Tweet tweet : tweets) {
            // Convert tweet date to LocalDate object
            LocalDate tweetDate = LocalDate.parse(tweet.tweetDate, formatter);
            
            // Step 3: Filter only tweets from February 2024
            if (tweetDate.getYear() == 2024 && tweetDate.getMonthValue() == 2) {
                // Extract hashtags using regex pattern
                Pattern pattern = Pattern.compile("#(\\w+)");
                Matcher matcher = pattern.matcher(tweet.tweetText);

                // Step 4: Update hashtag counts
                while (matcher.find()) {
                    String hashtag = matcher.group(1);
                    hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
                }
            }
        }

        // Step 5: Sort hashtags based on frequency and then alphabetically
        List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCount.entrySet());
        sortedHashtags.sort((a, b) -> 
            b.getValue().equals(a.getValue()) ? b.getKey().compareTo(a.getKey()) : b.getValue() - a.getValue()
        );

        // Step 6: Return the top 3 trending hashtags
        return sortedHashtags.subList(0, Math.min(3, sortedHashtags.size()));
    }

    public static void main(String[] args) {
        // Example input: List of Tweet objects
        List<Tweet> tweets = Arrays.asList(
            new Tweet(13, "Enjoying a great start to the day. #HappyDay #MorningVibes", "2024-02-01"),
            new Tweet(14, "Another #HappyDay with good vibes! #FeelGood", "2024-02-03"),
            new Tweet(15, "Productivity peaks! #WorkLife #ProductiveDay", "2024-02-04"),
            new Tweet(16, "Exploring new tech frontiers. #TechLife #Innovation", "2024-02-04"),
            new Tweet(17, "Gratitude for today's moments. #HappyDay #Thankful", "2024-02-05"),
            new Tweet(18, "Innovation drives us. #TechLife #FutureTech", "2024-02-06"),
            new Tweet(19, "Connecting with nature's serenity. #Nature #Peaceful", "2024-02-09")
        );

        // Get the top trending hashtags
        List<Map.Entry<String, Integer>> result = getTopHashtags(tweets);

        // Print the output
        System.out.println("+-----------+-------+");
        System.out.println("| hashtag   | count |");
        System.out.println("+-----------+-------+");
        for (Map.Entry<String, Integer> entry : result) {
            System.out.printf("| %-9s | %d     |\n", entry.getKey(), entry.getValue());
        }
        System.out.println("+-----------+-------+");
    }
}

// Helper class to store tweet data
class Tweet {
    int tweetId;
    String tweetText;
    String tweetDate;

    public Tweet(int tweetId, String tweetText, String tweetDate) {
        this.tweetId = tweetId;
        this.tweetText = tweetText;
        this.tweetDate = tweetDate;
    }
}

/*
Expected Output:

+-----------+-------+
| hashtag   | count |
+-----------+-------+
| HappyDay  | 3     |
| TechLife  | 2     |
| WorkLife  | 1     |
+-----------+-------+

Explanation:
- #HappyDay appears 3 times (highest frequency).
- #TechLife appears 2 times (second highest).
- Multiple hashtags appear once, but #WorkLife is chosen because hashtags with the same frequency are sorted in descending alphabetical order.
*/

