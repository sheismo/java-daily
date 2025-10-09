package dev.lpa;

public class Test {
    public static void main(String[] args) {
        print(null);
    }

    public static void print(Object obj) {
        System.out.println("Object");
    }

    public static void print (String str) {
        System.out.println("String");
    }
}
