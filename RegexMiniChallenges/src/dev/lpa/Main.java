package dev.lpa;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String regex1 = "Hello, World!";
        boolean matches = regex1.matches("Hello, World!");
        System.out.println(matches);


//        String regex2 = "[A-Z][a-z\\s]+\\.";
//        String regex2 = "[A-Z][a-z0-9\\s]+[.]";
//        String regex2 = "[A-Z][a-z\\s]+[.]";
        String regex2 = "[A-Z].*\\.";
        String word = "Apple.";
        String word2 = "apple.";
        System.out.println(word.matches(regex2));
        System.out.println(word2.matches(regex2));

        for (String s : List.of("The bike is red.",
                "I am a new student.",
                "A box of 3 apples.",
                "hello world.",
                "How are you?")) {
            boolean matched = s.matches(regex2);
            System.out.println(matched + ": " + s);
        }


//        String regex3 = "[A-Z][a-z\\p{Punct}\\s]\\p{Punct}$";
        String regex3 = "[A-Z].*\\p{Punct}$";
//        String regex3 = "^[A-Z][\\p{all}]+[.?!]$";

        for (String s : List.of("The bike is red, and has flat tires.",
                "I love being a new L.P.A student!",
                "Hello, friends and family: Welcome!",
                "How are you, Mary?")) {
            boolean matched = s.matches(regex3);
            System.out.println(matched + ": " + s);
        }
    }
}
