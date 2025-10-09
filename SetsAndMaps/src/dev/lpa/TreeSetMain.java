package dev.lpa;

import java.util.*;

public class TreeSetMain {
    public static void main(String[] args) {
        List<Contact> phones = ContactData.getData("phone");
        List<Contact> emails = ContactData.getData("email");

//        NavigableSet<Contact> sorted = new TreeSet<>(phones);
        Comparator<Contact> mySort = Comparator.comparing(Contact::getName);

        NavigableSet<Contact> sorted = new TreeSet<>(mySort);
        sorted.addAll(phones);
        sorted.forEach(System.out::println);

        NavigableSet<String> justNames = new TreeSet<>();
        phones.forEach(c -> justNames.add(c.getName()));
        System.out.println(justNames);

        NavigableSet<Contact> fullSet = new TreeSet<>(sorted);
        fullSet.addAll(emails);
        fullSet.forEach(System.out::println);

        List<Contact> fullList = new ArrayList<>(phones);
        fullList.addAll(emails);
        fullList.sort(sorted.comparator());
        System.out.println("----------------------------");
        fullList.forEach(System.out::println);

        Contact min = Collections.min(fullSet, fullSet.comparator());
        Contact max = Collections.max(fullSet, fullSet.comparator());

        Contact first = fullSet.first();
        Contact last = fullSet.last();

        System.out.println("--------------------------------------");
        System.out.printf("min = %s, first = %s %n", min.getName(), first.getName());
        System.out.printf("max = %s, first = %s %n", max.getName(), last.getName());

        NavigableSet<Contact> copiedSet = new TreeSet<>(fullSet);
        System.out.println("First Element = " + copiedSet.pollFirst());
        System.out.println("Last Element = " + copiedSet.pollLast());
        System.out.println("-------------------------------------");

        copiedSet.forEach(System.out::println);
        System.out.println("------------------------------------");

//        System.out.println("printing the copied set");
//        System.out.println("\n" + copiedSet);

        Contact daffy = new Contact("Daffy Duck");
        Contact daisy = new Contact("Daisy Duck");
        Contact snoopy = new Contact("Snoopy");
        Contact archie = new Contact("Archie");

        for (Contact c : List.of(daffy, daisy, last, snoopy)) {
            System.out.printf("ceiling(%s)=%s%n", c.getName(), fullSet.ceiling(c));
            System.out.printf("higher(%s)=%s%n", c.getName(), fullSet.higher(c));
        }
        System.out.println("-----------------------------------------");

        for (Contact c : List.of(daffy, daisy, first, archie)) {
            System.out.printf("floor(%s)=%s%n", c.getName(), fullSet.floor(c));
            System.out.printf("lower(%s)=%s%n", c.getName(), fullSet.lower(c));
        }
        System.out.println("-----------------------------------------");

        NavigableSet<Contact> descendingSet = fullSet.descendingSet();
        descendingSet.forEach(System.out::println);
        System.out.println("-----------------------------");

        Contact lastContact = descendingSet.pollLast();
        System.out.println("Removed " + lastContact);
        descendingSet.forEach(System.out::println);
        System.out.println("---------------------");
        fullSet.forEach(System.out::println);
        System.out.println("-----------------------");

        Contact marion = new Contact("Maid Marion");
        var headSet = fullSet.headSet(marion);
        headSet.forEach(System.out::println);
        System.out.println("-------------------------------");

        var tailSet = fullSet.tailSet(marion);
        tailSet.forEach(System.out::println);
        System.out.println("-------------------------------");

        Contact linus = new Contact("Linus Van Pelt");
        var subset = fullSet.subSet(linus, false, marion, true);
        subset.forEach(System.out::println);

    }
}
