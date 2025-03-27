package com.example.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ArrayList<String> goodName = new ArrayList<>();
    ArrayList<String> fact = new ArrayList<>();
    ArrayList<String> accounting = new ArrayList<>();
    EditText searchText;
    ListView listView;
    Button search;
    TextView foundLinesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        listView = findViewById(R.id.foundGoodsListView);
        searchText = findViewById(R.id.addInputText);
        search = findViewById(R.id.buttonStartSearch);
        foundLinesText = findViewById(R.id.foundLines);
        Base.focusedLine = 0;

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFoundGoodsArray();
            }
        });
    }

    private void setFoundGoodsArray() {
        Base.foundGoodsArray = new String[110][9];
        int s = 0;
        String text = searchText.getText().toString();
        if (text.length() > 1) {
            for (int i = 0; i < Base.goodsArray.length; i++) {
                for (int a = 0; a < 8; a++) {
                    if (Base.goodsArray[i][a].toLowerCase().contains(text.toLowerCase())) {
                        System.arraycopy(Base.goodsArray[i], 0, Base.foundGoodsArray[s], 0, 9);
                        s++;
                        break;
                    }
                }
                if (s == 105) {break;}
            }
        setFoundGoodsList();
        }
    }

    private void setFoundGoodsList() {
        int lines = 0;
        CommonBaseAdapter adapter = new CommonBaseAdapter(SearchActivity.this,
                goodName, fact, accounting, Base.focusedLine);
        goodName.clear();
        fact.clear();
        accounting.clear();
        for (int i = 0; i < 100; i++) {
            if (Base.foundGoodsArray[i][2] == null) {
                break;
            }
            lines++;
            goodName.add(Base.foundGoodsArray[i][1] + " " + Base.foundGoodsArray[i][2]);
            fact.add(Base.foundGoodsArray[i][7]);
            accounting.add(Base.foundGoodsArray[i][8]);
        }
        if (lines == 0) {
            Base.sound_fail.start();
            foundLinesText.setText("Нічого не знайдено");
        } else {
            foundLinesText.setText(String.format("Знайдено рядків: %d", lines).replace("100", "більше 100"));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = Base.foundGoodsArray[position][1];
                position = getPosition(code);
                Base.workingPosition = position;
                Base.focusedLine = position + 1;
                Base.oneLineArray = Base.goodsArray[position];
                Intent intent = new Intent(SearchActivity.this, ItemActivityFromSearchActivity.class);
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);

    }

    private int getPosition(String code) {
        int position = 100000;
        for (int i = 0; i < Base.goodsArray.length; i++) {
            if (Base.goodsArray[i][1].equals(code)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, GoodsListActivity.class);
            startActivity(intent);
        }
    }
}