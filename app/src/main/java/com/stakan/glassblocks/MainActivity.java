package com.stakan.glassblocks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xff050816);
        root.addView(new GameView(this), new FrameLayout.LayoutParams(-1, -1));

        AdView banner = new AdView(this);
        banner.setAdSize(AdSize.BANNER);
        banner.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        root.addView(banner, adParams);
        TextView about = new TextView(this);
        about.setText("ⓘ"); about.setTextColor(Color.CYAN); about.setTextSize(28); about.setGravity(Gravity.CENTER);
        about.setOnClickListener(v -> showAbout());
        FrameLayout.LayoutParams aboutParams = new FrameLayout.LayoutParams(72,72,Gravity.TOP|Gravity.END);
        aboutParams.setMargins(0,12,12,0); root.addView(about,aboutParams);
        TextView help = new TextView(this);
        help.setText("?"); help.setTextColor(Color.WHITE); help.setTextSize(25); help.setGravity(Gravity.CENTER);
        help.setOnClickListener(v -> showHelp());
        FrameLayout.LayoutParams helpParams = new FrameLayout.LayoutParams(72,72,Gravity.TOP|Gravity.START);
        helpParams.setMargins(12,12,0,0); root.addView(help,helpParams);
        setContentView(root);
        MobileAds.initialize(this, ignored -> banner.loadAd(new AdRequest.Builder().build()));
    }
    private void showHelp(){new AlertDialog.Builder(this).setTitle("How to play").setMessage("Move: swipe left or right.\nRotate: tap anywhere on the game screen.\nSoft drop: drag downward.\nHard drop: make a longer downward swipe and release.\n\nThe NEXT window shows the following piece. Complete a full horizontal row to clear it. A new piece always waits for a new finger gesture.").setPositiveButton("Got it",null).show();}
    private void showAbout(){new AlertDialog.Builder(this).setTitle("Glass Blocks — About").setMessage("Version 1.0.2\nDeveloped by Chysti Alex\n© 2026 Chysti Alex\n\nThis app does not collect personal data.\n\nSupport is voluntary and does not unlock features or digital benefits.").setNeutralButton("Support the developer",(d,w)->startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/Chysti75")))).setPositiveButton("Close",null).show();}
}
