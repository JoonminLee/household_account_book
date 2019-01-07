package com.example.jay.hhac_tab;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CursorAdapterActivity_Search extends CursorAdapter {
    public CursorAdapterActivity_Search(Context context, Cursor c) {
        super(context, c);
    }

    DecimalFormat df = new DecimalFormat("#,###원");

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.search_list, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView item_ic_s = view.findViewById(R.id.item_ic_s);
        TextView item_date_s = view.findViewById(R.id.item_date_s);
        TextView item_content_s = view.findViewById(R.id.item_content_s);
        TextView item_income_s = view.findViewById(R.id.item_income_s);
        TextView item_cost_s = view.findViewById(R.id.item_cost_s);

        String date = cursor.getString(cursor.getColumnIndex("hhac_date"));
        String contents = cursor.getString(cursor.getColumnIndex("hhac_content"));
        String income = cursor.getString(cursor.getColumnIndex("hhac_income"));
        String cost = cursor.getString(cursor.getColumnIndex("hhac_cost"));

        item_date_s.setText(date);
        item_content_s.setText(contents);
        item_income_s.setVisibility(View.VISIBLE);
        item_cost_s.setVisibility(View.VISIBLE);

        if (income == null) {
            item_ic_s.setText("-");
            item_cost_s.setText(df.format(Integer.parseInt(cost)));
            item_income_s.setVisibility(View.GONE);
        } else if (cost == null) {
            item_ic_s.setText("+");
            item_income_s.setText(df.format(Integer.parseInt(income)));
            item_cost_s.setVisibility(View.GONE);
        }
    }
}
