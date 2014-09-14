package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class GameScreen implements Screen {
	private SokobanGame m_game;
	private SpriteBatch m_spriteBatch;
	private OrthographicCamera m_camera;
	private Array<Texture> m_tileTextures;
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
	
	public GameScreen(SokobanGame game, String levelPackID, String levelPackFile, int startLevel) {
		loadTiles();
		m_game = game;
		m_spriteBatch = new SpriteBatch();
		m_camera = new OrthographicCamera();
		m_camera.setToOrtho(false, 800, 480);
		m_camera.zoom = 2.0f;
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_statusPanel = new StatusPanel(m_uiSkin);
		m_pauseMenu = new PauseMenu(m_uiSkin, m_click);
		m_levelCompleteMenu = new LevelCompleteMenu(m_uiSkin, m_click);
		m_levelPack = new LevelPack(levelPackID, levelPackFile, m_spriteBatch, m_camera, m_tileTextures);
		
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
		m_tileTextures = new Array<Texture>();
		m_tileTextures.add(new Texture(Gdx.files.internal("img/wall_brown.png")));
		m_tileTextures.add(new Texture(Gdx.files.internal("img/player_front.png")));
		m_tileTextures.add(new Texture(Gdx.files.internal("img/crate_off.png")));
		m_tileTextures.add(new Texture(Gdx.files.internal("img/marker.png")));
		m_tileTextures.add(new Texture(Gdx.files.internal("img/player_front.png")));
		m_tileTextures.add(new Texture(Gdx.files.internal("img/crate_on.png")));
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.45f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(m_currentLevel == null)
			return;
		
		if(m_pauseMenu.isOpen()) {
			m_currentLevel.render();
			m_statusPanel.render();
			m_pauseMenu.render(delta);
		}
		else if(m_levelCompleteMenu.isOpen()) {
			m_currentLevel.render();
			m_statusPanel.render();
			m_levelCompleteMenu.render(delta);
		}
		else {
			handleInput();
			m_currentLevel.render();
			m_elapsedTime += delta;
			m_statusPanel.setElapsedTime(m_elapsedTime);
			m_statusPanel.render();
		}
	}
	
	private void handleInput() {
		if(Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
			movePlayer(0, -1);
		if(Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S))
			movePlayer(0, 1);
		if(Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A))
			movePlayer(-1, 0);
		if(Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D))
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
			
			GameScore gameScore = m_game.getScore(m_levelPack, m_currentLevelIndex);
			m_bestMoves = gameScore != null ? gameScore.bestMoves : 0;
			m_bestTime = gameScore != null ? gameScore.bestTime : 0.0f;
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
		}
		m_levelCompleteMenu.open();
	}
	
	private void resetLevel() {
		m_pauseMenu.close();
		loadLevel(m_currentLevelIndex);
	}
	
	private void loadNextLevel() {
		if(m_currentLevelIndex < m_levelPack.getLevelCount() - 1) {
			m_pauseMenu.close();
			loadLevel(m_currentLevelIndex + 1);
		}
	}
	
	private void quitGame() {
		if(m_pauseMenu.isOpen())
			m_pauseMenu.close();
		else if(m_levelCompleteMenu.isOpen())
			m_levelCompleteMenu.close();
		
		Timer.schedule(new Task() {
			@Override
			public void run() {
				dispose();
				m_game.setScreen(new StartScreen(m_game));
			}
			
		}, 0.2f);
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
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
		m_statusPanel.dispose();
		m_pauseMenu.dispose();
		m_levelPack.dispose();
		m_spriteBatch.dispose();
		m_uiSkin.dispose();
		m_click.dispose();
		
		for(Texture texture: m_tileTextures) {
			texture.dispose();
		}
		m_tileTextures.clear();
	}
}
