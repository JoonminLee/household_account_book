package com.example.jay.hhac_tab;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GraphActivity extends Fragment {

    //DB관련 변수 설정
    DBHelper dbh;
    SQLiteDatabase db;
    Cursor cursor;

    //차트 변수 설정
    LineChart lineChart;
    BarChart barChart;

    //캘린더 변수
    private static Calendar Cal = Calendar.getInstance();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //현재 년도
    int thisYear = Cal.get(Calendar.YEAR);

    Button prevYear, nowYear;
    TextView txt_thisYear;

    public GraphActivity() {
    }

    public static GraphActivity newInstance(String param1, String param2) {
        GraphActivity fragment = new GraphActivity();
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
        View fv = inflater.inflate(R.layout.fragment_graph, container, false);

        prevYear = fv.findViewById(R.id.prevYear);
        nowYear = fv.findViewById(R.id.nowYear);
        lineChart = fv.findViewById(R.id.linechart);
        txt_thisYear = fv.findViewById(R.id.txt_thisYear);

        String isql;
        String csql;
        String sum;

        //데이터베이스 설정
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();

        txt_thisYear.setText(thisYear + "년");

        for (int i = 1; i < 13; i++) {
            isql = String.format("select sum(hhac_income) from %s where hhac_date like '%s'", "hhac_db", thisYear + "/" + i + "/" + "%");
            cursor = db.rawQuery(isql, null);
            cursor.moveToNext();
            sum = String.valueOf(cursor.getInt(0));
            entries.add(new Entry(i, Integer.parseInt(sum)));
        }
        for (int i = 1; i < 13; i++) {
            isql = String.format("select sum(hhac_cost) from %s where hhac_date like '%s'", "hhac_db", thisYear + "/" + i + "/" + "%");
            cursor = db.rawQuery(isql, null);
            cursor.moveToNext();
            sum = String.valueOf(cursor.getInt(0));
            entries2.add(new Entry(i, Integer.parseInt(sum)));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "수입");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(3);
        lineDataSet.setCircleColor(Color.parseColor("#d0dffe"));
        lineDataSet.setCircleHoleColor(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#d0dffe"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);

        LineDataSet lineDataSet2 = new LineDataSet(entries2, "지출");
        lineDataSet2.setLineWidth(2);
        lineDataSet2.setCircleRadius(3);
        lineDataSet2.setCircleColor(Color.parseColor("#fd9cb4"));
        lineDataSet2.setCircleHoleColor(Color.RED);
        lineDataSet2.setColor(Color.parseColor("#fd9cb4"));
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSet);
        lineDataSets.add(lineDataSet2);
        lineChart.setData(new LineData(lineDataSets));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setDrawLabels(false);
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.invalidate();

        barChart = fv.findViewById(R.id.barchart);

        List<BarEntry> entries3 = new ArrayList<>();
        List<BarEntry> entries4 = new ArrayList<>();

        for (int i = 1; i < 13; i++) {
            csql = String.format("select sum(hhac_income) from %s where hhac_date like '%s'", "hhac_db", thisYear + "/" + i + "/" + "%");
            cursor = db.rawQuery(csql, null);
            cursor.moveToNext();
            sum = String.valueOf(cursor.getInt(0));
            entries3.add(new BarEntry(i, Integer.parseInt(sum)));
        }
        for (int i = 1; i < 13; i++) {
            csql = String.format("select sum(hhac_cost) from %s where hhac_date like '%s'", "hhac_db", thisYear + "/" + i + "/" + "%");
            cursor = db.rawQuery(csql, null);
            cursor.moveToNext();
            sum = String.valueOf(cursor.getInt(0));
            entries4.add(new BarEntry(i, Integer.parseInt(sum)));
        }

        BarDataSet barDataSet = new BarDataSet(entries3, "수입");
        barDataSet.setColor(Color.parseColor("#d0dffe"));

        BarDataSet barDataSet2 = new BarDataSet(entries4, "지출");
        barDataSet2.setColor(Color.parseColor("#fd9cb4"));

        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);
        barDataSets.add(barDataSet2);
        barChart.setData(new BarData(barDataSets));

        XAxis xAxis2 = barChart.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setTextColor(Color.BLACK);
        xAxis2.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis2 = barChart.getAxisLeft();
        yLAxis2.setDrawLabels(false);
        yLAxis2.setTextColor(Color.BLACK);

        YAxis yRAxis2 = barChart.getAxisRight();
        yRAxis2.setDrawLabels(false);
        yRAxis2.setDrawAxisLine(false);
        yRAxis2.setDrawGridLines(false);


        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDescription(description);
        barChart.invalidate();

        prevYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisYear--;
                txt_thisYear.setText(thisYear + "년");
                refresh();
            }
        });

        nowYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisYear = Cal.get(Calendar.YEAR);
                txt_thisYear.setText(thisYear + "년");
                refresh();
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

    public void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
