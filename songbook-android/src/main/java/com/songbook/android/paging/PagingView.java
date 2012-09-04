/*
 *  Copyright (c) 2008 - Tomas Janecek.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.songbook.android.paging;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * View rendering the paging using Open-GL
 *
 * PERFORMANCE NOTES:
 * (1) Never use the queue to pass data to OpenGL thread
 * Remember that this requires creation of the new Runnable object
 * which is very costly - especially when done for every frame.
 *
 * (2) For OnTouch events always use FpsLimiter - OnTouch will be
 * called as often as possible thus causing 100% CPU load
 * and significant battery drain.
 *
 * (3) In the Renderer also use FpsLimiter in OnDraw frame to limit
 * max FPS - otherwise you will get very high frame rates which required
 * 100% of CPU and thus drain the battery.
 */
public class PagingView extends GLSurfaceView {
    private float activeRectAsFraction;
    private float leftRectX;
    private float rightRectX;
    private float leftDragThresholdX;
    private float rightDragThresholdX;


    private GestureDetector gestureDetector;
    private PagingModel pagingModel;
    private Status status;
    private PagingProvider pagingProvider;
    private FpsLimiter moveEventLimiter;
    private FpsLimiter rendererFrameRateLimiter;


    private enum Status {
        NONE,
        LEFT_RECTANGLE_DOWN, LEFT_DRAG,
        RIGHT_RECTANGLE_DOWN, RIGHT_DRAG
    }


    public PagingView(Context context, PagingProvider pagingProvider, float activeRectAsFraction, long maxFps) {
        super(context);
        this.pagingProvider = pagingProvider;
        this.activeRectAsFraction = activeRectAsFraction;
        this.moveEventLimiter = new FpsLimiter(1000/maxFps);

        // This needs to be done BEFORE setRenderer
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);

        // Set Renderer
        pagingModel = new PagingModel(this);
        rendererFrameRateLimiter = new FpsLimiter(1000/maxFps);
        PagingRenderer renderer = new PagingRenderer(pagingModel, rendererFrameRateLimiter);

        // Set renderer with update mode WHEN_DIRTY
        // ... this means that new frame is only drawn if requestRefresh() is called.
        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        this.gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener());
        this.gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                return onDoubleTapEventOccurred(motionEvent);
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });

        // Set other variables
        initialize();
    }


    public void updateParameters(float activeRectAsFraction, long maxFps) {
        // Update rectangle size
        this.activeRectAsFraction = activeRectAsFraction;

        // Update limiters
        rendererFrameRateLimiter.setMinDuration(1000 / maxFps);
        moveEventLimiter.setMinDuration(1000 / maxFps);
    }



    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        initialize();
    }


    private void initialize() {
        status = Status.NONE;
        leftRectX = getWidth() * activeRectAsFraction;
        rightRectX = getWidth() - leftRectX;
        leftDragThresholdX = getWidth() / 2;
        rightDragThresholdX = getWidth() / 2;
    }


    private Bitmap captureCurrentScreenAsBitmap() {
        pagingProvider.getOverlayedView().setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(pagingProvider.getOverlayedView().getDrawingCache());
        pagingProvider.getOverlayedView().setDrawingCacheEnabled(false);
        return bitmap;
    }



    public boolean onDoubleTapEventOccurred(final MotionEvent event) {
        if (event.getX() <= leftRectX) {
            final Bitmap current = captureCurrentScreenAsBitmap();
            pagingModel.startFlipAnimation(current, PagingModel.Direction.LEFT);
            pagingProvider.goToPreviousPage();
            return true;
        }
        if (event.getX() >= rightRectX) {
            final Bitmap current = captureCurrentScreenAsBitmap();
            pagingModel.startFlipAnimation(current, PagingModel.Direction.RIGHT);
            pagingProvider.goToNextPage();
            return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // Event handled by detector
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }

        // Process user events
        switch (event.getAction()) {
            // When user touches inside the left or right rectangle just update state
            case MotionEvent.ACTION_DOWN:
                // Check if touch occurred in the left rectangle
                if (status == Status.NONE) {
                    if (event.getX() <= leftRectX) {
                        status = Status.LEFT_RECTANGLE_DOWN;
                        return true;
                    }
                    if (event.getX() >= rightRectX) {
                        status = Status.RIGHT_RECTANGLE_DOWN;
                        return true;
                    }
                }
                break;

            // When user drags the pointer
            case  MotionEvent.ACTION_MOVE:
                switch (status) {
                    case LEFT_RECTANGLE_DOWN:
                        if (event.getX() > leftRectX) {
                            // Capture current screen
                            final Bitmap current = captureCurrentScreenAsBitmap();
                            pagingModel.startDrag(current, event.getX(), PagingModel.Direction.LEFT);
                            requestRender();
                            status = Status.LEFT_DRAG;
                            pagingProvider.goToPreviousPage();
                        }
                        return true;

                    case RIGHT_RECTANGLE_DOWN:
                        if (event.getX() < rightRectX) {
                            // Capture current screen
                            final Bitmap current = captureCurrentScreenAsBitmap();
                            pagingModel.startDrag(current, event.getX(), PagingModel.Direction.RIGHT);
                            requestRender();
                            status = Status.RIGHT_DRAG;
                            pagingProvider.goToNextPage();
                        }
                        return true;

                    case LEFT_DRAG:
                    case RIGHT_DRAG:
                        pagingModel.updateX(event.getX());
                        requestRender();

                        // Sleep for a bit - this avoids onTouch event flood
                        moveEventLimiter.sleepForRemainingTime();
                }
                break;

            // When user releases the pointer
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                switch (status) {
                    case LEFT_DRAG:
                        if (event.getX() < leftDragThresholdX) {
                            status = Status.NONE;
                            pagingModel.startAnimation(event.getX(), PagingModel.Direction.LEFT);
                            pagingProvider.goToNextPage();
                        }
                        else {
                            status = Status.NONE;
                            pagingModel.startAnimation(event.getX(), PagingModel.Direction.RIGHT);
                        }
                        return true;

                    case RIGHT_DRAG:
                        if (event.getX() > rightDragThresholdX) {
                            status = Status.NONE;
                            pagingModel.startAnimation(event.getX(), PagingModel.Direction.RIGHT);
                            pagingProvider.goToPreviousPage();
                        }
                        else {
                            status = Status.NONE;
                            pagingModel.startAnimation(event.getX(), PagingModel.Direction.LEFT);
                        }
                        return true;

                    case LEFT_RECTANGLE_DOWN:
                    case RIGHT_RECTANGLE_DOWN:
                        status = Status.NONE;
                        return true;
                }
                break;
        }

        return false;
    }
}
