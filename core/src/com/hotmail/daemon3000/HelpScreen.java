package com.hotmail.daemon3000;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class HelpScreen implements Screen {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Sound m_click;
	private SokobanGame m_game;
	
	public HelpScreen(SokobanGame game) {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_game = game;
		
		createWidgets();
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createWidgets() {
		m_window = new Window("How To Play", m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(260);
		m_window.setHeight(260);
		m_window.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		Label moveLabel = new Label("Move: Up, Down, Left, Right", m_uiSkin, "default");
		m_window.addActor(moveLabel);
		moveLabel.setPosition(15.0f, m_window.getHeight() - 80.0f);
		
		Label undoLabel = new Label("Undo: Z", m_uiSkin, "default");
		m_window.addActor(undoLabel);
		undoLabel.setPosition(15.0f, moveLabel.getY() - 25.0f);
		
		Button backButton = new TextButton("Back", m_uiSkin, "default");
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