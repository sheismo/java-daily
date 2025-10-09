import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try(FileReader reader = new FileReader("file.txt")) {
            char[] block = new char[1000];
            int data;
            while ((data = reader.read(block)) != -1) {
//                System.out.println("==================>>>>>>>>>>>>>>>" + data) ;
                String content = new String(block, 0, data);
                System.out.printf("---> [%d chars] %s%n", data, content);
//                System.out.println(Character.getNumericValue(data));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("------------------------");
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader("file.txt"))) {
            bufferedReader.lines().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader("file.txt"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
