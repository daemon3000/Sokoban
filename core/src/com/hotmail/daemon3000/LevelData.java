package com.hotmail.daemon3000;

public class LevelData {
	public byte[] tiles;
	public int width = 0;
	public int height = 0;
	public int playerPosX = 0;
	public int playerPosY = 0;
	
	public LevelData(byte[] tiles, int width, int height, int playerPosX, int playerPosY) {
		this.tiles = tiles;
		this.width = width;
		this.height = height;
		this.playerPosX = playerPosX;
		this.playerPosY = playerPosY;
	}
}
