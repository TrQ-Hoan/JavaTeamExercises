package Model;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ithaka.audioinfo.AudioInfo;
import com.ithaka.audioinfo.mp3.MP3Info;
import com.ithaka.audioinfo.m4a.M4AInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hoan
 */
public class Song {

    private String path;
    private String uri;
    private String fileName;
    private String title;
    private List<Artist> artists;
    private Album album;
    private String genre;

    public Song(String directory, String fileName) {
        this.path = directory + fileName;
        this.fileName = fileName;
        if (this.fileName.endsWith(".mp3") || this.fileName.endsWith(".m4a")) {
            try (InputStream input = new BufferedInputStream(new FileInputStream(path))) {
                AudioInfo song;
                artists = new ArrayList<>();
                if (this.fileName.endsWith(".mp3")) {
                    song = new MP3Info(input, new File(path).length());
                } else {
                    song = new M4AInfo(input);
                }
                this.uri = new File(path).toURI().toString();
                String title_ = song.getTitle();
                String genre_ = song.getGenre();
                this.title = title_ != null ? title_ : formatName();
                this.album = new Album(song.getAlbum());
                setArtists(song.getArtist());
                this.genre = genre_ == null ? "" : genre_;
                //System.out.printf("Track:  %s\n", song.getTrack());
                //System.out.printf("Year:   %s\n\n", song.getYear());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void setArtists(String sArtist) {
        if (sArtist != null) {
            String[] artist = sArtist.split(",\\s*");
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

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public Album getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getUri() {
        return uri;
    }
}
