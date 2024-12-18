package com.mouseboy.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.mouseboy.finalproject.weather.AnimatedBackgroundCommon;

public class AnimatedBackgroundView extends AnimatedBackgroundCommon {

    public AnimatedBackgroundView(Context context) {
        super(context);
    }

    public AnimatedBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        linePaint.setColor(Color.WHITE);
    }
}
