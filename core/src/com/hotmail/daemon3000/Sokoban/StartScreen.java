package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class StartScreen extends Screen {
	private SokobanGame m_game;
	private Stage m_stage;
	private Window m_window;
	private Sound m_click;
	private Vector2 m_screenSize;
	
	public StartScreen(ScreenManager owner, SokobanGame game, Skin uiSkin, Sound click) {
		super(ScreenID.Start, owner, false, false);
		m_game = game;
		m_click = click;
		m_stage = new Stage(game.getPlatformAdapter().createViewport());
		m_screenSize = game.getPlatformAdapter().getVirtualScreenSize();
		
		createWidgets(uiSkin);
	}
	
	private void createWidgets(Skin uiSkin) {
		I18NBundle bundle = m_game.getStringBundle();
		
		m_window = new Window(bundle.get("main_menu_title"), uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(250);
		m_window.setHeight(360);
		m_window.setPosition(m_screenSize.x + m_window.getWidth(), m_screenSize.y / 2 - m_window.getHeight() / 2);
		
		Button playButton = new TextButton(bundle.get("play_button"), uiSkin, "default");
		m_window.addActor(playButton);
		playButton.setWidth(220.0f);
		playButton.setPosition(m_window.getWidth() / 2 - playButton.getWidth() / 2, m_window.getHeight() / 2 + playButton.getHeight() + 30.0f);
		playButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						getOwner().changeScreen(ScreenID.LevelSelect);
					};
				}, 0.2f);
		    }
		});
		
		Button helpButton = new TextButton(bundle.get("help_button"), uiSkin, "default");
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
						getOwner().changeScreen(ScreenID.Help);
					};
				}, 0.2f);
		    }
		});
		
		Button creditsButton = new TextButton(bundle.get("credits_button"), uiSkin, "default");
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
						getOwner().changeScreen(ScreenID.Credits);
					};
				}, 0.2f);
		    }
		});
		
		Button optionsButton = new TextButton(bundle.get("options_button"), uiSkin, "default");
		m_window.addActor(optionsButton);
		optionsButton.setWidth(220.0f);
		optionsButton.setPosition(m_window.getWidth() / 2 - optionsButton.getWidth() / 2, creditsButton.getY() - optionsButton.getHeight() - 10.0f);
		optionsButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						getOwner().changeScreen(ScreenID.Options);
					};
				}, 0.2f);
		    }
		});
		
		Button exitButton = new TextButton(bundle.get("exit_button"), uiSkin, "default");
		m_window.addActor(exitButton);
		exitButton.setWidth(220.0f);
		exitButton.setPosition(m_window.getWidth() / 2 - exitButton.getWidth() / 2, optionsButton.getY() - exitButton.getHeight() - 10.0f);
		exitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						m_game.exit();
					};
				}, 0.2f);
		    }
		});
		
		m_stage.addActor(m_window);
	}
	
	@Override
	public void onEnter() {
		slideIn();
		Gdx.input.setInputProcessor(m_stage);
	}

	@Override
	public void onFocusEnter() {
	}
	
	@Override
	public void update(float delta) {
		m_stage.act(delta);
	}
	
	@Override
	public void render() {
		m_stage.draw();
	}
	
	@Override
	public void onFocusExit() {
	}
	
	@Override
	public void onExit() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
		m_stage.getViewport().update((int)screenSize.x, (int)screenSize.y, true);
		m_screenSize = virtualScreenSize;
		m_window.setPosition(m_screenSize.x / 2 - m_window.getWidth() / 2, m_screenSize.y / 2 - m_window.getHeight() / 2);
	}
	
	private void slideIn() {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(m_screenSize.x / 2 - m_window.getWidth() / 2, m_screenSize.y / 2 - m_window.getHeight() / 2);
		moveAction.setDuration(0.4f);
		m_window.setPosition(m_screenSize.x + m_window.getWidth(), m_screenSize.y / 2 - m_window.getHeight() / 2);
		m_window.addAction(moveAction);
	}
	
	private void slideOut() {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(m_screenSize.x + m_window.getWidth(), m_screenSize.y / 2 - m_window.getHeight() / 2);
		moveAction.setDuration(0.2f);
		m_window.addAction(moveAction);
	}

	@Override
	public void dispose() {
		m_stage.dispose();
		m_click = null;
	}	
}
