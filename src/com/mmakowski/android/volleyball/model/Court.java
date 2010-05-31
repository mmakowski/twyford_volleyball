package com.mmakowski.android.volleyball.model;

import static com.mmakowski.android.volleyball.model.GameElementDimensions.BALL_SIZE_TO_VIEW_WIDTH_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.COURT_WIDTH_TO_VIEW_WIDTH_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.FLOOR_LEVEL_TO_VIEW_HEIGHT_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.NET_HEIGHT_TO_VIEW_HEIGHT_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.PLAYER_HEIGHT_TO_VIEW_HEIGHT_RATIO;
import static com.mmakowski.android.volleyball.model.GameElementDimensions.PLAYER_WIDTH_TO_VIEW_WIDTH_RATIO;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import java.util.Random;

/**
 * Court represents all elements of the game and takes care of updating the physics.
 * 
 * @author mmakowski
 *
 */
public final class Court {
	public static final int HUMAN_TEAM = 0;
	public static final int AI_TEAM = 1;
	public static final int SIDE_LEFT = -1;
	public static final int SIDE_RIGHT = 1;
	
	private static final int STATE_NOT_SET_UP = 0;
	private static final int STATE_HUMAN_TEAM_SERVE = 1;
	private static final int STATE_AI_TEAM_SERVE = 2;
	private static final int STATE_PLAY = 3;
	
	private int state = STATE_NOT_SET_UP;
	private int lastTouch;
	private int viewWidth;
	public int width;
	public int netPositionX;
	public int netHeight = 130;
	public Player[][] players;
	public Ball ball;
	public int floorLevel;
	public int humanSide = SIDE_LEFT;
	private int ballSize;
	private int playerWidth;
	private int playerHeight;
	private int courtOffset;
	private int[] points = {0, 0};
	
	private Random random = new Random();
	
	public synchronized void setUp(int playersPerTeam) {
		players = new Player[2][playersPerTeam];
		int playerPosY = playerHeight + floorLevel;
		// distribute the players evenly in the court
		for (int t = 0; t < 2; t++) {
			for (int j = 0; j < playersPerTeam; j++) {
				players[t][j] = new Player(t, netPositionX + (t * 2 - 1) * (j + 1) * width / (2 * (playersPerTeam + 1)) - (playerWidth / 2), playerPosY);
				players[t][j].setDefaultBallTarget(this, oppositeSide(t));
			}
		}
		ball = new Ball();
		enterState(STATE_HUMAN_TEAM_SERVE);
	}

	private void enterState(int newState) {
		switch (newState) {
		case STATE_HUMAN_TEAM_SERVE:
			ball.positionX = courtOffset / 2;
			ball.positionY = netHeight + floorLevel;
			ball.velocityX = 230;
			ball.velocityY = 100;
			lastTouch = HUMAN_TEAM;
			break;
		case STATE_AI_TEAM_SERVE:
			ball.positionX = viewWidth - courtOffset / 2;
			ball.positionY = netHeight + floorLevel;
			ball.velocityX = -230;
			ball.velocityY = 100;
			lastTouch = AI_TEAM;
			break;
		}
		state = newState;
	}

	public synchronized void update(long elapsedTimeMs) {
		float secFraction = elapsedTimeMs / 1000f;
		updateBall(secFraction);
		updatePlayers(secFraction);
	}
	
	private void updatePlayers(float secFraction) {
		for (int t = 0; t < 2; t++) {
			for (Player player : players[t]) {
				int posDiff = abs(player.targetPositionX - player.positionX);
				if (posDiff != 0) {
					int dir = player.targetPositionX > player.positionX ? 1 : -1;
					int move = (int) min(posDiff, Physics.PLAYER_MAX_MOVEMENT_SPEED * secFraction);
					player.positionX += dir * move;
				}
			}
		}
	}

