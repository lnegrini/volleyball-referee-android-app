package com.tonkar.volleyballreferee.ui.team;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.business.ServicesProvider;
import com.tonkar.volleyballreferee.business.team.TeamDefinition;
import com.tonkar.volleyballreferee.interfaces.team.BaseTeamService;
import com.tonkar.volleyballreferee.interfaces.team.GenderType;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;
import com.tonkar.volleyballreferee.ui.UiUtils;
import com.tonkar.volleyballreferee.ui.data.SavedTeamActivity;

public class QuickTeamSetupFragment extends Fragment {

    private TeamType        mTeamType;
    private BaseTeamService mTeamService;
    private Button          mTeamColorButton;
    private Button          mCaptainButton;
    private ImageButton     mGenderButton;

    public QuickTeamSetupFragment() {
    }

    public static QuickTeamSetupFragment newInstance(TeamType teamType, boolean editable) {
        QuickTeamSetupFragment fragment = new QuickTeamSetupFragment();
        Bundle args = new Bundle();
        args.putString(TeamType.class.getName(), teamType.toString());
        args.putBoolean("editable", editable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("VBR-QTSActivity", "Create team setup fragment");
        View view = inflater.inflate(R.layout.fragment_quick_team_setup, container, false);

        final String teamTypeStr = getArguments().getString(TeamType.class.getName());
        mTeamType = TeamType.valueOf(teamTypeStr);

        final boolean editable = getArguments().getBoolean("editable");

        if (ServicesProvider.getInstance().isSavedTeamsServiceUnavailable()) {
            ServicesProvider.getInstance().restoreSavedTeamsService(getActivity().getApplicationContext());
        }

        mTeamService = ServicesProvider.getInstance().getSavedTeamsService().getCurrentTeam();

        final EditText teamNameInput = view.findViewById(R.id.team_name_input_text);
        mTeamColorButton = view.findViewById(R.id.team_color_button);

        switch (mTeamType) {
            case HOME:
                teamNameInput.setHint(R.string.home_team_hint);
                break;
            case GUEST:
                teamNameInput.setHint(R.string.guest_team_hint);
                break;
        }

        final String teamName = mTeamService.getTeamName(mTeamType);

        teamNameInput.setText(teamName);
        teamNameInput.setEnabled(editable);
        teamNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("VBR-QTSActivity", String.format("Update %s team name", mTeamType.toString()));
                mTeamService.setTeamName(mTeamType, s.toString());
                computeSaveItemVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mCaptainButton = view.findViewById(R.id.team_captain_number_button);
        updateCaptain();
        mCaptainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.animate(getContext(), mCaptainButton);
                switchCaptain();
            }
        });

        if (mTeamService.getTeamColor(mTeamType) == Color.parseColor(TeamDefinition.DEFAULT_COLOR)) {
            teamColorSelected(ShirtColors.getRandomShirtColor(getActivity()));
        } else {
            teamColorSelected(mTeamService.getTeamColor(mTeamType));
        }
        mTeamColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.animate(getContext(), mTeamColorButton);
                selectTeamColor();
            }
        });

        mGenderButton = view.findViewById(R.id.select_gender_button);
        mGenderButton.setEnabled(editable);
        updateGender(mTeamService.getGenderType(mTeamType));
        mGenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.animate(getContext(), mGenderButton);
                GenderType genderType = mTeamService.getGenderType(mTeamType).next();
                updateGender(genderType);
            }
        });

        computeSaveItemVisibility();

        return view;
    }

    private void selectTeamColor() {
        Log.i("VBR-QTSActivity", String.format("Select %s team color", mTeamType.toString()));
        ColorSelectionDialog colorSelectionDialog = new ColorSelectionDialog(getLayoutInflater(), getContext(), getResources().getString(R.string.select_shirts_color)) {
            @Override
            public void onColorSelected(int selectedColor) {
                teamColorSelected(selectedColor);
            }
        };
        colorSelectionDialog.show();
    }

    private void teamColorSelected(int color) {
        Log.i("VBR-QTSActivity", String.format("Update %s team color", mTeamType.toString()));
        UiUtils.colorTeamButton(getActivity(), color, mTeamColorButton);
        mTeamService.setTeamColor(mTeamType, color);
        updateCaptain();
    }

    private void updateCaptain() {
        int captain = mTeamService.getCaptain(mTeamType);
        captainUpdated(captain);
    }

    private void captainUpdated(int number) {
        Log.i("VBR-QTSActivity", String.format("Update %s team captain", mTeamType.toString()));
        mTeamService.setCaptain(mTeamType, number);
        mCaptainButton.setText(String.valueOf(number));
        UiUtils.styleTeamButton(getContext(), mTeamService, mTeamType, number, mCaptainButton);
    }

    private void switchCaptain() {
        int captain = mTeamService.getCaptain(mTeamType);

        switch (captain) {
            case 1:
                captain = 2;
                break;
            case 2:
                captain = 1;
                break;
            default:
                break;
        }

        captainUpdated(captain);
    }

    private void updateGender(GenderType genderType) {
        Context context = getContext();
        mTeamService.setGenderType(mTeamType, genderType);
        switch (genderType) {
            case MIXED:
                mGenderButton.setImageResource(R.drawable.ic_mixed);
                mGenderButton.getDrawable().mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorMixed), PorterDuff.Mode.SRC_IN));
                break;
            case LADIES:
                mGenderButton.setImageResource(R.drawable.ic_ladies);
                mGenderButton.getDrawable().mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorLadies), PorterDuff.Mode.SRC_IN));
                break;
            case GENTS:
                mGenderButton.setImageResource(R.drawable.ic_gents);
                mGenderButton.getDrawable().mutate().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorGents), PorterDuff.Mode.SRC_IN));
                break;
        }
    }

    private void computeSaveItemVisibility() {
        if (getActivity() instanceof SavedTeamActivity) {
            ((SavedTeamActivity) getActivity()).computeSaveItemVisibility();
        }
    }
}