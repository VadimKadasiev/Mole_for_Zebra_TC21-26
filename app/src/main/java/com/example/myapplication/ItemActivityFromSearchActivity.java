package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalTime;

public class ItemActivityFromSearchActivity extends AppCompatActivity {
    TextView goodName;
    TextView fact;
    TextView acc;
    EditText edit_add;
    Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_from_search);
        goodName = findViewById(R.id.goodNameTextView);
        fact = findViewById(R.id.insertedFactTextView2);
        acc = findViewById(R.id.accTextView);
        edit_add = findViewById(R.id.addInputText);
        btn_add = findViewById(R.id.buttonAdd);
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
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setDataToWindows() {
        String nameText = Base.oneLineArray[1] + "\n"
                + Base.oneLineArray[2] + "\n\n"
                + Base.oneLineArray[3] + "\n"
                + Base.oneLineArray[4] + "\n"
                + Base.oneLineArray[5];
        goodName.setText(nameText);
        fact.setText(String.format("Факт: %s", Base.oneLineArray[7]));
        acc.setText(String.format("Облік: %s", Base.oneLineArray[8]));
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
                    + "вручну після пошуку" + "\n";
            Base.saveLine();
        }
    }
}