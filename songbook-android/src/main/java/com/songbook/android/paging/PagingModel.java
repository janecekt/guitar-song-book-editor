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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;


/**
 * Model containing the actual rendering logic.
 * NOTE: The model as synchronized as it is updated by GUI thread
 * and read (rendered) by OpenGL thread.
 */
public class PagingModel {
    public static final int TEXTURE_LEVEL = 0;
    public static final int TEXTURE_BORDER = 0;
    public static final float MIN_BACKGROUND_ALPHA = 0.2f;

    public static enum State { TRANSPARENT, RENDER_CURL, ANIMATION }
    public static enum Direction { LEFT, RIGHT }

    private float animationStartX;
    private long animationStartTime;
    private Direction animationDirection;

    private State state;
    private float curlX;
    private Direction directionFrom;


    private int bitmapWidth;
    private int bitmapHeight;
    private Bitmap bitmap;

    private GLSurfaceView surfaceView;

    private float width;
    private float height;


    public PagingModel(GLSurfaceView surfaceView) {
        this.state = State.TRANSPARENT;
        this.surfaceView = surfaceView;
    }


    public synchronized void startFlipAnimation(Bitmap current, Direction direction) {
        if (direction == Direction.LEFT) {
            startDrag(current, width, Direction.RIGHT);
            startAnimation(width, Direction.LEFT);
        } else {
            startDrag(current, 0, Direction.LEFT);
            startAnimation(0, Direction.RIGHT);
        }
    }


    public synchronized void startAnimation(float startX, Direction direction) {
        if (state != State.RENDER_CURL) {
            return;
        }
        this.animationStartX = startX;
        this.animationDirection = direction;
        this.animationStartTime = System.currentTimeMillis();
        this.state = State.ANIMATION;
        this.curlX = startX;

        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    public synchronized void startDrag(Bitmap current, float x, Direction direction) {
        this.directionFrom = direction;
        this.bitmapWidth = getNextHighestPO2(current.getWidth());
        this.bitmapHeight = getNextHighestPO2(current.getHeight());

        // Create empty bitmap
        this.bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, current.getConfig());
        int[] pixels = new int[current.getHeight() * current.getWidth()];

        // Copy left bitmap into bitmap
        current.getPixels(pixels, 0, current.getWidth(), 0, 0, current.getWidth(), current.getHeight());
        bitmap.setPixels(pixels, 0, current.getWidth(), 0, 0, current.getWidth(), current.getHeight());

        // Recycle bitmaps
        current.recycle();

        // Update state
        state = State.RENDER_CURL;
        updateX(x);
    }


    public synchronized void updateX(float x) {
        this.curlX = x;
    }


    public synchronized void onInit(GL10 gl) {
        // Enable client states
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glShadeModel(GL10.GL_FLAT);

        // Generate textures
        int[] textureIds = new int[1];
        gl.glGenTextures(1, textureIds, 0);

        // Texture initialization
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_DECAL);
    }


    public synchronized void onSurfaceChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }


    public synchronized void onDrawFrame(GL10 gl) {
        // Clear screen
        switch (state) {
            case TRANSPARENT:
                gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                return;
            case ANIMATION:
                // update curlX and curlY
                long duration = System.currentTimeMillis() - animationStartTime;
                curlX = (animationDirection == Direction.LEFT)
                        ? (float) (animationStartX - 0.001 * (duration * duration))
                        : (float) (animationStartX + 0.001 * (duration * duration));
                if (curlX < 0 || curlX > width) {
                    state = State.TRANSPARENT;
                    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                    surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                    return;
                }
        }

        // Load textures if not done already
        if (bitmap != null) {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, TEXTURE_LEVEL, bitmap, TEXTURE_BORDER);
            bitmap.recycle();
            bitmap = null;
        }


        if (directionFrom == Direction.LEFT) {
            // Set clear color
            gl.glClearColor(0.0f, 0.0f, 0.0f, MIN_BACKGROUND_ALPHA + (1-MIN_BACKGROUND_ALPHA) * (width-curlX) / width);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            // Vertices
            float[] vertices = {
                    curlX, 0,
                    width, 0,
                    curlX, height,
                    width, height};
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, createBuffer(vertices));

            // Tex Coordinates
            float[] texCoordinates = {
                    0, height / bitmapHeight,
                    (width - curlX) / bitmapWidth, height / bitmapHeight,
                    0, 0,
                    (width - curlX) / bitmapWidth, 0 };
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, createBuffer(texCoordinates));
        }
        else {
            // Set clear color
            gl.glClearColor(0.0f, 0.0f, 0.0f, MIN_BACKGROUND_ALPHA + (1-MIN_BACKGROUND_ALPHA) * curlX / width);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            // Vertices
            float[] vertices = {
                    0, 0,
                    curlX, 0,
                    0, height,
                    curlX, height};
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, createBuffer(vertices));

            // Tex Coordinates
            float[] texCoordinates = {
                    (width-curlX) / bitmapWidth, height / bitmapHeight,
                    width / bitmapWidth, height / bitmapHeight,
                    (width-curlX) / bitmapWidth, 0,
                    width / bitmapWidth, 0 };
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, createBuffer(texCoordinates));
        }

        // Render bitmaps
        gl.glDisable(GL10.GL_BLEND);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }


    /**
     * Calculates the next highest power of two for a given integer.
     */
    private static int getNextHighestPO2(int n) {
        n -= 1;
        n = n | (n >> 1);
        n = n | (n >> 2);
        n = n | (n >> 4);
        n = n | (n >> 8);
        n = n | (n >> 16);
        return n + 1;
    }


    /**
     * Creates a buffer with required data.
     */
    private static Buffer createBuffer(float[] data) {
        return ByteBuffer.allocateDirect(data.length * Float.SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(data)
                .position(0);
    }
}
