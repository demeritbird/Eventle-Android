package com.example.sampleproject.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

public class CustomButtonView extends AppCompatButton {

    public CustomButtonView(Context context) {
        super(context);
    }

    public CustomButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                return true;

            case MotionEvent.ACTION_UP:
                performClick();
                return true;
        }
        return false;
    }

/*    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                return true; // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                v.getBackground().clearColorFilter();
                v.invalidate();
                return true; // if you want to handle the touch event
        }
        return false;
    }*/

    @Override
    public boolean performClick() {
        super.performClick();
        doSomething();
        return true;
    }

    private void doSomething() {
        Toast.makeText(getContext(), "did something", Toast.LENGTH_SHORT).show();
//        System.out.println("helpelehlpehphe");
    }
}