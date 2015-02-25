package com.hotmail.daemon3000.Sokoban;

import java.io.BufferedReader;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.*;

public class LevelPack {
	private final int MIN_LEVEL_SIZE = 3;
	
	private SpriteBatch m_spriteBatch;
	private Camera m_camera;
	private TextureRegion[] m_tileSet;
	private Array<LevelData> m_levelData;
	private Level m_loadedLevel;
	private String m_id;
	private int m_maxLevelSize;
	private boolean m_isDisposed = false;
	
	public LevelPack(String id, FileHandle fileHandle, TextureRegion[] tileSet, SpriteBatch spriteBatch, Camera camera) {
		m_id = id;
		m_tileSet = tileSet;
		m_spriteBatch = spriteBatch;
		m_camera = camera;
		parse(fileHandle);
	}
	
	private void parse(FileHandle fileHandle) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(fileHandle.reader());
		}
		catch(GdxRuntimeException e) {
			m_levelData = new Array<LevelData>();
			return;
		}
		
		try {
			m_levelData = new Array<LevelData>();
			
			Array<String> levelLines = new Array<String>();
			int levelWidth = 0, maxLevelWidth = 0, maxLevelHeight = 0;
			
			for(String line; (line = reader.readLine()) != null;) {
				if(line.length() < MIN_LEVEL_SIZE || line.startsWith(";") || line.startsWith("\n")) {
					if(levelLines.size >= MIN_LEVEL_SIZE) {
						if(levelLines.size > maxLevelHeight)
							maxLevelHeight = levelLines.size;
						
						parseLevelData(levelLines, levelWidth);
						levelLines.clear();
						levelWidth = 0;
					}
					continue;
				}
				
				if(line.length() > levelWidth)
					levelWidth = line.length();
				if(levelWidth > maxLevelWidth)
					maxLevelWidth = line.length();
				levelLines.add(line);
			}
			
			if(levelLines.size >= MIN_LEVEL_SIZE) {
				parseLevelData(levelLines, levelWidth);
			}
			
			m_maxLevelSize = maxLevelWidth * maxLevelHeight;
			
			reader.close();
		}
		catch(IOException ex) {
			if(m_levelData == null)
				m_levelData = new Array<LevelData>();
			else
				m_levelData.clear();
		}
	}
	
	private void parseLevelData(Array<String> lines, int width) {
		int levelHeight = lines.size;
		int playerPosX = 0, playerPosY = 0;
		int goalCount = 0, activeGoalCountAtStart = 0;
		byte[] tiles = new byte[lines.size * width];
		
		int count = 0, lineLength = 0;
		for(String line: lines) {
			lineLength = line.length();
			for(int i = 0; i < lineLength; i++) {
				switch(line.charAt(i)) {
				case '#':
					tiles[count] = Tiles.TILE_WALL;
					break;
				case '@':
					tiles[count] = Tiles.TILE_PLAYER;
					playerPosX = i;
					playerPosY = count / width;
					break;
				case '$':
					tiles[count] = Tiles.TILE_CRATE;
					break;
				case '.':
					tiles[count] = Tiles.TILE_GOAL;
					goalCount++;
					break;
				case '+':
					tiles[count] = Tiles.TILE_PLAYER_ON_GOAL;
					playerPosX = i;
					playerPosY = count / width;
					goalCount++;
					break;
				case '*':
					tiles[count] = Tiles.TILE_CRATE_ON_GOAL;
					goalCount++;
					activeGoalCountAtStart++;
					break;
				default:
					tiles[count] = Tiles.TILE_EMPTY;
					break;
				}
				count++;
			}
			if(lineLength < width) {
				for(int i = lineLength; i < width; i++)
					tiles[count++] = Tiles.TILE_EMPTY;
			}
		}
		
		m_levelData.add(new LevelData(tiles, width, levelHeight, playerPosX, playerPosY, goalCount, activeGoalCountAtStart));
	}
	
	public int getLevelCount() {
		if(!m_isDisposed)
			return m_levelData.size;
		else
			return 0;
	}
	
	public Level getLevel(int index) {
		if(!m_isDisposed) {
			if(index < 0 || index >= m_levelData.size)
				return null;
			
			if(m_loadedLevel != null) {
				m_loadedLevel.initialize(m_levelData.get(index));
			}
			else {
				m_loadedLevel = new Level(m_spriteBatch, m_camera, m_tileSet, m_maxLevelSize, m_levelData.get(index));
			}
			
			return m_loadedLevel;
		}
		
		return null;
	}
	
	public String getID() {
		return m_id;
	}
	
	public void dispose() {
		if(!m_isDisposed) {
			m_loadedLevel.dispose();
			m_spriteBatch = null;
			m_camera = null;
			m_tileSet = null;
			
			m_levelData.clear();
			m_levelData = null;
			
			m_isDisposed = true;
		}
	}
}
