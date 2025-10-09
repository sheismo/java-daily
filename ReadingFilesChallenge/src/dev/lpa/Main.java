package dev.lpa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("file.txt");

//        Main.challengeWithStreams(path);
//        Main.challengeWithoutStreams(path);

        try (BufferedReader br = new BufferedReader(
                new FileReader("file.txt")
        )) {
//            System.out.printf("%,d lines in file%n", br.lines().count());
            Pattern pattern = Pattern.compile("\\p{javaWhitespace}");
//            System.out.printf("%,d words in file%n", br.lines()
////                    .flatMap(pattern::splitAsStream)
//                    .flatMap(l -> Arrays.stream(l.split(pattern.toString())))
//                    .count());

//            System.out.printf("%,d words in file%n", br.lines()
//                    .mapToLong(l -> l.split(pattern.toString()).length)
//                    .sum());

            List<String> excluded = List.of(
                    "these", "represented", "their", "study", "include"
            );
            var results = br.lines()
                    .flatMap(pattern::splitAsStream)
                    .map(w -> w.replaceAll("\\p{Punct}", ""))
                    .filter(w -> w.length() > 4 && !excluded.contains(w))
                    .map(String::toLowerCase)
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

            results.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getValue,
                            Comparator.reverseOrder()))
                    .limit(10)
                    .forEach(e -> System.out.println(
                            e.getKey() + " - " + e.getValue() + " times"
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("=======================================================");
        String input = null;
        try {
            input = Files.readString(path);
            input = input.replaceAll("\\p{Punct}", "");

            Pattern pattern = Pattern.compile("\\w{5,}");
            Matcher matcher = pattern.matcher(input);
            Map<String, Long> results = new HashMap<>();

            while (matcher.find()) {
                String word = matcher.group().toLowerCase();
//                if (word.length() > 4) {
                    results.merge(word, 1L, (o, n) -> o += n);
//                }
            }

            var sortedEntries = new ArrayList<>(results.entrySet());
            sortedEntries.sort(Comparator.comparing(
                    Map.Entry::getValue, Comparator.reverseOrder()
            ));
            for (int i = 0; i < Math.min(10, sortedEntries.size()); i++) {
                var entry = sortedEntries.get(i);
                System.out.println(entry.getKey() + " - " + entry.getValue() + " times");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void challengeWithStreams(Path path) {
        try (Scanner scanner = new Scanner(path)) {
            var result = scanner.tokens()
                    .map(String::strip)
                    .map(s -> s.replaceAll("\\p{Punct}", ""))
                    .filter(s -> s.length() > 5)
                    .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

            result.entrySet().stream()
//                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .forEach(System.out::println);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void challengeWithoutStreams(Path path) {
        Map<String, Long> wordCount = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                String[] words = line.replaceAll("\\p{Punct}", "").split("\\s+");

                for (String word : words) {
                    word = word.strip().toLowerCase();

                    if (word.length() > 5) {
                        wordCount.put(word, wordCount.getOrDefault(word, 0L) + 1);
                    }
                }
            }

            List<Map.Entry<String, Long>> sortedList = new ArrayList<>(wordCount.entrySet());
            sortedList.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

            System.out.println("Top 10 most used words: ");
            for (int i = 0; i < Math.min(10, sortedList.size()); i++) {
                Map.Entry<String, Long> entry = sortedList.get(i);
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
