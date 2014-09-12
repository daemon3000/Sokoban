package com.hotmail.daemon3000;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.*;

public class Level {
	public static final int MAX_UNDO = 30;
	
	private SpriteBatch m_spriteBatch;
	private OrthographicCamera m_camera;
	private Array<Texture> m_tileTextures;
	private Array<UndoStep> m_undoStack;
	private Array<ActionListener> m_levelCompleteListeners;
	private byte[] m_tiles;
	private int m_width = 0;
	private int m_height = 0;
	private int m_playerPosX = 0;
	private int m_playerPosY = 0;
	private int m_moveCount = 0;
	private int m_goalCount = 0;
	private int m_currentActiveGoals = 0;
	private boolean m_isDisposed = false;
	private boolean m_isInitialized = false;
	
	public Level(SpriteBatch batch, OrthographicCamera camera, Array<Texture> tileTextures, int tileCapacity, LevelData levelData) {
		m_spriteBatch = batch;
		m_camera = camera;
		m_tileTextures = tileTextures;
		m_undoStack = new Array<UndoStep>(MAX_UNDO + 1);
		m_levelCompleteListeners = new Array<ActionListener>();
		m_tiles = new byte[tileCapacity];
		
		initialize(levelData);
	}
	
	public Level(SpriteBatch batch, OrthographicCamera camera, Array<Texture> tileTextures, int tileCapacity) {
		m_spriteBatch = batch;
		m_camera = camera;
		m_tileTextures = tileTextures;
		m_undoStack = new Array<UndoStep>(MAX_UNDO + 1);
		m_tiles = new byte[tileCapacity];
	}
	
	public void initialize(LevelData levelData) {
		if(levelData.tiles.length > m_tiles.length || m_tiles == null)
		{
			m_tiles = new byte[levelData.tiles.length];
		}
		for(int i = 0; i < levelData.tiles.length; i++) {
			m_tiles[i] = levelData.tiles[i];
		}
		
		m_width = levelData.width;
		m_height = levelData.height;
		m_playerPosX = levelData.playerPosX;
		m_playerPosY = levelData.playerPosY;
		m_goalCount = levelData.goalCount;
		m_currentActiveGoals = levelData.activeGoalCountAtStart;
		m_camera.position.set(m_playerPosX * Tiles.TILE_WIDTH, (m_height - (m_playerPosY + 1)) * Tiles.TILE_HEIGHT, 0);
		m_moveCount = 0;
		m_undoStack.clear();
		m_levelCompleteListeners.clear();
		m_isInitialized = true;
	}
	
	public void render() {
		if(m_isDisposed || !m_isInitialized || m_tiles.length == 0)
			return;
		
		m_camera.update();
		m_spriteBatch.setProjectionMatrix(m_camera.combined);
		m_spriteBatch.begin();
		
		int length = m_width * m_height;  
		int texturePosX, texturePosY;
		for(int i = 0; i < length; i++)
		{
			if(m_tiles[i] == Tiles.TILE_EMPTY || m_tiles[i] == Tiles.TILE_NULL)
				continue;
			
			texturePosY = i / m_width;
			texturePosX = i - (texturePosY * m_width);
			m_spriteBatch.draw(m_tileTextures.get(m_tiles[i]), texturePosX * Tiles.TILE_WIDTH, (m_height - (texturePosY + 1)) * Tiles.TILE_HEIGHT);
		}
		m_spriteBatch.end();
	}
	
