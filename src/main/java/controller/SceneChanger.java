package controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Objects;

public class SceneChanger {
    public static void changeScene(String url, Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneChanger.class.getResource(url)));
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e){
            Logger.error("Failed to load scene: "+ e.getMessage(), e);
        }
    }
}
