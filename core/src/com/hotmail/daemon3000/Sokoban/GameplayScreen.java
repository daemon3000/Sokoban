package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameplayScreen extends Screen {
	private Array<ActionListener> m_levelCompleteListeners;
	private Array<ActionListener> m_levelLoadedListeners;
	private Texture m_tileSheet;
	private TextureRegion[] m_tileSet;
	private SpriteBatch m_spriteBatch;
	private OrthographicCamera m_camera;
	private Viewport m_viewport;
	private LevelPack m_levelPack;
	private Level m_currentLevel;
	private InputAdapter m_inputAdapter;
	private float m_elapsedTime = 0.0f;
	private int m_currentLevelIndex = 0;
	
	public GameplayScreen(ScreenManager owner, SokobanGame game, String levelPackID, FileHandle levelPackFile) {
		super(ScreenID.Gameplay, owner, false, false);
		
		m_spriteBatch = new SpriteBatch();
		m_camera = new OrthographicCamera();
		m_camera.zoom = 1.5f;
		m_viewport = game.getPlatformAdapter().createViewport(m_camera);
		m_inputAdapter = game.getPlatformAdapter().getInputAdapter();
		
		m_levelCompleteListeners = new Array<ActionListener>();
		m_levelLoadedListeners = new Array<ActionListener>();
		m_tileSheet = new Texture("img/tilesheet.png");
		m_tileSet = new TextureRegion[m_tileSheet.getWidth() / Tiles.TILE_WIDTH];
		for(int i = 0; i < m_tileSet.length; i++) {
			m_tileSet[i] = new TextureRegion(m_tileSheet, i * Tiles.TILE_WIDTH, 0, Tiles.TILE_WIDTH, Tiles.TILE_HEIGHT);
		}
		m_levelPack = new LevelPack(levelPackID, levelPackFile, m_tileSet, m_spriteBatch, new CameraSmoothFollow(m_camera, 1.5f, Tiles.TILE_WIDTH * 4));
	}
	
	@Override
	public void onEnter() {
	}
	
	@Override
	public void onFocusEnter() {
		m_inputAdapter.enable();
	}

	@Override
	public void update(float delta) {
		if(m_currentLevel != null) {
			m_currentLevel.update(delta);
			m_elapsedTime += delta;
		}
	}
	
	@Override
	public void render() {
		if(m_currentLevel != null)
			m_currentLevel.render();
	}
	
	@Override
	public void onFocusExit() {
		m_inputAdapter.disable();
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
		m_viewport.update((int)screenSize.x, (int)screenSize.y);
	}
	
	public void movePlayer(MoveDirection dir) {
		switch(dir) {
		case Up:
			m_currentLevel.movePlayer(0, -1);
			break;
		case Down:
			m_currentLevel.movePlayer(0, 1);
			break;
		case Right:
			m_currentLevel.movePlayer(1, 0);
			break;
		case Left:
			m_currentLevel.movePlayer(-1, 0);
			break;
		}
	}
	
	public void undoMovePlayer() {
		m_currentLevel.undoMovePlayer();
	}
	
	public void loadLevel(int index) {
		m_currentLevelIndex = index;
		m_currentLevel = m_levelPack.getLevel(m_currentLevelIndex);
		if(m_currentLevel != null) {
			m_currentLevel.addLevelCompleteListener(new ActionListener() {
				public void handle() {
					for(ActionListener listener: m_levelCompleteListeners) {
						listener.handle();
					}
				}
			});
		}
		m_elapsedTime = 0.0f;
		
		for(ActionListener listener: m_levelLoadedListeners) {
			listener.handle();
		}
	}
	
	public void resetLevel() {
		loadLevel(m_currentLevelIndex);
	}
	
	public void loadNextLevel() {
		if(m_currentLevelIndex < m_levelPack.getLevelCount() - 1) {
			loadLevel(m_currentLevelIndex + 1);
		}
	}
	
	public int getMoveCount() {
		if(m_currentLevel != null)
			return m_currentLevel.getMoveCount();
		else
			return 0;
	}
	
	public int getCurrentLevelIndex() {
		return m_currentLevelIndex;
	}
	
	public float getElapsedTime() {
		return m_elapsedTime;
	}
	
	public LevelPack getLevelPack() {
		return m_levelPack;
	}
	
	public void addLevelCompleteListener(ActionListener listener) {
		m_levelCompleteListeners.add(listener);
	}
	
	public void addLevelLoadedListener(ActionListener listener) {
		m_levelLoadedListeners.add(listener);
	}

	@Override
	public void dispose() {
		m_currentLevel = null;
		m_tileSet = null;
		m_viewport = null;
		m_camera = null;
		m_levelPack.dispose();
		m_tileSheet.dispose();
		m_spriteBatch.dispose();
	}
}
