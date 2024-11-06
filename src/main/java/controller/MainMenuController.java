package controller;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import model.Player;
import org.tinylog.Logger;
import utility.AppColors;

import java.util.Objects;

public class MainMenuController {

    @FXML
    private TextField player1TextField;
    @FXML
    private TextField player2TextField;
    @FXML
    private Text player1Score;
    @FXML
    private Text player2Score;
    @FXML
    private Circle player1Circle;
    @FXML
    private Circle player2Circle;


    @FXML
    public Button continueButton;

    @Getter @Setter
    private static boolean isOngoing = false;

    public void initialize(){
        PersistentPlayerController.initializePlayers();

        player1TextField.setText(PersistentPlayerController.getPlayer1().getName());
        player2TextField.setText(PersistentPlayerController.getPlayer2().getName());

        player1Score.setText(String.valueOf(PersistentPlayerController.getPlayer1().getScore()));
        player2Score.setText(String.valueOf(PersistentPlayerController.getPlayer2().getScore()));

        player1Circle.setFill(AppColors.player1Color);
        player2Circle.setFill(AppColors.player2Color);

        if(checkForOngoing()) isOngoing=true;

        continueButton.setVisible(isOngoing);
    }

    private static boolean checkForOngoing(){
        ColorGameState state = PersistentPlayerController.readCurrentState();
        return state!=null;
    }

    @FXML
    public void onStartButtonClicked(MouseEvent mouseEvent) {
        isOngoing = false;
        SceneChanger.changeScene("/fxml/game-scene.fxml", (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        Logger.info("Game starts!");
    }

    @FXML
    public void onContinueButtonPressed(MouseEvent mouseEvent){
        SceneChanger.changeScene("/fxml/game-scene.fxml", (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        Logger.info("Game continued!");
    }

    @FXML
    public void onHighscoreButtonClicked(MouseEvent mouseEvent){
        SceneChanger.changeScene("/fxml/high-score.fxml", (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        Logger.info("Going to the highscores scene!");
    }

    private void setNewPlayer(int playerNumber, TextField playerTextField, Text playerScore) {
        String name = playerTextField.getText();


        String otherPlayerName = playerNumber == 1
                ? PersistentPlayerController.getPlayer2().getName()
                : PersistentPlayerController.getPlayer1().getName();

        if(!checkName(name)){
            resetNameAndScore(playerNumber, playerTextField, playerScore);
            return;
        }

        if (name.equals(otherPlayerName)) {
            Logger.warn("The two players cannot be the same!");
            resetNameAndScore(playerNumber, playerTextField, playerScore);
            return;
        }

        if (PersistentPlayerController.lookForPlayer(name)) {
            PersistentPlayerController.switchPlayer(Objects.requireNonNull(PersistentPlayerController.loadPlayerFromList(name)), playerNumber);
        }
        else {
            Player newPlayer = new Player(name, 0);
            PersistentPlayerController.switchPlayer(newPlayer, playerNumber);
            PersistentPlayerController.addPlayerToList(newPlayer);
        }

        playerScore.setText(String.valueOf(playerNumber == 1
                ? PersistentPlayerController.getPlayer1().getScore()
                : PersistentPlayerController.getPlayer2().getScore()));
    }

    @FXML
    public void setNewPlayer1() {
        setNewPlayer(1, player1TextField, player1Score);
    }

    @FXML
    public void setNewPlayer2() {
        setNewPlayer(2, player2TextField, player2Score);
    }

    private boolean checkName(String newName){
        if(newName.length()>6 || newName.isEmpty() || newName.isBlank()){
            Logger.error("The name must be between 1-6 characters");
            return false;
        }
        return true;
    }

    private void resetNameAndScore(int playerNumber, TextField playerTextField, Text playerScore){
        playerTextField.setText(playerNumber == 1
                ? PersistentPlayerController.getPlayer1().getName()
                : PersistentPlayerController.getPlayer2().getName());
        playerScore.setText(String.valueOf(playerNumber == 1
                ? PersistentPlayerController.getPlayer1().getScore()
                : PersistentPlayerController.getPlayer2().getScore()));
    }



}