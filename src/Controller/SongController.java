package Controller;

import Model.Song;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author Hoan
 */
public class SongController {

    private List<Song> listSong;
    private List<Integer> listSong_s;
    private int current;
    private DirectoryChooser directoryChooser;

    SongController() { // khởi tạo class
        listSong = new ArrayList<>();
        openFileX();
        // khởi tạo biến lưu vị trí cho bài hát hiện tại trong list
        current = 0;
    }

    public void addOtherFolder() {
        openFileX();
    }

    private void openFileX() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chọn thư mục có file *.mp3, *.m4a");
        File file = directoryChooser.showDialog(new Stage());
        if (file == null) { // nếu thoát cửa sổ mở file
            return;
        }
        addFolder(file.toString() + "/");
    }

    // mở thư mục và thêm bài hát vào list
    private void addFolder(String dir) {
        try {
            // đổi đường dẫn về dạng file/folder
            File f = new File(dir);
            // lọc ra những file có đuôi mp3 và m4a
            FilenameFilter filter = (File ff, String name) -> (name.endsWith(".mp3") || name.endsWith(".m4a"));
            // đưa các file mp3/m4a vào mảng
            File[] files = f.listFiles(filter);
            // nếu mở vào folder không có nhạc
            if (files.length < 1 && listSong.isEmpty()) {
                return;
            }
            for (File file : files) {
                // đưa các file mp3/m4a vào list Song
                listSong.add(new Song(dir, file.getName()));
            }
            // tạo một list số thứ tự các bài hát 
            listSong_s = IntStream.range(0, listSong.size()).boxed().collect(Collectors.toList());
            // xáo trộn list số thứ tự các bài hát để dùng khi shuffe được bật
            Collections.shuffle(listSong_s);

            // in ra list bài hát
            //listSong.forEach(System.out::println);
            //System.err.println(listSong.size());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // làm mới list shuffle
    public void shuffle() {
        Collections.shuffle(listSong_s, new Random());
    }

    // lấy vị trí cuối cùng của list
    public int lastSong() {
        return listSong.size() - 1;
    }

    // kiể tra danh sách bài hát rỗng hay không
    public boolean isEmpty() {
        return listSong.isEmpty();
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

    // trả về bài hát hiện tại
    public Song getSong() {
        return listSong.get(current);
    }
}
