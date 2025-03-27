package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalTime;

public class ItemActivity extends AppCompatActivity {
    TextView goodName;
    TextView fact;
    TextView acc;
    EditText edit_add;
    Button btn_add;
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item);
        goodName = findViewById(R.id.goodNameTextView);
        fact = findViewById(R.id.insertedFactTextView2);
        acc = findViewById(R.id.accTextView);
        edit_add = findViewById(R.id.addInputText);
        btn_add = findViewById(R.id.buttonAdd);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        setDataToWindows();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edit_add.getText().toString().equals("-")
                        && !edit_add.getText().toString().contains(".")
                        && !edit_add.getText().toString().isEmpty()) {
                    Base.needToLoadWorkingFile = false;
                    addFact();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(myBroadcastReceiver,filter);
        super.onResume();
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intentScan) {
            if (Base.action == null) {
                Base.action = intentScan.getAction();
            }
            assert Base.action != null;
            if (Base.action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                String scannedCode = intentScan.getStringExtra(getResources()
                        .getString(R.string.datawedge_intent_key_data));
                if (scannedCode != null && scannedCode.length() == 12) {
                    scannedCode = "0" + scannedCode;
                }
                if (scannedCode != null && scannedCode.length() == 13) {
                    findItem(scannedCode);
                } else {
                    Base.sound_fail.start();
                }
            }
        }
    };

    private void setDataToWindows() {
        String nameText = Base.oneLineArray[1] + "\n"
                + Base.oneLineArray[2] + "\n\n"
                + Base.oneLineArray[3] + "\n"
                + Base.oneLineArray[4] + "\n"
                + Base.oneLineArray[5];
        goodName.setText(nameText);
        fact.setText("Факт: " + Base.oneLineArray[7]);
        acc.setText("Облік: " + Base.oneLineArray[8]);
    }

    private void addFact() {
        if(!Base.workingFileName.contains("Записано в ")) {
            addLineToLogFile();
            int addingValue = Integer.parseInt(edit_add.getText().toString());
            int initialValue = Integer.parseInt(Base.goodsArray[Base.workingPosition][7]);
            Base.goodsArray[Base.workingPosition][7] = String.valueOf((initialValue + addingValue));
            Intent intent = new Intent(this, GoodsListActivity.class);
            startActivity(intent);
        }
    }

    @SuppressLint("NewApi")
    private void addLineToLogFile() {
        if (!Base.workingFileName.contains("Записано в ")) {
            Base.lineForSaving = LocalTime.now().toString().substring(0, 8) + ";"
                    + Base.oneLineArray[1] + ";"
                    + Base.oneLineArray[2] + ";"
                    + Base.oneLineArray[3] + ";"
                    + Base.oneLineArray[4] + ";"
                    + Base.oneLineArray[5] + ";"
                    + Base.oneLineArray[6] + ";"
                    + edit_add.getText().toString() + ";"
                    + "вручну" + "\n";
            Base.saveLine();
        }
    }

    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        } else {
                Intent intent = new Intent(this, GoodsListActivity.class);
                startActivity(intent);
        }
    }

    @SuppressLint("NewApi")
    public void findItem(String code) {
        int found = 0;
        for (int i = 0; i < Base.goodsArray.length; i++) {
            for (int a = 3; a < 7; a++) {
                if (Base.goodsArray[i][a].equals(code)) {
                    found++;
                        if (found > 0) {
                            Base.oneLineArray = Base.goodsArray[i];
                            Base.focusedLine = i + 1;
                            Base.workingPosition = i;
                            Base.sound_warning.start();
                            setDataToWindows();
                        }
                }
            }
        } if (found == 0) {
            if (Base.sound_fail.isPlaying()) {
                Base.sound_fail.pause();
                Base.sound_fail.seekTo(0);
                Base.sound_fail.setLooping(false);
            }
            Base.sound_fail.start();
        }
    }
}