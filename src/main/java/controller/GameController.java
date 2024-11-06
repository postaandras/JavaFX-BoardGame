package controller;
import game.State;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Player;
import model.Position;
import org.tinylog.Logger;
import utility.AppColors;

import java.util.List;
import java.util.Optional;

public class GameController {

    @FXML
    private GridPane board;

    @FXML
    private Label teamLabel;



    private ColorGameState state;
    Position start;
    private boolean startPosSet = false;

    private static final double CIRCLE_RADIUS = 25.0;
    private static final Border TILE_BORDER = new Border(new BorderStroke(
            AppColors.borderColor,
            BorderStrokeStyle.SOLID,
            new CornerRadii(2),
            new BorderWidths(1)
    ));


    @FXML
    public void initialize() {
        if(MainMenuController.isOngoing()){
            state = PersistentPlayerController.readCurrentState();
        }
        else{
            state = new ColorGameState();
        }

        updatePlayerText();
        initializeBoard();
        resetAllHighlights();

    }

    private void initializeBoard(){
        int numRows = board.getRowCount();
        int numCols = board.getColumnCount();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                initializeSingleTile(row, col);
            }
        }
    }

    private void initializeSingleTile(int row, int col){
        var tile = new StackPane();
        board.add(tile, row, col);
        tile.setBorder(TILE_BORDER);
        tile.setBackground(new Background(new BackgroundFill(
                AppColors.backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));

        initializeCircle(row, col, tile);
    }

    private void initializeCircle(int row, int col, StackPane tile){
        Circle circle = new Circle(CIRCLE_RADIUS);

        if(state.getPlayer1Positions().contains(new Position(col, row))){
            circle.setFill(AppColors.player1Color);
        }
        else{
            circle.setFill(AppColors.player2Color);
        }

        tile.setOnMouseClicked(this::handleClick);
        tile.getChildren().add(circle);
    }




    private void handleClick(MouseEvent mouseEvent){
        StackPane tile = (StackPane) mouseEvent.getSource();
        Position clickedPosition = new Position(GridPane.getRowIndex(tile), GridPane.getColumnIndex(tile));

        if(startPosSet){
            handleMove(clickedPosition);
        }
        else{
            selectStartPosition(clickedPosition);
        }

    }

    private void selectStartPosition(Position startPosition){

        if (state.isLegalToMoveFrom(startPosition)) {
            start = startPosition;
            startPosSet = true;
            highlightAvailableMoves();
            Logger.info("Start position selected: " + start);
        } else {
            resetSelection();
        }
    }

    private void handleMove(Position goalPosition){
        if(state.isLegalMove(start, goalPosition)){
            state.makeMove(start, goalPosition);
            Logger.info("Move made from "+ start + " to " + goalPosition);
            resetSelection();
            checkForGameOver();
            updatePlayerText();

        }
        selectStartPosition(goalPosition);
    }



    private void highlightAvailableMoves() {
        resetAllHighlights();
        List<Position> availableMoves = state.getCurrentAvailableMoves();

        for (Node node : board.getChildren()) {
            Position position = new Position(GridPane.getRowIndex(node), GridPane.getColumnIndex(node));
            if (availableMoves.contains(position)) {
                StackPane tile = (StackPane) node;
                Circle circle = (Circle) tile.getChildren().get(0);
                circle.setFill(AppColors.highLightColor);
            }
        }
    }

    private void resetAllHighlights(){
        for (Node node : board.getChildren()) {
            Position position = new Position(GridPane.getRowIndex(node), GridPane.getColumnIndex(node));
            StackPane tile = (StackPane) node;
            Circle circle = (Circle) tile.getChildren().get(0);



            if (state.getPlayer1Positions().contains(position)) {
                circle.setFill(AppColors.player1Color);
                circle.setDisable(false);
            } else if (state.getPlayer2Positions().contains(position)) {
                circle.setFill(AppColors.player2Color);
                circle.setDisable(false);
            } else {
                circle.setFill(AppColors.backgroundColor);
                circle.setDisable(true);
            }
        }
    }

    private void resetSelection(){
        start = null;
        startPosSet = false;
        resetAllHighlights();
    }



    private void checkForGameOver(){
        if(state.getStatus()!= State.Status.IN_PROGRESS){
            Logger.info("Game over! "+state.getStatus());
            try{showGameOverAlert();}
            catch (Exception e){
                Logger.error(e.getMessage());
            }

            gameReset();
        }
    }

    private void showGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game over!");

            switch (state.getStatus()) {
                case PLAYER_1_WINS:
                    setAlert(alert, PersistentPlayerController.getPlayer1());
                    break;
                case PLAYER_2_WINS:
                    setAlert(alert, PersistentPlayerController.getPlayer2());
                    break;
                default:
                    alert.setHeaderText("The game ended in a draw!");
                    break;
            }
        Optional<ButtonType> pushedButton = alert.showAndWait();

            if(pushedButton.isPresent()) {
                if (pushedButton.get() == ButtonType.CANCEL) {
                    onGoBackButtonClicked();
                }
            }
    }

    private static void setAlert(Alert alert, Player player){
        alert.setHeaderText(player.getName() + " wins!");
        player.increaseScore();
        PersistentPlayerController.updateCurrentPlayers();

        alert.setContentText("Want to restart?");
    }

    private void gameReset(){
        state = new ColorGameState();
        resetAllHighlights();
        updatePlayerText();
    }



    @FXML
    public void onGoBackButtonClicked() {
        if(state.getStatus()== State.Status.IN_PROGRESS){
            Logger.info("Time to save state!");
            PersistentPlayerController.saveState(state);
            MainMenuController.setOngoing(true);
        }
        else{
            PersistentPlayerController.saveState(null);
            MainMenuController.setOngoing(false);
        }

        SceneChanger.changeScene("/fxml/main-menu.fxml", (Stage)  board.getScene().getWindow());

        Logger.info("Back to main menu!");
    }

    @FXML
    public void onRestartButtonClicked(){
        Logger.info("Game restarted");
        gameReset();
    }

    @FXML
    public void undoLastMove(){
        if(state.getPreviousMoves().isEmpty()){
            Logger.warn("There are no more previous moves!");
            return;
        }
        state.undoLastMove();
        resetAllHighlights();
        updatePlayerText();
    }

    private void updatePlayerText(){
        switch (state.getNextPlayer()){
            case PLAYER_1 -> teamLabel.setText(PersistentPlayerController.getPlayer1().getName());
            case PLAYER_2 -> teamLabel.setText(PersistentPlayerController.getPlayer2().getName());
        }
    }
}
