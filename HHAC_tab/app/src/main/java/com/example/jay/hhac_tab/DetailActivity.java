package com.example.jay.hhac_tab;
// 어떠한 것들을 import했는지 확인 먼저 하세요 누나!! 대략적으로 이 클래스가 무슨 일을 하는 녀석인지 알기 쉬워져요

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    //사용할 변수 설정
    private TextView hhac_date, hhac_income_sum, hhac_cost_sum;
    private Button gotoCalendar, incomebtn, costbtn, prev_date, next_date;
    private EditText edit_content, edit_price;

    //DB관련 변수 설정
    DBHelper dbh;
    SQLiteDatabase db;
    Cursor cursor;
    CursorAdapterActivity adapter;

    //숫자에 화폐단위를 찍어주는 DecimalFormat 설정
    //사용 방법 df.format(int);
    DecimalFormat df = new DecimalFormat("#,###원");

    public static String view_date = getToday_date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("가계부");

        //데이터베이스 생성
        dbh = new DBHelper(this);
        db = dbh.getWritableDatabase();

        //레이아웃 변수설정
        hhac_date = findViewById(R.id.hhac_date);
        gotoCalendar = findViewById(R.id.gotoCalendar);
        hhac_income_sum = findViewById(R.id.hhac_income_sum);
        hhac_cost_sum = findViewById(R.id.hhac_cost_sum);
        prev_date = findViewById(R.id.prev_date);
        next_date = findViewById(R.id.next_date);
        incomebtn = findViewById(R.id.incomebtn);
        costbtn = findViewById(R.id.costbtn);
        edit_content = findViewById(R.id.edit_content);
        edit_price = findViewById(R.id.edit_price);
        ListView list = findViewById(R.id.account_list);

        //날짜 표시 인텐트 설정
        //CustomCalendarActivity에서 보내준 selectedDate를 받아서, SQL안에 담겨있는 데이터들을, 날짜들에 맞춰서 가져오기때문에 매우 중요한 부분!!
        final Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");
        if (!TextUtils.isEmpty(selectedDate)) {
            view_date = selectedDate;
            hhac_date.setText(selectedDate);
        } else {
            hhac_date.setText(view_date);
        }

        //리스트 새로고침 함수
        refresh();

        //달력 보기 버튼 눌렀을 시
        gotoCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendar_intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(calendar_intent);
            }
        });

        //CursorAdapter 생성
        final String sql1 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
        cursor = db.rawQuery(sql1, null);
        adapter = new CursorAdapterActivity(this, cursor);

        //리스트 뷰 어댑터 설정
        list.setAdapter(adapter);

        //리스트 뷰 안에 있는 아이템들을 길게 눌렀을 시, 삭제 되는 함수
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String deleteitemsql = String.format("delete from '%s' where _id = '%s'", "hhac_db", adapter.getItemId(position));
                db.execSQL(deleteitemsql);
                refresh();
                String listupdate = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                cursor = db.rawQuery(listupdate, null);
                adapter.changeCursor(cursor);
                Toast.makeText(DetailActivity.this, "삭제완료", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 수입 버튼을 눌렀을 시 실행되는 함수
        incomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ec = findViewById(R.id.edit_content);
                EditText ep = findViewById(R.id.edit_price);

                String contents = ec.getText().toString();
                int price = Integer.parseInt(ep.getText().toString());
                String today_date = getToday_date();

                // 문자열은 ' '로 감싸야 한다
                String sql1 = String.format("insert into '%s' values( null, '%s', %d, null, '%s');", "hhac_db", contents, price, view_date);
                db.execSQL(sql1);

                // 리스트 갱신
                refresh();
                String sql2 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                cursor = db.rawQuery(sql2, null);
                adapter.changeCursor(cursor);
                edit_content.setText("");
                edit_price.setText("");
            }
        });

        // 지출 버튼을 눌렀을 시 실행 되는 함수
        costbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ec = findViewById(R.id.edit_content);
                EditText ep = findViewById(R.id.edit_price);

                String contents = ec.getText().toString();
                int price = Integer.parseInt(ep.getText().toString());
                String today_date = getToday_date();

                // 문자열은 ' '로 감싸야 한다
                String sql1 = String.format("insert into '%s' values( null, '%s', null, %d, '%s');", "hhac_db", contents, price, view_date);
                db.execSQL(sql1);

                // 리스트 갱신
                refresh();
                String sql2 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                cursor = db.rawQuery(sql2, null);
                adapter.changeCursor(cursor);
                edit_content.setText("");
                edit_price.setText("");

            }
        });

        // 어제 버튼 클릭 시 실행 되는 함수
        prev_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = (String) hhac_date.getText();
                String[] arr = str.split("/");
                int year = Integer.parseInt(arr[0]);
                int month = Integer.parseInt(arr[1]);
                int day = Integer.parseInt(arr[2]);
                Calendar cal = new GregorianCalendar(year, month - 2, day);
                int daysOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                day--;
                if (day == 0) {
                    day = daysOfMonth;
                    month = month - 1;
                }

                if (month == 0) {
                    month = 12;
                    year--;
                }

                str = year + "/" + month + "/" + day;
                view_date = str;
                hhac_date.setText(str);

                refresh();
                String sql1 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                cursor = db.rawQuery(sql1, null);
                adapter.changeCursor(cursor);
                edit_content.setText("");
                edit_price.setText("");
            }
        });

        // 내일 버튼 클릭 시 실행 되는 함수
        next_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = (String) hhac_date.getText();
                String[] arr = str.split("/");
                int year = Integer.parseInt(arr[0]);
                int month = Integer.parseInt(arr[1]);
                int day = Integer.parseInt(arr[2]);
                Calendar cal = new GregorianCalendar(year, month - 1, day);
                int daysOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                Log.i("내일버튼", year + "/" + month + "/" + day + "/" + daysOfMonth);

                day++;
                if (day == (daysOfMonth + 1)) {
                    day = 1;
                    month = month + 1;
                }

                if (month == 13) {
                    month = 1;
                    year++;
                }

                str = year + "/" + month + "/" + day;
                view_date = str;
                hhac_date.setText(str);

                refresh();
                String sql1 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                cursor = db.rawQuery(sql1, null);
                adapter.changeCursor(cursor);
                edit_content.setText("");
                edit_price.setText("");
            }
        });
    }

    //오늘 날짜 가져오는 메소드
    private static String getToday_date() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        Date currentTime = new Date();
        String Today_day = sdf.format(currentTime);
        return Today_day;
    }

    //총 수입과, 총 지출을 출력해주는 함수 ( 새로고침 )
    public void refresh() {
        String sql1 = String.format("select sum(hhac_income) from '%s' where hhac_date = '%s'", "hhac_db", view_date);
        cursor = db.rawQuery(sql1, null);
        cursor.moveToNext();
        String income_sum = String.valueOf(cursor.getInt(0));
        hhac_income_sum.setText(df.format(Integer.parseInt(income_sum)));

        String sql2 = String.format("select sum(hhac_cost) from '%s' where hhac_date = '%s'", "hhac_db", view_date);
        cursor = db.rawQuery(sql2, null);
        cursor.moveToNext();
        String cost_sum = String.valueOf(cursor.getInt(0));
        hhac_cost_sum.setText(df.format(Integer.parseInt(cost_sum)));
    }
}









