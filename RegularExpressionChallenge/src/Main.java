import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String emailText = """
                john.boy@valid.com
                john.boy@invalid
                jane.doe-smith@valid.co.uk
                jane_Doe1976@valid.co.uk
                bob-1964@valid.net
                bob!@invalid.com
                elaineinvalid1983@.com
                david@valid.io
                david@invalid..com
                """;

        Pattern partialPattern = Pattern.compile("([\\p{Alnum}_.-]+)@(([\\w-]+\\.)+\\w{2,})");
//        Pattern partialPattern2 = Pattern.compile("([\\w_.-]+)@(([\\w-]+\\.)+\\w{2,})");
        Matcher emailMatcher = partialPattern.matcher(emailText);
        emailMatcher.results().forEach(mr -> {
            System.out.printf("[username=%s, domain=%s]%n",
                    mr.group(1),
                    mr.group(2));
        });

        System.out.println("====================================\n");
        Pattern emailPattern = Pattern.compile("([\\p{Alnum}_.-]+)@(([\\w-]+\\.)+\\w{2,})");
        String[] emailSamples = emailText.lines().toArray(String[]::new);
        for (String email : emailSamples) {
            Matcher eMatcher = emailPattern.matcher(email);
            boolean matched = eMatcher.matches();
            System.out.print(email + " is " + (matched ? "VALID " : "INVALID "));
            if (matched) {
                System.out.printf("[username=%s, domain=%s]%n",
                        eMatcher.group(1),
                        eMatcher.group(2));
            } else {
                System.out.println();
            }
        }
    }
}
