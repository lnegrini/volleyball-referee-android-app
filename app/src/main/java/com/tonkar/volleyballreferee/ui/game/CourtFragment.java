package com.tonkar.volleyballreferee.ui.game;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.business.ServicesProvider;
import com.tonkar.volleyballreferee.interfaces.ActionOriginType;
import com.tonkar.volleyballreferee.interfaces.card.PenaltyCardListener;
import com.tonkar.volleyballreferee.interfaces.card.PenaltyCardService;
import com.tonkar.volleyballreferee.interfaces.team.PositionType;
import com.tonkar.volleyballreferee.interfaces.team.TeamListener;
import com.tonkar.volleyballreferee.interfaces.team.TeamService;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;

import java.util.HashMap;
import java.util.Map;

public abstract class CourtFragment extends Fragment implements NamedGameFragment, TeamListener, PenaltyCardListener {

    protected       View                      mView;
    protected       TeamService               mTeamService;
    protected       PenaltyCardService        mPenaltyService;
    protected       TeamType                  mTeamOnLeftSide;
    protected       TeamType                  mTeamOnRightSide;
    protected final Map<PositionType, Button> mLeftTeamPositions;
    protected final Map<PositionType, Button> mRightTeamPositions;

    public CourtFragment() {
        mLeftTeamPositions = new HashMap<>();
        mRightTeamPositions = new HashMap<>();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTeamService.removeTeamListener(this);
        mPenaltyService.removePenaltyCardListener(this);
        mLeftTeamPositions.clear();
        mRightTeamPositions.clear();
    }

    @Override
    public String getGameFragmentTitle(Context context) {
        return context.getResources().getString(R.string.court_position_tab);
    }

    protected void initView() {
        Log.i("VBR-Court", "Create court fragment");
        mTeamService = ServicesProvider.getInstance().getTeamService();
        mPenaltyService = ServicesProvider.getInstance().getPenaltyCardService();

        mTeamOnLeftSide = mTeamService.getTeamOnLeftSide();
        mTeamOnRightSide = mTeamService.getTeamOnRightSide();
        mTeamService.addTeamListener(this);
        mPenaltyService.addPenaltyCardListener(this);
    }

    protected void addButtonOnLeftSide(final PositionType positionType, final Button button) {
        mLeftTeamPositions.put(positionType, button);
    }

    protected void addButtonOnRightSide(final PositionType positionType, final Button button) {
        mRightTeamPositions.put(positionType, button);
    }

    @Override
    public void onTeamsSwapped(TeamType leftTeamType, TeamType rightTeamType, ActionOriginType actionOriginType) {
        mTeamOnLeftSide = leftTeamType;
        mTeamOnRightSide = rightTeamType;
    }
}
