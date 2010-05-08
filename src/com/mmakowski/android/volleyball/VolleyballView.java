package com.mmakowski.android.volleyball;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * The main game view.
 * 
 * @author mmakowski
 *
 */
public class VolleyballView extends SurfaceView implements SurfaceHolder.Callback{
	private VolleyballThread thread;
	
	public VolleyballView(Context context, AttributeSet attrs) {
		super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        thread = new VolleyballThread(holder, context);
        setFocusable(true); 
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}

	public VolleyballThread getThread() {
		return thread;
	}
}
