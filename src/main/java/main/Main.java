package main;

import controller.ColorGameState;
import game.console.TwoPhaseMoveGame;
import javafx.application.Application;
import model.Position;
import view.ConsoleGame;
import view.RedBlueApplication;

public class Main {

    public static void main(String[] args) {
        /*var state = new ColorGameState();
        var game = new TwoPhaseMoveGame<>(state, ConsoleGame::parseMove);
        game.start();*/

        Application.launch(RedBlueApplication.class, args);


    }

}