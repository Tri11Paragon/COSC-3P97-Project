package com.mouseboy.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.mouseboy.finalproject.weather.AnimatedBackgroundCommon;

import java.util.Arrays;

public class AnimatedBackgroundView2 extends AnimatedBackgroundCommon {

    public AnimatedBackgroundView2(Context context) {
        super(context);
    }

    public AnimatedBackgroundView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void init() {
        super.init();
        Arrays.fill(colors, Color.WHITE);
        linePaint.setColor(Color.LTGRAY);
    }

}
