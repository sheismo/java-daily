package dev.lpa;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String helloWorld = "%s %s".formatted("Hello", "World");
        String helloWorld2 = String.format("%s %s", "Hello", "World");
        System.out.println("Using String's formatted method: " + helloWorld);
        System.out.println("Using String.format: " + helloWorld2);

        System.out.println(formatZ("%s %s %s", "Hello", "World", "By Zainab"));
        System.out.println(Main.formatT("%s %s %s", "Hello", "World", "By Tim"));

        String testString = "Anyone can Learn abc's, 123's, and any regular expression";
        String replacement = "(-)";

//        String[] patterns = {
//                "abc",
//                "123",
//                "A"
//        };
        String[] patterns = {
//                "a|b|c",
//                "ab|bc",
//                "[a-z]",
//                "[0-9]",
//                "[A-Z]",
//                "[123]",
//                "[A]"
//                "[a-zA-z]*",
//                "[0-9]*",
//                "[0-9]+","[0-9]{2}",
//                "[A-Z]*"
               "[a-zA-Z]*$",
                "^[a-zA-Z]{3}",
                "[aA]ny\\b"
        };

        for (String pattern : patterns) {
            String output = testString.replaceFirst(pattern, replacement);
            System.out.println("Pattern: " + pattern + " => " + output);
        }

        // Song of the Witches in MacBeth, a Play by Shakespeare
        String paragraph = """
                Double, double toil and trouble;
                Fire burn and caldron bubble.
                Fillet of a fenny snake,
                In the caldron boil and bake
                Eye of newt and toe of frog,
                Wool of bat and tongue of dog,
                Adder's fork and blind-worm's sting,
                Lizard's leg and howlet's wing,
                For a charm of powerful trouble,
                Like a hell-broth boil and bubble.
                """;

//        String[] lines = paragraph.split("\n");
        String[] lines = paragraph.split("\\R");
        System.out.println("This paragraph has " + lines.length + " lines.");


//        String[] words = paragraph.split(" ");
        String[] words = paragraph.split("\\s");
        System.out.println("This paragraph has " + words.length + " words.");

//        char[] letters = paragraph.toCharArray();
        String[] letters = paragraph.split("\\S");
        System.out.println("This paragraph has " + letters.length + " letters.");

        System.out.println(paragraph.replaceAll("[a-zA-Z]+ble", "[GRUB]"));

        Scanner scanner = new Scanner(paragraph);
        System.out.println(scanner.delimiter());
        scanner.useDelimiter("\\R");

//        while (scanner.hasNext()) {
//            String element = scanner.next();
//            System.out.println(element);
//        }

//        while (scanner.hasNextLine()) {
//            String element = scanner.nextLine();
//            System.out.println(element);
//        }

//        scanner.tokens()
////                .map(s-> Arrays.stream(s.split("\\s+")).count())
//                .map(s -> s.replaceAll("\\p{Punct}", ""))
//                .flatMap(s-> Arrays.stream(s.split("\\s+")))
//                .filter(s -> s.matches("[a-zA-Z]+ble"))
//                        .forEach(System.out::println);

        System.out.println(scanner.findInLine("[a-zA-Z]+ble"));
        System.out.println(scanner.findInLine("[a-zA-Z]+ble"));
        System.out.println(scanner.findInLine("[a-zA-Z]+ble"));
        System.out.println(scanner.findInLine("[a-zA-Z]+ble"));
        scanner.close();


    }

    private static String formatZ(String regexp, String... args) {
        String[] regexpArray = regexp.split(" ");
        for (int i = 0; i < regexpArray.length; i++) {
            regexpArray[i] = regexpArray[i].replace("%s", args[i]);
        }
        regexp = String.join(" ", regexpArray);
        return regexp;
    }

    private static String formatT(String regexp, String... args) {
        int index = 0;
        while (regexp.contains("%s")) {
            regexp = regexp.replaceFirst("%s", args[index++]);
        }
        return regexp;
    }

}
