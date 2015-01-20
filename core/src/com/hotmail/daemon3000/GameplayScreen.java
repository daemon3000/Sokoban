package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
	private float m_elapsedTime = 0.0f;
	private int m_currentLevelIndex = 0;
	
	public GameplayScreen(ScreenManager owner, SokobanGame game, String levelPackID, FileHandle levelPackFile) {
		super(ScreenID.Gameplay, owner, false, false);
		
		m_spriteBatch = new SpriteBatch();
		m_camera = new OrthographicCamera();
		m_camera.zoom = 1.5f;
		m_viewport = game.getPlatformSettings().createViewport(m_camera);
		
		m_levelCompleteListeners = new Array<ActionListener>();
		m_levelLoadedListeners = new Array<ActionListener>();
		m_tileSheet = new Texture("img/tilesheet.png");
		m_tileSet = new TextureRegion[m_tileSheet.getWidth() / Tiles.TILE_WIDTH];
		for(int i = 0; i < m_tileSet.length; i++) {
			m_tileSet[i] = new TextureRegion(m_tileSheet, i * Tiles.TILE_WIDTH, 0, Tiles.TILE_WIDTH, Tiles.TILE_HEIGHT);
		}
		m_levelPack = new LevelPack(levelPackID, levelPackFile, m_tileSet, m_spriteBatch, m_camera);
	}
	
	@Override
	public void onEnter() {
	}
	
	@Override
	public void onFocusEnter() {
	}

	@Override
	public void update(float delta) {
		if(m_currentLevel != null) {
			m_elapsedTime += delta;
			handleInput();
		}
	}
	
	private void handleInput() {
		if(Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
			movePlayer(0, -1);
		else if(Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S))
			movePlayer(0, 1);
		else if(Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A))
			movePlayer(-1, 0);
		else if(Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D))
			movePlayer(1, 0);
		if(Gdx.input.isKeyJustPressed(Keys.Z))
			undoMovePlayer();
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			getOwner().pushScreen(ScreenID.Pause);
	}
	
	@Override
	public void render() {
		if(m_currentLevel != null)
			m_currentLevel.render();
	}
	
	@Override
	public void onFocusExit() {
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
		m_viewport.update((int)screenSize.x, (int)screenSize.y);
	}
	
	private void movePlayer(int dx, int dy) {
		m_currentLevel.movePlayer(dx, dy);
	}
	
	private void undoMovePlayer() {
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
