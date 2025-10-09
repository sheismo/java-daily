package dev.lpa;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainFinalChallenge {
    public static void main(String[] args) {
        Course pymc = new Course("PYMC", "Python MasterClass", 50);
        Course jmc = new Course("JMC", "Python MasterClass", 100);
        Course jgames = new Course("JGA", "Creating Games in Java");

        int currentYear = LocalDate.now().getYear();
        List<Student> students = Stream
                .generate(() -> Student.getRandomStudent(jmc, pymc, jgames))
                .filter(s -> s.getYearEnrolled() >= (currentYear - 4))
                .limit(10000)
                .toList();

        System.out.println("Summary stats for students based on year enrolled");
        System.out.println(students
                .stream()
                .mapToInt(Student::getYearEnrolled)
                .summaryStatistics());
        System.out.println("==========================================");
        System.out.println();

        System.out.println("Sublist of students stream");
        students.subList(0, 10).forEach(System.out::println);
        System.out.println("==========================================");
        System.out.println();

        System.out.println("Summary stats on students' engagement map size");
        System.out.println(students
                .stream()
                .mapToInt(s -> s.getEngagementMap().size())
                .summaryStatistics());
        System.out.println("==========================================");
        System.out.println();

        System.out.println("Shows engagement map values of 10 students");
        students.stream()
                .limit(10)
                .flatMap(s -> s.getEngagementMap().values().stream())
                .forEach(System.out::println);
        System.out.println("==========================================");
        System.out.println();

        System.out.println("No of student engagements per course using course code");
        var mappedActivity = students.stream()
                .flatMap(s -> s.getEngagementMap().values().stream())
                .collect(Collectors.groupingBy(CourseEngagement::getCourseCode,
                        Collectors.counting())
                );
        mappedActivity.forEach((k, v) -> System.out.println(k + " " + v));
        System.out.println("==========================================");
        System.out.println();

        System.out.println("No of student engagements ");
        var classCounts = students.stream()
                .collect(Collectors.groupingBy(s -> s.getEngagementMap().size(),
                        Collectors.counting()));
        classCounts.forEach((k, v) -> System.out.println(k + " " + v));
        System.out.println("==========================================");
        System.out.println();

        System.out.println("Summary stats of percentage complete per course");
        var percentages = students.stream()
                .flatMap(s -> s.getEngagementMap().values().stream())
                .collect(Collectors.groupingBy(CourseEngagement::getCourseCode,
                        Collectors.summarizingDouble(CourseEngagement::getPercentComplete)));
        percentages.forEach((k, v) -> System.out.println(k + " " + v));
        System.out.println("==========================================");
        System.out.println();

        System.out.println("No of students grouped by last activity year then grouped by course code");
        var yearMap = students.stream()
                .flatMap(s -> s.getEngagementMap().values().stream())
                .collect(Collectors.groupingBy(CourseEngagement::getCourseCode,
                        Collectors.groupingBy(CourseEngagement::getLastActivityYear, Collectors.counting())));
        yearMap.forEach((k, v) -> System.out.println(k + " " + v));
        System.out.println("==========================================");
        System.out.println();

        System.out.println("No of students in an enrollment year per course");
        students.stream()
                .flatMap(s -> s.getEngagementMap().values().stream())
                .collect(Collectors.groupingBy(CourseEngagement::getEnrollmentYear,
                        Collectors.groupingBy(CourseEngagement::getCourseCode, Collectors.counting())))
                .forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println("==========================================");
        System.out.println();
    }
}

