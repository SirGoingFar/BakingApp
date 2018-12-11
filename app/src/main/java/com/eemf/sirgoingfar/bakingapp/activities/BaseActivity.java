package com.eemf.sirgoingfar.bakingapp.activities;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.eemf.sirgoingfar.bakingapp.R;

import java.util.List;


public class BaseActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Fragment currentFragment;
    protected FragmentManager fragmentManager = getSupportFragmentManager();

    protected void startFragmentOnMasterView(Fragment fragment, boolean addToBackStack, boolean allowStateLoss) {
        if (fragment != null) {
            currentFragment = fragment;
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container_master, fragment, fragment.getClass().getName());
            if (addToBackStack) {
                ft.addToBackStack(fragment.getClass().getName());
            }

            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }
    }

    public void startFragmentOnMasterView(Fragment fragment, boolean addToBackStack) {
        startFragmentOnMasterView(fragment, addToBackStack, false);
    }

    public void startFragmentAllowStateLossOnMasterView(Fragment fragment, boolean addToBackStack) {
        startFragmentOnMasterView(fragment, addToBackStack, true);
    }

    protected void startFragmentOnDetailView(Fragment fragment, boolean addToBackStack, boolean allowStateLoss) {
        if (fragment != null) {
            currentFragment = fragment;
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container_detail, fragment, fragment.getClass().getName());
            if (addToBackStack) {
                ft.addToBackStack(fragment.getClass().getName());
            }

            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }
    }

    public void startFragmentOnDetailView(Fragment fragment, boolean addToBackStack) {
        startFragmentOnDetailView(fragment, addToBackStack, false);
    }

    public void startFragmentAllowStateLossOnDetailView(Fragment fragment, boolean addToBackStack) {
        startFragmentOnDetailView(fragment, addToBackStack, true);
    }

    public void closeFragment() {
        if (fragmentManager.getBackStackEntryCount() > 0 && isActivityStarted()) {
            hideKeyboard();
            fragmentManager.popBackStack();
        } else {
            hideKeyboard();
            finish();
        }
    }

    public Fragment getCurrentFragment() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments.isEmpty()) {
            return null;
        }
        return fragments.get(fragments.size() - 1);
    }

    public boolean isActivityStarted() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }
    public void showKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    public void showKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            showKeyboard(view);
        }
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public boolean isDetailViewAvailable() {
        return findViewById(R.id.container_detail) != null;
    }

    public void showOrHideDetailView(boolean show) {

        View detailView = findViewById(R.id.container_detail);

        if (!isDetailViewAvailable())
            return;

        if (show)
            detailView.setVisibility(View.VISIBLE);
        else
            detailView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        if (fragmentManager == null)
            return;

        if (fragmentManager.getBackStackEntryCount() <= 1)
            finish();
        else
            super.onBackPressed();
    }

    protected void popBackStackTo(@NonNull String fragmentName) {

        if (TextUtils.isEmpty(fragmentName) || fragmentManager == null || fragmentManager.getBackStackEntryCount() == 0)
            return;

        fragmentManager.popBackStack(fragmentName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void setActionBarTitle(@NonNull String title) {

        if (actionBar == null)
            actionBar = getSupportActionBar();

            actionBar.setTitle(title);
    }
}
