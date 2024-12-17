package com.mouseboy.finalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class AnimatedBackgroundView extends View {

    private static final int POINT_COUNT = 50;
    private static final float MAX_DISTANCE = 300f;
    private final Paint pointPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final PointF[] points = new PointF[POINT_COUNT];
    private final PointF[] velocities = new PointF[POINT_COUNT];
    private final int[] colors = new int[POINT_COUNT];
    private final Random random = new Random();

    public AnimatedBackgroundView(Context context) {
        super(context);
        init();
    }

    public AnimatedBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pointPaint.setStyle(Paint.Style.FILL);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);

        for (int i = 0; i < POINT_COUNT; i++) {
            points[i] = new PointF(random.nextInt(1000), random.nextInt(1000));
            velocities[i] = new PointF(random.nextFloat() * 4 - 2, random.nextFloat() * 4 - 2);
            colors[i] = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < POINT_COUNT; i++) {
            pointPaint.setColor(colors[i]);
            canvas.drawCircle(points[i].x, points[i].y, 6, pointPaint);

            for (int j = i + 1; j < POINT_COUNT; j++) {
                float distance = distance(points[i], points[j]);
                if (distance < MAX_DISTANCE) {
                    int alpha = (int) ((1 - (distance / MAX_DISTANCE)) * 255);
                    linePaint.setColor(Color.argb(alpha, 255, 255, 255));
                    canvas.drawLine(points[i].x, points[i].y, points[j].x, points[j].y, linePaint);
                }
            }
        }

        for (int i = 0; i < POINT_COUNT; i++) {
            points[i].x += velocities[i].x;
            points[i].y += velocities[i].y;

            if (points[i].x < 0 || points[i].x > getWidth()) velocities[i].x *= -1;
            if (points[i].y < 0 || points[i].y > getHeight()) velocities[i].y *= -1;
        }

        // Invalidate to redraw
        postInvalidateOnAnimation();
    }

    private float distance(PointF a, PointF b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}
