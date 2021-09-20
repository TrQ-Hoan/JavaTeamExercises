package Controller;

import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Hoan
 */
public class MainAppController implements Initializable {

    private double x, y;
    private boolean isMute;
    private boolean isPlay;
    private boolean isShuf;
    private boolean blocked;
    private int nuLoop;
    private double currentVolume;
    private MediaPlayer mediaPlayer;
    private Media curSong;
    private SongController ctrlSong;
    private XYChart.Data<String, Number>[] series1Data, series2Data;
    private AudioSpectrumListener audioSpectrumListener;
    private XYChart.Series<String, Number> series1, series2;
    private final Image baseImage = new Image(getClass().getResourceAsStream("/resource/BaseImage.png"));;

    @FXML
    private Label titleSong;
    @FXML
    private Label artistSong;
    @FXML
    private ImageView imageSong;
    @FXML
    private BarChart<String, Number> bc;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private MaterialDesignIconView mute;
    @FXML
    private MaterialDesignIconView btnShuf;
    @FXML
    private FontAwesomeIconView btnPrev;
    @FXML
    private FontAwesomeIconView btnPlay;
    @FXML
    private FontAwesomeIconView btnNext;
    @FXML
    private MaterialDesignIconView btnLoop;
    @FXML
    private MaterialDesignIconView btnMenu;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private Label currentTime;
    @FXML
    private Label totalTime;

