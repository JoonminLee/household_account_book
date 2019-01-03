package com.example.jay.hhac_tab;
// 어떠한 것들을 import했는지 확인 먼저 하세요 누나!! 대략적으로 이 클래스가 무슨 일을 하는 녀석인지 알기 쉬워져요

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CursorAdapterActivity_Search extends CursorAdapter {
    // 생성자 생성
    public CursorAdapterActivity_Search(Context context, Cursor c) {
        super(context, c);
    }

    DecimalFormat df = new DecimalFormat("#,###원");

    // detail_list.xml을 객체화 시켜서, activity_detail.xml안의 ListView에 넣어주는 함수
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.search_list, parent, false);
        return v;
    }

    // search_list.xml안의 TextView들을 설정해 주는 함수
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView item_ic_s = view.findViewById(R.id.item_ic_s);
        TextView item_date_s = view.findViewById(R.id.item_date_s);
        TextView item_content_s = view.findViewById(R.id.item_content_s);
        TextView item_income_s = view.findViewById(R.id.item_income_s);
        TextView item_cost_s = view.findViewById(R.id.item_cost_s);

        //getColumnindex(name) : name에 해당하는 필드의 인덱스 번호를 반환한다.
        //cursor.getString(index) : 해당 커서가 위치한 인덱스 위치의 값을 반환한다.
        //curosr 안에는 select * from table_name;이 담겨져 있어요
        //columnIndex가 아닌, columnName을 입력!!
        String date = cursor.getString(cursor.getColumnIndex("hhac_date"));
        String contents = cursor.getString(cursor.getColumnIndex("hhac_content"));
        String income = cursor.getString(cursor.getColumnIndex("hhac_income"));
        String cost = cursor.getString(cursor.getColumnIndex("hhac_cost"));

        //내용 부분에 값 입력
        item_date_s.setText(date);
        item_content_s.setText(contents);
        //수입과 지출 모두 보이게 해준다
        item_income_s.setVisibility(View.VISIBLE);
        item_cost_s.setVisibility(View.VISIBLE);

        //만약 수입이 null 이라면, 수입을 안보이게 하고, 지출이 null 이라면, 지출을 안보이게 한다
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
