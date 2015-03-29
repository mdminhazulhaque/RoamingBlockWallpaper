package com.minhazulhaque.apps.roamingblockwallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class RoamingBlockWallpaperService extends WallpaperService {

    enum RoamDirection {
        RIGHT, DOWN, LEFT, UP
    }

    @Override
    public Engine onCreateEngine() {
        return new RoamingBlockWallpaperEngine();
    }

    private class RoamingBlockWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };

        private Paint paint;
        //private int width;
        //private int height;
        private boolean visible = true;
        public int roamX;
        public int roamY;
        public int roamMaxX;
        public int roamMaxY;
        public int len;
        public RoamDirection dir;

        public RoamingBlockWallpaperEngine() {
            paint = new Paint();
            
            roamX = 0;
            roamY = 0;

            paint = new Paint();
            len = 50;

            handler.post(drawRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            //this.width = width;
            //this.height = height;
            this.roamMaxX = width - len;
            this.roamMaxY = height - len;
            super.onSurfaceChanged(holder, format, width, height);
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    draw(canvas);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }

            handler.removeCallbacks(drawRunner);

            if (visible) {
                handler.postDelayed(drawRunner, 20);
            }
        }

        private void draw(Canvas canvas) {

            canvas.drawColor(0, PorterDuff.Mode.CLEAR);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#616161"));
            canvas.drawPaint(paint);

            paint.setColor(Color.parseColor("#0091EA"));

            if (roamX >= roamMaxX && roamY == 0)
                dir = RoamDirection.DOWN;
            else if (roamY >= roamMaxY && roamX >= roamMaxX)
                dir = RoamDirection.LEFT;
            else if (roamX == 0 && roamY >= roamMaxY)
                dir = RoamDirection.UP;
            else if (roamX == 0 && roamY == 0)
                dir = RoamDirection.RIGHT;

            switch (dir) {
                case RIGHT:
                    roamX += 10;
                    break;
                case DOWN:
                    roamY += 10;
                    break;
                case LEFT:
                    roamX -= 10;
                    break;
                case UP:
                    roamY -= 10;
            }

            canvas.drawRect(roamX, roamY, roamX + len, roamY + len, paint);
        }
    }
}
