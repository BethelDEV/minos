package funs.gamez.view.render;

import android.graphics.Canvas;
import android.graphics.Paint;

import funs.gamez.model.Direction;

public class RenderUtils {

    public static void drawWalls(Canvas canvas, int walls, int left,
            int top, int width, int height, int border, Paint paint) {
        if ((walls & Direction.NORTH.bit()) != 0) {
            canvas.drawRect(left, top, left + width, top + border, paint);
        }
        if ((walls & Direction.EAST.bit()) != 0) {
            canvas.drawRect(left + width - border, top, left + width, top
                    + height, paint);
        }
        if ((walls & Direction.SOUTH.bit()) != 0) {
            canvas.drawRect(left, top + height - border, left + width, top
                    + height, paint);
        }
        if ((walls & Direction.WEST.bit()) != 0) {
            canvas.drawRect(left, top, left + border, top + height, paint);
        }
    }

}
