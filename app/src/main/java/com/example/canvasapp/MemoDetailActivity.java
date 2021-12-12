package com.example.canvasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.canvasapp.data.Memo;
import com.example.canvasapp.util.BitmapConverter;

public class MemoDetailActivity extends AppCompatActivity {

    private Memo memo;

    private ImageView ivResultDraw;
    private TextView tvTitle;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        ivResultDraw = findViewById(R.id.iv_result_draw);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().getSerializableExtra("memo") != null) {
            memo = (Memo) getIntent().getSerializableExtra("memo");

            tvTitle.setText(memo.getTitle());
            tvContent.setText(memo.getContent());
            ivResultDraw.setImageBitmap(BitmapConverter.StringToBitmap(memo.getBitmap()));
        }
    }
}