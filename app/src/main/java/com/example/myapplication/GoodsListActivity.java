package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;

public class GoodsListActivity extends AppCompatActivity {
    TextView workingFile;
    Button btnOther;
    Button btnSearch;
    Button btnConfirmation;
    ListView listView;
    ArrayList<String> goodName = new ArrayList<>();
    ArrayList<String> fact = new ArrayList<>();
    ArrayList<String> accounting = new ArrayList<>();
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goods_list);
        workingFile = findViewById(R.id.workingFileName);
        workingFile.setText(String.format("Робочий файл: \"%s\"", Base.workingFileName));
        listView = findViewById(R.id.goodsListView);
        btnOther = findViewById(R.id.buttonSave);
        btnOther.setBackgroundColor(Color.parseColor("#1A0343"));
        btnSearch = findViewById(R.id.buttonSearch);
        btnSearch.setBackgroundColor(Color.parseColor("#1A0343"));
        btnConfirmation = findViewById(R.id.buttonConfirmation);
        btnConfirmation.setBackgroundColor(Color.parseColor(Base.confBtnColor));
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));

        if (Base.needToLoadWorkingFile) {
            //preparing sounds
            Base.sound_fail.seekTo(500);
            Base.sound_beep.seekTo(500);
            Base.sound_warning.seekTo(500);
            Base.sound_fail.start();
            Base.sound_beep.start();
            Base.sound_warning.start();
            try {
                loadWorkingFile();
                Base.needToLoadWorkingFile = false;
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Base.goodsList.isEmpty()) {
            setGoodsList();
        }

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoodsListActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        btnConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Base.confirmation) {
                    Base.confBtnColor = "#E91E1E";
                    Base.confirmation = true;
                } else {
                    Base.confBtnColor = "#1A0343";
                    Base.confirmation = false;
                }
                btnConfirmation.setBackgroundColor(Color.parseColor(Base.confBtnColor));
            }
        });
    }

    @Override
    protected void onPause() {
        if (!Base.workingFileName.contains("Записано в ") && !Base.goodsList.isEmpty()) {
            Base.resaveFile();
        }
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
                    findItem(scannedCode);
                }
                else if (scannedCode != null && scannedCode.length() == 13) {
                    findItem(scannedCode);
                }
                else if (scannedCode != null && scannedCode.contains("_")) {
                    scannedCode = "20000" + scannedCode.substring(0, 7);
                    Base.sound_warning.start();
                    findItem(scannedCode);
                } else {
                    Base.sound_fail.start();
                }
            }
        }
    };

    private void loadWorkingFile() throws IllegalAccessException, InstantiationException {
        Base.goodsList.clear();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/Download/", Base.workingFileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader
                    (fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] arr = line.split(";");
                if (arr[1].length() > 7 || arr.length != 9) {
                    Toast.makeText(this, "З файлом щось не так!!!_1", Toast.LENGTH_SHORT).show();
                    Base.sound_fail.start();
                    Base.goodsList.clear();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    break;
                }
                Base.goodsList.add(line);
            } if (!Base.goodsList.isEmpty()) {
                Base.setGoodsArray();
            }
            fileInputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, "З файлом щось не так!!!_2", Toast.LENGTH_SHORT).show();
            Base.sound_fail.start();
            Base.goodsList.clear();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void setGoodsList() {
        try {
            CommonBaseAdapter adapter = new CommonBaseAdapter(GoodsListActivity.this,
                    goodName, fact, accounting, Base.focusedLine);
            goodName.clear();
            fact.clear();
            accounting.clear();
            for (int i = 0; i < Base.goodsArray.length; i++) {
                goodName.add(Base.goodsArray[i][1] + " " + Base.goodsArray[i][2]);
                fact.add(Base.goodsArray[i][7]);
                accounting.add(Base.goodsArray[i][8]);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Base.workingPosition = position;
                    Base.focusedLine = position + 1;
                    Base.oneLineArray = Base.goodsArray[position];
                    Intent intent = new Intent(GoodsListActivity.this,ItemActivity.class);
                    startActivity(intent);
                }
            });
            listView.setAdapter(adapter);
            listView.setSelection(Base.focusedLine - 5);
        } catch (Exception e) {
            Toast.makeText(this, "З файлом щось не так!!!_3", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    @SuppressLint("NewApi")
    public void findItem(String code) {
        int found = 0;
        for (int i = 0; i < Base.goodsArray.length; i++) {
            for (int a = 3; a < 7; a++) {
                if (Base.goodsArray[i][a].contains(code)) {
                    found++;
                    if (!Base.confirmation) {
                        int value = Integer.parseInt(Base.goodsArray[i][7]);
                        value++;
                        String newValue = String.valueOf(value);
                        Base.goodsArray[i][7] = newValue;
                        Base.focusedLine = i + 1;
                        found++;
                        Base.lineForSaving = LocalTime.now().toString().substring(0, 8) + ";"
                                + Base.goodsArray[i][1] + ";"
                                + Base.goodsArray[i][2] + ";"
                        + Base.goodsArray[i][3] + ";"
                        + Base.goodsArray[i][4] + ";"
                        + Base.goodsArray[i][5] + ";"
                        + Base.goodsArray[i][6] + ";"
                                + "1" +";"
                                + "сканером" + "\n";
                        Base.saveLine();
                        Base.sound_beep.start();
                        setGoodsList();
                        break;
                    } else {
                        if (found > 0) {
                            Base.oneLineArray = Base.goodsArray[i];
                            Base.focusedLine = i + 1;
                            Base.workingPosition = i;
                            Intent intent = new Intent(this, ItemActivity.class);
                            startActivity(intent);
                        }
                    }
                }
             }if (found > 0) {
                if (Base.confirmation) {
                    Base.sound_warning.start();
                }
                    break;
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

    @Override
    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        } else {
            if (!Base.workingFileName.contains("Записано в ")) {
            Base.resaveFile();
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
