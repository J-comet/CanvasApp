package com.example.canvasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canvasapp.data.Memo;
import com.example.canvasapp.util.BitmapConverter;
import com.example.canvasapp.util.PreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MemoActivity extends AppCompatActivity {

    private Button btnDrawing;

    private EditText etTitle;
    private EditText etContent;

    private TextView tvNoneData;

    private ImageView ivResultDraw;

    private LinearLayout liSave;

    private ArrayList<Memo> memos;

    private String loginID = "test@test.com";

//    private RecyclerView rvCanvas;
//    private DrawAdapter drawAdapter;
//    private ArrayList<Bitmap> bitmaps;
//    private ArrayList<String> strBitmapList;

    private boolean isDrawerImg = false;

    private Bitmap resultBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        memos = new ArrayList<>();

//        rvCanvas = findViewById(R.id.rv_canvas);
        btnDrawing = findViewById(R.id.btn_drawing);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        tvNoneData = findViewById(R.id.tv_none_data);
        ivResultDraw = findViewById(R.id.iv_result_draw);
        liSave = findViewById(R.id.li_save);

//        drawAdapter = new DrawAdapter();

        etContent.addTextChangedListener(new TextWatcher() {

            String strPrevious = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                strPrevious = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 라인 수 6줄로 제한
                if (etContent.getLineCount() >= 7) {
                    etContent.setText(strPrevious);
                    etContent.setSelection(etContent.length());
                    Toast.makeText(MemoActivity.this, "최대 작성할 수 있는 라인은 6줄 입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemoActivity.this, DrawingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        liSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 값 다 넘어왔을 때
                if (isDrawerImg
                        && etTitle.getText().toString().length() > 0
                        && etContent.getText().toString().length() > 0) {

                    /**
                     * new Memo
                     */

                    memos = new ArrayList<>();

                    Memo memoData = new Memo();
                    memoData.setTitle(etTitle.getText().toString());
                    memoData.setContent(etContent.getText().toString());
                    memoData.setBitmap(BitmapConverter.BitmapToString(resultBitmap));

                    Log.e("memo", memoData.getTitle());
                    Log.e("memo", memoData.getContent());
                    Log.e("memo", memoData.getBitmap());

                    memos.add(memoData);

                    Log.e("버튼", memos.size() + "");

                    // ArrayList -> JSON 으로 변환
                    try {

                        /**
                         * preference 값 있을 때 가져와서 jsonArray 에 값 넣기
                         *
                         */
                        JSONArray jsonArray = null;

                        if (PreferenceUtil.getString(MemoActivity.this, loginID).length() > 0) {
                            jsonArray = new JSONArray(PreferenceUtil.getString(MemoActivity.this, loginID));
                            Log.e("jsonArray", "33333333333333333333333333333333");
                            Log.e("jsonArray", jsonArray.toString());
                        } else {
                            jsonArray = new JSONArray();
                        }

                        for (int i = 0; i < memos.size(); i++) {
                            JSONObject object = new JSONObject();   //배열 내에 들어갈 json
                            object.put("title", memoData.getTitle());
                            object.put("content", memoData.getContent());
                            object.put("bitmap", memoData.getBitmap());
                            jsonArray.put(object);
                        }

                        Log.d("JSON Test", jsonArray.toString());

                        PreferenceUtil.setString(MemoActivity.this, loginID, jsonArray.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // MainActivity 로 Memo 데이터 전달
                    Intent intent = new Intent(MemoActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("memo", memoData);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(MemoActivity.this, "모든 값을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("onStart", "onStart");

        // DrawingActivity 에서 그림값 가져옴
        if (getIntent().getByteArrayExtra("bitmap") != null) {
            byte[] bytes = getIntent().getByteArrayExtra("bitmap");
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ivResultDraw.setImageBitmap(bmp);
            isDrawerImg = true;
            resultBitmap = bmp;
        }

        if (PreferenceUtil.getStringArrayPref(MemoActivity.this, loginID) != null
                && PreferenceUtil.getStringArrayPref(MemoActivity.this, loginID).size() > 0) {

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Memo>>() {
            }.getType();
            ArrayList<Memo> memoArrayList = gson.fromJson(String.valueOf(PreferenceUtil.getStringArrayPref(MemoActivity.this, loginID)), type);

            Log.e("메모스타트", memoArrayList.size() + "");

            for (Memo memo : memoArrayList) {
                Log.e("MemoDetails", memo.getTitle() + "-" + memo.getContent() + "-" + memo.getBitmap());

                /*Memo prefMemo = new Memo();
                prefMemo.setTitle(memo.getTitle());
                prefMemo.setContent(memo.getContent());
                prefMemo.setBitmap(memo.getBitmap());*/

                memos.add(memo);

                Log.e("메모prefMemo", memo.getTitle());

            }
            Log.e("메모스타트", memos.size() + "");
        }
    }

}