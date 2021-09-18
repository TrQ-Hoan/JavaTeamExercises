package Laucher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Hoan
 */
public class MPlayer extends Application {

    private static String OS;

    @Override
    public void start(Stage stage) throws Exception {

        Parent root;
        if (OS.startsWith("Windows")) {
            root = getWin();
        } else if (OS.startsWith("Mac")) {
            root = getMac();
        } else {
            root = getMac();
            System.exit(0);
        }
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        scene.setFill(Color.TRANSPARENT);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        OS = System.getProperty("os.name");
        System.out.println(OS);
        launch(args);
    }

    public Parent getWin() {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MainApp.fxml"));
            root = (Parent) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return root;
    }

    public Parent getMac() throws IOException {
        return (Parent) FXMLLoader.load(getClass().getClassLoader().getResource("View/mainApp.fxml"));
    }

}
