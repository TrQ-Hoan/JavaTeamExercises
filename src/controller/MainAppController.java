package controller;

import model.Song;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.File;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private SongController listSong;  // ds bai hat
    private XYChart.Data<String, Number>[] series1Data, series2Data;
    private AudioSpectrumListener audioSpectrumListener;
    private XYChart.Series<String, Number> series1, series2;
    private final Image baseImage = new Image(getClass().getResourceAsStream("/resources/base_image.png"));
    private HamburgerBackArrowBasicTransition transition;

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

    @FXML
    private JFXTextField searchBar;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ListView<Label> musicList;
    ObservableList<Label> musicListObservableList = FXCollections.observableArrayList();

// ===================== ???n hi???n list nh???c ============================
    @FXML
    private void openSongList() {
        if (listSong.isEmpty()) {
            listSong.addOtherFolder();
            if (listSong.isEmpty()) {
                return;
            }
            blocked = false;
            volumeSlider.setDisable(false);
            curSong = new Media(new File(listSong.getSongPath()).toURI().toString());
            mediaPlayer = new MediaPlayer(curSong);
            createSongList();
            playMedia();
        }
        if (anchorPane.isVisible() == true) {
            anchorPane.setVisible(false);
        } else {
            anchorPane.setVisible(true);
        }
    }

    @FXML
    private void closeSongList() {
        anchorPane.setVisible(false);
    }

    //  ch???n v??o kho???ng tr???ng ngo??i list nh???c ????? ???n list nh???c
    private int select = 1;

    @FXML
    private void selected1() {
        select *= -1;
        if (select < 0) {
            anchorPane.setVisible(false);
            select = 1;
        }
    }

    @FXML
    private void selected2() {
        select *= -1;
    }

