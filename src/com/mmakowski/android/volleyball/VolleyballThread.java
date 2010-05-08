package com.mmakowski.android.volleyball;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;

import com.mmakowski.android.volleyball.model.Court;

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
	
	private static final long FRAME_DELAY = 2000;
	private static final int PLAYERS_PER_TEAM = 1;

	private final SurfaceHolder surfaceHolder;
	private final Context context;
	private boolean running;
	private int state = STATE_INIT;
	
	private Court court;

	private int floorLevel;
	private int canvasWidth;
	private int canvasHeight;
	private int playerWidth;
	private int playerHeight;
	private int ballWidth;
	private int ballHeight;
	private Bitmap backgroundImage;
	private Drawable playerImage;
	private Drawable ballImage;
	
	public VolleyballThread(SurfaceHolder holder, Context context) {
		surfaceHolder = holder;
		this.context = context;
		Resources res = context.getResources();
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);
		playerImage = context.getResources().getDrawable(R.drawable.player);
		playerWidth = playerImage.getIntrinsicWidth();
		playerHeight = playerImage.getIntrinsicHeight();
		ballImage = context.getResources().getDrawable(R.drawable.ball);
		ballWidth = ballImage.getIntrinsicWidth();
		ballHeight = ballImage.getIntrinsicHeight();
		court = new Court(800, PLAYERS_PER_TEAM);
	}

	@Override
	public void run() {
		while (running) {
			Canvas canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					if (state == STATE_RUNNING) {
						updatePhysics();
						// updateAnimation();
					}
					draw(canvas);
					sleep(FRAME_DELAY);
				}
			} catch (InterruptedException e) {
				// ignore
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

	private void updatePhysics() {
		// TODO Auto-generated method stub
	}

	private void draw(Canvas canvas) {
		canvas.drawBitmap(backgroundImage, 0, 0, null);
		int yTop = canvasHeight - playerHeight - floorLevel;
		for (int t = 0; t < 2; t++) {
			for (int p = 0; p < PLAYERS_PER_TEAM; p++) {
				int xLeft = court.players[t][p].positionX;
				//Log.w(this.getClass().getName(), String.valueOf(xLeft));
				playerImage.setBounds(xLeft, yTop, xLeft + playerWidth, yTop + playerHeight);
		        playerImage.draw(canvas);		
			}
		}
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
			floorLevel = 20 * height / 480;
			backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width, height, true);
		}
	}

}
