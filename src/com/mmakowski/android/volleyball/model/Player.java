package com.mmakowski.android.volleyball.model;

/**
 * A player.
 * 
 * @author mmakowski
 */
public class Player {
	public final int team;
	public int positionX;
	public int positionY;
	public int targetPositionX;
	// where the player wants to send the ball
	public int ballTargetX;
	public int ballTargetY;
	// skills
	public float accuracy = 0.4f;
	
	Player(int team, int positionX, int positionY) {
		this.team = team;
		this.positionX = positionX;
		this.positionY = positionY;
		targetPositionX = positionX;
	}
	
	// by default send the ball into the middle of opponent's court
	public void setDefaultBallTarget(Court court, int opponentSide) {
		ballTargetX = court.netPositionX + opponentSide * court.width / 2; 
		ballTargetY = court.floorLevel;
	}
}
