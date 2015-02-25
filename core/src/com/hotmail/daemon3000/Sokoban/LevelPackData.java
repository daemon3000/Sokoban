package com.hotmail.daemon3000.Sokoban;

public class LevelPackData {
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_NORMAL = 1;
	public static final int DIFFICULTY_HARD = 2;
	
	public String name;
	public String id;
	public String file;
	public String author;
	public int levelCount;
	public int difficulty;
	public boolean isInternal;
	
	public LevelPackData(String name, String id, String file, String author, int levelCount, int difficulty, boolean isInternal) {
		this.name = name;
		this.file = file;
		this.id = id;
		this.author = author;
		this.levelCount = levelCount;
		this.difficulty = difficulty;
		this.isInternal = isInternal;
	}
}
