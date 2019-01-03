package com.example.jay.hhac_tab;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CustomCalendarActivity extends Fragment {


    //DB관련 변수 설정
    DBHelper dbh;
    SQLiteDatabase db;
    Cursor cursor;

    //숫자에 화폐단위를 찍어주는 DecimalFormat 설정
    //사용 방법 df.format(int);
    DecimalFormat df = new DecimalFormat("#,###원");

    // 연/월, 월 총 수입/지출, 합계 텍스트뷰
    private TextView cal_date, cal_m_income_sum, cal_m_cost_sum, cal_m_all_sum;

    // 이전 달, 다음 달
    private Button cal_prev, cal_next;

    // 그리드뷰
    private GridView gridView;

    // 그리드뷰 어댑터
    private CustomCalendarActivity.GridAdapter gridAdapter;

    // 달력의 일을 저장 할 리스트 : 실질적으로 매 그리드 뷰 한칸 한칸을 채워줄 녀석들
    private ArrayList<String> dayList;

    // 캘린더 변수
    private static Calendar Cal = Calendar.getInstance();

    //현재 날짜, 시간을 가져와서 date 변수에 입력
    long now = System.currentTimeMillis();
    final Date date = new Date(now);

    //연,월,일을 원하는 패턴으로 따로 저장
    final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
    int thisYear = Cal.get(Calendar.YEAR);
    int thisMonth = Cal.get(Calendar.MONTH) + 1;
    int dayNum;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CustomCalendarActivity() {
    }

    public static CustomCalendarActivity newInstance(String param1, String param2) {
        CustomCalendarActivity fragment = new CustomCalendarActivity();
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
        View fv = inflater.inflate(R.layout.fragment_calendar, container, false);

        //레이아웃 변수설정
        cal_date = fv.findViewById(R.id.cal_date);
        gridView = fv.findViewById(R.id.gridview);
        cal_m_income_sum = fv.findViewById(R.id.cal_m_income_sum);
        cal_m_cost_sum = fv.findViewById(R.id.cal_m_cost_sum);
        cal_m_all_sum = fv.findViewById(R.id.cal_m_all_sum);
        cal_prev = fv.findViewById(R.id.cal_prev);
        cal_next = fv.findViewById(R.id.cal_next);

        //데이터베이스 설정
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        //오늘의 날짜를 텍스트뷰에 뿌려줌
        cal_date.setText(String.format(curYearFormat.format(date) + "/" + curMonthFormat.format(date)));

        //gridView 첫번째 줄에, 요일 표시를 하기위해, dayList라는 ArrayList에 값을 넣어준다,
        //차후 dayList에 계속 해서 값을 넣음으로, 기억하고있기~
        dayList = new ArrayList<String>();

        //현재 날짜 가져오기
        Cal = Calendar.getInstance();

        //이번 달 1일 무슨요일인지 알아내고, 설정해준다 Cal.set(Year,Month,Day)
        Cal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);

        //이번 달 1일이 무슨 요일인지 확인하여, dayNum에 넣는다
        //일요일부터 : 1(일), 2(월), 3(화), 4(수), 5(목), 6(금), 7(토)
        dayNum = Cal.get(Calendar.DAY_OF_WEEK);

        //그 달의 1일과 요일을 매칭 시키기 위해 공백을 추가해준다
        //2018/12 기준, dayNum = 7;
        //4개의 공백을 만들어서, dayList에 넣어준다
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }
        //캘린더의 월을 설정해 준다
        setCalendarDate(Cal.get(Calendar.YEAR), Cal.get(Calendar.MONTH) + 1);

        //다음 달
        cal_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayList = new ArrayList<>();
                Cal = Calendar.getInstance();
                Cal.set(thisYear, thisMonth, 1);
                dayNum = Cal.get(Calendar.DAY_OF_WEEK);
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                if (thisMonth < 12) {
                    thisMonth++;
                    setCalendarDate(thisYear, thisMonth);
                } else {
                    thisYear++;
                    thisMonth = 1;
                    setCalendarDate(thisYear, thisMonth);
                }
                cal_date.setText(thisYear + "/" + thisMonth);
            }
        });

        //이전 달
        cal_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayList = new ArrayList<>();
                Cal = Calendar.getInstance();
                Cal.set(thisYear, thisMonth - 2, 1);
                dayNum = Cal.get(Calendar.DAY_OF_WEEK);
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                if (thisMonth > 1) {
                    thisMonth--;
                    setCalendarDate(thisYear, thisMonth);
                } else {
                    thisYear--;
                    thisMonth = 12;
                    setCalendarDate(thisYear, thisMonth);
                }
                cal_date.setText(thisYear + "/" + thisMonth);

            }
        });


        //캘린더(gridView)의 날짜를 눌렀을 때 실행되는 함수
        //MainAcitivty.class로 보내준다!
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = cal_date.getText() + "/" + (position - (dayNum - 2));
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);
            }
        });
        return fv;
    }

    //해당 월에 표시할 일 수 구함
    //@param month
    private void setCalendarDate(int year, int month) {
        cal_date.setText(year + "/" + month);
        Cal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < Cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));

            //해당 월의 총 수입, 지출, 합계를 설정
            String misql = String.format("select sum(hhac_income) from '%s' where hhac_date like '%s'", "hhac_db", "%" + cal_date.getText() + "%");
            cursor = db.rawQuery(misql, null);
            cursor.moveToNext();
            String m_income_sum = String.valueOf(cursor.getInt(0));
            cal_m_income_sum.setText(df.format(Integer.parseInt(m_income_sum)));
            cal_m_income_sum.setTextColor(getResources().getColor(R.color.colorBlue));

            String mcsql = String.format("select sum(hhac_cost) from '%s' where hhac_date like '%s'", "hhac_db", "%" + cal_date.getText() + "%");
            cursor = db.rawQuery(mcsql, null);
            cursor.moveToNext();
            String m_cost_sum = String.valueOf(cursor.getInt(0));
            cal_m_cost_sum.setText(df.format(Integer.parseInt(m_cost_sum)));
            cal_m_cost_sum.setTextColor(getResources().getColor(R.color.colorAccent));

            int m_all_sum = Integer.parseInt(m_income_sum) - Integer.parseInt(m_cost_sum);
            cal_m_all_sum.setText(df.format(m_all_sum));
            cal_m_all_sum.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            //어댑터 설정
            gridAdapter = new GridAdapter(getActivity(), dayList);
            gridView.setAdapter(gridAdapter);
        }
    }

    //그리드뷰 어댑터
    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;
        // 생성자
        // @param context
        // @param list

        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //리스트의 사이즈를 반환
        @Override
        public int getCount() {
            return list.size();
        }

        //리스트 안의 각 아이템들의 인덱스 값을 반환
        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        //리스트 안의 각 아이템들의 아이디 값을 반환
        @Override
        public long getItemId(int position) {
            return position;
        }

        //CustomCalendarActivity가 실행될때, onCreate와 같이 실행되는 함수
        //실질적인 View를 담당하고있다.
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomCalendarActivity.ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar_gridview, parent, false);
                holder = new ViewHolder();
                holder.calitemGridView = convertView.findViewById(R.id.cal_item_gridview);
                holder.calitemIncome = convertView.findViewById(R.id.cal_item_income);
                holder.calitemCost = convertView.findViewById(R.id.cal_item_cost);
                convertView.setTag(holder);
            } else {
                holder = (CustomCalendarActivity.ViewHolder) convertView.getTag();
            }
            holder.calitemGridView.setText(getItem(position));

            // 달력에 그 날에 대한 수입 표시
            String isql = String.format("select sum(hhac_income) from '%s' where hhac_date = '%s'", "hhac_db", cal_date.getText() + "/" + getItem(position));
            cursor = db.rawQuery(isql, null);
            cursor.moveToNext();
            String income_sum = String.valueOf(cursor.getInt(0));
            if (Integer.parseInt(income_sum) == 0) { // 수입이 없다면, 띄우지 않는다
                holder.calitemIncome.setVisibility(View.INVISIBLE);
            } else {
                holder.calitemIncome.setText(df.format(Integer.parseInt(income_sum))); //화폐단위로 출력
            }

            // 달력에 그 날에 대한 지출 표시
            String csql = String.format("select sum(hhac_cost) from '%s' where hhac_date = '%s'", "hhac_db", cal_date.getText() + "/" + getItem(position));
            cursor = db.rawQuery(csql, null);
            cursor.moveToNext();
            String cost_sum = String.valueOf(cursor.getInt(0));
            if (Integer.parseInt(cost_sum) == 0) { // 지출이 없다면, 띄우지 않는다
                holder.calitemCost.setVisibility(View.INVISIBLE);
            } else {
                holder.calitemCost.setText(df.format(Integer.parseInt(cost_sum))); //화폐단위로 출력
            }

            //일, 수입, 지출에 대한 textcolor변경
            holder.calitemGridView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            holder.calitemIncome.setTextColor(getResources().getColor(R.color.colorBlue));
            holder.calitemCost.setTextColor(getResources().getColor(R.color.colorAccent));

            //해당 날짜 텍스트 컬러,배경 변경
            Cal = Calendar.getInstance();
            String thisy = String.valueOf(Cal.get(Calendar.YEAR));
            String thism = String.valueOf(Cal.get(Calendar.MONTH) + 1);
            String thisym = thisy + "/" + thism;
            if (thisym.equals(cal_date.getText())) {
                //해당 월의 당일날짜를 가져와, 눈에 띄도록 textcolor변경
                Integer today = Cal.get(Calendar.DAY_OF_MONTH);
                String sToday = String.valueOf(today);
                if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                    holder.calitemGridView.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
            return convertView;
        }
    }

    //calendar_gridview.xml와, fragment_calendar 각 view들에 대한 변수들을 저장해 두는 함수
    //밖으로 꺼내 놓거나, onCreate에서 실행해도 상관없다
    private class ViewHolder {
        TextView calitemGridView, calitemIncome, calitemCost;
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
