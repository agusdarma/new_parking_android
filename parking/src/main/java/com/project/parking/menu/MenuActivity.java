package com.project.parking.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.goka.blurredgridmenu.GridMenu;
import com.goka.blurredgridmenu.GridMenuFragment;
import com.project.parking.R;
import com.project.parking.activity.ChangePasswordActivity;
import com.project.parking.activity.LoginActivity;
import com.project.parking.activity.LogoutActivity;
import com.project.parking.data.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yohanes on 14/06/2017.
 */

public class MenuActivity extends AppCompatActivity {

    private GridMenuFragment mGridMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridMenuFragment = GridMenuFragment.newInstance(R.drawable.back);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, mGridMenuFragment);
        tx.addToBackStack(null);
        tx.commit();

        setupGridMenu();

        mGridMenuFragment.setOnClickMenuListener(new GridMenuFragment.OnClickMenuListener() {
            @Override
            public void onClickMenu(GridMenu gridMenu, int position) {
               Intent i = null;
               if(Constants.CODE_LOGOUT.equals(gridMenu.getCode())){
                   i = new Intent(MenuActivity.this, LogoutActivity.class);
               }
               else if(Constants.CODE_CHANGE_PASSWORD.equals(gridMenu.getCode())){
                   i = new Intent(MenuActivity.this, ChangePasswordActivity.class);
               }
                startActivity(i);
                finish();

                Toast.makeText(MenuActivity.this, "Title:" + gridMenu.getTitle() + ", Position:" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGridMenu() {
        List<GridMenu> menus = new ArrayList<>();
        menus.add(new GridMenu(Constants.CODE_CHANGE_PASSWORD, MenuActivity.this.getResources().getString(R.string.buton_menu_change_password), R.drawable.home));
        menus.add(new GridMenu(Constants.CODE_LIST_MALL, MenuActivity.this.getResources().getString(R.string.buton_menu_mall), R.drawable.calendar));
        menus.add(new GridMenu(Constants.CODE_HISTORY_BOOKING, MenuActivity.this.getResources().getString(R.string.buton_menu_history_booking), R.drawable.overview));
        menus.add(new GridMenu(Constants.CODE_CHECK_IN, MenuActivity.this.getResources().getString(R.string.buton_menu_check_in), R.drawable.groups));
        menus.add(new GridMenu(Constants.CODE_REFRESHING_MALL, MenuActivity.this.getResources().getString(R.string.buton_menu_refreshing_mall), R.drawable.lists));
        menus.add(new GridMenu(Constants.CODE_LOGOUT, MenuActivity.this.getResources().getString(R.string.buton_menu_logout), R.drawable.profile));
//        menus.add(new GridMenu("Timeline", R.drawable.timeline));
//        menus.add(new GridMenu("Setting", R.drawable.settings));

        mGridMenuFragment.setupMenu(menus);
    }

    private boolean doubleBackToExitPressedOnce = false;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            Intent goToLogin = new Intent(this.getApplicationContext(), LoginActivity.class);
            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToLogin);
            finish();
        }else {
            this.doubleBackToExitPressedOnce=false;
            Toast.makeText(this, getResources().getString(R.string.confirmation_back), Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
//        if (0 == getSupportFragmentManager().getBackStackEntryCount()) {
//            super.onBackPressed();
//        } else {
//            getSupportFragmentManager().popBackStack();
//        }
    }

//    private boolean doubleBackToExitPressedOnce = false;
//    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
//    private long mBackPressed;
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
//                Intent goToLogin = new Intent(this.getApplicationContext(), LoginActivity.class);
//                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(goToLogin);
//                finish();
//            }else {
//                this.doubleBackToExitPressedOnce=false;
//                Toast.makeText(this, getResources().getString(R.string.confirmation_back), Toast.LENGTH_SHORT).show();
//            }
//        }
//        mBackPressed = System.currentTimeMillis();
//        return true;
//    }
}
