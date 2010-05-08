package com.mmakowski.android.volleyball.model;

public class Court {
	public int width;
	public int netPositionX;
	public int netHeight;
	public Player[][] players;
	public Ball ball;

	public Court(int width, int playersPerTeam) {
		setWidth(width);
		players = new Player[2][playersPerTeam];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < playersPerTeam; j++) players[i][j] = new Player(netPositionX + (i * 2 - 1) * (j + 1) * netPositionX / (playersPerTeam + 1)); 
		}
		
	}

	public void setWidth(int width) {
		this.width = width;
		netPositionX = width / 2;
		// TODO: reposition players and the ball
	}
}
