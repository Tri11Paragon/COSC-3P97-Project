package com.mouseboy.finalproject.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Paint;
import android.graphics.PointF;

import androidx.annotation.NonNull;

import java.util.Random;

public class AnimatedBackgroundCommon extends View {

    protected static final int POINT_COUNT = 50; // Number of points
    protected static final float MAX_DISTANCE = 300f; // Max distance to draw a line
    protected final Paint pointPaint = new Paint();
    protected final Paint linePaint = new Paint();
    protected final PointF[] points = new PointF[POINT_COUNT];
    protected final PointF[] velocities = new PointF[POINT_COUNT];
    protected final int[] colors = new int[POINT_COUNT];
    protected final Random random = new Random();

    public AnimatedBackgroundCommon(Context context) {
        super(context);
        init();
    }

    public AnimatedBackgroundCommon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init(){
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
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < POINT_COUNT; i++) {
            pointPaint.setColor(colors[i]);
            canvas.drawCircle(points[i].x, points[i].y, 6, pointPaint);

            for (int j = i + 1; j < POINT_COUNT; j++) {
                float distance = distance(points[i], points[j]);
                if (distance < MAX_DISTANCE) {
                    int alpha = (int) ((1 - (distance / MAX_DISTANCE)) * 255);
                    linePaint.setAlpha(alpha);
                    canvas.drawLine(points[i].x, points[i].y, points[j].x, points[j].y, linePaint);
                }
            }
        }

        updatePoints();
        postInvalidateOnAnimation();
    }

    protected void updatePoints() {
        for (int i = 0; i < POINT_COUNT; i++) {
            points[i].x += velocities[i].x;
            points[i].y += velocities[i].y;

            // Bounce points off the edges of the screen
            if (points[i].x < 0 || points[i].x > getWidth()) velocities[i].x *= -1;
            if (points[i].y < 0 || points[i].y > getHeight()) velocities[i].y *= -1;
        }
    }

    protected float distance(PointF a, PointF b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

}
