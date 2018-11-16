package com.eemf.sirgoingfar.bakingapp.activities;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.eemf.sirgoingfar.bakingapp.R;

import java.util.List;


public class BaseActivity extends AppCompatActivity {

    private Fragment currentFragment;

    protected void startFragmentOnMasterView(Fragment fragment, boolean addToBackStack, boolean allowStateLoss) {
        if (fragment != null) {
            currentFragment = fragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 && isActivityStarted()) {
            hideKeyboard();
            getSupportFragmentManager().popBackStack();
        } else {
            hideKeyboard();
            finish();
        }
    }

    public Fragment getCurrentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
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
}
