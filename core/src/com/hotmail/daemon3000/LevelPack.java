package com.hotmail.daemon3000;

import java.io.BufferedReader;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.*;

public class LevelPack {
	private final int MIN_LEVEL_SIZE = 3;
	
	private SpriteBatch m_spriteBatch;
	private OrthographicCamera m_camera;
	private Array<Texture> m_tileTextures;
	private Array<LevelData> m_levels;
	private boolean m_isDisposed = false;
	
	public LevelPack(String file, SpriteBatch batch, OrthographicCamera camera, Array<Texture> tileTextures) {
		this(Gdx.files.internal(file), batch, camera, tileTextures);
	}
	
	public LevelPack(FileHandle fileHandle, SpriteBatch batch, OrthographicCamera camera, Array<Texture> tileTextures) {
		m_spriteBatch = batch;
		m_camera = camera;
		m_tileTextures = tileTextures;

		loadLevels(fileHandle);
	}
	
	private void loadLevels(FileHandle fileHandle) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(fileHandle.reader());
		}
		catch(GdxRuntimeException e) {
			m_levels = new Array<LevelData>();
			return;
		}
		
		try {
			m_levels = new Array<LevelData>();
			
			Array<String> levelLines = new Array<String>();
			int levelWidth = 0;
			
			for(String line; (line = reader.readLine()) != null;) {
				if(line.length() < MIN_LEVEL_SIZE || line.startsWith(";") || line.startsWith("\n")) {
					if(levelLines.size >= MIN_LEVEL_SIZE) {
						createLevel(levelLines, levelWidth);
						levelLines.clear();
						levelWidth = 0;
					}
					continue;
				}
				
				if(line.length() > levelWidth)
					levelWidth = line.length();
				levelLines.add(line);
			}
			
			if(levelLines.size >= MIN_LEVEL_SIZE) {
				createLevel(levelLines, levelWidth);
			}
			
			reader.close();
		}
		catch(IOException ex) {
			if(m_levels == null)
				m_levels = new Array<LevelData>();
			else
				m_levels.clear();
		}
	}
	
	private void createLevel(Array<String> levelLines, int levelWidth) {
		int levelHeight = levelLines.size;
		int playerPosX = 0, playerPosY = 0;
		byte[] tiles = new byte[levelLines.size * levelWidth];
		
		int count = 0, lineLength = 0;
		for(String line: levelLines) {
			lineLength = line.length();
			for(int i = 0; i < lineLength; i++) {
				switch(line.charAt(i)) {
				case '#':
					tiles[count] = Tiles.TILE_WALL;
					break;
				case '@':
					tiles[count] = Tiles.TILE_PLAYER;
					playerPosX = i;
					playerPosY = count / levelWidth;
					break;
				case '$':
					tiles[count] = Tiles.TILE_CRATE;
					break;
				case '.':
					tiles[count] = Tiles.TILE_GOAL;
					break;
				case '+':
					tiles[count] = Tiles.TILE_PLAYER_ON_GOAL;
					playerPosX = i;
					playerPosY = count / levelWidth;
					break;
				case '*':
					tiles[count] = Tiles.TILE_CRATE_ON_GOAL;
					break;
				default:
					tiles[count] = Tiles.TILE_EMPTY;
					break;
				}
				count++;
			}
			if(lineLength < levelWidth) {
				for(int i = lineLength; i < levelWidth; i++)
					tiles[count++] = Tiles.TILE_EMPTY;
			}
		}
		
		m_levels.add(new LevelData(tiles, levelWidth, levelHeight, playerPosX, playerPosY));
	}
	
	public int getLevelCount() {
		if(!m_isDisposed)
			return m_levels.size;
		else
			return 0;
	}
	
	public Level getLevel(int index) {
		if(!m_isDisposed)
			return new Level(m_spriteBatch, m_camera, m_tileTextures, m_levels.get(index));
		else
			return null;
	}
	
	public void dispose() {
		if(!m_isDisposed) {
			m_spriteBatch = null;
			m_camera = null;
			m_tileTextures = null;
			
			m_levels.clear();
			m_levels = null;
			
			m_isDisposed = true;
		}
	}
}