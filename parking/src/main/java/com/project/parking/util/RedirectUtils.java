package com.project.parking.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import com.project.parking.MainFragment;
import com.project.parking.menu.MenuActivity;

/**
 * Created by Yohanes on 16/06/2017.
 */

public class RedirectUtils extends Fragment {
    private static final String TAG = "RedirectUtils";
    private Context ctx;
    private Activity act;

	/* STANDART MESSAGE */

    public RedirectUtils(Context ctx,Activity activity) {
        super();
        this.ctx = ctx;
        this.act = activity;
    }

    public void redirectToLogin() {
        act.getFragmentManager().beginTransaction().remove(this).commit();
        act.finish();
        Intent intent = new Intent(ctx, MainFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    public void redirectToMainMenu() {
        act.getFragmentManager().beginTransaction().remove(this).commit();
        act.finish();
        Intent intent = new Intent(ctx, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }
}