// =================================================================
    @FXML // thay ?????i gi???a c??c ch??? ????? l???p
    void changeLoop(MouseEvent event) {
        switch (nuLoop) {
            case 0: // kh??ng l???p -> l???p to??n b???
                nuLoop = 2;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT);
                break;
            case 2: // l???p to??n b??? -> l???p 1 b??i
                nuLoop = 1;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT_ONCE);
                break;
            default: // l???p 1 b??i -> kh??ng l???p
                nuLoop = 0;
                btnLoop.setIcon(MaterialDesignIcon.REPEAT_OFF);
                break;
        }
    }

    // thay ?????i b??i h??t ti???p theo khi thay ?????i v??ng l???p (0 l???p, l???p to??n b???, l???p m???t b??i)
    private void changedLoop() {
        // l???p to??n b??? HO???C ??ang kh??ng l???p v?? kh??ng ph???i b??i cu???i c??ng
        if (nuLoop == 2 || (nuLoop == 0 && !listSong.isLastSong())) {
            // khi ch???y h???t b??i th?? g???i b??i ti???p theo (t??? ?????ng quay l???i khi ?????n b??i cu???i c??ng)
            mediaPlayer.setOnEndOfMedia(() -> nextSong(null));
        } else if (nuLoop == 0 && listSong.isLastSong()) { // kh??ng l???p
            // khi b??i cu???i c??ng trong danh s??ch ch???y xong th?? d???ng ph??t
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                playPause(null);
                nextSong(null);
            });
        } else { // l???p m???t b??i
            // khi b??i h??t ch???y h???t l???i ?????t l???i th???i gian l?? 0
            mediaPlayer.setOnEndOfMedia(() -> {
                timeSlider.setValue(0);
                mediaPlayer.seek(Duration.ZERO);
            });
        }
    }

    @FXML // b??i h??t ti???p theo
    void nextSong(MouseEvent event) {
        if (blocked) { // n???u nh?? v?? hi???u h??a th?? tho??t kh???i h??m
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.dispose();
        cssSelected();
        if (!isShuf) {
            // n???u ??ang ??? ch??? ????? shuffle
            listSong.nextSong();
        } else {
            // n???u t???t ch??? ????? shuffle
            listSong.nextShuffleSong();
        }
        curSong = new Media(new File(listSong.getSongPath()).toURI().toString());
        mediaPlayer = new MediaPlayer(curSong);
        cssUnSelected();
        playMedia();

    }

    @FXML // ph??t/d???ng b??i h??t
    void playPause(MouseEvent event) {
        if (blocked) { // n???u nh?? v?? hi???u h??a th?? tho??t kh???i h??m
            return;
        }
        // thay ?????i gi?? tr??? bi???n ki???m tra ph??t hay kh??ng ph??t
        isPlay = !isPlay;
        // thay ?????i icon play/pause
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

    @FXML // tr??? l???i b??i h??t tr?????c
    void prevSong(MouseEvent event) {
        if (blocked) { // n???u nh?? v?? hi???u h??a th?? tho??t kh???i h??m
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.dispose();
        cssSelected();

        if (!isShuf) {
            // n???u ??ang ??? ch??? ????? shuffle
            listSong.previousSong();
        } else {
            // n???u ??ang ??? ch??? ????? shuffle
            listSong.previousShuffleSong();
        }
        curSong = new Media(new File(listSong.getSongPath()).toURI().toString());
        mediaPlayer = new MediaPlayer(curSong);
        cssUnSelected();
        playMedia();
    }

    @FXML // chuy???n ?????i gi???a shuffle v?? kh??ng shuffle
    void toggleShuffle(MouseEvent event) {
        // thay ?????i gi?? tr??? bi???n ki???m tra shuffle hay kh??ng shuffle
        isShuf = !isShuf;
        // t???o list shuffle m???i
        if (isShuf && !blocked) {
            listSong.shuffle();
        }
        // thay ?????i icon shuffle/non-shuffle
        btnShuf.setIcon(isShuf ? MaterialDesignIcon.SHUFFLE : MaterialDesignIcon.SHUFFLE_DISABLED);
    }

    @FXML // chuy???n ?????i gi???a mute v?? unmute
    void toggleMute(MouseEvent event) {
        // thay ?????i gi?? tr??? bi???n ki???m tra mute hay unmute
        isMute = !isMute;
        // thay ?????i icon mute/unmute
        mute.setIcon(isMute ? MaterialDesignIcon.VOLUME_OFF : MaterialDesignIcon.VOLUME_MEDIUM);
        if (isMute) {
            mediaPlayer.setVolume(0);
        } else {
            mediaPlayer.setVolume(currentVolume / 100);
        }
    }

    // thay ?????i icon ??m l?????ng theo m???c
    private void setIconMute() {
        // l???y gi?? tr??? ??m l?????ng hi???n t???i
        int volume = (int) currentVolume;
        if (volume == 0) { // ??m l?????ng = 0%
            mute.setIcon(MaterialDesignIcon.VOLUME_OFF);
        } else if (volume < 20) { // 0% < ??m l?????ng < 20%
            mute.setIcon(MaterialDesignIcon.VOLUME_LOW);
        } else if (volume < 70) { // 20% <= ??m l?????ng < 70%
            mute.setIcon(MaterialDesignIcon.VOLUME_MEDIUM);
        } else { // 70% <= ??m l?????ng
            mute.setIcon(MaterialDesignIcon.VOLUME_HIGH);
        }
    }

    @FXML // di chuy???n c???a s??? ???ng d???ng khi nh???n d???
    void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML // l???y gi?? tr??? v??? tr?? c?? c???a c???a s??? khi nh???n gi??? thanh taskbar
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML // ????ng ???ng d???ng
    void closeApp(MouseEvent event) {
        DBConnection.closeConnection();
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML // ???n ???ng d???ng
    void minimizeApp(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

// ===========================================================================
    private void cssSelected() {
        int index = listSong.getCurrentIndex();
        musicListObservableList.get(index).setPadding(new Insets(0, 360, 0, 0));
        musicListObservableList.get(index).setStyle("-fx-background-color:none");
    }

    private void cssUnSelected() {
        int index = listSong.getCurrentIndex();
        musicListObservableList.get(index).setPadding(new Insets(10, 360, 10, 10));
        musicListObservableList.get(index).setStyle("-fx-background-color: linear-gradient(#328BDB 0%, #207BCF 25%, #1973C9 75%, #0A65BF 100%);");
    }

    // ================================= T??m ki???m b??i h??t =========================================
    private final KeyCombination keyCtrlBS = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.CONTROL_DOWN);
    private final KeyCombination keyCtrlDEL = new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN);

    @FXML
    void pressedReturn(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            searchSong();
        }
        final int txtLen = searchBar.getText().length();
        final int posCar = searchBar.getCaretPosition();
        final int cntStr = searchBar.getText().split("\\s++").length;
        if (event.getCode().equals(KeyCode.BACK_SPACE) // khi b???m ph??m xo??
                && txtLen == 1 // c??n 1 k?? t???
                && posCar == 1) { // con tr??? b??n ph???i k?? t???
            musicList.setItems(musicListObservableList);
        }
        if (event.getCode().equals(KeyCode.DELETE) // khi b???m ph??m delete
                && txtLen == 1 // c??n 1 k?? t???
                && posCar == 0) { // con tr??? b??n tr??i k?? t???
            musicList.setItems(musicListObservableList);
        }
        if (keyCtrlBS.match(event) && cntStr < 2 && posCar == txtLen) {
            musicList.setItems(musicListObservableList);
        }
        if (keyCtrlDEL.match(event) && cntStr < 2 && posCar == 0) {
            musicList.setItems(musicListObservableList);
        }
    }

    @FXML
    private void searchSong() {
        String s = searchBar.getText().trim().replaceAll("\\s++", " ");
        if (s.isEmpty()) {
            musicList.setItems(musicListObservableList);
            return;
        }
        ObservableList<Label> newObservableList = FXCollections.observableArrayList();
        HashSet<String> songSearch = DBConnection.searchSong(s);
        musicListObservableList.stream()
                .filter((a) -> (songSearch.contains(a.getText().split("\n")[0])))
                .forEachOrdered(a -> newObservableList.add(a));
        musicList.setItems(newObservableList);
    }

// ============================ click v??o b??i h??t ============================
    private void addEventHandle(Label label) {
        EventHandler<MouseEvent> eventHandlerBox;
        eventHandlerBox = (javafx.scene.input.MouseEvent e) -> {
            mediaPlayer.stop();
            cssSelected();
            int index = Integer.parseInt(label.getId());
            listSong.setCurrent(index);
            cssUnSelected();
            curSong = new Media(new File(listSong.getSongPath()).toURI().toString());
            mediaPlayer = new MediaPlayer(curSong);
            playMedia();
            if (isPlay) {
                mediaPlayer.play();
            }
        };
        label.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandlerBox);
    }

    private void createSongList() {
        ImageView imageView;
        if (listSong != null) {
            for (int i = 0; i < listSong.getSizeOfList(); i++) {
                imageView = new ImageView();
                imageView.setFitHeight(35);
                imageView.setFitWidth(35);
                imageView.setPreserveRatio(true);
                imageView.setImage(listSong.hasSongCover() ? baseImage : listSong.getSongCover());
                Label label = new Label();
                label.setId(String.format("%d", i));
                label.setText(
                        listSong.getSongInfo("songTitle") + "\n"
                        + listSong.getSongInfo("artistsName")
                );
                label.setGraphic(imageView);
                label.setTextFill(Color.WHITE);
                label.setFont(new Font("Arial", 18));
                label.setPadding(new Insets(0, 360, 0, 0));
                //------------------------------------------------------------
                addEventHandle(label);
                musicListObservableList.add(label);
                listSong.nextSong();
                musicList.setItems(musicListObservableList);
            }
        }
    }

// ===========================================================================
    @Override // kh???i t???o ???ng d???ng
    public void initialize(URL url, ResourceBundle rb) {

        // gi?? tr??? bi???n mute ban ?????u kh??ng mute
        isMute = false;
        // gi?? tr??? bi???n play ban ?????u kh??ng play
        isPlay = false;
        // gi?? tr??? bi???n shuffle ban ?????u kh??ng shuffle
        isShuf = false;
        // gi?? tr??? bi???n loop ban ?????u = 0 (-> kh??ng loop)
        nuLoop = 0;
        // gi?? tr??? ??m l?????ng ban ?????u b???ng gi?? tr??? ??m l?????ng tr??n thanh tr?????t m???c ?????nh (= 50)
        currentVolume = volumeSlider.getValue();

        // tr???c y c???a ????? th??? bi???u di???n gi?? tr??? dB
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "dB"));
        // l??u c??c gi?? tr??? nh???n ???????c t??? b??i h??t (n???a tr??n c???a ????? th???)
        series1Data = new XYChart.Data[128];
        // l??u c??c gi?? tr??? nh???n ???????c t??? b??i h??t (n???a d?????i c???a ????? th???)
        series2Data = new XYChart.Data[128];

        // kh???i t???o m???t SongController
        listSong = new SongController();
        if (!listSong.isEmpty()) { // n???u nh?? trong SongController c?? b??i h??t
            curSong = new Media(new File(listSong.getSongPath()).toURI().toString()); // kh???i t???o m???t media
            mediaPlayer = new MediaPlayer(curSong); // kh???i t???o m???t mediaPlayer t??? file media ??? tr??n
            blocked = false; // c??c ch???c n??ng kh??ng b??? v?? hi???u h??a
            createSongList(); // kh???i t???o list nh???c
        } else { // n???u nh?? kh??ng c?? b??i h??t n??o th?? v?? hi???u h??a m???t s??? ch???c n??ng
            volumeSlider.setDisable(true);
            blocked = true;
        }

        // thay ?????i gi?? tr??? ??m l?????ng khi k??o thanh tr?????t
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
        // l???y v?? g??n gi?? tr??? khi k??o thanh tr?????t cho ??m l?????ng
        volumeSlider.setOnMouseDragged(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
        // l???y v?? g??n gi?? tr??? khi click m???t v??? tr?? tr??n thanh tr?????t
        volumeSlider.setOnMouseReleased(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
        playMedia();

    }

    private void playMedia() {
        if (blocked) { // n???u nh?? v?? hi???u h??a th?? tho??t kh???i h??m
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
        // g??n ??m l?????ng t??? gi?? tr??? ??ang c?? tr??n thanh tr?????t
        mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        // ????a thanh tr?????t th???i gian v??? 0
        timeSlider.setValue(0);

        // v?? h??m n??y ???????c g???i t??? next v?? previous n??n ph???i ki???m tra l???i bi???n isPlay
        if (!isPlay) { // n???u nh?? ??ang g??n kh??ng ch???y th?? t???t s??ng ??m thanh
            bc.setVisible(false);
        } else { // n???u nh?? g??n ch???y th?? ti???p t???c ph??t ??m thanh
            mediaPlayer.play();
            bc.setVisible(true);
        }

        // t???o ????? th??? s??ng ??m
        series1 = new XYChart.Series<>(); // n???a tr??n c???a ????? th??? s??ng ??m
        series2 = new XYChart.Series<>(); // n???a d?????i c???a ????? th??? s??ng ??m
        // th??m m???ng c??c gi?? tr??? v??o n???a tr??n ????? th???
        for (int i = 0; i < series1Data.length; i++) {
            series1Data[i] = new XYChart.Data<>(Integer.toString(i + 13), 50);
            series1.getData().add(series1Data[i]);
        }
        // th??m m???ng c??c gi?? tr??? v??o n???a d?????i ????? th???
        for (int i = 0; i < series2Data.length; i++) {
            series2Data[i] = new XYChart.Data<>(Integer.toString(i + 13), 50);
            series2.getData().add(series2Data[i]);
        }
        bc.getData().clear(); // x??a d??? li???u ????? th??? c??
        bc.getData().add(series1); // th??m n???a ????? th??? tr??n
        bc.getData().add(series2); // th??m n???a ????? th??? d?????i
        // g??n c??c gi?? tr??? trong m???ng t????ng ???ng v???i gi?? tr??? nh???n ???????c t??? magnitudes c???a audioSpectrumListener
        audioSpectrumListener = (double timestamp, double duration, float[] magnitudes, float[] phases) -> {
            for (int i = 0; i < series1Data.length; i++) {
                series1Data[i].setYValue(magnitudes[(i + 110) % 128] + 60);
                series2Data[i].setYValue(-(magnitudes[(i + 110) % 128] + 60));
            }
        };
        // l???y c??c gi?? tr??? c???a media v??o audioSpectrumListener
        mediaPlayer.setAudioSpectrumListener(audioSpectrumListener);

        // T = MediaPlayer.Status
        // m???i khi c?? m???t file media m???i ???????c ch???n
        mediaPlayer.statusProperty().addListener((observableValue, oldStatus, newStatus) -> {
            if (newStatus == MediaPlayer.Status.READY) { // n???u nh?? mediaPlayer ???? s???n s??ng
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds()); // g??n th???i gian l???n nh???t v??o thanh tr?????t th???i gian
                Song currentSong = new Song("", listSong.getSongPath());
                titleSong.setText(currentSong.getTitle()); // hi???n th??? t??n b??i h??t
                artistSong.setText(currentSong.getArtists()); // hi???n th??? t??n ca s??
                // n???u nh?? trong file b??i h??t kh??ng c?? ???nh cover th?? l???y ???nh m???c ?????nh
                imageSong.setImage((Image) currentSong.getCover() != null ? currentSong.getCover() : baseImage);
                // ?????t th???i gian max l?? th???i gian c???a b??i h??t
                totalTime.setText(String.format("%02d:%02d", (int) timeSlider.getMax() / 60, (int) timeSlider.getMax() % 60));
            }
        });

        // T = Boolean
        // thay ?????i th???i gian c???a b??i h??t khi ph??t hi???n thay ?????i tr??n thanh tr?????t th???i gian
        timeSlider.valueChangingProperty().addListener((observableValue, wasChanging, isChanging) -> {
            if (!isChanging) {
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
            }
        });

        // T = Number
        // kho???ng th???i gian ch??nh l???ch khi k??o thanh tr?????t th???i gian
        timeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            double curTime = mediaPlayer.getCurrentTime().toSeconds();
            if (Math.abs(curTime - newValue.doubleValue()) > 0.5) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }
        });

        // thay ?????i th???i gian khi d???ng nh???c v?? k??o thanh th???i gian
        timeSlider.setOnMouseDragged(event -> {
            if (!isPlay) {
                int seconds = (int) timeSlider.getValue() % 60;
                int minutes = (int) timeSlider.getValue() / 60;
                currentTime.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });

        // T = Duration
        // thay ?????i th???i gia khi nh???c ??ang ph??t
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
