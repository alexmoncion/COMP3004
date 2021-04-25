package com.endgame.endgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class OutlineTextView extends androidx.appcompat.widget.AppCompatTextView {
    public OutlineTextView(Context context) {
        super(context);
    }

    public OutlineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint p = getPaint();
        // save original color
        int oldColor = getCurrentTextColor();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(18);
        // use shadow color
        setTextColor(getShadowColor());
        // draw outline
        super.draw(canvas);
        // restore color
        setTextColor(oldColor);
        p.setStyle(Paint.Style.FILL);

        super.draw(canvas);
    }

}
