package com.mmakowski.android.volleyball;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * The game thread.
 * 
 * @author mmakowski
 * 
 */
public class VolleyballThread extends Thread {
	public static final int STATE_INIT = 0;
	public static final int STATE_PAUSE = 1;
	public static final int STATE_READY = 2;
	public static final int STATE_RUNNING = 3;

	private final SurfaceHolder surfaceHolder;
	private final Context context;
	private boolean running;
	private int state = STATE_INIT;

	private int canvasWidth;
	private int canvasHeight;
	private Bitmap backgroundImage;

	public VolleyballThread(SurfaceHolder holder, Context context) {
		surfaceHolder = holder;
		this.context = context;
		Resources res = context.getResources();
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);

	}

	@Override
	public void run() {
		while (running) {
			Canvas canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					// if (mMode == STATE_RUNNING) updatePhysics();
					draw(canvas);
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private void draw(Canvas canvas) {
		canvas.drawBitmap(backgroundImage, 0, 0, null);
	}

	public void pause() {
		synchronized (surfaceHolder) {
			if (state == STATE_RUNNING)
				setState(STATE_PAUSE);
		}
	}

	public void setState(int state) {
		synchronized (surfaceHolder) {
			this.state = state;
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setSurfaceSize(int width, int height) {
		synchronized (surfaceHolder) {
			canvasWidth = width;
			canvasHeight = height;
			backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width, height, true);
		}
	}

}
