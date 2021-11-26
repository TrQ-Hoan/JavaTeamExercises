package model;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import audioinfo.AudioInfo;
import audioinfo.mp3.MP3Info;
import audioinfo.m4a.M4AInfo;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

public class Song {

    private String path;
    private String fileName;
    private String title;
    private List<Artist> artists;
    private Album album;
    private String genre;
    private Image cover;
    private byte[] coverByte;

    public Song(String s, String path) {
        this.path = path;
        String[] _path = path.split("/");
        this.fileName = _path[_path.length - 1];
        getInfo();
    }

    public Song(String s, String directory, String fileName) {
        this.path = directory + fileName;
        this.fileName = fileName;
        getInfo();
    }

    private void getInfo() {
        if (this.fileName.endsWith(".mp3") || this.fileName.endsWith(".m4a")) {
            try (InputStream input = new BufferedInputStream(new FileInputStream(path))) {
                AudioInfo song;
                artists = new ArrayList<>();
                if (this.fileName.endsWith(".mp3")) {
                    song = new MP3Info(input, new File(path).length());
                } else {
                    song = new M4AInfo(input);
                }
                String title_ = song.getTitle();
                String genre_ = song.getGenre();
                this.title = title_ != null ? title_ : formatName();
                this.album = new Album(song.getAlbum());
                setArtists(song.getArtist());
                this.genre = genre_ == null ? "" : genre_;
                boolean validCover = song.getCover() != null;
                if (validCover) {
                    coverByte = song.getCover();
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(coverByte));
                    this.cover = SwingFXUtils.toFXImage(image, null);
                    image.flush(); // clear buffer
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void setArtists(String sArtist) {
        if (sArtist != null) {
            String[] artist = sArtist.split("[,;]\\s*");
            for (String ar : artist) {
                artists.add(new Artist(ar));
            }
        } else {
            artists.add(new Artist(sArtist));
        }
    }

    private String formatName() {
        String title_tmp = fileName.substring(0, fileName.length() - 4);
        title_tmp = title_tmp.replaceAll("_", "");
        StringBuilder builder = new StringBuilder();
        int x = 0;
        String PATTERN = "[(].+[)]";
        Matcher m = Pattern.compile(PATTERN).matcher(title_tmp);
        while (m.find()) {
            builder.append(title_tmp.substring(x, m.start()));
            x = m.end();
        }
        title_tmp = (builder.toString());
        return title_tmp;
    }

    @Override
    public String toString() {
        String sArrArtist = artists.toString();
        return "Song: " + title
                + "\n\tPath:\t\t" + path
                + "\n\tFile Name:\t" + fileName
                + "\n\tArtist:\t\t" + sArrArtist.substring(1, sArrArtist.length() - 1)
                + "\n\tAlbum:\t\t" + album
                + "\n\tGenre:\t\t" + genre;
    }

    public byte[] getCoverByte() {
        return coverByte;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtists() {
        String sArrArtist = artists.toString();
        return sArrArtist.substring(1, sArrArtist.length() - 1);
    }

    public Image getCover() {
        return cover;
    }

    public Album getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }
}
