package com.hotmail.daemon3000.Sokoban;

public final class Tiles {
	public static final int TILE_WIDTH = 64;
	public static final int TILE_HEIGHT = 64;
	
	public static final byte TILE_NULL = -2;
	public static final byte TILE_EMPTY = -1;
	public static final byte TILE_WALL = 0;
	public static final byte TILE_CRATE = 1;
	public static final byte TILE_CRATE_ON_GOAL = 2;
	public static final byte TILE_GOAL = 3;
	public static final byte TILE_PLAYER_ON_GOAL = 4;
	public static final byte TILE_PLAYER = 5;
	
	private Tiles() {
	}
}
