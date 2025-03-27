package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ReservedBaseAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<String> goodNameArray;
    ArrayList<String> factArray;
    ArrayList<String> accountingArray;
    TextView goods;
    TextView factQuantity;
    TextView accountingQuantity;
    int focusedLine;

    public ReservedBaseAdapter(Context c,
                             ArrayList<String> goodNameArray,
                             ArrayList<String> factArray,
                             ArrayList<String> accountingArray,
                             int focusedLine)
    {
        context = c;
        this.goodNameArray = goodNameArray;
        this.factArray = factArray;
        this.accountingArray = accountingArray;
        this.focusedLine = focusedLine;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return goodNameArray.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.row_for_reserved, null);
        goods = view.findViewById(R.id.goodsTextView2);
        goods.setText(goodNameArray.get(position));
        if (focusedLine > 0 && position == focusedLine - 1)  {
            goods.setBackgroundColor(Color.parseColor("#8BC884"));
        }
        factQuantity = view.findViewById(R.id.insertedFactTextView2);
        factQuantity.setText(factArray.get(position));
        accountingQuantity = view.findViewById(R.id.time);
        accountingQuantity.setText(accountingArray.get(position));
        return view;
    }
}
