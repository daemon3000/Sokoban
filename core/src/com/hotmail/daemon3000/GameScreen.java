package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
	private SpriteBatch m_spriteBatch;
	private OrthographicCamera m_camera;
	private Array<Texture> m_tileTextures;
	private LevelPack m_levelPack;
	private Level m_currentLevel = null;
	private int m_currentLevelIndex = 0;
	
	public GameScreen() {
		loadTiles();
		
		m_spriteBatch = new SpriteBatch();
		m_camera = new OrthographicCamera();
		m_camera.setToOrtho(false, 800, 480);
		m_camera.zoom = 2.0f;
		m_levelPack = new LevelPack("levels/pack_01.txt", m_spriteBatch, m_camera, m_tileTextures);
		if(m_levelPack.getLevelCount() > 0)
			m_currentLevel = m_levelPack.getLevel(m_currentLevelIndex);
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
		
		if(Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
			m_currentLevel.movePlayer(0, -1);
		if(Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S))
			m_currentLevel.movePlayer(0, 1);
		if(Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A))
			m_currentLevel.movePlayer(-1, 0);
		if(Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D))
			m_currentLevel.movePlayer(1, 0);
		if(Gdx.input.isKeyJustPressed(Keys.Z))
			m_currentLevel.undoMovePlayer();
		if(Gdx.input.isKeyJustPressed(Keys.COMMA))
			cycleLevelLeft();
		if(Gdx.input.isKeyJustPressed(Keys.PERIOD))
			cycleLevelRight();
		
		m_currentLevel.render();
	}
	
	private void cycleLevelLeft() {
		if(m_currentLevelIndex > 0) {
			m_currentLevelIndex--;
			m_currentLevel = m_levelPack.getLevel(m_currentLevelIndex);
		}
	}
	
	private void cycleLevelRight() {
		if(m_currentLevelIndex < m_levelPack.getLevelCount() - 1) {
			m_currentLevelIndex++;
			m_currentLevel = m_levelPack.getLevel(m_currentLevelIndex);
		}
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
		m_currentLevel.dispose();
		m_levelPack.dispose();
		m_spriteBatch.dispose();
		
		for(Texture texture: m_tileTextures) {
			texture.dispose();
		}
		m_tileTextures.clear();
	}
}
