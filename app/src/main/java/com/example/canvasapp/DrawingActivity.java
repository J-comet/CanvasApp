package com.example.canvasapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canvasapp.util.BitmapConverter;
import com.example.canvasapp.util.PreferenceUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DrawingActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] arrFilePermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int CODE_FILE_PERMISSION_ALL_GRANTED = 200;  // 권한 허용 코드
    private static final int CODE_FILE_PERMISSION_DENIED_TRUE = 100;  // 허용 거부한적 있는 유저일 때 코드
    private static final int CODE_FILE_PERMISSION_FIRST = 1000;  // 처음 권한 요청하는 유저일 때 코드

    private CustomCanvasView canvasView;

    private LinearLayout btnSave;
    private TextView tvPen;
    private TextView tvEraser;

    private ArrayList<String> strBitmapList;

    // 권한 획득 launcher
    ActivityResultLauncher<String[]> filePermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {

                        Boolean fileReadGranted = null;
                        Boolean fileWriteGranted = null;

                        Log.e("result", "result :" + result.toString());

                        // API 24 이상에서 동작
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            fileReadGranted = result.getOrDefault(
                                    Manifest.permission.READ_EXTERNAL_STORAGE, false);
                            fileWriteGranted = result.getOrDefault(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, false);

                        } else {
                            // API 24 이하에서 동작
                            fileReadGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                            fileWriteGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                        }


                        if (fileReadGranted && fileWriteGranted) {
                            Toast.makeText(DrawingActivity.this, "파일권한 허용", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DrawingActivity.this, "파일권한 거부", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        if (PreferenceUtil.getStringArrayPref(this, "list") != null) {

            strBitmapList = PreferenceUtil.getStringArrayPref(this, "list");
            Log.e("strBitmapList", strBitmapList.size()+"");
        } else {
            strBitmapList = new ArrayList<>();
            Log.e("strBitmapList", "nonononononononononono");
        }

        canvasView = findViewById(R.id.canvas_view);
        btnSave = findViewById(R.id.btn_save);
        tvPen = findViewById(R.id.tv_pen);
        tvEraser = findViewById(R.id.tv_eraser);

        // 처음에 연필모드로 사용자가 알도록 세팅
        drawModeStatus(CustomCanvasView.MODE_PEN);

        tvPen.setOnClickListener(this);
        tvEraser.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void drawModeStatus(int mode) {
        tvPen.setBackgroundResource(R.drawable.bg_round_01);
        tvEraser.setBackgroundResource(R.drawable.bg_round_01);

        switch (mode) {
            case CustomCanvasView.MODE_PEN:
                tvPen.setBackgroundResource(R.drawable.bg_round_02);
                break;
            case CustomCanvasView.MODE_ERASER:
                tvEraser.setBackgroundResource(R.drawable.bg_round_02);
                break;
        }
    }

    // 폴더 생성
    private void createFolder() {
        String mediaPath = Environment.getExternalStorageDirectory() + "/";
        String directoryName = "canvas";

        File dir = new File(mediaPath + directoryName);

        if (dir.exists()) {
            Log.d("폴더생성", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ폴더 존재ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        } else {
            dir.mkdirs();
            Log.d("폴더생성", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ폴더 생성완료ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        }
    }

    // 비트맵 -> PNG 로 저장
    private void saveBitmapToPNG(Bitmap saveFile, String name) {

        createFolder();
        Log.e("hs", "saveBitmapToPNG");

        name = name + ".png";

        try {
            FileOutputStream fos = openFileOutput(name, Context.MODE_PRIVATE);
            saveFile.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            Log.e("fail", "Don't save canvas");
            e.printStackTrace();
        }
    }


    // 비트맵 -> JPG 로 저장
   /* private String saveBitmapToJpg(Bitmap bitmap, String name) {

        createFolder();

        File storage = getCacheDir(); //  path = /data/user/0/YOUR_PACKAGE_NAME/cache
        String fileName = name + ".jpg";
        File imgFile = new File(storage, fileName);
        try {
            imgFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.e("saveBitmapToJpg", "FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmapToJpg", "IOException : " + e.getMessage());
        }
        Log.d("imgPath", getCacheDir() + "/" + fileName);
        return getCacheDir() + "/" + fileName;
    }*/


    private int permissionCheck() {

        int isPermission = -1;

        // 권한 허용한 사용자인지 체크
        if (ContextCompat.checkSelfPermission(DrawingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(DrawingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            /**
             * 권한 모두 허용
             */
            isPermission = CODE_FILE_PERMISSION_ALL_GRANTED;

        } else {

            /**
             * 권한 거부
             * 1. 명시적으로 거부한 유저인지
             * 2. 권한을 요청한 적 없는 유저인지
             */

            // 사용자가 권한요청을 명시적으로 거부했던 적 있는 경우 true 를 반환
            if (ActivityCompat.shouldShowRequestPermissionRationale(DrawingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(DrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                isPermission = CODE_FILE_PERMISSION_DENIED_TRUE;
            } else {
                isPermission = CODE_FILE_PERMISSION_FIRST;
            }
        }
        return isPermission;
    }

    /**
     * 권한 얻은 후 행동할 메서드
     */
    private void permissionResultAction(int permissionResult) {

        switch (permissionResult) {
            case CODE_FILE_PERMISSION_ALL_GRANTED:  // 모두허용
//                Toast.makeText(SketchActivity.this, "권한 모두허용", Toast.LENGTH_SHORT).show();
                /*saveBitmapToPNG(canvasView.resultBitmap(), "tt");

                bitmaps.add(canvasView.resultBitmap());
                PreferenceUtil.setBitmapList(this, "bitmapList", bitmaps);
                drawAdapter.setData(bitmaps);

                Intent intent = new Intent(DrawingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/

                break;

            case CODE_FILE_PERMISSION_DENIED_TRUE:  // 권한 거부한적 있는 유저

                AlertDialog.Builder builder = new AlertDialog.Builder(DrawingActivity.this);
                builder.setTitle("필수권한")
                        .setMessage("파일 저장을 위해\n권한을 허용해주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(DrawingActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();

                break;

            case CODE_FILE_PERMISSION_FIRST:  // 처음 실행
                filePermissionRequest.launch(arrFilePermissions);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (ActivityCompat.checkSelfPermission(DrawingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(DrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    // 권한 얻은 사용자
//                    Log.e("saveImgUrl", saveBitmapToJpg(canvasView.resultBitmap(), "test"));
                    saveBitmapToPNG(canvasView.resultBitmap(), "tt");

                    /**
                     *  1. Bitmap -> String 변환 후 arrayList 에 저장
                     *  2. arrayList Preference 에 저장
                     */

                    /*if (PreferenceUtil.getStringArrayPref(this, "list") != null) {
                        strBitmapList.add(BitmapConverter.BitmapToString(canvasView.resultBitmap()));
                        PreferenceUtil.setStringArrayPref(this, "list", strBitmapList);
                    }*/

                    /**
                     * 40KB 이상의 bitmap 을 넣을 경우 'FAILED BINDER TRANSACTION' 에러
                     * 보낼 때 byteArray 로 변환 후 intent 로 전달
                     */

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    canvasView.resultBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();

                    Intent intent = new Intent(DrawingActivity.this, MemoActivity.class);
                    intent.putExtra("bitmap", bytes);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    // 권한 거부 사용자
                    permissionResultAction(permissionCheck());
                }
                break;
            case R.id.tv_pen:
                canvasView.changeMode(CustomCanvasView.MODE_PEN);
                drawModeStatus(CustomCanvasView.MODE_PEN);
                break;
            case R.id.tv_eraser:
                canvasView.changeMode(CustomCanvasView.MODE_ERASER);
                drawModeStatus(CustomCanvasView.MODE_ERASER);
                break;

        }
    }
}