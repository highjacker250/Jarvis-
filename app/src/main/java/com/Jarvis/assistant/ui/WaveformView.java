package com.jarvis.assistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class WaveformView extends View {

    private Paint paint;
    private int[] amplitudes = new int[20];
    private Random random = new Random();

    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        paint = new Paint();
        paint.setColor(0xFF00FFFF); // Cyan color
        paint.setStrokeWidth(8f);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int barWidth = width / amplitudes.length;

        for (int i = 0; i < amplitudes.length; i++) {

            int amplitude = random.nextInt(height);

            int x = i * barWidth;
            int y = height / 2;

            canvas.drawLine(
                    x,
                    y - amplitude / 2,
                    x,
                    y + amplitude / 2,
                    paint
            );
        }

        // animation loop
        postInvalidateDelayed(100);
    }
}