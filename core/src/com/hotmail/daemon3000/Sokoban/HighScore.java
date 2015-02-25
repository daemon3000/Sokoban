package com.hotmail.daemon3000.Sokoban;

import java.io.Serializable;

public class HighScore implements Serializable {
	private static final long serialVersionUID = 7586506829146222958L;
	public int[] bestMoves;
	public float[] bestTime;
	
	public HighScore() {
	}
	
	public HighScore(int length) {
		bestMoves = new int[length];
		bestTime = new float[length];
	}
}
