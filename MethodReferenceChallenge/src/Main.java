import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Main {
    private static Random random = new Random();

    private record Person(String first) {
        public String last(String s) {
            return first + " " + s.substring(0, s.indexOf(" "));
        }
    }

    public static void main(String[] args) {
//        String[] names = {"Anna", "Bob", "Carmen", "Donald", "Eva", "Frank"};
//
//        Person tim = new Person("Tim");
//
//        List<UnaryOperator<String>> list = new ArrayList<>(List.of(
//                String::toUpperCase,
//                s -> s += " " + getRandomChar('D', 'M') + ".",
//                s -> s += " " + reverse(s, 0, s.indexOf(" ")),
//                Main::reverse,
//                String::new,
////                s -> new String("Howdy" + s),
//                String::valueOf,
//                tim::last,
//                (new Person("Mary")::last)
//        ));
//        applyChanges(names, list);

        String name = "Tim";
        Function<String, String> uCase = String::toUpperCase;
        System.out.println(uCase.apply(name));

        Function<String, String> lastName = s -> s.concat(" Buchalka");
        Function<String, String> uCaseLastName = uCase.andThen(lastName);
        System.out.println(uCaseLastName.apply(name));

        uCaseLastName = uCase.compose(lastName);
        System.out.println(uCaseLastName.apply(name));

        Function<String, String[]> f0 = uCase
                .andThen(s -> s.concat(" Buchalka"))
                .andThen(s -> s.split(" "));
        System.out.println(Arrays.toString(f0.apply(name)));

        Function<String, String> f1 = uCase
                .andThen(s -> s.concat(" Buchalka"))
                .andThen(s -> s.split(" "))
                .andThen(s -> s[1].toUpperCase() + ", " + s[0]);
        System.out.println(f1.apply(name));

        Function<String, Integer> f2 = uCase
                .andThen(s -> s.concat(" Buchalka"))
                .andThen(s -> s.split(" "))
                .andThen(s -> String.join(", ", s))
                .andThen(String::length);
        System.out.println(f2.apply(name));

        Predicate<String> p1 = s -> s.equals("TIM");
        Predicate<String> p2 = s -> s.equalsIgnoreCase("Tim");
        Predicate<String> p3 = s -> s.startsWith("T");
        Predicate<String> p4 = s -> s.endsWith("e");

        Predicate<String> combined1 = p1.or(p2);
        System.out.println("combined1 = " + combined1.test(name));

        Predicate<String> combined2 = p3.and(p4);
        System.out.println("combined2 = " + combined2.test(name));

        Predicate<String> combined3 = p3.and(p4).negate();
        System.out.println("combined3");
//        http://localhost:8080/hello
     }

    private static void applyChanges(String[] names, List<UnaryOperator<String>> stringFunctions) {
        List<String> backedByArray = Arrays.asList(names);
        for (var function : stringFunctions) {
            backedByArray.replaceAll(s -> s.transform(function));
            System.out.println(Arrays.toString(names));
        }
    }

    private static char getRandomChar(char value1, char value2) {
        int rand = new Random().nextInt((int)value1, (int) value2 + 1);
        return (char) rand;

    }

    private static String reverse(String s) {
        return reverse(s, 0, s.length());
    }

    private static String reverse(String s, int start, int end ) {
        return new StringBuilder(s.substring(start, end)).reverse().toString();
    }
}
