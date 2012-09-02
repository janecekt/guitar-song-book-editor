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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class PagingRenderer implements GLSurfaceView.Renderer {
    private PagingModel pagingModel;
    private FpsLimiter fpsLimiter;


    public PagingRenderer(PagingModel pagingModel, FpsLimiter fpsLimiter) {
        this.pagingModel = pagingModel;
        this.fpsLimiter = fpsLimiter;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color (transparent)
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Initialize paging model
        pagingModel.onInit(gl);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Draw paging model frame
        pagingModel.onDrawFrame(gl);

        // Sleep for remaining time to limit maximum frame rate
        // NOTE: This is essential to avoid 100% CPU usage thus excessive battery load
        fpsLimiter.sleepForRemainingTime();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set view port
        gl.glViewport(0, 0, width, height);

        // Setup ModelMatrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);

        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        // Update width and height
        this.pagingModel.onSurfaceChanged(width, height);
    }
}