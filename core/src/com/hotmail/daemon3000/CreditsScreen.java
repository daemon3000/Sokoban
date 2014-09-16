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

public class CreditsScreen implements Screen {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Sound m_click;
	private SokobanGame m_game;
	
	public CreditsScreen(SokobanGame game) {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_game = game;
		
		createWidgets();
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createWidgets() {
		I18NBundle bundle = m_game.getStringBundle();
		
		m_window = new Window(bundle.get("credits_menu_title"), m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(300);
		m_window.setHeight(260);
		m_window.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		Label artCreditsLabel = new Label(bundle.format("tiles_credits", "1001.com"), m_uiSkin, "default");
		m_window.addActor(artCreditsLabel);
		artCreditsLabel.setPosition(15.0f, m_window.getHeight() - 80.0f);
		
		Label uiCreditsLabel = new Label(bundle.format("ui_credits", "www.kenney.nl"), m_uiSkin, "default");
		m_window.addActor(uiCreditsLabel);
		uiCreditsLabel.setPosition(15.0f, artCreditsLabel.getY() - 25.0f);
		
		Label musicCreditsLabel = new Label(bundle.format("music_credits", "estudiocafofo"), m_uiSkin, "default");
		m_window.addActor(musicCreditsLabel);
		musicCreditsLabel.setPosition(15.0f, uiCreditsLabel.getY() - 25.0f);
		
		Button backButton = new TextButton(bundle.get("back_button"), m_uiSkin, "default");
		m_window.addActor(backButton);
		backButton.setWidth(220.0f);
		backButton.setPosition(m_window.getWidth() / 2 - backButton.getWidth() / 2, 10.0f);
		backButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						dispose();
						m_game.setScreen(new StartScreen(m_game));
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
