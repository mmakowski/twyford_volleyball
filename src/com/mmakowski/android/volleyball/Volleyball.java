package com.mmakowski.android.volleyball;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * The main activity of the game.
 * 
 * @author mmakowski
 */
public class Volleyball extends Activity {
	private VolleyballThread thread;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        VolleyballView view = (VolleyballView) findViewById(R.id.volleyball);
        thread = view.getThread();
        if (savedInstanceState == null) {
            thread.setState(VolleyballThread.STATE_READY);
        } else {
            //TODO: thread.restoreState(savedInstanceState);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        thread.pause();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: thread.saveState(outState);
    }
    
}