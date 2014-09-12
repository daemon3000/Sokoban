package com.hotmail.daemon3000;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartScreen implements Screen {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Game m_game;
	
	public StartScreen(Game game) {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		m_game = game;
		
		createWidgets();
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createWidgets() {
		m_window = new Window("Main Menu", m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setWidth(200);
		m_window.setHeight(150);
		m_window.setPosition(Gdx.graphics.getWidth() / 2 - m_window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		Button playButton = new TextButton("Play", m_uiSkin, "default");
		m_window.addActor(playButton);
		playButton.setWidth(150.0f);
		playButton.setHeight(30.0f);
		playButton.setPosition(m_window.getWidth() / 2 - playButton.getWidth() / 2, m_window.getHeight() / 2);
		playButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_game.setScreen(new LevelSelectScreen(m_game));
				dispose();
		    }
		});
		
		Button exitButton = new TextButton("Exit", m_uiSkin, "default");
		m_window.addActor(exitButton);
		exitButton.setWidth(150.0f);
		exitButton.setHeight(30.0f);
		exitButton.setPosition(m_window.getWidth() / 2 - exitButton.getWidth() / 2, m_window.getHeight() / 2 - 40);
		exitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
		    }
		});
		
		m_stage.addActor(m_window);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.45f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		m_stage.act(delta);
		m_stage.draw();
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
		m_stage.dispose();
		m_uiSkin.dispose();
	}
}
