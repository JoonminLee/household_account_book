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

    DBHelper dbh;
    SQLiteDatabase db;
    Cursor cursor;
    DecimalFormat df = new DecimalFormat("#,###원");
    private TextView cal_date, cal_m_income_sum, cal_m_cost_sum, cal_m_all_sum;
    private Button cal_prev, cal_next;
    private GridView gridView;
    private CustomCalendarActivity.GridAdapter gridAdapter;
    private ArrayList<String> dayList;
    private static Calendar Cal = Calendar.getInstance();
    long now = System.currentTimeMillis();
    final Date date = new Date(now);
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

        cal_date = fv.findViewById(R.id.cal_date);
        gridView = fv.findViewById(R.id.gridview);
        cal_m_income_sum = fv.findViewById(R.id.cal_m_income_sum);
        cal_m_cost_sum = fv.findViewById(R.id.cal_m_cost_sum);
        cal_m_all_sum = fv.findViewById(R.id.cal_m_all_sum);
        cal_prev = fv.findViewById(R.id.cal_prev);
        cal_next = fv.findViewById(R.id.cal_next);

        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        cal_date.setText(String.format(curYearFormat.format(date) + "/" + curMonthFormat.format(date)));

        dayList = new ArrayList<String>();

        Cal = Calendar.getInstance();

        Cal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);

        dayNum = Cal.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }
        setCalendarDate(Cal.get(Calendar.YEAR), Cal.get(Calendar.MONTH) + 1);

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


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = cal_date.getText() + "/" + (position - (dayNum - 2));
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return fv;
    }

    private void setCalendarDate(int year, int month) {
        cal_date.setText(year + "/" + month);
        Cal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < Cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));

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

            gridAdapter = new GridAdapter(getActivity(), dayList);
            gridView.setAdapter(gridAdapter);
        }
    }

    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

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

            String isql = String.format("select sum(hhac_income) from '%s' where hhac_date = '%s'", "hhac_db", cal_date.getText() + "/" + getItem(position));
            cursor = db.rawQuery(isql, null);
            cursor.moveToNext();
            String income_sum = String.valueOf(cursor.getInt(0));
            if (Integer.parseInt(income_sum) == 0) {
                holder.calitemIncome.setVisibility(View.INVISIBLE);
            } else {
                holder.calitemIncome.setText(df.format(Integer.parseInt(income_sum)));
            }

            String csql = String.format("select sum(hhac_cost) from '%s' where hhac_date = '%s'", "hhac_db", cal_date.getText() + "/" + getItem(position));
            cursor = db.rawQuery(csql, null);
            cursor.moveToNext();
            String cost_sum = String.valueOf(cursor.getInt(0));
            if (Integer.parseInt(cost_sum) == 0) {
                holder.calitemCost.setVisibility(View.INVISIBLE);
            } else {
                holder.calitemCost.setText(df.format(Integer.parseInt(cost_sum))); //화폐단위로 출력
            }

            holder.calitemGridView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            holder.calitemIncome.setTextColor(getResources().getColor(R.color.colorBlue));
            holder.calitemCost.setTextColor(getResources().getColor(R.color.colorAccent));

            Cal = Calendar.getInstance();
            String thisy = String.valueOf(Cal.get(Calendar.YEAR));
            String thism = String.valueOf(Cal.get(Calendar.MONTH) + 1);
            String thisym = thisy + "/" + thism;
            if (thisym.equals(cal_date.getText())) {
                Integer today = Cal.get(Calendar.DAY_OF_MONTH);
                String sToday = String.valueOf(today);
                if (sToday.equals(getItem(position))) {
                    holder.calitemGridView.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
            return convertView;
        }
    }

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
