import controller.ColorGameState;
import game.State;
import model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.State.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class StateTest {
    private ColorGameState state;

    @BeforeEach
    void setup() {
        state = new ColorGameState();
    }
    static Stream<Arguments> providePlayer1StartingPositions(){
        return Stream.of(
                Arguments.of(Arrays.asList(
                        new Position(0, 4),
                        new Position(2, 2),
                        new Position(4, 0)
                ))
        );
    }

    static Stream<Arguments> providePositionsForOutOfBoundsTesting(){
        return Stream.of(
                Arguments.of(new Position(-1, 0), true),
                Arguments.of(new Position(0, -1), true),
                Arguments.of(new Position(-1, -1), true),
                Arguments.of(new Position(11, 1), true),
                Arguments.of(new Position(1, 11), true),
                Arguments.of(new Position(11, 11), true),

                Arguments.of(new Position(0, 0), false),
                Arguments.of(new Position(4, 4), false)
        );
    }

    static Stream<Arguments> providePositionsForIsLegalMoveTesting(){
        return Stream.of(
                //Test cases for moves that are out of bounds and on the edges
                Arguments.of(new Position(0, 4), new Position(0, 5), false),
                Arguments.of(new Position(0, 4), new Position(-1, 4), false),
                Arguments.of(new Position(0, 4), new Position(1, 4), true),
                Arguments.of(new Position(0, 4), new Position(0, 3), true),


                //Test cases for moves that go further than one vertical or horizontal unit
                Arguments.of(new Position(2, 2), new Position(4, 2), false),
                Arguments.of(new Position(2, 2), new Position(2, 4), false),
                Arguments.of(new Position(2, 2), new Position(0, 2), false),
                Arguments.of(new Position(2, 2), new Position(2, 0), false),

                //Test cases for moves that go one horizontal unit but do so diagonally
                Arguments.of(new Position(2, 2), new Position(1, 1), false),
                Arguments.of(new Position(2, 2), new Position(1, 3), false),
                Arguments.of(new Position(2, 2), new Position(3, 1), false),
                Arguments.of(new Position(2, 2), new Position(3, 3), false),

                //Test cases for possible moves
                Arguments.of(new Position(2, 2), new Position(1, 2), true),
                Arguments.of(new Position(2, 2), new Position(3, 2), true),
                Arguments.of(new Position(2, 2), new Position(2, 1), true),
                Arguments.of(new Position(2, 2), new Position(2, 3), true)
        );
    }

    static Stream<Arguments> providePositionsForIsLegalMoveTestingAfterMoveMade() {
        return Stream.of(
                //Test cases for valid moves to the place from where we moved
                Arguments.of(new Position(2, 3), new Position(2, 2), true),
                Arguments.of(new Position(3, 2), new Position(2, 2), true),
                Arguments.of(new Position(1, 2), new Position(2, 2), true),

                //Test case for trying to move back
                Arguments.of(new Position(2, 1), new Position(2, 2), false),

                //Test cases for trying to move onto the position where we moved
                Arguments.of(new Position(2, 2), new Position(2, 1), false),
                Arguments.of(new Position(2, 0), new Position(2, 1), false),
                Arguments.of(new Position(1, 1), new Position(2, 1), false),
                Arguments.of(new Position(3, 1), new Position(2, 1), false)
        );
    }






    @ParameterizedTest
    @MethodSource("providePlayer1StartingPositions")
    void testInitialBoardPositions(List<Position> positions) {
        //Verifying starting player positions
        for (int i = 0; i < ColorGameState.getBOARD_SIZE(); i++) {
            for (int j = 0; j < ColorGameState.getBOARD_SIZE(); j++) {
                if (positions.contains(new Position(i,j))) {
                    assertEquals(State.Player.PLAYER_1, state.getBoard()[i][j],
                            String.format("Test failed. The position (%d,%d) should be a PLAYER_1 position", i, j));
                } else {
                    assertEquals(State.Player.PLAYER_2, state.getBoard()[i][j],
                            String.format("Test failed. The position (%d,%d) should be a PLAYER_2 position", i, j));
                }
            }
        }
    }

    @Test
    void testInitialPlayer(){
        //Verifying starting player
        assertEquals(State.Player.PLAYER_1, state.getNextPlayer(),
                "Test failed. The initial player should be PLAYER_1");
    }

    @ParameterizedTest
    @MethodSource("providePlayer1StartingPositions")
    void testInitialPlayer1PositionsInList(List<Position> positions){
        // Verifying player1 positions
        for (Position pos: positions) {
            assertTrue(state.getPlayer1Positions().contains(pos),
                    String.format("Test failed. The position %s%n should be a PLAYER_1 position",
                            pos));
        }
    }

    @ParameterizedTest
    @MethodSource("providePlayer1StartingPositions")
    void testInitialPlayer2PositionsInList(List<Position> positions){
        // Verifying player2 positions
        List<Position> expectedPlayer2Positions = new ArrayList<>();
        for (int i = 0; i < ColorGameState.getBOARD_SIZE(); i++) {
            for (int j = 0; j < ColorGameState.getBOARD_SIZE(); j++) {
                if (!positions.contains(new Position(i,j))) {
                    expectedPlayer2Positions.add(new Position(i, j));
                }
            }
        }

        assertTrue(state.getPlayer2Positions().containsAll(expectedPlayer2Positions),
                "Test failed. Some expected positions are not present in the list");
        assertEquals(expectedPlayer2Positions.size(), state.getPlayer2Positions().size(),
                "Test failed. Player2Positions are not the expected size");
    }

    @Test
    public void testSwitchPlayer() {
        assertEquals(Player.PLAYER_1, state.getNextPlayer());
        state.switchPlayer();
        assertEquals(Player.PLAYER_2, state.getNextPlayer());
        state.switchPlayer();
        assertEquals(Player.PLAYER_1, state.getNextPlayer());
    }

    @ParameterizedTest
    @MethodSource("providePositionsForOutOfBoundsTesting")
    void testingIsPositionOutOfBounds(Position position, boolean truth) {
       assertEquals(truth, state.isPositionOufOfBounds(position),
               String.format("Test failed. %s%n %s be out of bounds", position, truth?"shouldn't":"should"));
    }

    @ParameterizedTest
    @MethodSource("providePlayer1StartingPositions")
    void testingIsLegalMoveFrom(List<Position> positions) {

        for (Position pos: positions) {
            assertTrue(state.isLegalToMoveFrom(pos),
                    String.format("Test failed. The player should be able to move from %s%n", pos));
            state.switchPlayer();
            assertFalse(state.isLegalToMoveFrom(pos),
                    String.format("Test failed. The player shouldn't be able to move from %s%n", pos));
            state.switchPlayer();
        }
    }

    @ParameterizedTest
    @MethodSource("providePositionsForIsLegalMoveTesting")
    void testingIsLegalMove(Position from, Position to, boolean truth) {
        assertEquals(truth, state.isLegalMove(from, to),
                String.format("Test failed. Moving from %s%n to %s%n %s be possible",
                        from, to, truth?"should":"shouldn't"));
    }

    @ParameterizedTest
    @MethodSource("providePositionsForIsLegalMoveTestingAfterMoveMade")
    void testingIsLegalMoveAfterMoveMade(Position from, Position to, boolean truth) {
        state.makeMove(new Position(2,2), new Position(2,1));
        assertEquals(truth, state.isLegalMove(from, to),
                String.format("Test failed. Moving from %s%n to %s%n %s be possible",
                        from, to, truth?"should":"shouldn't"));
    }


    @Test
    void testIsGameOverForTeamRed(){
        state.makeMove(new Position(0, 4), new Position(0,3));
        state.makeMove(new Position(1, 4), new Position(0,4));
        state.makeMove(new Position(0, 3), new Position(0,2));
        state.makeMove(new Position(0, 4), new Position(0,3));

        assertFalse(state.isGameOver());

        state.makeMove(new Position(4, 0), new Position(4,1));
        state.makeMove(new Position(3, 0), new Position(4,0));
        state.makeMove(new Position(4, 1), new Position(4,2));

        assertTrue(state.isGameOver());
    }

    @Test
    void testIsGameOverForTeamBlue(){
        state.makeMove(new Position(0, 4), new Position(0,3));
        state.makeMove(new Position(1, 4), new Position(0,4));

        state.makeMove(new Position(2, 2), new Position(2,1));
        state.makeMove(new Position(3, 2), new Position(2,2));

        state.makeMove(new Position(2, 1), new Position(2,0));
        state.makeMove(new Position(4, 2), new Position(3,2));

        state.makeMove(new Position(2, 0), new Position(3,0));
        state.makeMove(new Position(4, 1), new Position(4,2));

        state.makeMove(new Position(0, 3), new Position(0,4));
        assertFalse(state.isGameOver());


        state.makeMove(new Position(3, 1), new Position(2,1));
        assertTrue(state.isGameOver());
    }


    @Test
    void testForUndoLastMove(){
        assertThrows(NoSuchElementException.class, () -> state.undoLastMove(),
                "Test failed. There shouldn't be any moves to be undone");

        state.makeMove(new Position(0, 4), new Position(0,3));
        state.makeMove(new Position(1, 4), new Position(0,4));
        assertFalse(state.getPreviousMoves().isEmpty());
        state.undoLastMove();
        state.undoLastMove();
        assertTrue(state.getPreviousMoves().isEmpty());
    }


}
