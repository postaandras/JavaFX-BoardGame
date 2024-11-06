package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import game.State;
import lombok.Getter;
import model.Player;
import org.tinylog.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.hildan.fxgson.FxGson;

public class PersistentPlayerController {
    @Getter
    private static Player Player1;
    @Getter
    private static Player Player2;

    private static final Path jsonFolder = Path.of("json");
    private static final Path currentPlayersJsonPath = Paths.get("json/currentPlayers.json");
    private static final Path playersJsonPath = Paths.get("json/players.json");
    private static final Path stateJsonPath = Paths.get("json/state.json");
    private static final Gson gson = FxGson.fullBuilder().setPrettyPrinting().create();

    private static final Type playerListType = new TypeToken<List<Player>>() {
    }.getType();

    public static void initializePlayers() {
        try {
            if (!Files.exists(jsonFolder)) {
                Files.createDirectory(jsonFolder);
            }
            if (Files.exists(currentPlayersJsonPath)) {
                loadCurrentPlayers();
            } else {
                Logger.info("The file doesn't exist! Creating default JSON file..");
                setDefaultPlayers();
            }


        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

    }

    private static void loadCurrentPlayers() throws IOException {
        String content = Files.readString(currentPlayersJsonPath);
        Player[] currentPlayers = gson.fromJson(content, Player[].class);

        if (currentPlayers != null && currentPlayers.length == 2) {

            Player1 = currentPlayers[0];
            Player2 = currentPlayers[1];

        } else {
            Logger.info("The JSON file is empty! Initializing default data.");
            setDefaultPlayers();
        }
    }

    private static void setDefaultPlayers() throws IOException {
        Player1 = getOrCreatePlayer("PLAYER_1");
        Player2 = getOrCreatePlayer("PLAYER_2");

        writeToFile(currentPlayersJsonPath, gson.toJson(new Player[]{Player1, Player2}));
    }

    private static Player getOrCreatePlayer(String name) {
        Player player = lookForPlayer(name) ?
                loadPlayerFromList(name) :
                new Player(name, 0);

        if (!lookForPlayer(name)) {
            addPlayerToList(player);
        }

        return player;
    }


    public static void addPlayerToList(Player player) {
        try {
            List<Player> allPlayers = Files.exists(playersJsonPath)
                    ? gson.fromJson(Files.readString(playersJsonPath), playerListType)
                    : new ArrayList<>();

            allPlayers.add(player);

            allPlayers = allPlayers.stream().sorted(Comparator.comparing(Player::getScore).reversed().
                    thenComparing(Player::getName)).collect(Collectors.toList());

            writeToFile(playersJsonPath, gson.toJson(allPlayers));
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

    }

    public static boolean lookForPlayer(String name) {
        Logger.info(name);
        try {
            if (Files.exists(playersJsonPath)) {
                Player[] allPlayers = gson.fromJson(Files.readString(playersJsonPath), Player[].class);
                return allPlayers != null && Arrays.stream(allPlayers).anyMatch(
                        player -> player.getName().equals(name));
            } else {
                Logger.info("The file doesn't exist! Creating default JSON file..");
                writeToFile(playersJsonPath, "[]");
            }

        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

        return false;
    }

    public static Player loadPlayerFromList(String name) {
        try {
            if (Files.exists(playersJsonPath)) {
                Player[] allPlayers = gson.fromJson(Files.readString(playersJsonPath), Player[].class);
                return Arrays.stream(allPlayers).filter(player -> player.getName().equals(name)).
                        findFirst().orElse(null);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    private static void updatePlayer(Player player) {
        try {
            List<Player> allPlayers = gson.fromJson(Files.readString(playersJsonPath), playerListType);

            allPlayers.stream().filter(player1 -> player1.getName().
                    equals(player.getName())).findFirst().ifPresent(player1 -> player1.setScore(player.getScore()));


            allPlayers = allPlayers.stream().sorted(Comparator.comparing(Player::getScore).reversed().
                    thenComparing(Player::getName)).collect(Collectors.toList());

            writeToFile(playersJsonPath, gson.toJson(allPlayers));
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public static List<Player> loadPlayersList() {
        try {
            if (Files.exists(playersJsonPath)) {
                return gson.fromJson(Files.readString(playersJsonPath), playerListType);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    public static void updateCurrentPlayers() {
        String jsonString = gson.toJson(new Player[]{Player1, Player2});
        writeToFile(currentPlayersJsonPath, jsonString);
        updatePlayer(Player1);
        updatePlayer(Player2);
    }

    public static void switchPlayer(Player player, int playerNumber) {
        try {
            Player loadedPlayer = lookForPlayer(player.getName()) ? loadPlayerFromList(player.getName()) : player;

            switch (playerNumber) {
                case 1 -> Player1 = loadedPlayer;
                case 2 -> Player2 = loadedPlayer;
                default ->
                        throw new IllegalAccessException(playerNumber + " is not a valid player number. Choose 1 or 2!");
            }

            String jsonString = gson.toJson(new Player[]{Player1, Player2});
            writeToFile(currentPlayersJsonPath, jsonString);
        } catch (IllegalAccessException e) {
            Logger.error(e.getMessage());
        }
    }

    public static void saveState(State state) {
        writeToFile(stateJsonPath, gson.toJson(state));
    }

    public static ColorGameState readCurrentState() {
        try {
            String jsonString = Files.readString(stateJsonPath);
            return gson.fromJson(jsonString, ColorGameState.class);
        } catch (IOException e) {
            Logger.error("Failed to read the current state JSON file", e);
        }
        return new ColorGameState();
    }

    private static void writeToFile(Path path, String jsonString) {
        try {
            Files.write(path, jsonString.getBytes());
            Logger.info("JSON data has been written to file successfully to " + path + ".");
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }
}
