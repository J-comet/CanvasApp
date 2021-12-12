package com.example.canvasapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomCanvasView extends View {

    public static final int MODE_PEN = 1;       //모드 (펜)
    public static final int MODE_ERASER = 0;     //모드 (지우개)

    private int PEN_SIZE = 3;             //펜 사이즈
    private int ERASER_SIZE = 30;       //지우개 사이즈

    private Paint paint = new Paint();
    private Canvas canvas;
    private Bitmap bitmap;

    private float oldX = -1;
    private float oldY = -1;

    private int color;
    private int size;

    public CustomCanvasView(Context context) {
        super(context);
        // 기본 설정
        init();
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 기본 설정
        init();
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 기본 설정
        init();
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null)
            canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private void init() {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        this.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }


    /**
     *  연필 or 지우개 모드 설정
     */
    public void changeMode(int mode) {
        if (mode == MODE_PEN) {
            this.color = Color.BLACK;
            size = PEN_SIZE;
        } else {
            this.color = Color.WHITE;
            size = ERASER_SIZE;
        }
        paint.setColor(color);
        paint.setStrokeWidth(size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        // 비트맵과 캔버스 연결
        canvas.setBitmap(bitmap);
        canvas.drawColor(Color.WHITE);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float curX = event.getX();  // 눌린 곳의 X 좌표
        float curY = event.getY();  // 눌린 곳의 Y 좌표

        if (action == MotionEvent.ACTION_DOWN) {  // 처음 누를 때
            Log.e("ACTION_DOWN", "ACTION_DOWN =" + curX + " , " + curY);

            oldX = curX;
            oldY = curY;
            // 그리기 시작

        } else if (action == MotionEvent.ACTION_MOVE) {  // 누르고 움직일 때

            if (oldX != -1) {
                canvas.drawLine(oldX, oldY, curX, curY, paint);
                invalidate();
            }

            oldX = curX;
            oldY = curY;
            Log.e("ACTION_MOVE", "ACTION_MOVE =" + curX + " , " + curY);

            // 계속 그리기

        } else if (action == MotionEvent.ACTION_UP) {  // 누르고 있는 걸 뗐을 때
            Log.e("ACTION_UP", "ACTION_UP =" + curX + " , " + curY);

            // 그리기 끝
            if (oldX != -1) {
                canvas.drawLine(oldX, oldY, curX, curY, paint);
                invalidate();
            }

            oldX = -1;
            oldY = -1;
        }

        return true;
    }

    public Bitmap resultBitmap() {
        Bitmap resultBitmap;

        if (bitmap != null) {
            resultBitmap = bitmap;
        } else {
            resultBitmap = null;
        }
        return resultBitmap;
    }
}
