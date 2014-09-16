package com.hotmail.daemon3000;

public class LevelPackData {
	public String name;
	public String id;
	public String file;
	public String author;
	public int levelCount;
	public boolean isAddon;
	
	public LevelPackData(String name, String id, String file, int levelCount, String author, boolean isAddon) {
		this.name = name;
		this.file = file;
		this.id = id;
		this.author = author;
		this.levelCount = levelCount;
		this.isAddon = isAddon;
	}
}
