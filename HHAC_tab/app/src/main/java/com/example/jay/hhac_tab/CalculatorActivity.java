package com.example.jay.hhac_tab;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CalculatorActivity extends Fragment {

    TextView calc_data1, calc_op, calc_data2;
    TextView calc_result;
    TextView calc_ing;
    ListView result_list;


    Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    Button btnC, btnAbsol, btnDel, btnDot;
    Button btnDiv, btnMul, btnSub, btnPlus, btnEqual;

    String data;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CalculatorActivity() {
    }

    public static CalculatorActivity newInstance(String param1, String param2) {
        CalculatorActivity fragment = new CalculatorActivity();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fv = inflater.inflate(R.layout.fragment_calculator, container, false);

        calc_data1 = fv.findViewById(R.id.calc_data1);
        calc_op = fv.findViewById(R.id.calc_op);
        calc_data2 = fv.findViewById(R.id.calc_data2);
        calc_result = fv.findViewById(R.id.calc_result);
        calc_ing = fv.findViewById(R.id.calc_ing);
        result_list = fv.findViewById(R.id.result_list);

        final List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, list);
        result_list.setAdapter(adapter);

        btn0 = fv.findViewById(R.id.btn0);
        btn1 = fv.findViewById(R.id.btn1);
        btn2 = fv.findViewById(R.id.btn2);
        btn3 = fv.findViewById(R.id.btn3);
        btn4 = fv.findViewById(R.id.btn4);
        btn5 = fv.findViewById(R.id.btn5);
        btn6 = fv.findViewById(R.id.btn6);
        btn7 = fv.findViewById(R.id.btn7);
        btn8 = fv.findViewById(R.id.btn8);
        btn9 = fv.findViewById(R.id.btn9);
        btnDot = fv.findViewById(R.id.btnDot);
        btnC = fv.findViewById(R.id.btnC);
        btnAbsol = fv.findViewById(R.id.btnAbsol);
        btnDel = fv.findViewById(R.id.btnDel);
        btnDiv = fv.findViewById(R.id.btnDiv);
        btnMul = fv.findViewById(R.id.btnMul);
        btnSub = fv.findViewById(R.id.btnSub);
        btnPlus = fv.findViewById(R.id.btnPlus);
        btnEqual = fv.findViewById(R.id.btnEqual);

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "0");
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "1");
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "2");
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "3");
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "4");
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "5");
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "6");
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "7");
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "8");
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + "9");
            }
        });
        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText(calc_ing.getText().toString() + ".");
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc_ing.setText("");
                calc_data1.setText("");
                calc_op.setText("");
                calc_data2.setText("");
                calc_result.setText("");
            }
        });
        btnAbsol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Double.parseDouble(calc_ing.getText().toString())) - ((int) Double.parseDouble(calc_ing.getText().toString()))) == 0.0) {
                    calc_ing.setText("" + (Integer.parseInt(calc_ing.getText().toString()) * -1));
                } else {
                    calc_ing.setText("" + (Double.parseDouble(calc_ing.getText().toString()) * -1));
                }
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = (String) calc_ing.getText();
                calc_ing.setText(data.substring(0, data.length() - 1));
            }
        });

        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(calc_result.getText().toString())) {
                    data = (String) calc_result.getText();
                    calc_result.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("/");
                } else {
                    data = (String) calc_ing.getText();
                    calc_ing.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("/");
                }
            }
        });
        btnMul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(calc_result.getText().toString())) {
                    data = (String) calc_result.getText();
                    calc_result.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("*");
                } else {
                    data = (String) calc_ing.getText();
                    calc_ing.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("*");
                }
            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(calc_result.getText().toString())) {
                    data = (String) calc_result.getText();
                    calc_result.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("-");
                } else {
                    data = (String) calc_ing.getText();
                    calc_ing.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("-");
                }
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(calc_result.getText().toString())) {
                    data = (String) calc_result.getText();
                    calc_result.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("+");
                } else {
                    data = (String) calc_ing.getText();
                    calc_ing.setText("");
                    calc_data1.setText(data);
                    calc_data2.setText("");
                    calc_op.setText("+");
                }
            }
        });
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = (String) calc_ing.getText();
                calc_ing.setText("");
                calc_data2.setText(data);
                String data1 = calc_data1.getText().toString();
                String op = calc_op.getText().toString();
                String data2 = calc_data2.getText().toString();
                String res = "";
                if (op.equals("+")) {
                    res = String.valueOf((Integer.parseInt(data1) + Integer.parseInt(data2)));

                } else if (op.equals("-")) {
                    res = String.valueOf((Integer.parseInt(data1) - Integer.parseInt(data2)));

                } else if (op.equals("*")) {
                    res = String.valueOf((Integer.parseInt(data1) * Integer.parseInt(data2)));

                } else if (op.equals("/")) {
                    res = String.valueOf((Integer.parseInt(data1) / Integer.parseInt(data2)));

                } else {
                    Toast.makeText(getActivity(), "연산자가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                    calc_ing.setText("");
                    calc_data1.setText("");
                    calc_op.setText("");
                    calc_data2.setText("");
                    calc_result.setText("");
                }
                calc_result.setText(res);
                String list_result = data1 + " " + op + " " + data2 + " = " + res;
                list.add(list_result);
            }
        });


        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] arr = list.get(position).split(" ");
                ClipboardManager cbm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cd = ClipData.newPlainText("CalculatedData", arr[4]);
                cbm.setPrimaryClip(cd);
                Toast.makeText(getActivity(), "계산값이 복사되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        return fv;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}