	private void updateBall(float secFraction) {
		ball.positionX = (int) (ball.positionX + ball.velocityX * secFraction);
		ball.velocityX += Physics.aerodynamicDragDeceleration(ball.velocityX) * secFraction;  
		//Log.w(getClass().getName(), String.valueOf(ball.velocityX));
		ball.positionY = (int) (ball.positionY + ball.velocityY * secFraction);
		for (int t = 0; t < 2; t++) {
			for (Player player : players[t]) {
				int offsetX = player.positionX - ball.positionX;
				int offsetY = player.positionY - ball.positionY;
				// TODO: better player bounce
				if (ballHitPlayer(offsetX, offsetY)) {
					bounceBallOffPlayer(player, offsetX);
					lastTouch = t;
					return;
				}
			}
		}
		if (ball.positionY - ballSize <= floorLevel) {
			ballTouchedGround();
			/*
			ball.positionY = (int) (floorLevel + ballSize + (floorLevel - (ball.positionY - ballSize)) * Physics.BALL_REBOUND_FACTOR);
			ball.velocityY = -ball.velocityY * Physics.BALL_REBOUND_FACTOR;
			*/
		} else {
			ball.velocityY -= Physics.GRAVITY * secFraction;
		}
		// TODO: net collision 
		ball.velocityY += Physics.aerodynamicDragDeceleration(ball.velocityY) * secFraction;
		//Log.w(getClass().getName(), String.valueOf(ball.velocityY));
	}

	private boolean ballHitPlayer(int offsetX, int offsetY) {
		return offsetX >= -playerWidth && offsetX <= ballSize && ball.positionY + ballSize > floorLevel && offsetY >= -ballSize;
	}

	private void bounceBallOffPlayer(Player player, int offsetX) {
		int oppositeSide = oppositeSide(player.team);
		int idealY = player.positionY + ballSize;
		int idealOffsetX = oppositeSide == SIDE_LEFT ? player.positionX - ballSize : player.positionX + playerWidth;
		float ballPositionPenaltyX = ((float) offsetX - idealOffsetX) / ((float) playerWidth + (float) ballSize); // worst X is at the opposite edge of the player
		float ballPositionPenaltyY = ((float) ball.positionY - idealY) / ((float) playerHeight); // worst Y is at the feet of the player;
		float ballPositionPenalty = ballPositionPenaltyX * ballPositionPenaltyY;
		  
		ball.velocityY = 160f + penalty(ballPositionPenalty, 20) + penalty(player.accuracy, 20);
		ball.velocityX = 120f * oppositeSide * (float) pow(((float) abs(player.positionX - player.ballTargetX)) / ((float) width), 2)
				+ penalty(ballPositionPenalty, 20) + penalty(player.accuracy, 20);
	}
	
	/**
	 * @param bonus a number in the range 0..1 -- if 1 there will be no penalty, if 0 there will be maximum penalty 
	 * @param max maximum penalty 
	 * @return normally-distributed penalty in the range -(1 - bonus) * max .. (1 - bonus) * max 
	 */
	private float penalty(float bonus, float max) {
		return 2f * ((float) random.nextGaussian() - 0.5f) * (1f - bonus) * max;
	}

	private int oppositeSide(int team) {
		return team == AI_TEAM ? humanSide : -1 * humanSide;  
	}

	private void ballTouchedGround() {
		int winner;
		int x = ball.positionX + ballSize / 2;
		if (x < courtOffset || x > viewWidth - courtOffset) winner = opponent(lastTouch);
		else if (x <= netPositionX) winner = AI_TEAM;
		else winner = HUMAN_TEAM;
		points[winner]++;
		// TODO: check for victory
		enterState(winner == HUMAN_TEAM ? STATE_HUMAN_TEAM_SERVE : STATE_AI_TEAM_SERVE);
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

	public void movePlayer(int team, int targetX) {
		if (targetX < 0) targetX = 0;
		else if (targetX + playerWidth > viewWidth) targetX = viewWidth - playerWidth;
		else if (team == HUMAN_TEAM && targetX + playerWidth > netPositionX) targetX = netPositionX - playerWidth;
		else if (team == AI_TEAM && targetX < netPositionX) targetX = netPositionX;
		players[team][0].targetPositionX = targetX;
	}

	private final int opponent(int team) {
		return team == HUMAN_TEAM ? AI_TEAM : HUMAN_TEAM;
	}
	
}
