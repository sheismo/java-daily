package dev.lpa;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class MainChallenge {
    public static void main(String[] args) {
        Course pymc = new Course("PYMC", "Python MasterClass", 50);
        Course jmc = new Course("JMC", "Python MasterClass", 100);
        Course jgames = new Course("JGA", "Creating Games in Java");

//        List<Student> students = Stream
//                .iterate(1, s -> s <= 5000, s -> s + 1) // dont use post increment operator here
//                .map(s -> Student.getRandomStudent(jmc, pymc))
//                .toList();

        List<Student> students = IntStream
                .rangeClosed(1, 5000)
                .mapToObj(s -> Student.getRandomStudent(jmc, pymc))
                .toList();

        double totalPercent = students.stream()
                .mapToDouble(s -> s.getPercentComplete("JMC"))
//                .sum()
                .reduce(0, Double::sum);

        double averagePercent = totalPercent/ students.size();
        System.out.printf("Average Percentage Complete = %.2f%% %n", averagePercent);

        int topPercent = (int) (1.25 * averagePercent);
        System.out.printf("Best Percentage Complete = %d%% %n", topPercent);

        Comparator<Student> longTermStudent = Comparator.comparing(Student::getYearEnrolled);

        List<Student> hardWorkers = students.stream()
                .filter(s -> s.getMonthsSinceActive("JMC") == 0)
                .filter(s -> s.getPercentComplete("JMC") >= topPercent)
                .sorted(longTermStudent)
                .limit(10)
                .toList();
        hardWorkers.forEach(s -> {
            s.addCourse(jgames);
            System.out.print(s.getStudentId() + " ");
        });

        System.out.println();

        Comparator<Student> uniqueSorted = longTermStudent.thenComparing(Student::getStudentId);

        students.stream()
                .filter(s -> s.getMonthsSinceActive("JMC") == 0)
                .filter(s -> s.getPercentComplete("JMC") >= topPercent)
                .sorted(longTermStudent)
                .limit(10)
//                .toList()
//                .collect(Collectors.toList())
//                .collect(Collectors.toSet())
//                .collect(() -> new TreeSet<>(uniqueSorted),
//                        TreeSet::add, TreeSet::addAll)
                .forEach(s -> {
                    s.addCourse(jgames);
                    System.out.print(s.getStudentId() + " ");
                });
    }
}
