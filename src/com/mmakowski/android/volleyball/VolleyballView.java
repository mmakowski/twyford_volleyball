package com.mmakowski.android.volleyball;

import com.mmakowski.android.volleyball.model.Court;
import com.mmakowski.android.volleyball.model.Player;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * The main game view.
 * 
 * @author mmakowski
 *
 */
public class VolleyballView extends SurfaceView {
	private int canvasHeight;
	private Bitmap backgroundImage;
	private Bitmap playerImage;
	private Bitmap ballImage;
	
	public VolleyballView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = context.getResources();
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);
		playerImage = BitmapFactory.decodeResource(res, R.drawable.player);
		ballImage = BitmapFactory.decodeResource(res, R.drawable.ball);
        setFocusable(true); 
	}

	void draw(Court court) {
		Canvas canvas = null;
		SurfaceHolder holder = getHolder();
		try {
			canvas = holder.lockCanvas(null);
			synchronized (holder) {
				canvas.drawBitmap(backgroundImage, 0, 0, null);
				for (int t = 0; t < 2; t++) {
					for (int p = 0; p < court.players[t].length; p++) {
						Player player = court.players[t][p];
						canvas.drawBitmap(playerImage, player.positionX, canvasHeight - player.positionY, null);
					}
				}
				canvas.drawBitmap(ballImage, court.ball.positionX, canvasHeight - court.ball.positionY, null);
			}
		} finally {
			// do this in a finally so that if an exception is thrown
			// during the above, we don't leave the Surface in an
			// inconsistent state
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void setSurfaceSize(int width, int height) {
		synchronized (getHolder()) {
			canvasHeight = height;
			backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width, height, true);
		}
	}
	
}
