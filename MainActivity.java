package ru.happer.smartui;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.KeyEvent;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(9,11,20);
    private final int panel = Color.rgb(21,25,42);
    private final int purple = Color.rgb(139,92,246);
    private LinearLayout root;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        buildUi();
    }

    private GradientDrawable shape(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(22);
        if (stroke != 0) d.setStroke(2, stroke);
        return d;
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 22, 32, 24);
        root.setBackgroundColor(bg);
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setOrientation(LinearLayout.HORIZONTAL);
        TextView brand = text("HAPPER", 28, Color.WHITE, true);
        top.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        String date = new SimpleDateFormat("EEE, d MMM  •  HH:mm", Locale.getDefault()).format(new Date());
        top.addView(text(date, 16, Color.LTGRAY, false));
        root.addView(top);

        TextView heading = text("ТВОЙ SMART TV", 25, Color.WHITE, true);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = 26; hp.bottomMargin = 8;
        root.addView(heading, hp);
        root.addView(text("Быстрый доступ к приложениям", 15, 0xFFADB5D0, false));

        LinearLayout cards = new LinearLayout(this);
        cards.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.topMargin = 22;
        root.addView(cards, cp);
        addCard(cards, "▶", "YouTube", "Открыть клиент", 0xFF27213D, new Runnable() {
            public void run() { launchOrWeb(new String[]{"com.google.android.youtube.tv","com.google.android.youtube"}, "https://m.youtube.com"); }
        });
        addCard(cards, "▶", "VK Видео", "Видео и трансляции", 0xFF182D4B, new Runnable() {
            public void run() { launchOrWeb(new String[]{"com.vk.vkvideo","com.vkontakte.android"}, "https://vk.com/video"); }
        });
        addCard(cards, "▣", "Кинопоиск", "Фильмы и сериалы", 0xFF34251E, new Runnable() {
            public void run() { launchOrWeb(new String[]{"ru.kinopoisk"}, "https://hd.kinopoisk.ru"); }
        });

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
        rp.topMargin = 18;
        root.addView(row, rp);
        addSmall(row, "Все приложения", new Runnable() { public void run() { showApps(); }});
        addSmall(row, "Настройки", new Runnable() {
            public void run() {
                try { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); }
                catch(Exception e) { toast("Не удалось открыть настройки"); }
            }
        });
        addSmall(row, "Wi-Fi", new Runnable() {
            public void run() {
                try { startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)); }
                catch(Exception e) { toast("Настройки Wi-Fi недоступны"); }
            }
        });
        addSmall(row, "Перезагрузка*", new Runnable() {
            public void run() { toast("Перезапусти телевизор кнопкой питания или через системное меню"); }
        });

        TextView note = text("* Перезагрузка из приложения требует системных прав. Используй меню питания телевизора.", 12, 0xFF858CA6, false);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, -2);
        np.topMargin = 22;
        root.addView(note, np);

        root.setFocusableInTouchMode(true);
        root.requestFocus();
    }

    private void addCard(LinearLayout parent, String icon, String title, String sub, int color, final Runnable action) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 18, 12, 18);
        card.setBackground(shape(color, 0xFF35304F));
        card.setFocusable(true); card.setClickable(true);
        card.setOnClickListener(v -> action.run());
        card.setOnKeyListener((v,k,e) -> {
            if (e.getAction()==KeyEvent.ACTION_UP && (k==KeyEvent.KEYCODE_DPAD_CENTER || k==KeyEvent.KEYCODE_ENTER)) { action.run(); return true; }
            return false;
        });
        TextView ic = text(icon, 30, Color.WHITE, true);
        TextView name = text(title, 18, Color.WHITE, true);
        TextView desc = text(sub, 12, 0xFFD1D4E2, false);
        card.addView(ic); addGap(card, 13); card.addView(name); addGap(card, 7); card.addView(desc);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, 150, 1);
        p.setMargins(0,0,12,0);
        parent.addView(card,p);
    }

    private void addGap(LinearLayout parent, int h) {
        View v = new View(this); parent.addView(v, new LinearLayout.LayoutParams(1,h));
    }

    private void addSmall(LinearLayout parent, String label, final Runnable action) {
        TextView b = text(label, 14, Color.WHITE, true);
        b.setGravity(Gravity.CENTER);
        b.setPadding(10, 17, 10, 17);
        b.setBackground(shape(panel, 0xFF30364F));
        b.setFocusable(true); b.setClickable(true);
        b.setOnClickListener(v -> action.run());
        b.setOnKeyListener((v,k,e) -> {
            if (e.getAction()==KeyEvent.ACTION_UP && (k==KeyEvent.KEYCODE_DPAD_CENTER || k==KeyEvent.KEYCODE_ENTER)) { action.run(); return true; }
            return false;
        });
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,-2,1);
        p.setMargins(0,0,8,0);
        parent.addView(b,p);
    }

    private void launchOrWeb(String[] packages, String url) {
        PackageManager pm = getPackageManager();
        for (String p : packages) {
            Intent launch = pm.getLaunchIntentForPackage(p);
            if (launch != null) {
                try { startActivity(launch); return; } catch(Exception ignored) {}
            }
        }
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        } catch(Exception e) { toast("Нет подходящего приложения или браузера. Проверь интернет."); }
    }

    private void showApps() {
        final LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv = new ScrollView(this);
        sv.addView(list);
        final PopupWindow popup = new PopupWindow(sv, Math.max(500, getResources().getDisplayMetrics().widthPixels-80),
            Math.max(300, getResources().getDisplayMetrics().heightPixels-100), true);
        popup.setBackgroundDrawable(shape(panel, purple));
        popup.setOutsideTouchable(true);
        TextView title = text("УСТАНОВЛЕННЫЕ ПРИЛОЖЕНИЯ", 18, Color.WHITE, true);
        title.setPadding(18,18,18,18); list.addView(title);
        java.util.List<android.content.pm.ApplicationInfo> apps = getPackageManager().getInstalledApplications(0);
        java.util.Collections.sort(apps, (a,b) -> getPackageManager().getApplicationLabel(a).toString().compareToIgnoreCase(getPackageManager().getApplicationLabel(b).toString()));
        for (final android.content.pm.ApplicationInfo app : apps) {
            Intent launch = getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (launch == null || app.packageName.equals(getPackageName())) continue;
            TextView item = text("  " + getPackageManager().getApplicationLabel(app).toString(), 16, Color.WHITE, false);
            item.setPadding(18,14,18,14); item.setFocusable(true); item.setBackground(shape(bg,0));
            item.setOnClickListener(v -> { try { startActivity(getPackageManager().getLaunchIntentForPackage(app.packageName)); popup.dismiss(); } catch(Exception ex) { toast("Не удалось открыть приложение"); }});
            list.addView(item);
        }
        popup.showAtLocation(root, Gravity.CENTER, 0, 0);
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
