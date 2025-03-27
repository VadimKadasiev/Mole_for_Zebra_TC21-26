package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Base extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Context context = ContextCreator.getContext();
    public static String workingFileName;
    public static List<String> goodsList = new ArrayList<>();
    public static List<String> reservedGoodsList = new ArrayList<>();
    public static String[][] goodsArray;
    public static String[][] reservedGoodsArray;
    public static String[] oneLineArray;
    public static boolean needToLoadWorkingFile = true;
    public static int workingPosition;
    public static int focusedLine = 0;
    public static String[][] foundGoodsArray;
    public static String lineForSaving;
    public static String action;
    public static String textForSearching;
    public static int count = 0;
    public static boolean confirmation = false;
    public static String confBtnColor = "#1A0343";
    public static MediaPlayer sound_fail = MediaPlayer.create(context,R.raw.fail);
    public static MediaPlayer sound_warning = MediaPlayer.create(context,R.raw.warning);
    public static MediaPlayer sound_beep = MediaPlayer.create(context,R.raw.beep_simple);
    //public static MediaPlayer sound_triangle = MediaPlayer.create(context,R.raw.triangle);

    public static void setGoodsArray() {
        goodsArray = new String[goodsList.size()][9];
        for (int i = 0; i < goodsArray.length; i++) {
            String[] arr = goodsList.get(i).split(";");
            System.arraycopy(arr, 0, goodsArray[i], 0, arr.length);
        }
    }

    public static void setReservedGoodsArray() {
        reservedGoodsArray = new String[reservedGoodsList.size()][10];
        for (int i = 0; i < reservedGoodsArray.length; i++) {
            String[] arr = reservedGoodsList.get(i).split(";");
            System.arraycopy(arr, 0, reservedGoodsArray[i], 0, arr.length);
        }
    }

    public static void saveLine() {
        saveFileOrWriteLine("Записано в " + workingFileName,true);
        count++;
        if (count == 20) {resaveFile();count = 0;}
    }

    public static void resaveFile() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < goodsArray.length; i++) {
                for (int a = 0; a < 9; a++) {
                    if (a != 0) {
                        stringBuilder.append(";");
                    }
                    stringBuilder.append(goodsArray[i][a]);
                    if (a == 8) {
                        stringBuilder.append("\n");
                    }
                }
            }
            lineForSaving = stringBuilder.toString();
            saveFileOrWriteLine(workingFileName, false);
        } catch (Exception e) {
            Toast.makeText(context, "Не збережено!!!_1", Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveFileOrWriteLine(String fileName, boolean appendMode) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/Download/", fileName);
            FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file, appendMode);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                fileOutputStream.write((lineForSaving.getBytes()));
                fileOutputStream.close();
            } catch (IOException e) {
                Toast.makeText(context, "Не збережено!!!_2", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
