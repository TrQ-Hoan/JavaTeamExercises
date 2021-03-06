package launcher;

import controller.DBConnection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MPlayer extends Application {

    private static String OS;

    @Override
    public void start(Stage stage) {
        DBConnection.createConnection("Database");
        DBConnection.createTable();
        Parent root = null;
        FXMLLoader loader = null;
        if (OS.startsWith("Windows")) {
            loader = new FXMLLoader(getClass().getResource("/view/MainApp.fxml"));
        } else if (OS.startsWith("Mac")) {
            loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainApp.fxml"));
        } else {
            System.out.println("Your OS doesn't support");
            System.exit(0);
        }
        try {
            root = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage.setTitle("MPlayer");
        stage.getIcons().add(new Image("/resources/logo.png"));
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        scene.setFill(Color.TRANSPARENT);
    }

    public static void main(String[] args) {
        OS = System.getProperty("os.name");
        launch(args);
    }
}
