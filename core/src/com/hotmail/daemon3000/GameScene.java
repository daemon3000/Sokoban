package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class GameScene implements Scene {
	private SokobanGame m_game;
	private Texture m_tileSheet;
	private TextureRegion[] m_tileSet;
	private Skin m_uiSkin;
	private Sound m_click;
	private StatusPanel m_statusPanel;
	private PauseMenu m_pauseMenu;
	private LevelCompleteMenu m_levelCompleteMenu;
	private LevelPack m_levelPack;
	private Level m_currentLevel = null;
	private float m_elapsedTime = 0.0f;
	private int m_currentLevelIndex = 0;
	private int m_bestMoves = 0;
	private float m_bestTime = 0.0f; 
	
	public GameScene(SokobanGame game, String levelPackID, FileHandle levelPackFile, int startLevel) {
		loadTiles();
		m_game = game;
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_statusPanel = new StatusPanel(m_uiSkin, m_game.getStringBundle());
		m_pauseMenu = new PauseMenu(game, m_uiSkin, m_click, m_game.getStringBundle());
		m_levelCompleteMenu = new LevelCompleteMenu(game, m_uiSkin, m_click, m_game.getStringBundle());
		m_levelPack = new LevelPack(levelPackID, levelPackFile, m_tileSet);
		
		m_pauseMenu.addResetLevelListener(new ActionListener() {
			public void handle() {
				resetLevel();
			}
		});
		m_pauseMenu.addSkipLevelListener(new ActionListener() {
			public void handle() {
				loadNextLevel();
			}
		});
		m_pauseMenu.addQuitGameListener(new ActionListener() {
			public void handle() {
				quitGame();
			}
		});
		
		m_levelCompleteMenu.addContinueGameListener(new ActionListener() {
			public void handle() {
				loadNextLevel();
			}
		});
		m_levelCompleteMenu.addQuitGameListener(new ActionListener() {
			public void handle() {
				quitGame();
			}
		});
		
		loadLevel(startLevel);
	}
	
	private void loadTiles() {
		m_tileSheet = new Texture("img/tilesheet.png");
		m_tileSet = new TextureRegion[m_tileSheet.getWidth() / Tiles.TILE_WIDTH];
		for(int i = 0; i < m_tileSet.length; i++) {
			m_tileSet[i] = new TextureRegion(m_tileSheet, i * Tiles.TILE_WIDTH, 0, Tiles.TILE_WIDTH, Tiles.TILE_HEIGHT);
		}
	}
	
	@Override
	public void update(float delta) {
		if(m_currentLevel != null)
		{
			if(m_pauseMenu.isOpen()) {
				m_pauseMenu.update(delta);
			}
			else if(m_levelCompleteMenu.isOpen()) {
				m_levelCompleteMenu.update(delta);
			}
			else {
				handleInput();
				m_elapsedTime += delta;
				m_statusPanel.setElapsedTime(m_elapsedTime);
			}
		}
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.45f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(m_currentLevel != null)
		{
			m_currentLevel.render();
			m_statusPanel.render();
			if(m_pauseMenu.isOpen()) {
				m_pauseMenu.render();
			}
			else if(m_levelCompleteMenu.isOpen()) {
				m_levelCompleteMenu.render();
			}
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
			m_pauseMenu.open();
	}
	
	private void movePlayer(int dx, int dy) {
		m_currentLevel.movePlayer(dx, dy);
		m_statusPanel.setMoveCount(m_currentLevel.getMoveCount());
	}
	
	private void undoMovePlayer() {
		m_currentLevel.undoMovePlayer();
	}
	
	private void loadLevel(int index) {
		m_currentLevelIndex = index;
		m_currentLevel = m_levelPack.getLevel(m_currentLevelIndex);
		if(m_currentLevel != null) {
			m_currentLevel.addLevelCompleteListener(new ActionListener() {
				public void handle() {
					handleLevelComplete();
				}
			});
			
			HighScore score = m_game.getScore(m_levelPack);
			m_bestMoves = score != null ? score.bestMoves[m_currentLevelIndex] : 0;
			m_bestTime = score != null ? score.bestTime[m_currentLevelIndex] : 0.0f;
		}
		m_elapsedTime = 0.0f;
		m_statusPanel.reset();
		m_statusPanel.setLevelIndex(m_currentLevelIndex + 1, m_levelPack.getLevelCount());
		m_statusPanel.setBestMoves(m_bestMoves);
		m_statusPanel.setBestTime(m_bestTime);
	}
	
	private void handleLevelComplete() {
		int moveCount = m_currentLevel.getMoveCount();
		
		if((moveCount <= m_bestMoves || m_bestMoves <= 0) && (m_elapsedTime < m_bestTime || m_bestTime <= 0.0f)) {
			m_game.setScore(m_levelPack, m_currentLevelIndex, moveCount, m_elapsedTime);
			m_levelCompleteMenu.setNewRecord(true);
		}
		else {
			m_levelCompleteMenu.setNewRecord(false);
		}	
		m_levelCompleteMenu.open();
	}
	
	private void resetLevel() {
		loadLevel(m_currentLevelIndex);
	}
	
	private void loadNextLevel() {
		if(m_currentLevelIndex < m_levelPack.getLevelCount() - 1) {
			loadLevel(m_currentLevelIndex + 1);
		}
	}
	
	private void quitGame() {
		Timer.schedule(new Task() {
			@Override
			public void run() {
				m_game.setScene(new StartScene(m_game));
			}
			
		}, 0.2f);
	}
	
	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		m_currentLevel = null;
		m_tileSet = null;
		m_statusPanel.dispose();
		m_pauseMenu.dispose();
		m_levelPack.dispose();
		m_uiSkin.dispose();
		m_click.dispose();
		m_tileSheet.dispose();
	}
}
