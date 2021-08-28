package Laucher;

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
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View/MainApp.fxml"));
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
        launch(args);
    }

    
}
