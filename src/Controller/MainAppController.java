package Controller;

import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 *
 * @author Hoan
 */
public class MainAppController implements Initializable, ChangeListener {
    
    private double x, y;
    private boolean isMute;
    private boolean isPlay;
    private boolean isShuf;
    private int nuLoop;
    private double currentVolume;
    private double time;
    private MediaPlayer mediaPlayer;
    
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
    
    @FXML
    void changeLoop(MouseEvent event) {
        switch (nuLoop) {
            case 0:
                nuLoop = 2;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT);
                break;
            case 2:
                nuLoop = 1;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT_ONCE);
                break;
            default:
                nuLoop = 0;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT_OFF);
                break;
        }
    }
    
    @FXML
    void nextSong(MouseEvent event) {
        
    }
    
    @FXML
    void playPause(MouseEvent event) {
        isPlay = !isPlay;
        btnPlay.setIcon(isPlay ? FontAwesomeIcon.PAUSE : FontAwesomeIcon.PLAY);
    }
    
    @FXML
    void prevSong(MouseEvent event) {
        
    }
    
    @FXML
    void showMenu(MouseEvent event) {
        
    }
    
    @FXML
    void toggleShuffle(MouseEvent event) {
        isShuf = !isShuf;
        btnShuf.setIcon(isShuf ? MaterialDesignIcon.SHUFFLE : MaterialDesignIcon.SHUFFLE_DISABLED);
    }
    
    @FXML
    void toggleMute(MouseEvent event) {
        isMute = !isMute;
        // set mute
        mute.setIcon(isMute ? MaterialDesignIcon.VOLUME_OFF : MaterialDesignIcon.VOLUME_MEDIUM);
    }
    
    private void setIconMute() {
        int volume = (int) currentVolume;
        if (volume == 0) {
            mute.setIcon(MaterialDesignIcon.VOLUME_OFF);
        } else if (volume < 20) {
            mute.setIcon(MaterialDesignIcon.VOLUME_LOW);
        } else if (volume < 70) {
            mute.setIcon(MaterialDesignIcon.VOLUME_MEDIUM);
        } else {
            mute.setIcon(MaterialDesignIcon.VOLUME_HIGH);
        }
    }
    
    @FXML
    void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }
    
    @FXML
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    
    @FXML
    void closeApp(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
    
    @FXML
    void minimizeApp(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isMute = false;
        isPlay = false;
        isShuf = false;
        nuLoop = 0;
        time = 0;
        
        volumeSlider.valueProperty().addListener(this);
//        volumeSlider.setOnMouseReleased(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
//        volumeSlider.setOnMouseDragged(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
    }
    
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        currentVolume = volumeSlider.getValue();
        time = timeSlider.getValue();
        setIconMute();
//        mediaPlayer.setVolume(currentVolume / 100);
    }
    
}
