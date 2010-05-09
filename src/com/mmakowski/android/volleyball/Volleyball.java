package com.mmakowski.android.volleyball;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import com.mmakowski.android.volleyball.model.Court;

/**
 * The main activity of the game. Connects the main components, i.e.
 * - the view
 * - the model
 * - the game thread
 * to each other, handles user input and app lifecycle events. 
 * 
 * @author mmakowski
 */
public class Volleyball extends Activity implements SurfaceHolder.Callback {
	private static final int PLAYERS_PER_TEAM = 1;

	private static final int MENU_RESTART = 1;
	private static final int MENU_PAUSE = 2;
	
	private VolleyballThread thread;
	private Court court;
	private VolleyballView view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        view = (VolleyballView) findViewById(R.id.volleyball);
        view.getHolder().addCallback(this);
        court = new Court();
        /*
        if (savedInstanceState == null) {
            thread.pause();
        } else {
            //TODO: thread.restoreState(savedInstanceState);
        }*/
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        thread.pause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (!court.isSetUp()) court.setUp(PLAYERS_PER_TEAM);
		thread = new VolleyballThread(court, view);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: thread.saveState(outState);
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (thread.getGameState() == VolleyballThread.STATE_PAUSED) 
				thread.unpause();
			else
				thread.pause();
		}
		return super.onTouchEvent(event);
	}    
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESTART:
        		court.setUp(PLAYERS_PER_TEAM);
        		thread.pause();
                return true;
            case MENU_PAUSE:
    			if (thread.getGameState() == VolleyballThread.STATE_PAUSED) 
    				thread.unpause();
    			else
    				thread.pause();
        		// TODO: change manu item caption to "resume" when paused
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_RESTART, 0, R.string.menu_restart);
        menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
        return true;
    }
    
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		court.setViewDimensions(width, height); 
		view.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (thread == null) {
	        thread = new VolleyballThread(court, view);
		}
		if (!thread.isRunning()) {
			thread.setRunning(true);
			thread.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.pause();
        thread.setRunning(false);
        while (thread != null) {
            try {
                thread.join();
                thread = null;
            } catch (InterruptedException e) {
            }
        }
	}
    
}