package dev.lpa;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static String ARTIST_INSERT = "INSERT INTO artists (artist_name) VALUES(?)";
    private static String ALBUM_INSERT = "INSERT INTO albums (artist_id, album_name) VALUES(?, ?)";
    private static String SONG_INSERT = "INSERT INTO music.songs (track_number, song_title, album_id) VALUES(?, ?, ?)";

    public static void main(String[] args) {
        var dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("music");
        try {
            dataSource.setContinueBatchOnError(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = dataSource.getConnection(
                System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASS"))
        ) {
            addDataFromFile(conn);

            String sql = "SELECT * FROM music.albumview WHERE artist_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "Bob Dylan");
            ResultSet rs = pstmt.executeQuery();
            printRecords(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean printRecords(ResultSet resultSet) throws SQLException {
        boolean foundData = false;

        var metaData = resultSet.getMetaData();
        System.out.println("====================================================================");

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            System.out.printf("%-25s", metaData.getColumnName(i).toUpperCase());
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.printf("%-25s", resultSet.getString(i));
            }
            System.out.println();
            foundData = true;
        }

        return foundData;
    }

    private static int addArtist(Connection conn, PreparedStatement ps, String artistName) throws SQLException {
        int artistId = -1;
        ps.setString(1, artistName);
        int count = ps.executeUpdate();

        if (count > 0){
            ResultSet key = ps.getGeneratedKeys();
            if (key.next()){
                artistId = key.getInt(1);
                System.out.println("Artist ID is: " + artistId);
            }
        }
        return artistId;
    }

    private static int addAlbum(Connection conn, PreparedStatement ps, int artistId,String albumName) throws SQLException {
        int albumId = -1;
        ps.setInt(1, artistId);
        ps.setString(2, albumName);
        int count = ps.executeUpdate();

        if (count > 0){
            ResultSet key = ps.getGeneratedKeys();
            if (key.next()){
                albumId = key.getInt(1);
                System.out.println("Album ID is: " + albumId);
            }
        }
        return albumId;
    }

    private static void addSong(Connection conn, PreparedStatement ps, int trackNumber, String songTitle, int albumId) throws SQLException {
        ps.setInt(1, trackNumber);
        ps.setString(2, songTitle);
        ps.setInt(3, albumId);
        ps.addBatch();
    }

    private static void addDataFromFile(Connection conn) throws SQLException {
        List<String> records = null;
        try {
            records = Files.readAllLines(Path.of("NewAlbums.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String lastArtist = null;
        String lastAlbum = null;
        int artistId = -1;
        int albumId = -1;
        try (
                PreparedStatement psArtist = conn.prepareStatement(ARTIST_INSERT, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psAlbum = conn.prepareStatement(ALBUM_INSERT, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psSong = conn.prepareStatement(SONG_INSERT, Statement.RETURN_GENERATED_KEYS);
        ) {
            conn.setAutoCommit(false);

            for (String record : records) {
                String[] columns = record.split(",");
                if (lastArtist == null || !lastArtist.equals(columns[0])) {
                    lastArtist = columns[0];
                    artistId = addArtist(conn, psArtist, lastArtist);
                }

                if (lastAlbum == null || !lastAlbum.equals(columns[1])) {
                    lastAlbum = columns[1];
                    albumId = addAlbum(conn, psAlbum, artistId, lastAlbum);
                }

                addSong(conn, psSong, Integer.parseInt(columns[2]), columns[3], albumId);
            }
            int[] inserts = psSong.executeBatch();
            int totalInserts = Arrays.stream(inserts).sum();
            System.out.printf("Total songs inserted: %d%n", totalInserts);

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("SQL STate is: " + e.getSQLState());
            System.out.println("SQL error code is: " + e.getErrorCode());
            System.out.println("SQL error message is: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
