package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameScene implements Scene {
	private ScreenManager m_screenManager;
	private GameplayScreen m_gameplayScreen;
	private GameplayStatusScreen m_statusScreen;
	private SokobanGame m_game;
	private Skin m_uiSkin;
	private Sound m_click;
	private String m_levelPackID;
	private FileHandle m_levelPackFile;
	private int m_startLevel;
	
	public GameScene(SokobanGame game, String levelPackID, FileHandle levelPackFile, int startLevel) {
		m_game = game;
		m_levelPackID = levelPackID;
		m_levelPackFile = levelPackFile;
		m_startLevel = startLevel;
	}
	
	@Override
	public void onLoad() {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_screenManager = new ScreenManager();
		m_statusScreen = new GameplayStatusScreen(m_screenManager, m_game, m_uiSkin);
		
		m_gameplayScreen = new GameplayScreen(m_screenManager, m_game, m_levelPackID, m_levelPackFile);
		m_gameplayScreen.addLevelCompleteListener(new ActionListener() {
			public void handle() {
				handleLevelComplete();
			}
		});		
		m_gameplayScreen.addLevelLoadedListener(new ActionListener() {
			public void handle() {
				handleLevelLoaded();
			}
		});
		m_gameplayScreen.loadLevel(m_startLevel);
		
		
		PauseScreen pauseScreen = new PauseScreen(m_screenManager, m_game, m_uiSkin, m_click);
		pauseScreen.addResetLevelListener(new ActionListener() {
			public void handle() {
				handleResetLevel();
			}
		});
		pauseScreen.addSkipLevelListener(new ActionListener() {
			public void handle() {
				handleLoadNextLevel();
			}
		});
		pauseScreen.addQuitGameListener(new ActionListener() {
			public void handle() {
				handleQuitGame();
			}
		});
		
		LevelCompleteScreen levelCompleteScreen = new LevelCompleteScreen(m_screenManager, m_game, m_uiSkin, m_click);
		levelCompleteScreen.addContinueGameListener(new ActionListener() {
			public void handle() {
				handleLoadNextLevel();
			}
		});
		levelCompleteScreen.addQuitGameListener(new ActionListener() {
			public void handle() {
				handleQuitGame();
			}
		});
		
		m_screenManager.addScreen(m_gameplayScreen);
		m_screenManager.addScreen(m_statusScreen);
		m_screenManager.addScreen(pauseScreen);
		m_screenManager.addScreen(levelCompleteScreen);
		m_screenManager.pushScreen(ScreenID.Gameplay);
		m_screenManager.pushScreen(ScreenID.GameplayStatus);
	}
	
	private void handleLevelComplete() {
		LevelCompleteScreen levelCompleteScreen = (LevelCompleteScreen)m_screenManager.getScreen(ScreenID.LevelComplete);
		
		LevelPack levelPack = m_gameplayScreen.getLevelPack();
		HighScore score = m_game.getScore(levelPack);
		int currentLevelIndex = m_gameplayScreen.getCurrentLevelIndex();
		int moveCount = m_gameplayScreen.getMoveCount();
		int bestMoves = score != null ? score.bestMoves[currentLevelIndex] : 0;
		float bestTime = score != null ? score.bestTime[currentLevelIndex] : 0.0f;
		float elapsedTime = m_gameplayScreen.getElapsedTime();
		
		if((moveCount <= bestMoves || bestMoves <= 0) && (elapsedTime < bestTime || bestTime <= 0.0f)) {
			m_game.setScore(levelPack, currentLevelIndex, moveCount, elapsedTime);
			levelCompleteScreen.setNewRecord(true);
		}
		else {
			levelCompleteScreen.setNewRecord(false);
		}
		
		m_screenManager.pushScreen(ScreenID.LevelComplete);
	}
	
	private void handleResetLevel() {
		m_gameplayScreen.resetLevel();
	}
	
	private void handleLoadNextLevel() {
		m_gameplayScreen.loadNextLevel();
	}
	
	private void handleLevelLoaded() {
		LevelPack levelPack = m_gameplayScreen.getLevelPack();
		HighScore score = m_game.getScore(levelPack);
		int currentLevelIndex = m_gameplayScreen.getCurrentLevelIndex();
		int bestMoves = score != null ? score.bestMoves[currentLevelIndex] : 0;
		float bestTime = score != null ? score.bestTime[currentLevelIndex] : 0.0f;
		
		m_statusScreen.reset();
		m_statusScreen.setLevelIndex(currentLevelIndex + 1, levelPack.getLevelCount());
		m_statusScreen.setBestMoves(bestMoves);
		m_statusScreen.setBestTime(bestTime);
	}
	
	private void handleQuitGame() {
		m_game.setScene(new StartScene(m_game));
	}
	
	@Override
	public void update(float delta) {
		m_screenManager.update(delta);
		m_statusScreen.setElapsedTime(m_gameplayScreen.getElapsedTime());
		m_statusScreen.setMoveCount(m_gameplayScreen.getMoveCount());
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.45f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		m_screenManager.render();
	}

	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
		m_screenManager.resize(screenSize, virtualScreenSize);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {	
	}

	@Override
	public void dispose() {
		m_screenManager.dispose();
		m_uiSkin.dispose();
		m_click.dispose();
	}
}
