package dev.lpa;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(Path.of("music.properties"), StandardOpenOption.READ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var dataSource = new MysqlDataSource();
        dataSource.setServerName(props.getProperty("serverName"));
        dataSource.setPortNumber(Integer.parseInt(props.getProperty("port")));
        dataSource.setDatabaseName(props.getProperty("databaseName"));
        try {
            dataSource.setMaxRows(10);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

//        String query = "SELECT * FROM music.artists LIMIT 10";
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter an artist id: ");
//        String artistId = scanner.nextLine();
//        int artistid = Integer.parseInt(artistId);

        String query = "SELECT * FROM music.artists";
//        String query = """
//                WITH RankedRows AS (
//                    SELECT *,
//                    ROW_NUMBER() OVER (ORDER BY artist_id) AS row_num
//                    FROM music.artists
//                )
//                SELECT *
//                    FROM RankedRows
//                WHERE row_num <= 10
//                """;

        try (Connection conn = dataSource.getConnection(props.getProperty("user"), System.getenv("MY_SQL_PASS"));
                Statement statement = conn.createStatement();
        ) {
            System.out.println("Connected to database successfully");
            ResultSet resultSet = statement.executeQuery(query);
            
            var metaData = resultSet.getMetaData();
            System.out.println("====================================================================");

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.printf("%-15s", metaData.getColumnName(i).toUpperCase());
            }
            System.out.println();

            while (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    System.out.printf("%-15s", resultSet.getString(i));
                }
                System.out.println();
//                System.out.printf(
//                        "%d %s %s %n",
//                        resultSet.getInt("track_number"),
//                        resultSet.getString("artist_name"),
//                        resultSet.getString("song_title")
//                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
