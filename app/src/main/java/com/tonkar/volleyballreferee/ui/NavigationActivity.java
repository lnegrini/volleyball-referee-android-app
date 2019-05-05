package com.tonkar.volleyballreferee.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.business.PrefUtils;
import com.tonkar.volleyballreferee.business.billing.BillingManager;
import com.tonkar.volleyballreferee.api.ApiUtils;
import com.tonkar.volleyballreferee.interfaces.Tags;
import com.tonkar.volleyballreferee.interfaces.billing.BillingService;
import com.tonkar.volleyballreferee.ui.billing.PurchasesListActivity;
import com.tonkar.volleyballreferee.ui.data.RecordedGamesListActivity;
import com.tonkar.volleyballreferee.ui.data.StoredRulesListActivity;
import com.tonkar.volleyballreferee.ui.data.StoredTeamsListActivity;
import com.tonkar.volleyballreferee.ui.setup.ScheduledGamesListActivity;
import com.tonkar.volleyballreferee.ui.util.UiUtils;

public abstract class NavigationActivity extends AppCompatActivity {

    protected DrawerLayout mDrawerLayout;

    protected abstract String getToolbarTitle();

    protected abstract int getCheckedItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initNavigationMenu() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getToolbarTitle());
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(getCheckedItem()).setChecked(true);

        computeAvailableGamesItemVisibility(navigationView);
        computePurchaseItemVisibility(navigationView.getMenu().findItem(R.id.action_purchase));

        navigationView.setNavigationItemSelectedListener(item -> {
            if (getCheckedItem() != item.getItemId()) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Log.i(Tags.MAIN_UI, "Home");
                        UiUtils.navigateToHome(this, R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case R.id.action_purchase:
                        Log.i(Tags.BILLING, "Purchase");
                        Intent intent = new Intent(this, PurchasesListActivity.class);
                        startActivity(intent);
                        UiUtils.animateForward(this);
                        break;
                    case R.id.action_stored_rules:
                        Log.i(Tags.STORED_RULES, "Stored Rules");
                        intent = new Intent(this, StoredRulesListActivity.class);
                        startActivity(intent);
                        UiUtils.animateForward(this);
                        break;
                    case R.id.action_settings:
                        Log.i(Tags.SETTINGS, "Settings");
                        intent = new Intent(this, SettingsActivity.class);
                        startActivity(intent);
                        UiUtils.animateForward(this);
                        break;
                    case R.id.action_stored_games:
                        Log.i(Tags.STORED_GAMES, "Stored games");
                        intent = new Intent(this, RecordedGamesListActivity.class);
                        startActivity(intent);
                        UiUtils.animateForward(this);
                        break;
                    case R.id.action_stored_teams:
                        Log.i(Tags.STORED_TEAMS, "Stored teams");
                        intent = new Intent(this, StoredTeamsListActivity.class);
                        startActivity(intent);
                        UiUtils.animateForward(this);
                        break;
                    case R.id.action_available_games:
                        if (PrefUtils.canSync(this)) {
                            Log.i(Tags.SCHEDULE_UI, "Scheduled games");
                            intent = new Intent(this, ScheduledGamesListActivity.class);
                            startActivity(intent);
                            UiUtils.animateForward(this);
                        }
                        break;
                    case R.id.action_live_games_vbr_com:
                        Log.i(Tags.WEB, "Live games on VBR.com");
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApiUtils.WEB_APP_LIVE_URL));
                        startActivity(intent);
                        UiUtils.animateForward(this);
                        break;
                    case R.id.action_facebook:
                        Log.i(Tags.WEB, "Facebook");
                        Intent browserIntent;
                        try {
                            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1983857898556706"));
                            startActivity(browserIntent);
                            UiUtils.animateForward(this);
                        } catch (ActivityNotFoundException e) {
                            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/VolleyballReferee/"));
                            startActivity(browserIntent);
                            UiUtils.animateForward(this);
                        }
                        break;
                    case R.id.action_credits:
                        Log.i(Tags.MAIN_UI, "Credits");
                        intent = new Intent(this, CreditsActivity.class);
                        startActivity(intent);
                        UiUtils.animateForward(this);
                        break;
                }
            }
            mDrawerLayout.closeDrawers();
            return true;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(Tags.MAIN_UI, "Drawer");
                if (isNavigationDrawerOpen()) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavigationDrawerOpen()) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isNavigationDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void computeAvailableGamesItemVisibility(NavigationView navigationView) {
        navigationView.getMenu().findItem(R.id.action_available_games).setVisible(PrefUtils.canSync(this));
    }

    protected void computePurchaseItemVisibility(MenuItem item) {
        final BillingService billingService = new BillingManager(this);
        billingService.addBillingListener(() -> item.setVisible(!billingService.isAllPurchased()));
        billingService.executeServiceRequest(() -> item.setVisible(!billingService.isAllPurchased()));
    }
}
