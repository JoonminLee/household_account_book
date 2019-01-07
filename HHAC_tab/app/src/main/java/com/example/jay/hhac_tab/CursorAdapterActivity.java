package com.example.jay.hhac_tab;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CursorAdapterActivity extends CursorAdapter {
    public CursorAdapterActivity(Context context, Cursor c) {
        super(context, c);
    }

    DecimalFormat df = new DecimalFormat("#,###원");

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.detail_list, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView item_ic = view.findViewById(R.id.item_ic);
        TextView item_content = view.findViewById(R.id.item_content);
        TextView item_income = view.findViewById(R.id.item_income);
        TextView item_cost = view.findViewById(R.id.item_cost);
        TextView item_time = view.findViewById(R.id.item_time);

        String contents = cursor.getString(cursor.getColumnIndex("hhac_content"));
        String income = cursor.getString(cursor.getColumnIndex("hhac_income"));
        String cost = cursor.getString(cursor.getColumnIndex("hhac_cost"));
        String time = cursor.getString(cursor.getColumnIndex("hhac_time"));

        item_content.setText(contents);
        item_time.setText(time);
        item_income.setVisibility(View.VISIBLE);
        item_cost.setVisibility(View.VISIBLE);

        if (income == null) {
            item_ic.setText("-");
            item_cost.setText(df.format(Integer.parseInt(cost)));
            item_income.setVisibility(View.GONE);
        } else if (cost == null) {
            item_ic.setText("+");
            item_income.setText(df.format(Integer.parseInt(income)));
            item_cost.setVisibility(View.GONE);
        }
    }
}
