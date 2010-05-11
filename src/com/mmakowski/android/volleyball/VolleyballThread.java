package com.mmakowski.android.volleyball;

import com.mmakowski.android.volleyball.ai.AI;
import com.mmakowski.android.volleyball.model.Court;

/**
 * The game thread. Keeps track of the game time and invokes model updates and view redraws.
 * 
 * @author mmakowski
 * 
 */
public class VolleyballThread extends Thread {
	private static final long FRAME_DELAY = 10;

	public static final int STATE_INIT = 0;
	public static final int STATE_PAUSED = 1;
	public static final int STATE_RUNNING = 3;
	
	private boolean running;
	private int state = STATE_INIT;
	
	private final Court court;
	private final VolleyballView view;
	private final AI ai; 

	public VolleyballThread(Court court, AI ai, VolleyballView view) {
		this.court = court;
		this.view = view;
		this.ai = ai;
	}

	@Override
	public void run() {
		long prevFrameTime;
		long currFrameTime = 0;
		while (running) {
			try {
				prevFrameTime = currFrameTime == 0 ? System.currentTimeMillis() : currFrameTime;
				currFrameTime = System.currentTimeMillis();
				if (state == STATE_RUNNING) {
					ai.movePlayers(court);
					court.update(currFrameTime - prevFrameTime);
				}
				view.draw(court);
				sleep(FRAME_DELAY);
			} catch (InterruptedException e) {
				// ignore
			} 
		}
	}

	public void pause() {
		if (state == STATE_RUNNING || state == STATE_INIT)
			this.state = STATE_PAUSED;
	}

	public void unpause() {
		if (state == STATE_PAUSED)
			this.state = STATE_RUNNING;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isPaused() {
		return state == STATE_PAUSED;
	}

}
