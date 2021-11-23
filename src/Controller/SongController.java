package Controller;

import Model.Song;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
<<<<<<< HEAD
=======
import java.io.IOException;
>>>>>>> hoan1
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Hoan
 */
public class SongController {

    private List<Integer> listSong_s;
    private ArrayList<String> songIdList;
    private HashMap<String, String> songPathMap;
    private int current;
    private DirectoryChooser directoryChooser;
    private String folderName;
    private Image cover;

    public SongController() {
        songIdList = new ArrayList<>();
        songPathMap = new HashMap<>();
        if (DBConnection.hasDB()) {
            DBConnection.updateMusicList(songIdList, songPathMap);
            // tạo một list số thứ tự các bài hát
            listSong_s = IntStream.range(0, songIdList.size()).boxed().collect(Collectors.toList());
            // xáo trộn list số thứ tự các bài hát để dùng khi shuffe được bật
            Collections.shuffle(listSong_s);
        } else {
            openFileX();
        }
        // khởi tạo biến lưu vị trí cho bài hát hiện tại trong list
//        openFileX();
        current = 0;
    }

    public void addOtherFolder() {
        openFileX();
    }

    // mở thư mục và thêm bài hát vào list
    private void addFolder(String dir) {
        // đổi đường dẫn về dạng file/folder
        File f = new File(dir);
        folderName = f.getName();
        // lọc ra những file có đuôi mp3 và m4a
        FilenameFilter filter = (File ff, String name) -> (name.endsWith(".mp3") || name.endsWith(".m4a"));
        // đưa các file mp3/m4a vào mảng
        File[] files = f.listFiles(filter);
        // nếu mở vào folder không có nhạc
        if (files.length < 1 && songIdList.isEmpty()) {
            return;
        }
        for (File file : files) {
            // đưa các file mp3/m4a vào list Song
            DBConnection.addSong(new Song("", dir, file.getName()));
        }
        DBConnection.updateMusicList(songIdList, songPathMap);
        // tạo một list số thứ tự các bài hát
        listSong_s = IntStream.range(0, songIdList.size()).boxed().collect(Collectors.toList());
        // xáo trộn list số thứ tự các bài hát để dùng khi shuffe được bật
        Collections.shuffle(listSong_s);

        // in ra list bài hát
        //listSong.forEach(System.out::println);
        //System.err.println(listSong.size());
    }

    private void openFileX() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chọn thư mục có file *.mp3, *.m4a");
        File file = directoryChooser.showDialog(new Stage());
        if (file == null) { // nếu thoát cửa sổ mở file
            DBConnection.updateMusicList(songIdList, songPathMap);
            // tạo một list số thứ tự các bài hát
            listSong_s = IntStream.range(0, songIdList.size()).boxed().collect(Collectors.toList());
            // xáo trộn list số thứ tự các bài hát để dùng khi shuffe được bật
            Collections.shuffle(listSong_s);
            return;
        }
        addFolder(file.toString() + "/");
    }

    // làm mới list shuffle
    public void shuffle() {
        Collections.shuffle(listSong_s, new Random());
    }

    // lấy vị trí cuối cùng của list
    public int lastSong() {
        return listSong_s.size() - 1;
    }

    // kiể tra danh sách bài hát rỗng hay không
    public boolean isEmpty() {
        return listSong_s.isEmpty();
    }

    public boolean isLastSong() {
        return current == lastSong();
    }

    // kiểm tra chỉ mục i trong list
    private int roudBin(int i) {
        if (i < 0) {
            // nếu nhỏ hơn 0 thì đặt lại giá trị là cuối list
            i = lastSong();
        } else if (i > lastSong()) {
            // nếu lớn hơn vị trí cuối thì đặt lại giá trị là 0
            i = 0;
        }
        return i;
    }

    // bài tiếp theo trong list
    public void nextSong() {
        current = roudBin(current + 1);
    }

    // bài trước đó trong list
    public void previousSong() {
        current = roudBin(current - 1);
    }

    // bài tiếp theo trong list shuffle
    public void nextShuffleSong() {
        int i = roudBin(listSong_s.indexOf(current) + 1);
        current = listSong_s.get(i);
    }

    // bài tiếp theo trong list shuffle
    public void previousShuffleSong() {
        int i = roudBin(listSong_s.indexOf(current) - 1);
        current = listSong_s.get(i);
    }

    // trả về đường dẫn bài hát hiện tại
    public String getSongPath() {
        return songPathMap.get(songIdList.get(current));
    }

    public String getSongInfo(String info) {
        return (String) DBConnection.getMusicInfo(info, songIdList.get(current));
    }

    public boolean hasSongCover() {
        cover = null;
        byte[] img = (byte[]) DBConnection.getMusicInfo("songCover", songIdList.get(current));
        if (img != null) {
            try {
                BufferedImage inBufImg = ImageIO.read(new ByteArrayInputStream(img));
                BufferedImage outBufImg = new BufferedImage(35, 35, inBufImg.getType());
                // scales the input image to the output image
                Graphics2D g2d = outBufImg.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(inBufImg, 0, 0, 35, 35, null);
                g2d.dispose();
                cover = SwingFXUtils.toFXImage(outBufImg, null);
                inBufImg.flush(); // clear buffer
                outBufImg.flush(); // clear buffer
            } catch (IOException e) {
            }
        }
        return cover == null;
    }

    public Image getSongCover() {
        return cover;
    }

    public int getSizeOfList() {
        return songIdList.size();
    }

    public int getCurrentIndex() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
<<<<<<< HEAD

<<<<<<< HEAD
    public void sortSongList(){
        Collections.sort(listSong, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getTitle().toLowerCase(Locale.ROOT).compareTo(b.getTitle().toLowerCase(Locale.ROOT));
            }
        });
=======
    public String getFolderName() {
        return folderName;
>>>>>>> hoan1
    }
=======
>>>>>>> 86161dd (debug phan tim kiem bai hat)
}