    @FXML // thay đổi giữa các chế độ lặp
    void changeLoop(MouseEvent event) {
        switch (nuLoop) {
            case 0: // không lặp -> lặp toàn bộ
                nuLoop = 2;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT);
                break;
            case 2: // lặp toàn bộ -> lặp 1 bài
                nuLoop = 1;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT_ONCE);
                break;
            default: // lặp 1 bài -> không lặp
                nuLoop = 0;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT_OFF);
                break;
        }
    }

    // thay đổi bài hát tiếp theo khi thay đổi vòng lặp (0 lặp, lặp toàn bộ, lặp một bài)
    private void changedLoop() {
        // lặp toàn bộ HOẶC đang không lặp và không phải bài cuối cùng
        if (nuLoop == 2 || (nuLoop == 0 && !ctrlSong.isLastSong())) {
            // khi chạy hết bài thì gọi bài tiếp theo (tự động quay lại khi đến bài cuối cùng)
            mediaPlayer.setOnEndOfMedia(() -> nextSong(null));
        } else if (nuLoop == 0 && ctrlSong.isLastSong()) { // không lặp
            // khi bài cuối cùng trong danh sách chạy xong thì dừng phát
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                playPause(null);
                nextSong(null);
            });
        } else { // lặp một bài
            // khi bài hát chạy hết lại đặt lại thời gian là 0
            mediaPlayer.setOnEndOfMedia(() -> {
                timeSlider.setValue(0);
                mediaPlayer.seek(Duration.ZERO);
            });
        }
    }

    @FXML // bài hát tiếp theo
    void nextSong(MouseEvent event) {
        if (blocked) { // nếu như vô hiệu hóa thì thoát khỏi hàm
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.dispose();
        if (!isShuf) {
            // nếu đang ở chế độ shuffle
            ctrlSong.nextSong();
        } else {
            // nếu tắt chế độ shuffle
            ctrlSong.nextShuffleSong();
        }
        curSong = new Media(ctrlSong.getSong().getUri());
        mediaPlayer = new MediaPlayer(curSong);
        playMedia();

    }

    @FXML // phát/dừng bài hát
    void playPause(MouseEvent event) {
        if (blocked) { // nếu như vô hiệu hóa thì thoát khỏi hàm
            return;
        }
        // thay đổi giá trị biến kiểm tra phát hay không phát
        isPlay = !isPlay;
        // thay đổi icon play/pause
        btnPlay.setIcon(isPlay ? FontAwesomeIcon.PAUSE : FontAwesomeIcon.PLAY);
        if (isPlay) {
            mediaPlayer.play();
            if (!bc.isVisible()) {
                bc.setVisible(true);
            }
        } else {
            mediaPlayer.pause();
        }
    }

    @FXML // trở lại bài hát trước
    void prevSong(MouseEvent event) {
        if (blocked) { // nếu như vô hiệu hóa thì thoát khỏi hàm
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.dispose();
        if (!isShuf) {
            // nếu đang ở chế độ shuffle
            ctrlSong.previousSong();
        } else {
            // nếu đang ở chế độ shuffle
            ctrlSong.previousShuffleSong();
        }
        curSong = new Media(ctrlSong.getSong().getUri());
        mediaPlayer = new MediaPlayer(curSong);
        playMedia();
    }

    // (next version) hiển thị nội dung menu
    @FXML // thêm thư mục bài hát
    void showMenu(MouseEvent event) {
        ctrlSong.addOtherFolder();
        if (ctrlSong.lastSong() > 0) {
            blocked = false;
            volumeSlider.setDisable(false);
        }
    }

    @FXML // chuyển đổi giữa shuffle và không shuffle
    void toggleShuffle(MouseEvent event) {
        // thay đổi giá trị biến kiểm tra shuffle hay không shuffle
        isShuf = !isShuf;
        // tạo list shuffle mới
        if (isShuf && !blocked) {
            ctrlSong.shuffle();
        }
        // thay đổi icon shuffle/non-shuffle
        btnShuf.setIcon(isShuf ? MaterialDesignIcon.SHUFFLE : MaterialDesignIcon.SHUFFLE_DISABLED);
    }

    @FXML // chuyển đổi giữa mute và unmute
    void toggleMute(MouseEvent event) {
        // thay đổi giá trị biến kiểm tra mute hay unmute
        isMute = !isMute;
        // thay đổi icon mute/unmute
        mute.setIcon(isMute ? MaterialDesignIcon.VOLUME_OFF : MaterialDesignIcon.VOLUME_MEDIUM);
        if (isMute) {
            mediaPlayer.setVolume(0);
        } else {
            mediaPlayer.setVolume(currentVolume / 100);
        }
    }

    // thay đổi icon âm lượng theo mức
    private void setIconMute() {
        // lấy giá trị âm lượng hiện tại
        int volume = (int) currentVolume;
        if (volume == 0) { // âm lượng = 0%
            mute.setIcon(MaterialDesignIcon.VOLUME_OFF);
        } else if (volume < 20) { // 0% < âm lượng < 20%
            mute.setIcon(MaterialDesignIcon.VOLUME_LOW);
        } else if (volume < 70) { // 20% <= âm lượng < 70%
            mute.setIcon(MaterialDesignIcon.VOLUME_MEDIUM);
        } else { // 70% <= âm lượng
            mute.setIcon(MaterialDesignIcon.VOLUME_HIGH);
        }
    }

    @FXML // di chuyển cửa sổ ứng dụng khi nhấn dữ
    void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML // lấy giá trị vị trí cũ của cửa số khi nhấn giữ thanh taskbar
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML // đóng ứng dụng
    void closeApp(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML // ẩn ứng dụng
    void minimizeApp(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @Override // khởi tạo ứng dụng
    public void initialize(URL url, ResourceBundle rb) {
        // giá trị biến mute ban đầu không mute
        isMute = false;
        // giá trị biến play ban đầu không play
        isPlay = false;
        // giá trị biến shuffle ban đầu không shuffle
        isShuf = false;
        // giá trị biến loop ban đầu = 0 (-> không loop)
        nuLoop = 0;
        // giá trị âm lượng ban đầu bằng giá trị âm lượng trên thanh trượt mặc định (= 50)
        currentVolume = volumeSlider.getValue();

        // trục y của đồ thị biếu diễn giá trị dB
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "dB"));
        // lưu các giá trị nhận được từ bài hát (nửa trên của đồ thị)
        series1Data = new XYChart.Data[128];
        // lưu các giá trị nhận được từ bài hát (nửa dưới của đồ thị)
        series2Data = new XYChart.Data[128];
        
        // khởi tạo một SongController
        ctrlSong = new SongController();
        if (ctrlSong.lastSong() > 0) { // nếu như trong SongController có bài hát
            curSong = new Media(ctrlSong.getSong().getUri()); // khởi tạo một media
            mediaPlayer = new MediaPlayer(curSong); // khởi tạo một mediaPlayer từ file media ở trên
            blocked = false; // các chức năng không bị vô hiệu hóa
        } else { // nếu như không có bài hát nào thì vô hiệu hóa một số chức năng
            volumeSlider.setDisable(true);
            blocked = true;
        }

        // thay đổi giá trị âm lượng khi kéo thanh trượt
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentVolume = volumeSlider.getValue();
            setIconMute();
        });
        timeSlider.valueFactoryProperty().setValue(param -> new StringBinding() {
            @Override
            protected String computeValue() {
                return "*";
            }
        });
        // lấy và gán giá trị khi kéo thanh trượt cho âm lượng
        volumeSlider.setOnMouseDragged(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
        // lấy và gán giá trị khi click một vị trí trên thanh trượt
        volumeSlider.setOnMouseReleased(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
        playMedia();
    }

    private void playMedia() {
        if (blocked) { // nếu như vô hiệu hóa thì thoát khỏi hàm
            return;
        }
        /*
        Normal using:
            ClassObject.<func>.addListener(new ChangeListener<T>() {
                (Overrider)
                public void changed (
                        ObservableValue<? extends T> observableValue,
                        T oldValue, T newValue) {
                    <code>
                }
            }
         
        Lamda using:
            Object.<func>.addListener((observableValue, oldStatus,newStatus) -> {
                <code>
            });
        
         */
        // gán âm lượng từ giá trị đang có trên thanh trượt
        mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        // đưa thanh trượt thời gian về 0
        timeSlider.setValue(0);

        // vì hàm này được gọi từ next và previous nên phải kiểm tra lại biến isPlay
        if (!isPlay) { // nếu như đang gán không chạy thì tắt sóng âm thanh
            bc.setVisible(false);
        } else { // nếu như gán chạy thì tiếp tục phát âm thanh
            mediaPlayer.play();
            bc.setVisible(true);
        }
        
        // tạo đồ thị sóng âm
        series1 = new XYChart.Series<>(); // nửa trên của đồ thị sóng âm
        series2 = new XYChart.Series<>(); // nửa dưới của đồ thị sóng âm
        // thêm mảng các giá trị vào nửa trên đồ thị
        for (int i = 0; i < series1Data.length; i++) {
            series1Data[i] = new XYChart.Data<>(Integer.toString(i + 13), 50);
            series1.getData().add(series1Data[i]);
        }
        // thêm mảng các giá trị vào nửa dưới đồ thị
        for (int i = 0; i < series2Data.length; i++) {
            series2Data[i] = new XYChart.Data<>(Integer.toString(i + 13), 50);
            series2.getData().add(series2Data[i]);
        }
        bc.getData().clear(); // xóa dữ liệu đồ thị cũ
        bc.getData().add(series1); // thêm nửa đồ thị trên
        bc.getData().add(series2); // thêm nửa đồ thị dưới
        // gán các giá trị trong mảng tương ứng với giá trị nhận được từ magnitudes của audioSpectrumListener
        audioSpectrumListener = (double timestamp, double duration, float[] magnitudes, float[] phases) -> {
            for (int i = 0; i < series1Data.length; i++) {
                series1Data[i].setYValue(magnitudes[(i + 110) % 128] + 60);
                series2Data[i].setYValue(-(magnitudes[(i + 110) % 128] + 60));
            }
        };
        // lấy các giá trị của media vào audioSpectrumListener
        mediaPlayer.setAudioSpectrumListener(audioSpectrumListener);
        
        // T = MediaPlayer.Status
        // mỗi khi có một file media mới được chọn
        mediaPlayer.statusProperty().addListener((observableValue, oldStatus, newStatus) -> {
            if (newStatus == MediaPlayer.Status.READY) { // nếu như mediaPlayer đã sẵn sàng
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds()); // gán thời gian lớn nhất vào thanh trượt thời gian
                titleSong.setText(ctrlSong.getSong().getTitle()); // hiển thị tên bài hát
                artistSong.setText(ctrlSong.getSong().getArtists()); // hiển thị tên ca sĩ
                // nếu như trong file bài hát không có ảnh cover thì lấy ảnh mặc định
                imageSong.setImage((Image) ctrlSong.getSong().getCover() != null ? ctrlSong.getSong().getCover() : baseImage);
                // đặt thời gian max là thời gian của bài hát
                totalTime.setText(String.format("%02d:%02d", (int) timeSlider.getMax() / 60, (int) timeSlider.getMax() % 60));
            }
        });

        // T = Boolean
        // thay đổi thời gian của bài hát khi phát hiện thay đổi trên thanh trượt thời gian
        timeSlider.valueChangingProperty().addListener((observableValue, wasChanging, isChanging) -> {
            if (!isChanging) {
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
            }
        });

        // T = Number
        // khoảng thời gian chênh lệch khi kéo thanh trượt thời gian
        timeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            double curTime = mediaPlayer.getCurrentTime().toSeconds();
            if (Math.abs(curTime - newValue.doubleValue()) > 0.5) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }
        });
        
        // thay đổi thời gian khi dừng nhạc và kéo thanh thời gian
        timeSlider.setOnMouseDragged(event -> {
            if (!isPlay) {
                int seconds = (int) timeSlider.getValue() % 60;
                int minutes = (int) timeSlider.getValue() / 60;
                currentTime.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });

        // T = Duration
        // thay đổi thời gia khi nhạc đang phát
        mediaPlayer.currentTimeProperty().addListener((observableValue, oldTime, newTime) -> {
            if (!timeSlider.isValueChanging()) {
                int seconds = (int) timeSlider.getValue() % 60;
                int minutes = (int) timeSlider.getValue() / 60;
                currentTime.setText(String.format("%02d:%02d", minutes, seconds));
                timeSlider.setValue(newTime.toSeconds());
            }
            changedLoop();
        });
    }
}
