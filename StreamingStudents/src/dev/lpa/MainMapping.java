package dev.lpa;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.*;

public class MainMapping {
    public static void main(String[] args) {
        Course pymc = new Course("PYMC", "Python MasterClass", 50);
        Course jmc = new Course("JMC", "Python MasterClass", 100);
        Course jgames = new Course("JGA", "Creating Games in Java");

        List<Student> students = IntStream
                .rangeClosed(1, 5000)
                .mapToObj(s -> Student.getRandomStudent(jmc, pymc))
                .toList();

        var mappedStudents = students.stream()
                .collect(Collectors.groupingBy(Student::getCountryCode));

        mappedStudents.forEach((k, v) -> System.out.println(k + " students => " + v.size() + ". Country Student Rep: " + v.getFirst()));

        System.out.println("-------------------------");
        int minAge = 25;
        var youngerSet = students.stream()
                .collect(groupingBy(Student::getCountryCode,
                        filtering(s -> s.getAge() <= minAge, toList())));

        youngerSet.forEach((k, v) -> System.out.println(k + " students => " + v.size() + ". Country Student Rep: " + v.getFirst()));

        var experienced = students.stream()
                .collect(partitioningBy(Student::hasProgrammingExperience));
        System.out.println("Experienced Students = " + experienced.get(true).size());

        var expCount = students.stream()
                .collect(partitioningBy(Student::hasProgrammingExperience, counting()));
//        System.out.println(":::::::::::::::::::::::::::::::::::::::::" + experienced);
        System.out.println("Experienced Students = " + expCount.get(false));
        System.out.println("Experienced Students = " + expCount.get(true));

        var experiencedAndActive = students.stream()
                .collect(partitioningBy(
                        s -> s.hasProgrammingExperience()
                        && s.getMonthsSinceActive() == 0
                        , counting()
                ));
        System.out.println("Experienced and active students = " + experiencedAndActive.get(true));

        var multilevel = students.stream()
                .collect(groupingBy(Student::getCountryCode,
//                        groupingBy(Student::getGender)));
                        averagingInt(Student::getAge)));
        multilevel.forEach((key, value) -> {
            System.out.println(key);
//            value.forEach((key1, value1) ->  {
//                System.out.println("\t" + key1 + " " + value1.size());
//            });
            System.out.println(value);
        });

        long studentBodyCount = 0;
        for (var list : experienced.values()) {
            studentBodyCount += list.size();
        }
        System.out.println("studentBodyCount = " + studentBodyCount);

        studentBodyCount = experienced.values().stream()
                .mapToInt(List::size)
                .sum();
        System.out.println("studentBodyCount = " + studentBodyCount);

        studentBodyCount = experienced.values().stream()
                .map(l -> l.stream()
                        .filter(s -> s.getMonthsSinceActive() <= 3)
                        .count()
                )
                .mapToLong(l -> l)
                .sum();
        System.out.println("studentBodyCount = " + studentBodyCount);

        long count = experienced.values().stream()
                .flatMap(l -> l.stream())
                .filter(s -> s.getMonthsSinceActive() <= 3)
                .count();
        System.out.println("Active Students = " + count);

    }
}