	public void movePlayer(int dx, int dy) {
		if(m_isDisposed || !m_isInitialized || m_tiles.length == 0)
			return;
		
		UndoStep undoStep = new UndoStep();
		undoStep.deltaX = dx;
		undoStep.deltaY = dy;
		undoStep.movedCrate = false;
		
		if(isWallInDirection(dx, dy))
			return;
		if(isCrateInDirection(dx, dy)) {
			if(moveCrate(dx, dy))
				undoStep.movedCrate = true;
			else
				return;
		}
		
		int tile = getTileAt(m_playerPosX, m_playerPosY);
		if(tile == Tiles.TILE_PLAYER)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_EMPTY);
		else if(tile == Tiles.TILE_PLAYER_ON_GOAL)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_GOAL);
		
		m_playerPosX += dx;
		m_playerPosY += dy;
		m_moveCount++;
		m_camera.position.set(m_playerPosX * Tiles.TILE_WIDTH, (m_height - (m_playerPosY + 1)) * Tiles.TILE_HEIGHT, 0);
		
		tile = getTileAt(m_playerPosX, m_playerPosY);
		if(tile == Tiles.TILE_EMPTY)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_PLAYER);
		else if(tile == Tiles.TILE_GOAL)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_PLAYER_ON_GOAL);
		
		if(m_undoStack.size < MAX_UNDO) {
			m_undoStack.add(undoStep);
		}
		else {
			for(int i = 0; i < m_undoStack.size - 1; i++) {
				m_undoStack.set(i, m_undoStack.get(i + 1));
			}
			m_undoStack.set(m_undoStack.size - 1, undoStep);
		}
		
		if(m_currentActiveGoals == m_goalCount) {
			for(ActionListener listener: m_levelCompleteListeners) {
				listener.handle();
			}
		}
	}
	
	private boolean isWallInDirection(int dx, int dy) {
		return getTileAt(m_playerPosX + dx, m_playerPosY + dy) == Tiles.TILE_WALL;
	}
	
	private boolean isWallInDirection(int x, int y, int dx, int dy) {
		return getTileAt(x + dx, y + dy) == Tiles.TILE_WALL;
	}
	
	private boolean isCrateInDirection(int dx, int dy) {
		return getTileAt(m_playerPosX + dx, m_playerPosY + dy) == Tiles.TILE_CRATE ||
				getTileAt(m_playerPosX + dx, m_playerPosY + dy) == Tiles.TILE_CRATE_ON_GOAL;
	}
	
	private boolean isCrateInDirection(int x, int y, int dx, int dy) {
		return getTileAt(x + dx, y + dy) == Tiles.TILE_CRATE ||
				getTileAt(x + dx, y + dy) == Tiles.TILE_CRATE_ON_GOAL;
	}
	
	private boolean moveCrate(int dx, int dy) {
		int cratePosX = m_playerPosX + dx, cratePosY = m_playerPosY + dy;
		if(isWallInDirection(cratePosX, cratePosY, dx, dy) || isCrateInDirection(cratePosX, cratePosY, dx, dy))
			return false;
		
		int tile = getTileAt(cratePosX, cratePosY);
		if(tile == Tiles.TILE_CRATE) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_EMPTY);
		}
		else if(tile == Tiles.TILE_CRATE_ON_GOAL) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_GOAL);
			m_currentActiveGoals--;
		}
		
		cratePosX += dx;
		cratePosY += dy;
		
		tile = getTileAt(cratePosX, cratePosY);
		if(tile == Tiles.TILE_EMPTY) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_CRATE);
		}
		else if(tile == Tiles.TILE_GOAL) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_CRATE_ON_GOAL);
			m_currentActiveGoals++;
		}
		
		return true;
	}
	
	public void undoMovePlayer() {
		if(m_isDisposed || !m_isInitialized || m_tiles.length == 0 || m_undoStack.size == 0)
			return;
		
		UndoStep undoStep = m_undoStack.pop();
		
		int tile = getTileAt(m_playerPosX, m_playerPosY);
		if(tile == Tiles.TILE_PLAYER)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_EMPTY);
		else if(tile == Tiles.TILE_PLAYER_ON_GOAL)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_GOAL);
		
		m_playerPosX -= undoStep.deltaX;
		m_playerPosY -= undoStep.deltaY;
		
		tile = getTileAt(m_playerPosX, m_playerPosY);
		if(tile == Tiles.TILE_EMPTY)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_PLAYER);
		else if(tile == Tiles.TILE_GOAL)
			setTileAt(m_playerPosX, m_playerPosY, Tiles.TILE_PLAYER_ON_GOAL);
		
		m_camera.position.set(m_playerPosX * Tiles.TILE_WIDTH, (m_height - (m_playerPosY + 1)) * Tiles.TILE_HEIGHT, 0);
		
		if(!undoStep.movedCrate)
			return;
		
		int cratePosX = m_playerPosX + 2 * undoStep.deltaX, cratePosY = m_playerPosY + 2 * undoStep.deltaY;
		tile = getTileAt(cratePosX, cratePosY);
		if(tile == Tiles.TILE_CRATE) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_EMPTY);
		}
		else if(tile == Tiles.TILE_CRATE_ON_GOAL) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_GOAL);
			m_currentActiveGoals--;
		}
		
		cratePosX -= undoStep.deltaX;
		cratePosY -= undoStep.deltaY;
		
		tile = getTileAt(cratePosX, cratePosY);
		if(tile == Tiles.TILE_EMPTY) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_CRATE);
		}
		else if(tile == Tiles.TILE_GOAL) {
			setTileAt(cratePosX, cratePosY, Tiles.TILE_CRATE_ON_GOAL);
			m_currentActiveGoals++;
		}
	}
	
	private int getTileAt(int x, int y) {
		if(x < 0 || x >= m_width || y < 0 || y >= m_height)
			return Tiles.TILE_NULL;
		
		return m_tiles[y * m_width + x];
	}
	
	private void setTileAt(int x, int y, byte tile) {
		if(x >= 0 && x < m_width && y >= 0 && y < m_height)
			m_tiles[y * m_width + x] = tile;
	}
	
	public int getUndoCount() {
		return m_undoStack.size;
	}
	
	public int getMoveCount() {
		return m_moveCount;
	}
	
	public void addLevelCompleteListener(ActionListener listener) {
		m_levelCompleteListeners.add(listener);
	}
	
	public void dispose() {
		if(!m_isDisposed) {
			m_spriteBatch = null;
			m_camera = null;
			m_tileTextures = null;
			m_undoStack = null;
			m_tiles = null;
			m_isInitialized = false;
			m_isDisposed = true;
		}
	}
}
