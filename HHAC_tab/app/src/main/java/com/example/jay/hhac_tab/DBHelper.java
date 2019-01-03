package com.example.jay.hhac_tab;
// 어떠한 것들을 import했는지 확인 먼저 하세요 누나!! 대략적으로 이 클래스가 무슨 일을 하는 녀석인지 알기 쉬워져요

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // 버전 설정 : 그냥 아무렇게나 값 잡아도 되는듯
    public static final int DATABASE_VERSION = 1;

    // 생성자 생성
    //public DBHelper(Context context) { super(어디에서 실행 할건지, 자신이 원하는 DB명, 커서 팩토리(?) 쓰는지, DB버전);}
    public DBHelper(Context context) {
        super(context, "hhac.db", null, DATABASE_VERSION);
    }

    // 앱이 최초로 시작 될 때, 최초로 실행 되는 함수, 대부분 여기에서 테이블을 생성
    // 왜 그러는지는 모르겠는데 데이터들의 타입을 number, varchartext로 잘 안 쓰고 integer, double, long 으로 쓰더라구용?
    @Override
    public void onCreate(SQLiteDatabase db) {
        //autoincrement 속성 사용 시, primary key로 지정한다.
        db.execSQL("create table hhac_db (" +
                //_id라는 컬럼은 무조건 만들어 주어야합니다, 그래야 CursorAdapter가 인식을 해서, 하나하나 출력해줘요!
                "_id integer primary key autoincrement," +
                " hhac_content text," +
                " hhac_income integer," +
                " hhac_cost integer," +
                " hhac_date text)");

    }

    // DB버전이 바뀌 였을 때, 앱와 연동되어있는 DB 안의 테이블을 제거해주고, 다시 새롭게 만들주는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists hhac_db");
        onCreate(db);
    }
}
