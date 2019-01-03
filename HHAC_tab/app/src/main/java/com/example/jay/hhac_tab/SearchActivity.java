package com.example.jay.hhac_tab;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class SearchActivity extends Fragment {

    //사용할 변수 설정
    private EditText search_content;
    private Button searchbtn;

    //DB관련 변수 설정
    DBHelper dbh;
    SQLiteDatabase db;
    Cursor cursor;
    CursorAdapterActivity_Search adapter;

    //숫자에 화폐단위를 찍어주는 DecimalFormat 설정
    //사용 방법 df.format(int);
    DecimalFormat df = new DecimalFormat("#,###원");

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchActivity() {
    }

    public static SearchActivity newInstance(String param1, String param2) {
        SearchActivity fragment = new SearchActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fv = inflater.inflate(R.layout.fragment_search, container, false);

        search_content = fv.findViewById(R.id.search_content);
        searchbtn = fv.findViewById(R.id.searchbtn);
        final ListView list = fv.findViewById(R.id.search_account_list);

        //데이터베이스 생성
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        //CursorAdapter 생성
        String sql1 = String.format("select * from %s order by hhac_date desc", "hhac_db");
        cursor = db.rawQuery(sql1, null);
        adapter = new CursorAdapterActivity_Search(getActivity(), cursor);

        //리스트 뷰 어댑터 설정
        list.setAdapter(adapter);

        //검색 버튼 눌렀을 시
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql2 = String.format("select * from %s where hhac_content like '%s' order by hhac_date desc", "hhac_db", "%" + search_content.getText() + "%");
                cursor = db.rawQuery(sql2, null);
                adapter.changeCursor(cursor);
            }
        });
        return fv;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
