package com.tonkar.volleyballreferee.ui.game.sanction;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.engine.game.IGame;
import com.tonkar.volleyballreferee.engine.game.sanction.SanctionType;
import com.tonkar.volleyballreferee.engine.stored.api.ApiPlayer;
import com.tonkar.volleyballreferee.engine.team.IBaseTeam;
import com.tonkar.volleyballreferee.engine.team.TeamType;
import com.tonkar.volleyballreferee.ui.team.PlayerToggleButton;
import com.tonkar.volleyballreferee.ui.util.UiUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MisconductSanctionSelectionFragment extends Fragment {

    private SanctionSelectionDialog mSanctionSelectionDialog;
    private IGame                   mGame;
    private TeamType                mTeamType;
    private MisconductPlayerAdapter mMisconductPlayerAdapter;
    private PlayerToggleButton      mYellowCardButton;
    private PlayerToggleButton      mRedCardButton;
    private PlayerToggleButton      mExpulsionCardButton;
    private PlayerToggleButton      mDisqualificationCardButton;
    private ViewGroup               mYellowCardLayout;
    private ViewGroup               mRedCardLayout;
    private ViewGroup               mExpulsionCardLayout;
    private ViewGroup               mDisqualificationCardLayout;
    private SanctionType            mSelectedMisconductSanction;

    void init(SanctionSelectionDialog sanctionSelectionDialog, IGame game, TeamType teamType) {
        mSanctionSelectionDialog = sanctionSelectionDialog;
        mGame = game;
        mTeamType = teamType;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_misconduct_sanction_selection, container, false);

        if (mGame != null && mTeamType != null) {
            mYellowCardButton = view.findViewById(R.id.yellow_card_button);
            mRedCardButton = view.findViewById(R.id.red_card_button);
            mExpulsionCardButton = view.findViewById(R.id.red_card_expulsion_button);
            mDisqualificationCardButton = view.findViewById(R.id.red_card_disqualification_button);

            mYellowCardLayout = view.findViewById(R.id.yellow_card_layout);
            mRedCardLayout = view.findViewById(R.id.red_card_layout);
            mExpulsionCardLayout = view.findViewById(R.id.red_card_expulsion_layout);
            mDisqualificationCardLayout = view.findViewById(R.id.red_card_disqualification_layout);

            mYellowCardButton.setColor(getContext(), mGame.getTeamColor(mTeamType));
            mRedCardButton.setColor(getContext(), mGame.getTeamColor(mTeamType));
            mExpulsionCardButton.setColor(getContext(), mGame.getTeamColor(mTeamType));
            mDisqualificationCardButton.setColor(getContext(), mGame.getTeamColor(mTeamType));

            mYellowCardButton.setOnCheckedChangeListener((cButton, isChecked) -> {
                UiUtils.animate(getContext(), cButton);
                if (isChecked) {
                    mSelectedMisconductSanction = SanctionType.YELLOW;
                    mRedCardButton.setChecked(false);
                    mExpulsionCardButton.setChecked(false);
                    mDisqualificationCardButton.setChecked(false);
                    mSanctionSelectionDialog.computeOkAvailability(R.id.misconduct_sanction_tab);
                }
            });

            mRedCardButton.setOnCheckedChangeListener((cButton, isChecked) -> {
                UiUtils.animate(getContext(), cButton);
                if (isChecked) {
                    mSelectedMisconductSanction = SanctionType.RED;
                    mYellowCardButton.setChecked(false);
                    mExpulsionCardButton.setChecked(false);
                    mDisqualificationCardButton.setChecked(false);
                    mSanctionSelectionDialog.computeOkAvailability(R.id.misconduct_sanction_tab);
                }
            });

            mExpulsionCardButton.setOnCheckedChangeListener((cButton, isChecked) -> {
                UiUtils.animate(getContext(), cButton);
                if (isChecked) {
                    mSelectedMisconductSanction = SanctionType.RED_EXPULSION;
                    mYellowCardButton.setChecked(false);
                    mRedCardButton.setChecked(false);
                    mDisqualificationCardButton.setChecked(false);
                    mSanctionSelectionDialog.computeOkAvailability(R.id.misconduct_sanction_tab);
                }
            });

            mDisqualificationCardButton.setOnCheckedChangeListener((cButton, isChecked) -> {
                UiUtils.animate(getContext(), cButton);
                if (isChecked) {
                    mSelectedMisconductSanction = SanctionType.RED_DISQUALIFICATION;
                    mYellowCardButton.setChecked(false);
                    mRedCardButton.setChecked(false);
                    mExpulsionCardButton.setChecked(false);
                    mSanctionSelectionDialog.computeOkAvailability(R.id.misconduct_sanction_tab);
                }
            });


            GridView misconductPlayerGrid = view.findViewById(R.id.misconduct_player_grid);

            mMisconductPlayerAdapter = new MisconductPlayerAdapter(getActivity().getLayoutInflater(), getContext(), mGame, mTeamType);
            misconductPlayerGrid.setAdapter(mMisconductPlayerAdapter);

            onMisconductPlayerSelected(-1);

        }

        return view;
    }

    SanctionType getSelectedMisconductSanction() {
        return mSelectedMisconductSanction;
    }

    int getSelectedMisconductPlayer() {
        return mMisconductPlayerAdapter.getSelectedPlayer();
    }

    private void onMisconductPlayerSelected(int number) {
        Set<SanctionType> possibleMisconductSanctions = number >= 0 ? mGame.getPossibleMisconductSanctions(mTeamType, number) : new HashSet<>();

        mYellowCardLayout.setVisibility(possibleMisconductSanctions.contains(SanctionType.YELLOW) ? View.VISIBLE : View.GONE);
        mRedCardLayout.setVisibility(possibleMisconductSanctions.contains(SanctionType.RED) ? View.VISIBLE : View.GONE);
        mExpulsionCardLayout.setVisibility(possibleMisconductSanctions.contains(SanctionType.RED_EXPULSION) ? View.VISIBLE : View.GONE);
        mDisqualificationCardLayout.setVisibility(possibleMisconductSanctions.contains(SanctionType.RED_DISQUALIFICATION) ? View.VISIBLE : View.GONE);

        mSelectedMisconductSanction = null;
        mYellowCardButton.setChecked(false);
        mRedCardButton.setChecked(false);
        mExpulsionCardButton.setChecked(false);
        mDisqualificationCardButton.setChecked(false);
        mSanctionSelectionDialog.computeOkAvailability(R.id.misconduct_sanction_tab);
    }

    private class MisconductPlayerAdapter extends BaseAdapter {

        private final LayoutInflater  mLayoutInflater;
        private final Context         mContext;
        private final IBaseTeam       mTeamService;
        private final TeamType        mTeamType;
        private final List<ApiPlayer> mPlayers;
        private       int             mSelectedPlayer;

        private MisconductPlayerAdapter(LayoutInflater layoutInflater, Context context, IBaseTeam teamService, TeamType teamType) {
            mLayoutInflater = layoutInflater;
            mContext = context;
            mTeamService = teamService;
            mTeamType = teamType;
            mPlayers = new ArrayList<>(mTeamService.getPlayers(mTeamType));
            mSelectedPlayer = -1;
        }

        @Override
        public int getCount() {
            // Coach + players
            return 1 + mPlayers.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, final ViewGroup parent) {
            PlayerToggleButton button;

            if (view == null) {
                button = (PlayerToggleButton) mLayoutInflater.inflate(R.layout.player_toggle_item, null);
            } else {
                button = (PlayerToggleButton) view;
            }

            final int player;

            if (mPlayers.size() > position) {
                player = mPlayers.get(position).getNum();
                button.setText(UiUtils.formatNumberFromLocale(player));
                if (mTeamService.isLibero(mTeamType, player)) {
                    button.setColor(mContext, mTeamService.getLiberoColor(mTeamType));
                } else {
                    button.setColor(mContext, mTeamService.getTeamColor(mTeamType));
                }
            } else {
                player = 100; // coach
                button.setText(mContext.getString(R.string.coach_abbreviation));
                button.setColor(mContext, mTeamService.getTeamColor(mTeamType));
            }


            button.setOnCheckedChangeListener((cButton, isChecked) -> {
                UiUtils.animate(mContext, cButton);
                if (isChecked) {
                    for (int index = 0; index < parent.getChildCount(); index++) {
                        PlayerToggleButton child = (PlayerToggleButton) parent.getChildAt(index);
                        if (child != cButton && child.isChecked()) {
                            child.setChecked(false);
                            mSelectedPlayer = -1;
                        }
                    }
                    mSelectedPlayer = player;
                } else {
                    mSelectedPlayer = -1;
                }
                onMisconductPlayerSelected(mSelectedPlayer);
            });

            return button;
        }

        int getSelectedPlayer() {
            return mSelectedPlayer;
        }
    }
}