package com.hotmail.daemon3000;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class StartScreen implements Screen {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Sound m_click;
	private SokobanGame m_game;
	
	public StartScreen(SokobanGame game) {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_game = game;
		
		createWidgets();
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createWidgets() {
		I18NBundle bundle = m_game.getStringBundle();
		
		m_window = new Window(bundle.get("main_menu_title"), m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(250);
		m_window.setHeight(300);
		m_window.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		Button playButton = new TextButton(bundle.get("play_button"), m_uiSkin, "default");
		m_window.addActor(playButton);
		playButton.setWidth(220.0f);
		playButton.setPosition(m_window.getWidth() / 2 - playButton.getWidth() / 2, m_window.getHeight() / 2 + playButton.getHeight() + 5.0f);
		playButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						dispose();
						m_game.setScreen(new LevelSelectScreen(m_game));
					};
				}, 0.2f);
		    }
		});
		
		Button helpButton = new TextButton(bundle.get("help_button"), m_uiSkin, "default");
		m_window.addActor(helpButton);
		helpButton.setWidth(220.0f);
		helpButton.setPosition(m_window.getWidth() / 2 - helpButton.getWidth() / 2, playButton.getY() - helpButton.getHeight() - 10.0f);
		helpButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						dispose();
						m_game.setScreen(new HelpScreen(m_game));
					};
				}, 0.2f);
		    }
		});
		
		Button creditsButton = new TextButton(bundle.get("credits_button"), m_uiSkin, "default");
		m_window.addActor(creditsButton);
		creditsButton.setWidth(220.0f);
		creditsButton.setPosition(m_window.getWidth() / 2 - creditsButton.getWidth() / 2, helpButton.getY() - creditsButton.getHeight() - 10.0f);
		creditsButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						dispose();
						m_game.setScreen(new CreditsScreen(m_game));
					};
				}, 0.2f);
		    }
		});
		
		Button exitButton = new TextButton(bundle.get("exit_button"), m_uiSkin, "default");
		m_window.addActor(exitButton);
		exitButton.setWidth(220.0f);
		exitButton.setPosition(m_window.getWidth() / 2 - exitButton.getWidth() / 2, creditsButton.getY() - exitButton.getHeight() - 10.0f);
		exitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				m_game.saveScores();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						Gdx.app.exit();
					};
				}, 0.2f);
		    }
		});
		
		m_stage.addActor(m_window);
		slideIn();
	}
	
	private void slideIn() {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(Gdx.graphics.getWidth() / 2 - m_window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		moveAction.setDuration(0.4f);
		m_window.addAction(moveAction);
	}
	
	private void slideOut() {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		moveAction.setDuration(0.2f);
		m_window.addAction(moveAction);
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
		m_click.dispose();
	}
}
