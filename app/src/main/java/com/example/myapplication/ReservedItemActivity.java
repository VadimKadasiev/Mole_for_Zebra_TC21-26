package com.example.myapplication;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ReservedItemActivity extends AppCompatActivity {
    TextView goodInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reserved_item);
        goodInfo = findViewById(R.id.goodInfoTextView);
        setGoodInfo();
    }

    private void setGoodInfo() {
        String text = Base.oneLineArray[1] + "\n"
                + Base.oneLineArray[2] + "\n\n"
                + Base.oneLineArray[3] + "\n"
                + Base.oneLineArray[4] + "\n"
                + Base.oneLineArray[5] + "\n"
                + "додано " + Base.oneLineArray[7] + " од. " + Base.oneLineArray[8] + "\n"
                + Base.oneLineArray[0];
        goodInfo.setText(text);
    }
}