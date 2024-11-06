import model.Position;
import org.junit.jupiter.api.Test;
import view.ConsoleGame;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleGameTest {
    @Test
    void testParseMove(){
        assertEquals(new Position(2, 2), ConsoleGame.parseMove("2 2"));
        assertEquals(new Position(2, 2), ConsoleGame.parseMove("    2   2    "));
        assertEquals(new Position(2, 2), ConsoleGame.parseMove("\t2\n2\t"));

        assertThrows(IllegalArgumentException.class, ()->ConsoleGame.parseMove("-2 -5"));
        assertThrows(IllegalArgumentException.class, ()->ConsoleGame.parseMove(String.format("%d %d", -4, -5)));

        assertThrows(IllegalArgumentException.class, ()->ConsoleGame.parseMove("22"));
        assertThrows(IllegalArgumentException.class, ()->ConsoleGame.parseMove("string"));
        assertThrows(IllegalArgumentException.class, ()->ConsoleGame.parseMove(""));
        assertThrows(IllegalArgumentException.class, ()->ConsoleGame.parseMove(" "));


    }
}
