package view;

import controller.ColorGameState;
import game.console.TwoPhaseMoveGame;
import javafx.application.Application;
import model.Position;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleGame {
    public static void main(String[] args) throws IOException {
        Logger.info("Game started");
        if (args.length != 0 && args[0].equals("-c")) {
            var state = new ColorGameState();
            var game = new TwoPhaseMoveGame<Position>(state, ConsoleGame::parseMove);
            game.start();
        } else {
            Application.launch(RedBlueApplication.class, args);
        }
    }
    public static Position parseMove(String s) {
        s = s.trim();
        if (!s.matches("\\d+\\s+\\d+")) {
            throw new IllegalArgumentException();
        }
        var scanner = new Scanner(s);
        return new Position(scanner.nextInt(), scanner.nextInt());
    }
}