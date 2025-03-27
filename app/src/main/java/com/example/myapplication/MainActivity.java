package com.example.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String SIGNATURE = "\"Кріт\" by Vadym Kadasiev V56_21022025";
    List<String> files = new ArrayList<>();
    ListView listView;
    TextView versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Base.focusedLine = 0;
        Base.needToLoadWorkingFile = true;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        versionText = findViewById(R.id.version);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (LocalDate.now().isAfter(LocalDate.of(2025,12,31))) {
                Intent intent = new Intent(this, Warning.class);
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            versionText.setText(SIGNATURE);
        }
        listView = findViewById(R.id.filesListView);
        getFiles();
        setFilesList();
    }

    private void getFiles() {
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Download/");
        File[] filesInFolder = folder.listFiles();
        assert filesInFolder != null;
        for (File file : filesInFolder) {
            if (!file.isDirectory() && file.getName().contains(".csv")) {
                files.add(file.getName());
            }
        } files.sort(String::compareToIgnoreCase);
    }

    private void setFilesList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.item_for_files_list,R.id.objectInfo,files);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Base.workingFileName = listView.getItemAtPosition(position).toString();
                if (Base.workingFileName.contains("Записано в")) {
                    Intent intent = new Intent(MainActivity.this, ReservedFileListActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, GoodsListActivity.class);
                    startActivity(intent);
                }
            }
        });
        listView.setAdapter(adapter);
    }

    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        } else {
            this.finishAffinity();
        }
    }
}