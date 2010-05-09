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
public class Court {
	private static final float GRAVITY = 80f;
	
	private int viewWidth;
	private int viewHeight;
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

	public void setUp(int playersPerTeam) {
		players = new Player[2][playersPerTeam];
		int playerPosY = playerHeight + floorLevel;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < playersPerTeam; j++) 
				players[i][j] = new Player(netPositionX + (i * 2 - 1) * (j + 1) * width / (2 * (playersPerTeam + 1)) - (playerWidth / 2), playerPosY); 
		}
		ball = new Ball(courtOffset / 2, netHeight + floorLevel);
		ball.velocityX = 230;
		ball.velocityY = 100;
	}

	public void update(long elapsedTimeMs) {
		updateBall(elapsedTimeMs);
	}
	
	public void updateBall(long elapsedTimeMs) {
		float secFraction = elapsedTimeMs / 1000f;
		//Log.w(getClass().getName(), String.valueOf(elapsedTimeMs));
		//Log.w(getClass().getName(), String.valueOf(secFraction));
		ball.positionX = (int) (ball.positionX + ball.velocityX * secFraction);
		ball.velocityX += aerodynamicDragDeceleration(ball.velocityX, Ball.AERODYNAMIC_DRAG_COEFFICIENT) * secFraction;  
		//Log.w(getClass().getName(), String.valueOf(ball.velocityX));
		// TODO: full collision detection 
		ball.positionY = (int) (ball.positionY + ball.velocityY * secFraction);
		if (ball.positionY - ballSize < floorLevel) {
			ball.positionY = (int) (floorLevel + ballSize + (floorLevel - (ball.positionY - ballSize)) * Ball.REBOUND_FACTOR);
			ball.velocityY = -ball.velocityY * Ball.REBOUND_FACTOR;
		} else {
			ball.velocityY -= GRAVITY * secFraction;
		}
		ball.velocityY += aerodynamicDragDeceleration(ball.velocityY, Ball.AERODYNAMIC_DRAG_COEFFICIENT) * secFraction;
		//Log.w(getClass().getName(), String.valueOf(ball.velocityY));
	}

	// aerodynamic drag is proportional to v^2
	private float aerodynamicDragDeceleration(float velocity, float dragCoefficient) {
		float aeroDragDec = (velocity > 0 ? -1 : 1) * dragCoefficient * velocity * velocity;
		return (Math.abs(aeroDragDec) > Math.abs(velocity)) ? -velocity : aeroDragDec;
	}
	
	public void setViewDimensions(int width, int height) {
		viewWidth = width;
		viewHeight = height;
		this.width = (int) (width * COURT_WIDTH_TO_VIEW_WIDTH_RATIO);
		courtOffset = (viewWidth - this.width) / 2;
		netPositionX = width / 2;
		netHeight = (int) (height * NET_HEIGHT_TO_VIEW_HEIGHT_RATIO);
		floorLevel = (int) (height * FLOOR_LEVEL_TO_VIEW_HEIGHT_RATIO);
		playerWidth = (int) (width * PLAYER_WIDTH_TO_VIEW_WIDTH_RATIO);
		playerHeight = (int) (height * PLAYER_HEIGHT_TO_VIEW_HEIGHT_RATIO);
		ballSize = (int) (width * BALL_SIZE_TO_VIEW_WIDTH_RATIO);
	}
	
}
