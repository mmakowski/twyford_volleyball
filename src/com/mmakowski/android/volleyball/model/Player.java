package com.mmakowski.android.volleyball.model;

/**
 * A player.
 * 
 * @author mmakowski
 */
public class Player {
	public int positionX;
	public int positionY;
	public int targetPositionX;
	
	Player(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
		targetPositionX = positionX;
	}
}
