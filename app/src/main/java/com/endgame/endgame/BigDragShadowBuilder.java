package com.endgame.endgame;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

// make drag bigger
class BigDragShadowBuilder extends View.DragShadowBuilder {
    private float scale = 2.5f;

    BigDragShadowBuilder(View v) {
        super(v);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        // get width, height
        int width = (int) (getView().getWidth() * scale);
        int height = (int) (getView().getHeight() * scale);

        // set shadow
        shadowSize.set(width, height);
        shadowTouchPoint.set((int) (width * 0.75), (int) (height * 0.75));
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale(scale, scale);
        super.onDrawShadow(canvas);
    }
}
