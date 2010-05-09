package com.mmakowski.android.volleyball;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.mmakowski.android.volleyball.model.Court;
import com.mmakowski.android.volleyball.model.Player;

/**
 * The game thread.
 * 
 * @author mmakowski
 * 
 */
public class VolleyballThread extends Thread {
	public static final int STATE_INIT = 0;
	public static final int STATE_PAUSE = 1;
	public static final int STATE_RUNNING = 3;
	
	private static final long FRAME_DELAY = 10;
	private static final int PLAYERS_PER_TEAM = 1;

	private final SurfaceHolder surfaceHolder;
	private final Context context;
	private boolean running;
	private int state = STATE_INIT;
	
	private Court court;

	private int canvasWidth;
	private int canvasHeight;
	private Bitmap backgroundImage;
	private Bitmap playerImage;
	private Bitmap ballImage;
	private long prevFrameTime;
	private long currFrameTime;
	
	public VolleyballThread(SurfaceHolder holder, Context context) {
		surfaceHolder = holder;
		this.context = context;
		Resources res = context.getResources();
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);
		playerImage = BitmapFactory.decodeResource(res, R.drawable.player);
		ballImage = BitmapFactory.decodeResource(res, R.drawable.ball);
		court = new Court();
	}

	@Override
	public void run() {
		court.setUp(PLAYERS_PER_TEAM);
		while (running) {
			Canvas canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					prevFrameTime = currFrameTime == 0 ? System.currentTimeMillis() : currFrameTime;
					currFrameTime = System.currentTimeMillis();
					if (state == STATE_RUNNING) {
						updatePhysics();
						// updateAnimation();
					}
					draw(canvas);
				}
				sleep(FRAME_DELAY);
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
		court.update(currFrameTime - prevFrameTime);
	}

	private void draw(Canvas canvas) {
		canvas.drawBitmap(backgroundImage, 0, 0, null);
		for (int t = 0; t < 2; t++) {
			for (int p = 0; p < PLAYERS_PER_TEAM; p++) {
				Player player = court.players[t][p];
				canvas.drawBitmap(playerImage, player.positionX, canvasHeight - player.positionY, null);
			}
		}
		canvas.drawBitmap(ballImage, court.ball.positionX, canvasHeight - court.ball.positionY, null);
	}

	public void pause() {
		synchronized (surfaceHolder) {
			if (state == STATE_RUNNING)
				setState(STATE_PAUSE);
		}
	}

	public void unpause() {
		synchronized (surfaceHolder) {
			if (state == STATE_PAUSE)
				setState(STATE_RUNNING);
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
			court.setViewDimensions(width, height); 
			backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width, height, true);
		}
	}

	public int getGameState() {
		return state;
	}

}
