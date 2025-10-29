package dev.lpa;

import java.sql.*;
import java.util.Arrays;

public class MusicDML {
    public static void main(String[] args) {
        // for legacy code or older applications
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/music?continueBatchOnError=false",
                System.getenv("MY_SQL_USER"),
                System.getenv("MY_SQL_PASS"));
             Statement stmt = connection.createStatement();
        ) {
           String tableName = "music.artists";
           String columnName = "artist_name";
           String columnValue = "Bob Dylan";

           if (!executeSelect(stmt, tableName, columnName, columnValue)) {
               insertArtistRecord(stmt, columnValue, columnValue);
           } else {
               try {
                   deleteArtistAlbum(connection, stmt, columnValue, columnValue);
               } catch (SQLException e) {
                   e.printStackTrace();
               }
                executeSelect(stmt, "music.albumview", "album_name", columnValue);
                executeSelect(stmt, "music.albums", "album_name", columnValue);
           }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static boolean printRecords(ResultSet resultSet) throws SQLException {
        boolean foundData = false;

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
            foundData = true;
        }

        return foundData;
    }
    
    private static boolean executeSelect(Statement statement, String table, String columnName, String columnValue) throws SQLException {
        String query = "SELECT * FROM %s WHERE %s='%s'".formatted(table, columnName, columnValue );
        var rs = statement.executeQuery(query);
//        boolean  b = statement.execute(query);
        if (rs != null) {
            return printRecords(rs);
        }
        return false;
    }

    private static boolean insertRecord (Statement statement, String table, String[] columnNames, String[] columnValues) throws SQLException {
        String colNames = String.join(",", columnNames);
        String colValues = String.join("','", columnValues);

        String query = "INSERT INTO %s (%s) VALUES ('%s')".formatted(table, colNames, colValues);
        System.out.println(query);

        boolean insertResult =  statement.execute(query);
        int recordsInserted = statement.getUpdateCount();
        if (recordsInserted > 0) {
            executeSelect(statement, table, columnNames[0], columnValues[0]);
        }
        return recordsInserted > 0;
    }

    private static boolean deleteRecord(Statement statement, String table, String columnName, String columnValue) throws SQLException {
        String query = "DELETE FROM %s WHERE %s='%s'".formatted(table, columnName, columnValue);
        System.out.println(query);
        statement.execute(query);

        int recordsDeleted = statement.getUpdateCount();
        if (recordsDeleted > 0) {
            executeSelect(statement, table, columnName, columnValue);
        }
        return recordsDeleted > 0;
    }

    private static boolean updateRecord(Statement statement, String table, String updatedColumn, String updatedValue, String matchedColumn, String matchedValue) throws SQLException {
        String query = "UPDATE %s SET %s = '%s' WHERE %s='%s'".formatted(table, updatedColumn, updatedValue, matchedColumn, matchedValue);
        System.out.println(query);
        statement.execute(query);

        int recordsUpdated = statement.getUpdateCount();
        if (recordsUpdated > 0) {
            executeSelect(statement, table, updatedColumn, updatedValue);
        }
        return recordsUpdated > 0;
    }

    private static void insertArtistRecord(Statement statement, String artistName, String albumName) throws SQLException {
        String queryArtist = "INSERT INTO music.artists (artist_name) VALUES (%s)".formatted(statement.enquoteLiteral(artistName));
        System.out.println(queryArtist);
        statement.execute(queryArtist, Statement.RETURN_GENERATED_KEYS);

        ResultSet resultSet = statement.getGeneratedKeys();
        int artistId = (resultSet != null && resultSet.next()) ? resultSet.getInt(1) : -1;

        String queryAlbum = "INSERT INTO music.albums (album_name, artist_id) VALUES (%s, %d)".formatted(statement.enquoteLiteral(albumName), artistId);
        System.out.println(queryAlbum);
        statement.execute(queryAlbum, Statement.RETURN_GENERATED_KEYS);

        resultSet = statement.getGeneratedKeys();
        int albumId = (resultSet != null && resultSet.next()) ? resultSet.getInt(1) : -1;

        String[] songs = new String[] {
                "You're no good",
                "Talkin' New York",
                "In my time of dyin'",
                "Man of Constant Sorrow",
                "Fixing to Die",
                "Pretty Peggy-O",
                "Highway 51 Blues"
        };

        String songInsert = "INSERT INTO music.songs " + "(track_number, song_title, album_id) VALUES (%d, %s, %d) ";

        for (int i = 0; i < songs.length; i++) {
            String songQuery = songInsert.formatted(i + 1, statement.enquoteLiteral(songs[i]), albumId);
            System.out.println(songQuery);
            statement.execute(songQuery);
        }
        executeSelect(statement, "music.albumview", "album_name", "Bob Dylan");

    }

    private static void deleteArtistAlbum (Connection conn, Statement stmt, String artistName, String albumName) throws SQLException {
        System.out.println("AUTO COMMIT: " + conn.getAutoCommit());
        conn.setAutoCommit(false);

        try {
            String deleteSongs = """
                    DELETE FROM songs WHERE album_id IN
                    (SELECT ALBUM_ID FROM music.albums WHERE album_name='%s')
                    """.formatted(albumName);
            String deleteAlbum = "DELETE FROM music.albums WHERE album_name='%s'".formatted(albumName);
            String deleteArtist = "DELETE FROM music.artists WHERE artist_name='%s'".formatted(artistName);

            stmt.addBatch(deleteSongs);
            stmt.addBatch(deleteAlbum);
            stmt.addBatch(deleteArtist);

            int[]  results = stmt.executeBatch();
            System.out.println(Arrays.toString(results));

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        }

        conn.setAutoCommit(true);
    }


}
