package com.mmakowski.android.volleyball.model;

import static com.mmakowski.android.volleyball.model.GameElementDimensions.BALL_SIZE_TO_VIEW_WIDTH_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.COURT_WIDTH_TO_VIEW_WIDTH_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.FLOOR_LEVEL_TO_VIEW_HEIGHT_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.NET_HEIGHT_TO_VIEW_HEIGHT_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.PLAYER_HEIGHT_TO_VIEW_HEIGHT_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.PLAYER_WIDTH_TO_VIEW_WIDTH_RATIO;
import android.util.Log;

/**
 * Court represents all elements of the game and takes care of updating the physics.
 * 
 * @author mmakowski
 *
 */
public final class Court {
	private static final int STATE_NOT_SET_UP = 0;
	private static final int STATE_TEAM1_SERVE = 1;
	
	private int state = STATE_NOT_SET_UP;
	private int viewWidth;
	public int width;
	public int netPositionX;
	public int netHeight = 130;
	public Player[][] players;
	public Ball ball;
	private int floorLevel;
	private int ballSize;
	private int playerWidth;
	private int playerHeight;
	private int courtOffset;

	public synchronized void setUp(int playersPerTeam) {
		players = new Player[2][playersPerTeam];
		int playerPosY = playerHeight + floorLevel;
		// distribute the players evenly in the court
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < playersPerTeam; j++) 
				players[i][j] = new Player(netPositionX + (i * 2 - 1) * (j + 1) * width / (2 * (playersPerTeam + 1)) - (playerWidth / 2), playerPosY); 
		}
		ball = new Ball(courtOffset / 2, netHeight + floorLevel);
		ball.velocityX = 230;
		ball.velocityY = 100;
		state = STATE_TEAM1_SERVE;
	}

	public synchronized void update(long elapsedTimeMs) {
		updateBall(elapsedTimeMs);
	}
	
	private void updateBall(long elapsedTimeMs) {
		float secFraction = elapsedTimeMs / 1000f;
		//Log.w(getClass().getName(), String.valueOf(elapsedTimeMs));
		//Log.w(getClass().getName(), String.valueOf(secFraction));
		ball.positionX = (int) (ball.positionX + ball.velocityX * secFraction);
		ball.velocityX += Physics.aerodynamicDragDeceleration(ball.velocityX) * secFraction;  
		//Log.w(getClass().getName(), String.valueOf(ball.velocityX));
		// TODO: full collision detection 
		ball.positionY = (int) (ball.positionY + ball.velocityY * secFraction);
		if (ball.positionY - ballSize < floorLevel) {
			ball.positionY = (int) (floorLevel + ballSize + (floorLevel - (ball.positionY - ballSize)) * Physics.BALL_REBOUND_FACTOR);
			ball.velocityY = -ball.velocityY * Physics.BALL_REBOUND_FACTOR;
		} else {
			ball.velocityY -= Physics.GRAVITY * secFraction;
		}
		ball.velocityY += Physics.aerodynamicDragDeceleration(ball.velocityY) * secFraction;
		//Log.w(getClass().getName(), String.valueOf(ball.velocityY));
	}
	
	public synchronized void setViewDimensions(int width, int height) {
		viewWidth = width;
		this.width = (int) (width * COURT_WIDTH_TO_VIEW_WIDTH_RATIO);
		courtOffset = (viewWidth - this.width) / 2;
		netPositionX = width / 2;
		netHeight = (int) (height * NET_HEIGHT_TO_VIEW_HEIGHT_RATIO);
		floorLevel = (int) (height * FLOOR_LEVEL_TO_VIEW_HEIGHT_RATIO);
		playerWidth = (int) (width * PLAYER_WIDTH_TO_VIEW_WIDTH_RATIO);
		playerHeight = (int) (height * PLAYER_HEIGHT_TO_VIEW_HEIGHT_RATIO);
		ballSize = (int) (width * BALL_SIZE_TO_VIEW_WIDTH_RATIO);
		// TODO: reposition players and ball
	}
	
	public boolean isSetUp() {
		return state != STATE_NOT_SET_UP;
	}
	
}
