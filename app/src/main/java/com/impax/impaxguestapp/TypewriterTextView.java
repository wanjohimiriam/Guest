package com.impax.impaxguestapp;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypewriterTextView extends androidx.appcompat.widget.AppCompatTextView {
    private CharSequence text;
    private int index;
    private long delay = 150; // Default delay in milliseconds
    private Handler handler = new Handler();

    public TypewriterTextView(Context context) {
        super(context);
    }

    public TypewriterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if (index <= text.length()) {
                handler.postDelayed(characterAdder, delay);
            }
        }
    };

    public void animateText(CharSequence txt) {
        this.text = txt;
        this.index = 0;
        setText(""); // Clear text before animation
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    public void setCharacterDelay(long delay) {
        this.delay = delay;
    }
}
