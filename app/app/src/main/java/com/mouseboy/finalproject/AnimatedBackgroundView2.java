package com.mouseboy.finalproject;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import java.util.Random;

public class AnimatedBackgroundView2 extends View {

    private static final int POINT_COUNT = 50; // Number of points
    private static final float MAX_DISTANCE = 300f; // Max distance to draw a line
    private final Paint pointPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final PointF[] points = new PointF[POINT_COUNT];
    private final PointF[] velocities = new PointF[POINT_COUNT];
    private final Random random = new Random();

    public AnimatedBackgroundView2(Context context) {
        super(context);
        init();
    }

    public AnimatedBackgroundView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStyle(Paint.Style.FILL);

        linePaint.setColor(Color.LTGRAY);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);

        // Initialize points and velocities
        for (int i = 0; i < POINT_COUNT; i++) {
            points[i] = new PointF(random.nextInt(1000), random.nextInt(1000));
            velocities[i] = new PointF(random.nextFloat() * 4 - 2, random.nextFloat() * 4 - 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw points and check for connections
        for (int i = 0; i < POINT_COUNT; i++) {
            canvas.drawCircle(points[i].x, points[i].y, 6, pointPaint);

            for (int j = i + 1; j < POINT_COUNT; j++) {
                float distance = distance(points[i], points[j]);
                if (distance < MAX_DISTANCE) {
                    // Draw line with alpha based on distance
                    int alpha = (int) ((1 - (distance / MAX_DISTANCE)) * 255);
                    linePaint.setAlpha(alpha);
                    canvas.drawLine(points[i].x, points[i].y, points[j].x, points[j].y, linePaint);
                }
            }
        }

        // Update point positions
        updatePoints();

        // Redraw the view
        postInvalidateOnAnimation();
    }

    private void updatePoints() {
        for (int i = 0; i < POINT_COUNT; i++) {
            points[i].x += velocities[i].x;
            points[i].y += velocities[i].y;

            // Bounce points off the edges of the screen
            if (points[i].x < 0 || points[i].x > getWidth()) velocities[i].x *= -1;
            if (points[i].y < 0 || points[i].y > getHeight()) velocities[i].y *= -1;
        }
    }

    private float distance(PointF a, PointF b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

}
