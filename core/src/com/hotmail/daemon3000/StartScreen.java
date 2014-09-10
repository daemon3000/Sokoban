package com.hotmail.daemon3000;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

public class StartScreen implements Screen {
	private Texture m_bodyTexture;
	private Texture m_handTexture;
	private Texture m_playButtonUpTexture;
	private Texture m_exitButtonUpTexture;
	private Stage m_stage;
	private Game m_game;
	
	public StartScreen(Game game) {
		m_bodyTexture = new Texture(Gdx.files.internal("img/start_screen_player_body.png"));
		m_handTexture = new Texture(Gdx.files.internal("img/start_screen_player_hand.png"));
		m_playButtonUpTexture = new Texture(Gdx.files.internal("ui/img/play_button_up.png"));
		m_exitButtonUpTexture = new Texture(Gdx.files.internal("ui/img/exit_button_up.png"));
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
		ImageButton playButton = new ImageButton(new SpriteDrawable(new Sprite(m_playButtonUpTexture)));
		playButton.setPosition(0.0f, m_exitButtonUpTexture.getHeight() + 5);
		playButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				m_game.setScreen(new GameScreen());
				return true;
			}
		});
		
		ImageButton exitButton = new ImageButton(new SpriteDrawable(new Sprite(m_exitButtonUpTexture)));
		exitButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}
		});
		
		Group buttons = new Group();
		buttons.addActor(playButton);
		buttons.addActor(exitButton);
		buttons.setPosition(Gdx.graphics.getWidth() / 2 - m_playButtonUpTexture.getWidth() / 2, 50);
		
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
		m_bodyTexture.dispose();
		m_handTexture.dispose();
		m_playButtonUpTexture.dispose();
		m_exitButtonUpTexture.dispose();
	}
}
