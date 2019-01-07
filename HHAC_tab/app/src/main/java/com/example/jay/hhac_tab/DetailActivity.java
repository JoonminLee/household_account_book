package com.example.jay.hhac_tab;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;
    private TextView hhac_date, hhac_income_sum, hhac_cost_sum, album_uri;
    private Button gotoCalendar, incomebtn, costbtn, prev_date, next_date;
    private EditText edit_content, edit_price;
    public static String view_date = getToday_date();
    DBHelper dbh;
    SQLiteDatabase db;
    Cursor cursor;
    CursorAdapterActivity adapter;
    DecimalFormat df = new DecimalFormat("#,###원");
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;
    String price_type, timeStamp;
    ImageView dialog_imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbh = new DBHelper(this);
        db = dbh.getWritableDatabase();

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
        dialog_imgView = findViewById(R.id.dialog_imgView);
        ListView list = findViewById(R.id.account_list);

        final Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");
        if (!TextUtils.isEmpty(selectedDate)) {
            view_date = selectedDate;
            hhac_date.setText(selectedDate);
        } else {
            hhac_date.setText(view_date);
        }

        refresh();

        gotoCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendar_intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(calendar_intent);
            }
        });

        final String sql1 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
        cursor = db.rawQuery(sql1, null);
        adapter = new CursorAdapterActivity(this, cursor);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dialog dialog = new Dialog(DetailActivity.this);
                dialog.setContentView(R.layout.detail_dialog);

                ImageView iv = (ImageView) dialog.findViewById(R.id.dialog_imgView);
                String str = ((TextView) view.findViewById(R.id.item_time)).getText().toString();

                String sql1 = String.format("select hhac_picture from %s where hhac_time = '%s'", "hhac_db", str);
                cursor = db.rawQuery(sql1, null);
                cursor.moveToNext();

                Uri imgUri = Uri.parse(cursor.getString(0));
                iv.setImageURI(imgUri);

                dialog.show();
            }
        });


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

        incomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setMessage("사진을 첨부 하시겠습니까?");
                dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAlbum();
                        price_type = "수입";
                    }

                });
                dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText ec = findViewById(R.id.edit_content);
                        EditText ep = findViewById(R.id.edit_price);

                        String contents = ec.getText().toString();
                        int price = Integer.parseInt(ep.getText().toString());

                        String sql1 = String.format("insert into '%s' values( null, '%s', %d, null, '%s', null, null);", "hhac_db", contents, price, view_date);
                        db.execSQL(sql1);

                        refresh();
                        String sql2 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                        cursor = db.rawQuery(sql2, null);
                        adapter.changeCursor(cursor);
                        edit_content.setText("");
                        edit_price.setText("");
                    }
                });
                dialog.show();
            }
        });

        costbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setMessage("사진을 첨부 하시겠습니까?");
                dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAlbum();
                        price_type = "지출";
                    }
                });
                dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText ec = findViewById(R.id.edit_content);
                        EditText ep = findViewById(R.id.edit_price);

                        String contents = ec.getText().toString();
                        int price = Integer.parseInt(ep.getText().toString());

                        String sql1 = String.format("insert into '%s' values( null, '%s', null, %d, '%s', null, null);", "hhac_db", contents, price, view_date);
                        db.execSQL(sql1);

                        refresh();
                        String sql2 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                        cursor = db.rawQuery(sql2, null);
                        adapter.changeCursor(cursor);
                        edit_content.setText("");
                        edit_price.setText("");
                    }
                });
                dialog.show();
            }
        });

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

    private static String getToday_date() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        Date currentTime = new Date();
        String Today_day = sdf.format(currentTime);
        return Today_day;
    }

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

    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    public void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "내역을 누르면, 사진이 뜹니다", Toast.LENGTH_SHORT).show();
    }

    public File createImageFile() throws IOException {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "HHAC");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    galleryAddPic();

                    EditText ec = findViewById(R.id.edit_content);
                    EditText ep = findViewById(R.id.edit_price);

                    String contents = ec.getText().toString();
                    int price = Integer.parseInt(ep.getText().toString());

                    if (price_type.equals("수입")) {
                        String sql1 = String.format("insert into '%s' values( null, '%s', %d, null, '%s', '%s','%s');", "hhac_db", contents, price, view_date, albumURI, timeStamp);
                        db.execSQL(sql1);
                    } else if (price_type.equals("지출")) {
                        String sql1 = String.format("insert into '%s' values( null, '%s', null, %d, '%s', '%s','%s');", "hhac_db", contents, price, view_date, albumURI, timeStamp);
                        db.execSQL(sql1);
                    }

                    refresh();
                    String sql2 = String.format("select * from %s where hhac_date = '%s'", "hhac_db", view_date);
                    cursor = db.rawQuery(sql2, null);
                    adapter.changeCursor(cursor);
                    edit_content.setText("");
                }
                edit_price.setText("");
                break;
        }
    }

}








