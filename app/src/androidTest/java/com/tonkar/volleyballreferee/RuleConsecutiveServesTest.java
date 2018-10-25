package com.tonkar.volleyballreferee;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tonkar.volleyballreferee.business.game.IndoorGame;
import com.tonkar.volleyballreferee.business.web.Authentication;
import com.tonkar.volleyballreferee.interfaces.ActionOriginType;
import com.tonkar.volleyballreferee.interfaces.team.PositionType;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;
import com.tonkar.volleyballreferee.rules.Rules;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RuleConsecutiveServesTest {

    @Test
    public void consecutive_unlimited() {
        IndoorGame game = createGame(9999);

        for (int index = 0; index < 15; index++) {
            game.addPoint(TeamType.HOME);
            assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
            assertFalse(game.samePlayerHadServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
            assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
            assertFalse(game.samePlayerHadServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        }
    }

    @Test
    public void consecutive_3() {
        IndoorGame game = createGame(3);

        game.addPoint(TeamType.HOME);
        game.addPoint(TeamType.HOME);
        assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
        game.addPoint(TeamType.HOME);
        assertTrue(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
        game.addPoint(TeamType.HOME);
        assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
        game.removeLastPoint();
        assertTrue(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
        game.removeLastPoint();
        assertTrue(game.samePlayerHadServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
    }

    @Test
    public void consecutive_3_opposite() {
        IndoorGame game = createGame(3);

        game.addPoint(TeamType.GUEST);
        game.addPoint(TeamType.GUEST);
        game.addPoint(TeamType.GUEST);
        assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        game.addPoint(TeamType.GUEST);
        assertTrue(game.samePlayerServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        game.addPoint(TeamType.HOME);
        assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        game.removeLastPoint();
        assertTrue(game.samePlayerServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        game.removeLastPoint();
        assertTrue(game.samePlayerHadServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
    }

    @Test
    public void consecutive_2() {
        IndoorGame game = createGame(2);

        game.addPoint(TeamType.HOME);
        game.addPoint(TeamType.HOME);
        assertTrue(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
        game.addPoint(TeamType.GUEST);
        game.addPoint(TeamType.GUEST);
        assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        assertTrue(game.samePlayerHadServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        game.addPoint(TeamType.GUEST);
        assertTrue(game.samePlayerServedNConsecutiveTimes(TeamType.GUEST, game.getPoints(TeamType.GUEST), game.getPointsLadder()));
        game.addPoint(TeamType.HOME);
        game.addPoint(TeamType.HOME);
        assertFalse(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
        game.addPoint(TeamType.HOME);
        assertTrue(game.samePlayerServedNConsecutiveTimes(TeamType.HOME, game.getPoints(TeamType.HOME), game.getPointsLadder()));
    }

    private IndoorGame createGame(int consecutiveServes) {
        Rules rules = new Rules(Authentication.VBR_USER_ID, "My rules", System.currentTimeMillis(),
                5, 25, true, 15, true, true,
                true,2, 30,true, 60, true, 180,
                Rules.FIVB_LIMITATION, 6, false, 0, 0, consecutiveServes);

        IndoorGame game = new IndoorGame(System.currentTimeMillis(), System.currentTimeMillis(), rules);

        for (int index = 1; index <= 6; index++) {
            game.addPlayer(TeamType.HOME, index);
            game.addPlayer(TeamType.GUEST, index);
        }

        game.startMatch();

        for (int index = 1; index <= 6; index++) {
            game.substitutePlayer(TeamType.HOME, index, PositionType.fromInt(index), ActionOriginType.USER);
            game.substitutePlayer(TeamType.GUEST, index, PositionType.fromInt(index), ActionOriginType.USER);
        }

        game.confirmStartingLineup();

        return game;
    }
}
