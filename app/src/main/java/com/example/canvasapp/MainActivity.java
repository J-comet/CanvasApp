package com.example.canvasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.canvasapp.adapter.MemoAdapter;
import com.example.canvasapp.data.Memo;
import com.example.canvasapp.util.PreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LinearLayout liAddMemo;
    private RecyclerView rvMemo;
    private MemoAdapter memoAdapter;

    private ArrayList<Memo> memos;

    private String loginID = "test@test.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memos = new ArrayList<>();

        liAddMemo = findViewById(R.id.li_add_memo);
        rvMemo = findViewById(R.id.rv_memo);

        liAddMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        memoAdapter = new MemoAdapter();
        rvMemo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMemo.setAdapter(memoAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * preference 에 저장된 값이 있을 때는 list 불러옴
         */
        if (PreferenceUtil.getStringArrayPref(this, loginID) != null
                && PreferenceUtil.getStringArrayPref(this, loginID).size() > 0) {
//            JSONArray jsonArray = PreferenceUtil.getStringArrayPref(MemoActivity.this, loginID);

            memos = new ArrayList<>();

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Memo>>(){}.getType();
            ArrayList<Memo> memoArrayList = gson.fromJson(String.valueOf(PreferenceUtil.getStringArrayPref(this, loginID)), type);
            for (Memo memo : memoArrayList){
                Log.e("Memo Details", memo.getTitle() + "-" + memo.getContent() + "-" + memo.getBitmap());

                memos.add(memo);
            }

            memoAdapter.setData(memos);
        }


        // 추가된 메모가 있을 때
        /*if (getIntent().getSerializableExtra("memo") != null) {
            Memo memo = (Memo) getIntent().getSerializableExtra("memo");
            memos.add(memo);
            memoAdapter.setData(memos);
        }*/




    }
}