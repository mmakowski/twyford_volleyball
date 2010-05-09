package com.mmakowski.android.volleyball.model;


/**
 * The ball.
 * 
 * @author mmakowski
 */
public class Ball {
	public int positionX;
	public int positionY;
	public float velocityX;
	public float velocityY;
	
	Ball(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
	}
}
