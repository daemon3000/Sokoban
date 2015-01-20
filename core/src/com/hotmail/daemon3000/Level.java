package com.hotmail.daemon3000;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.*;

public class Level {
	private static final int MAX_UNDO_STEPS = 32;
	
	private enum PlayerDirection {
		Front, Back, Left, Right
	}
	
	private SpriteBatch m_spriteBatch;
	private Camera m_camera;
	private TextureRegion[] m_tileRegions;
	private Pool<UndoStep> m_undoPool;
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
	private PlayerDirection m_playerDir;
	
	public Level(SpriteBatch batch, Camera camera, TextureRegion[] tileRegions, int tileCapacity) {
		m_spriteBatch = batch;
		m_camera = camera;
		m_tileRegions = tileRegions;
		m_undoStack = new Array<UndoStep>(MAX_UNDO_STEPS + 1);
		m_undoPool = new Pool<UndoStep>(MAX_UNDO_STEPS + 1) {
			@Override 
			protected UndoStep newObject() {
				return new UndoStep();
			}
		};
		m_levelCompleteListeners = new Array<ActionListener>();
		m_tiles = new byte[tileCapacity];
	}
	
	public Level(SpriteBatch batch, Camera camera, TextureRegion[] tileRegions, int tileCapacity, LevelData levelData) {
		this(batch, camera, tileRegions, tileCapacity);
		initialize(levelData);
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
		m_playerDir = PlayerDirection.Front;
		m_levelCompleteListeners.clear();
		m_isInitialized = true;
		
		clearUndoStack();
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
			if(m_tiles[i] == Tiles.TILE_PLAYER || m_tiles[i] == Tiles.TILE_PLAYER_ON_GOAL) {
				switch(m_playerDir) {
				case Front:
					m_spriteBatch.draw(m_tileRegions[Tiles.TILE_PLAYER - 1], texturePosX * Tiles.TILE_WIDTH, (m_height - (texturePosY + 1)) * Tiles.TILE_HEIGHT);
					break;
				case Back:
					m_spriteBatch.draw(m_tileRegions[Tiles.TILE_PLAYER], texturePosX * Tiles.TILE_WIDTH, (m_height - (texturePosY + 1)) * Tiles.TILE_HEIGHT);
					break;
				case Left:
					m_spriteBatch.draw(m_tileRegions[Tiles.TILE_PLAYER + 1], texturePosX * Tiles.TILE_WIDTH, (m_height - (texturePosY + 1)) * Tiles.TILE_HEIGHT);
					break;
				case Right:
					m_spriteBatch.draw(m_tileRegions[Tiles.TILE_PLAYER + 2], texturePosX * Tiles.TILE_WIDTH, (m_height - (texturePosY + 1)) * Tiles.TILE_HEIGHT);
					break;
				}
			}
			else {
				m_spriteBatch.draw(m_tileRegions[m_tiles[i]], texturePosX * Tiles.TILE_WIDTH, (m_height - (texturePosY + 1)) * Tiles.TILE_HEIGHT);
			}
		}
		m_spriteBatch.end();
	}
	
	public void movePlayer(int dx, int dy) {
		if(m_isDisposed || !m_isInitialized || m_tiles.length == 0)
			return;
		
		boolean movedCrate = false;
		
		if(isWallInDirection(dx, dy))
			return;
		if(isCrateInDirection(dx, dy)) {
			if(moveCrate(dx, dy))
				movedCrate = true;
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
		setPlayerDirection(dx, dy);
		
		registerUndoStep(dx, dy, movedCrate);
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
		setPlayerDirection(undoStep.deltaX, undoStep.deltaY);
		
		m_camera.position.set(m_playerPosX * Tiles.TILE_WIDTH, (m_height - (m_playerPosY + 1)) * Tiles.TILE_HEIGHT, 0);
		
		if(!undoStep.movedCrate) {
			m_undoPool.free(undoStep);
			return;
		}
		
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
		
		m_undoPool.free(undoStep);
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
	
	private void setPlayerDirection(int dx, int dy) {
		if(dx == 1) {
			m_playerDir = PlayerDirection.Right;
		}
		else if(dx == -1) {
			m_playerDir = PlayerDirection.Left;
		}
		else if(dy == 1) {
			m_playerDir = PlayerDirection.Front;
		}
		else if(dy == -1) {
			m_playerDir = PlayerDirection.Back;
		}
		else {
			m_playerDir = PlayerDirection.Front;
		}
	}
	
	private void clearUndoStack() {
		while(m_undoStack.size > 0) {
			UndoStep undoStep = m_undoStack.pop();
			m_undoPool.free(undoStep);
		}
	}
	
	private void registerUndoStep(int deltaX, int deltaY, boolean movedCrate) {
		UndoStep undoStep = m_undoPool.obtain();
		undoStep.init(deltaX, deltaY, movedCrate);
		
		if(m_undoStack.size < MAX_UNDO_STEPS) {
			m_undoStack.add(undoStep);
		}
		else {
			m_undoPool.free(m_undoStack.removeIndex(0));
			m_undoStack.add(undoStep);
		}
		
		if(m_currentActiveGoals == m_goalCount) {
			for(ActionListener listener: m_levelCompleteListeners) {
				listener.handle();
			}
		}
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
			m_undoStack.clear();
			m_undoPool.clear();
			m_spriteBatch = null;
			m_camera = null;
			m_tileRegions = null;
			m_undoStack = null;
			m_tiles = null;
			m_isInitialized = false;
			m_isDisposed = true;
		}
	}
}
