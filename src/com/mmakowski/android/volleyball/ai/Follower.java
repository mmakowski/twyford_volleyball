package com.mmakowski.android.volleyball.ai;

import com.mmakowski.android.volleyball.model.Court;

/**
 * An extremely simple AI. Always follow the ball if in our part of the court.
 * 
 * @author mmakowski
 */
public class Follower implements AI {
	public final void movePlayers(Court court) {
		if (court.ball.positionX > court.netPositionX) {
			court.movePlayer(Court.AI_TEAM, court.ball.positionX);
		} else {
			court.movePlayer(Court.AI_TEAM, court.netPositionX + court.width / 3);
		}
	}
}
