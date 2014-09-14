package com.hotmail.daemon3000;

import java.io.Serializable;

public class GameScore implements Serializable {
	private static final long serialVersionUID = 7586506829146222958L;
	public int bestMoves;
	public float bestTime;
	
	public GameScore() {
		this.bestMoves = 0;
		this.bestTime = 0.0f;
	}
	
	public GameScore(int bestMoves, float bestTime) {
		this.bestMoves = bestMoves;
		this.bestTime = bestTime;
	}
}
