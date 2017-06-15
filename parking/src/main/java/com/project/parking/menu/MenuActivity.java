package com.project.parking.menu;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.goka.blurredgridmenu.GridMenu;
import com.goka.blurredgridmenu.GridMenuFragment;
import com.project.parking.R;
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

                if (gridMenu.getTitle().equals("Change Password")){

                }
                else if(gridMenu.getTitle().equals("Daftar Mall")){

                }
                else if(gridMenu.getTitle().equals("History Booking")){

                }

                Toast.makeText(MenuActivity.this, "Title:" + gridMenu.getTitle() + ", Position:" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGridMenu() {
        List<GridMenu> menus = new ArrayList<>();
        menus.add(new GridMenu(Constants.CODE_CHANGE_PASSWORD, "Change Password", R.drawable.home));
        menus.add(new GridMenu(Constants.CODE_DAFTAR_MALL,"Change Password", R.drawable.calendar));
        menus.add(new GridMenu(Constants.CODE_HISTORY_BOOKING,"Change Password", R.drawable.overview));
        menus.add(new GridMenu(Constants.CODE_CHECK_IN,"Change Password", R.drawable.groups));
        menus.add(new GridMenu(Constants.CODE_REFRESHING_MALL,"Change Password", R.drawable.lists));
        menus.add(new GridMenu(Constants.CODE_LOGOUT,"Change Password", R.drawable.profile));
//        menus.add(new GridMenu("Timeline", R.drawable.timeline));
//        menus.add(new GridMenu("Setting", R.drawable.settings));

        mGridMenuFragment.setupMenu(menus);
    }

    @Override
    public void onBackPressed() {
        if (0 == getSupportFragmentManager().getBackStackEntryCount()) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
