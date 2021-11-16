package Controller;

import Model.Song;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Hoan
 */
public class DBConnection {

    private static Connection connection;
    private static Statement stmt;
    private static final String dbName = "MusiqueDB.sqlite";

    public static void createConnection(String filePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String pathTillProject = System.getProperty("user.dir");
            connection = DriverManager.getConnection("jdbc:sqlite:" + pathTillProject + "/" + filePath + "/" + dbName);
            System.out.println("Opened database successfully");
            stmt = connection.createStatement();
            System.out.println("DB Create Connection successfully");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
        }
    }
    
    public static boolean hasDB(){
        try {
            ResultSet rs = stmt.executeQuery("Select * from MUSICLIST");
            int cnt = 0;
            while (rs.next()) {
                cnt++;
                if(cnt > 0) break;
            }
            return cnt != 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static Object getMusicInfo(String info, String songId) {
        String sql = "";
        try {
            sql = "SELECT songTitle, artistsName, albumsName, songGenre, songCover "
                    + "FROM SONGS, ARTISTS, ALBUMS "
                    + "WHERE SONGS.songId = '" + songId + "'"
                    + " AND SONGS.artistsId = ARTISTS.artistsId"
                    + " AND SONGS.albumsId = ALBUMS.albumsId;";
            ResultSet rs = stmt.executeQuery(sql);
            if (info.equals("songCover")) {
                return rs.getBytes(info);
            } else {
                return rs.getString(info);
            }
        } catch (SQLException e) {
            System.out.println(sql);
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static void updateMusicList(ArrayList<String> songIdList, HashMap<String, String> songPathMap) {
        try {
            ResultSet rs = stmt.executeQuery("Select * from MUSICLIST");
            String songId;
            while (rs.next()) {
                songId = rs.getString("songId");
                if (!songPathMap.containsKey(songId)) {
                    songIdList.add(songId);
                    songPathMap.put(songId, rs.getString("songPath"));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void addSong(Song song) {
        try {
            String songinf = song.getTitle() + "" + song.getArtists() + "" + song.getAlbum();
            String sql = "INSERT OR IGNORE INTO SONGS "
                    + "(songID, songTitle, artistsId, albumsId, songGenre, songCover) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
                prstmt.setString(1, getMD5(songinf.replaceAll("'", "`")));
                prstmt.setString(2, song.getTitle().replaceAll("'", "`"));
                prstmt.setString(3, getMD5(song.getArtists()));
                prstmt.setString(4, getMD5(song.getAlbum().toString()));
                prstmt.setString(5, song.getGenre());
                prstmt.setBytes(6, song.getCoverByte());
                prstmt.executeUpdate();
            }
            sql = "INSERT OR IGNORE INTO ARTISTS (artistsId, artistsName) VALUES ("
                    + "'" + getMD5(song.getArtists()) + "', "
                    + "'" + song.getArtists() + "'"
                    + ");";
            stmt.executeUpdate(sql);
            sql = "INSERT OR IGNORE INTO ALBUMS (albumsId, albumsName) VALUES ("
                    + "'" + getMD5(song.getAlbum().toString()) + "', "
                    + "'" + song.getAlbum().toString().replaceAll("'", "`") + "'"
                    + ");";
            stmt.executeUpdate(sql);
            sql = "INSERT OR IGNORE INTO MUSICLIST (songID, songPath) VALUES ("
                    + "'" + getMD5(songinf.replaceAll("'", "`")) + "', "
                    + "'" + song.getPath().replaceAll("'", "''") + "'"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
//            System.out.println(song);
            System.err.println(e.getMessage());
        }
    }

    public static void deleteSong(String songId) {
        try {
            String sql = "DELETE FROM MUSICLIST WHERE songId = '" + songId + "';";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(s.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static void createTable() {
        createTableArtists();
        createTableAlbums();
        createTableSongs();
        createTableMusicPath();
        System.out.println("Create Table done");
    }

    public static void dropTable() {
        try {
            String sql = "Drop table if exists musicList;";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        createTableArtists();
        createTableAlbums();
        createTableSongs();
        createTableMusicPath();
    }

    private static void createTableMusicPath() {
        try {
            // Creating Table
            String sql = "CREATE TABLE IF NOT EXISTS MUSICLIST ("
                    + "songId       TEXT    NOT NULL,"
                    + "songPath     TEXT    NOT NULL,"
                    + "FOREIGN KEY  (songId)    REFERENCES SONGS(songId)"
                    + "PRIMARY KEY  (songPath)"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void createTableSongs() {
        try {
            // Creating Table
            String sql = "CREATE TABLE IF NOT EXISTS SONGS ("
                    + "songId       TEXT    NOT NULL,"
                    + "songTitle    TEXT    NOT NULL,"
                    + "artistsId    TEXT    NOT NULL,"
                    + "albumsId     TEXT    NOT NULL,"
                    + "songGenre    TEXT,"
                    + "songCover    BLOB,"
                    + "FOREIGN KEY  (artistsId) REFERENCES ARTISTS(artistsId),"
                    + "FOREIGN KEY  (albumsId)  REFERENCES ALBUMS(albumsId),"
                    + "PRIMARY KEY  (songId)"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void createTableArtists() {
        try {
            // Creating Table
            String sql = "CREATE TABLE IF NOT EXISTS ARTISTS ("
                    + "artistsId    TEXT    NOT NULL,"
                    + "artistsName  TEXT    NOT NULL,"
                    + "PRIMARY KEY  (artistsId)"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void createTableAlbums() {
        try {
            // Creating Table
            String sql = "CREATE TABLE IF NOT EXISTS ALBUMS ("
                    + "albumsId     TEXT    NOT NULL,"
                    + "albumsName   TEXT    NOT NULL,"
                    + "PRIMARY KEY  (albumsId)"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
