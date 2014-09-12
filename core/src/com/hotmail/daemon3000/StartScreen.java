package com.hotmail.daemon3000;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartScreen implements Screen {
	private Texture m_bodyTexture;
	private Texture m_handTexture;
	private Skin m_uiSkin;
	private Stage m_stage;
	private Game m_game;
	
	public StartScreen(Game game) {
		m_bodyTexture = new Texture(Gdx.files.internal("img/start_screen_player_body.png"));
		m_handTexture = new Texture(Gdx.files.internal("img/start_screen_player_hand.png"));
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		m_game = game;
		
		createLogo();
		createMenu();
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createLogo() {
		Image body = new Image(m_bodyTexture);
		Image hand = new Image(m_handTexture);
		hand.setOrigin(m_handTexture.getWidth() * 0.5f, m_handTexture.getHeight() * 0.94f);
		hand.setPosition(121.0f, 30.0f);
		hand.setRotation(150);
		
		RotateToAction rotateForward = new RotateToAction();
		rotateForward.setRotation(130.0f);
		rotateForward.setDuration(0.5f);
		
		RotateToAction rotateBack = new RotateToAction();
		rotateBack.setRotation(150.0f);
		rotateBack.setDuration(0.5f);
		
		RepeatAction handWave = new RepeatAction();
		handWave.setAction(new SequenceAction(rotateForward, rotateBack));
		handWave.setCount(RepeatAction.FOREVER);
		
		hand.addAction(handWave);
		
		Group logo = new Group();
		logo.addActor(body);
		logo.addActor(hand);
		logo.setPosition(Gdx.graphics.getWidth() / 2 - m_bodyTexture.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 25);
		
		m_stage.addActor(logo);
	}
	
	private void createMenu() {
		Button exitButton = new TextButton("Exit", m_uiSkin, "default");
		exitButton.setWidth(150.0f);
		exitButton.setHeight(50.0f);
		exitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
		    }
		});
		
		Button playButton = new TextButton("Play", m_uiSkin, "default");
		playButton.setWidth(150.0f);
		playButton.setHeight(50.0f);
		playButton.setPosition(0.0f, playButton.getHeight() + 5);
		playButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_game.setScreen(new GameScreen(m_game));
				dispose();
		    }
		});
		
		Group buttons = new Group();
		buttons.addActor(playButton);
		buttons.addActor(exitButton);
		buttons.setPosition(Gdx.graphics.getWidth() / 2 - exitButton.getWidth() / 2, 50);
		
		m_stage.addActor(buttons);
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
		m_bodyTexture.dispose();
		m_handTexture.dispose();
	}
}
