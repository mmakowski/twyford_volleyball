package com.mmakowski.android.volleyball.model;


/**
 * The ball.
 * 
 * @author mmakowski
 */
public class Ball {
	// 1 = ideally elastic
	public static final float REBOUND_FACTOR = 0.8f;
	// the drag 
	public static final float AERODYNAMIC_DRAG_COEFFICIENT = 0.002f;

	public int positionX;
	public int positionY;
	public float velocityX;
	public float velocityY;
	
	Ball(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
	}
}
