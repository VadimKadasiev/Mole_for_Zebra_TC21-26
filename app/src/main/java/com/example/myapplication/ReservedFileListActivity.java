package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class ReservedFileListActivity extends AppCompatActivity {
    TextView workingFileText;
    Button btnSearch;
    EditText editText;
    ListView listView;
    ArrayList<String> goodName = new ArrayList<>();
    ArrayList<String> fact = new ArrayList<>();
    ArrayList<String> accounting = new ArrayList<>();
    int arrayIndex = -1;
    int found = 0;
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reserved_file_list);
        workingFileText = findViewById(R.id.reserved_workingFileName);
        workingFileText.setText(String.format("Робочий файл: \"%s\"", Base.workingFileName));
        Base.focusedLine = 0;
        btnSearch = findViewById(R.id.buttonSearch);
        editText = findViewById(R.id.searchInput);
        listView = findViewById(R.id.goodsListViewReserved);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        try {
            loadWorkingFile();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        setGoodsList();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
//                    move keyboard down
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    Base.textForSearching = editText.getText().toString();
                    findItem();
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
                    Base.textForSearching = scannedCode;
                    Base.sound_warning.start();
                    findItem();
                } else {
                    Base.sound_fail.start();
                }
            }
        }
    };



    private void loadWorkingFile() throws IllegalAccessException, InstantiationException {
        Base.reservedGoodsList.clear();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/Download/", Base.workingFileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader
                    (fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Base.reservedGoodsList.add(line);
            }
            Base.setReservedGoodsArray();
            fileInputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, "З файлом щось не так!!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void setGoodsList() {
        try {
            ReservedBaseAdapter adapter = new ReservedBaseAdapter(ReservedFileListActivity.this,
                    goodName, fact, accounting, Base.focusedLine);
            goodName.clear();
            fact.clear();
            accounting.clear();
            for (int i = 0; i < Base.reservedGoodsArray.length; i++) {
                goodName.add(Base.reservedGoodsArray[i][1] + " " + Base.reservedGoodsArray[i][2]);
                fact.add(Base.reservedGoodsArray[i][7]);
                accounting.add(Base.reservedGoodsArray[i][0] + " "
                + Base.reservedGoodsArray[i][8].charAt(0));
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Base.workingPosition = position;
                    Base.focusedLine = position + 1;
                    Base.oneLineArray = Base.reservedGoodsArray[position];
                    Intent intent = new Intent(ReservedFileListActivity.this, ReservedItemActivity.class);
                    startActivity(intent);
                }
            });
            listView.setAdapter(adapter);
            if (Base.focusedLine == 0) {
                listView.setSelection(Base.reservedGoodsArray.length);
            } else {
                listView.setSelection(Base.focusedLine - 6);
            }

        } catch (Exception e) {
            Toast.makeText(this, "З файлом щось не так!!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void findItem() {
        if (arrayIndex >= Base.reservedGoodsArray.length - 1) {
            arrayIndex = -1;
        }
        for (int i = arrayIndex + 1; i < Base.reservedGoodsArray.length; i++) {
            arrayIndex = i;
            if (Base.reservedGoodsArray[i][1].toLowerCase().contains(Base.textForSearching.toLowerCase()) ||
                Base.reservedGoodsArray[i][2].toLowerCase().contains(Base.textForSearching.toLowerCase()) ||
                Base.reservedGoodsArray[i][3].toLowerCase().contains(Base.textForSearching.toLowerCase()) ||
                Base.reservedGoodsArray[i][4].toLowerCase().contains(Base.textForSearching.toLowerCase()) ||
                Base.reservedGoodsArray[i][5].toLowerCase().contains(Base.textForSearching.toLowerCase()) ||
                Base.reservedGoodsArray[i][6].toLowerCase().contains(Base.textForSearching.toLowerCase())) {
                found++;
                    break;
                } else if (found > 0 && arrayIndex >= Base.reservedGoodsArray.length - 1) {
                    arrayIndex = -1;
                    found = 0;
                    findItem();
            } else if (found == 0 && arrayIndex >= Base.reservedGoodsArray.length - 1) {
                arrayIndex = -1;
            }
        } Base.focusedLine = arrayIndex + 1;
        setGoodsList();
    }
}