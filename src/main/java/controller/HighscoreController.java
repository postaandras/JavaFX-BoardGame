package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Player;
import org.tinylog.Logger;

import java.util.List;

public class HighscoreController {

    @FXML
    private Button backToMainMenu;

    @FXML
    private ListView<String> listOfNames;

    @FXML
    private ListView<Integer> listOfScores;

    public void initialize(){
        List<Player> playerList = PersistentPlayerController.loadPlayersList();
        if(playerList!=null){
            int limit = Math.min(10, playerList.size());
            for(int i = 0; i<limit; i++){
                listOfNames.getItems().add(playerList.get(i).getName());
                listOfScores.getItems().add(playerList.get(i).getScore());
            }
        }
    }

    @FXML
    void onBackButtonClicked(MouseEvent mouseEvent) {
        SceneChanger.changeScene("/fxml/main-menu.fxml", (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());

        Logger.info("Back to main menu!");
    }

}
