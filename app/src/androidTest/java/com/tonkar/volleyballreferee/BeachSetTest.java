package com.tonkar.volleyballreferee;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tonkar.volleyballreferee.business.game.BeachSet;
import com.tonkar.volleyballreferee.business.web.Authentication;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;
import com.tonkar.volleyballreferee.rules.Rules;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BeachSetTest {

    @Test
    public void point_add() {
        BeachSet set = new BeachSet(Rules.officialBeachRules(), 8, TeamType.GUEST, null, null);
        assertTrue(set.getPointsLadder().isEmpty());
        assertEquals(1, set.addPoint(TeamType.HOME));
        assertEquals(TeamType.HOME, set.getPointsLadder().get(0));
        assertEquals(2, set.addPoint(TeamType.HOME));
        assertEquals(TeamType.HOME, set.getPointsLadder().get(1));
        assertEquals(1, set.addPoint(TeamType.GUEST));
        assertEquals(TeamType.GUEST, set.getPointsLadder().get(2));
        assertEquals(3, set.addPoint(TeamType.HOME));
        assertEquals(TeamType.HOME, set.getPointsLadder().get(3));
    }

    @Test
    public void point_remove() {
        BeachSet set = new BeachSet(Rules.officialBeachRules(), 12, TeamType.HOME, null, null);
        set.addPoint(TeamType.HOME);
        set.addPoint(TeamType.HOME);
        assertEquals(TeamType.HOME, set.removeLastPoint());
        assertEquals(TeamType.HOME, set.getPointsLadder().get(0));
        assertEquals(TeamType.HOME, set.removeLastPoint());
        assertTrue(set.getPointsLadder().isEmpty());
        assertNull(set.removeLastPoint());
    }

    @Test
    public void team_leading() {
        BeachSet set = new BeachSet(Rules.officialBeachRules(), 5, TeamType.HOME, null, null);

        set.addPoint(TeamType.HOME);
        assertEquals(TeamType.HOME, set.getLeadingTeam());

        set.addPoint(TeamType.GUEST);
        set.addPoint(TeamType.GUEST);
        assertEquals(TeamType.GUEST, set.getLeadingTeam());
    }

    @Test
    public void team_serving() {
        BeachSet set = new BeachSet(Rules.officialBeachRules(), 7, TeamType.GUEST, null, null);
        assertEquals(TeamType.GUEST, set.getServingTeam());

        set.addPoint(TeamType.GUEST);
        assertEquals(TeamType.GUEST, set.getServingTeam());

        set.addPoint(TeamType.HOME);
        assertEquals(TeamType.HOME, set.getServingTeam());
    }

    @Test
    public void winSet_normal() {
        BeachSet set = new BeachSet(Rules.officialBeachRules(), 4, TeamType.HOME, null, null);

        for (int index = 0; index < 4; index++) {
            assertFalse(set.isSetCompleted());
            set.addPoint(TeamType.GUEST);
        }

        assertTrue(set.isSetCompleted());
    }

    @Test
    public void winSet_2PointsGap() {
        BeachSet set = new BeachSet(Rules.officialBeachRules(), 3, TeamType.HOME, null, null);

        for (int index = 0; index < 2; index++) {
            assertFalse(set.isSetCompleted());
            set.addPoint(TeamType.GUEST);
            set.addPoint(TeamType.HOME);
        }

        set.addPoint(TeamType.HOME);
        assertFalse(set.isSetCompleted());
        assertTrue(set.isSetPoint());
        set.addPoint(TeamType.HOME);
        assertTrue(set.isSetCompleted());
    }

    @Test
    public void winSet_1PointGap() {
        Rules rules = new Rules(Authentication.VBR_USER_ID, "My rules", System.currentTimeMillis(),
                3, 21, true, 15, false, true,
                true, 1, 30,true, 30, true, 180,
                Rules.FIVB_LIMITATION, 0, true, 7, 5, 9999);
        BeachSet set = new BeachSet(rules, rules.getPointsPerSet(), TeamType.GUEST, null, null);

        for (int index = 0; index < 20; index++) {
            assertFalse(set.isSetCompleted());
            set.addPoint(TeamType.GUEST);
            set.addPoint(TeamType.HOME);
        }

        assertTrue(set.isSetPoint());
        set.addPoint(TeamType.GUEST);
        assertTrue(set.isSetCompleted());
    }
}
