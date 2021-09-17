package Controller;

import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
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
    private int nuLoop;
    private double currentVolume;
    private MediaPlayer mediaPlayer;
    private Media curSong;
    private SongController ctrlSong;

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
    
    private void changedLoop() {
        if (nuLoop == 2 || (nuLoop == 0 && !ctrlSong.isLastSong())) {
            mediaPlayer.setOnEndOfMedia(() -> nextSong(null));
        } else if (nuLoop == 0 && ctrlSong.isLastSong()) {
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                playPause(null);
                nextSong(null);
            });
        } else {
            mediaPlayer.setOnEndOfMedia(() -> {
                timeSlider.setValue(0);
                mediaPlayer.seek(Duration.ZERO);
            });
        }
    }
    
    @FXML // bài hát tiếp theo
    void nextSong(MouseEvent event) {
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
        if (isPlay) {
            mediaPlayer.play();
        }
        playMedia();

    }

    @FXML // phát/dừng bài hát
    void playPause(MouseEvent event) {
        // thay đổi giá trị biến kiểm tra phát hay không phát
        isPlay = !isPlay;
        // thay đổi icon play/pause
        btnPlay.setIcon(isPlay ? FontAwesomeIcon.PAUSE : FontAwesomeIcon.PLAY);
        if (isPlay) {
            mediaPlayer.play();
            timeSlider.setMax(curSong.getDuration().toSeconds());
        } else {
            mediaPlayer.pause();
        }
    }

    @FXML // trở lại bài hát trước
    void prevSong(MouseEvent event) {
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
//        timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
        if (isPlay) {
            mediaPlayer.play();
        }
        playMedia();
    }

    // (next version) hiển thị nội dung menu
    @FXML // thêm thư mục bài hát
    void showMenu(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a music directory");
        File file = directoryChooser.showDialog(new Stage());
        
        ctrlSong.addOtherFolder(file.toString() + "/");

    }

    @FXML // chuyển đổi giữa shuffle và không shuffle
    void toggleShuffle(MouseEvent event) {
        // thay đổi giá trị biến kiểm tra shuffle hay không shuffle
        isShuf = !isShuf;
        // tạo list shuffle mới
        if (isShuf) {
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

    @FXML // di chuyển cửa sổ ứng dụng khi nhấn dữ vào taskbar
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
        // giá trị âm lượn ban đầu bằng giá trị âm lượ trên thanh trượt mặc định (= 50)
        currentVolume = volumeSlider.getValue();

        ctrlSong = new SongController();
        curSong = new Media(ctrlSong.getSong().getUri());
        mediaPlayer = new MediaPlayer(curSong);

        // thay đổi giá trị âm lượng khi kéo thanh trượt
        volumeSlider.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                currentVolume = volumeSlider.getValue();
                setIconMute();
            }
        });
        timeSlider.valueFactoryProperty().setValue(param -> new StringBinding() {
            @Override
            protected String computeValue() {
                return "*";
            }
        });
        volumeSlider.setOnMouseDragged(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
        volumeSlider.setOnMouseReleased(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
        playMedia();
    }

    private void playMedia() {
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
        timeSlider.setValue(0);

        // T = MediaPlayer.Status
        mediaPlayer.statusProperty().addListener((observableValue, oldStatus, newStatus) -> {
            if (newStatus == MediaPlayer.Status.READY) {
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                totalTime.setText(String.format("%02d:%02d", (int) timeSlider.getMax() / 60, (int) timeSlider.getMax() % 60));
            }
        });

        // T = Boolean
        timeSlider.valueChangingProperty().addListener((observableValue, wasChanging, isChanging) -> {
            if (!isChanging) {
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
            }
        });

        // T = Number
        timeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            double curTime = mediaPlayer.getCurrentTime().toSeconds();
            if (Math.abs(curTime - newValue.doubleValue()) > 0.5) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }
        });

        timeSlider.setOnMouseDragged(event -> {
            if (!isPlay) {
                int seconds = (int) timeSlider.getValue() % 60;
                int minutes = (int) timeSlider.getValue() / 60;
                currentTime.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });

        // T = Duration
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
