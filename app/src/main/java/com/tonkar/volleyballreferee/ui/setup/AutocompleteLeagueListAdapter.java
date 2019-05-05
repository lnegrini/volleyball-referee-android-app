package com.tonkar.volleyballreferee.ui.setup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.api.ApiLeagueDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class AutocompleteLeagueListAdapter extends ArrayAdapter<ApiLeagueDescription> {

    private final LayoutInflater             mLayoutInflater;
    private final List<ApiLeagueDescription> mStoredLeagueList;
    private final List<ApiLeagueDescription> mFilteredStoredLeagueList;
    private final NameFilter                 mNameFilter;

    public AutocompleteLeagueListAdapter(Context context, LayoutInflater layoutInflater, List<ApiLeagueDescription> storedLeagueList) {
        super(context, R.layout.autocomplete_list_item, storedLeagueList);
        mLayoutInflater = layoutInflater;
        mStoredLeagueList = storedLeagueList;
        mFilteredStoredLeagueList = new ArrayList<>();
        mFilteredStoredLeagueList.addAll(mStoredLeagueList);
        mNameFilter = new NameFilter();
    }

    @Override
    public int getCount() {
        return mFilteredStoredLeagueList.size();
    }

    @Override
    public ApiLeagueDescription getItem(int index) {
        return mFilteredStoredLeagueList.get(index);
    }

    @Override
    public long getItemId(int index) {
        return 0;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        TextView leagueTextView;

        if (view == null) {
            leagueTextView = (TextView) mLayoutInflater.inflate(R.layout.autocomplete_list_item, null);
        } else {
            leagueTextView = (TextView) view;
        }

        ApiLeagueDescription league = mFilteredStoredLeagueList.get(index);
        leagueTextView.setText(league.getName());

        return leagueTextView;
    }

    @Override
    public Filter getFilter() {
        return mNameFilter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                results.values = mStoredLeagueList;
                results.count = mStoredLeagueList.size();
            } else {
                String lowerCaseText = prefix.toString().toLowerCase(Locale.getDefault());

                List<ApiLeagueDescription> matchValues = new ArrayList<>();

                for (ApiLeagueDescription league : mStoredLeagueList) {
                    if (lowerCaseText.isEmpty() || league.getName().toLowerCase(Locale.getDefault()).contains(lowerCaseText)) {
                        matchValues.add(league);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredStoredLeagueList.clear();

            if (results.values != null) {
                mFilteredStoredLeagueList.clear();
                mFilteredStoredLeagueList.addAll((Collection<? extends ApiLeagueDescription>) results.values);
            }

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
