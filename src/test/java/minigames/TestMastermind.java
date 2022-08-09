package minigames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import minigames.mastermind.model.MastermindModel;
import minigames.mastermind.model.MastermindModelImpl;
import utils.graphics.stagemanager.StageManager;
import utils.graphics.stagemanager.StageManagerImpl;

/**
 * Test class for MastermindModel.
 */
class TestMastermind {

    private final StageManager<String> s = new StageManagerImpl<String>("");
    private final List<String> players = List.of("Luca", "Giovanni", "Lorenzo", "Marco");

    @Test
    void testInput() {
        final MastermindModel<String, String> m = new MastermindModelImpl<String, String>(players, s);
        m.setMaxAttempts(10);
        m.runGame();
        m.doAttempt("");
        m.doAttempt("1232");
        m.doAttempt("12e3");
        m.doAttempt("wasd");
        m.doAttempt("123456789");
        assertEquals(0, m.getNAttempts());
        m.doAttempt("1234");
        assertEquals(1, m.getNAttempts());
    }

    @Test
    void testWin() {
        final MastermindModel<String, String> m = new MastermindModelImpl<String, String>(players, s);
        m.setMaxAttempts(10);
        m.runGame();
        m.doAttempt(m.getSolution());
        assertTrue(m.getWin());
    }

    @Test
    void testLose() {
        final MastermindModel<String, String> m = new MastermindModelImpl<String, String>(players, s);
        m.setMaxAttempts(1);
        m.runGame();
        final char[] attemptDigits = m.getSolution().toCharArray();
        char temp = attemptDigits[3];
        attemptDigits[3] = attemptDigits[0];
        attemptDigits[0] = temp;
        final String attempt = "" + attemptDigits[0] + attemptDigits[1] + attemptDigits[2] + attemptDigits[3];
        m.doAttempt(attempt);
        assertTrue(m.getLose());
    }

}
