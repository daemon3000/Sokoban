package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class PauseScreen extends Screen {
	private Array<ActionListener> m_resetLevelListeners;
	private Array<ActionListener> m_skipLevelListeners;
	private Array<ActionListener> m_quitGameListeners;
	private Stage m_stage;
	private Window m_window;
	private Sound m_click;
	private Vector2 m_screenSize;
	
	public PauseScreen(ScreenManager owner, SokobanGame game, Skin uiSkin, Sound click) {
		super(ScreenID.Pause, owner, false, true);
		m_resetLevelListeners = new Array<ActionListener>();
		m_skipLevelListeners = new Array<ActionListener>();
		m_quitGameListeners = new Array<ActionListener>();
		m_stage = new Stage(game.getPlatformAdapter().createViewport());
		m_click = click;
		m_screenSize = game.getPlatformAdapter().getVirtualScreenSize();
		
		createWidgets(uiSkin, game.getStringBundle());
	}
	
	private void createWidgets(Skin uiSkin, I18NBundle stringBundle) {
		m_window = new Window(stringBundle.get("pause_menu_title"), uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(250);
		m_window.setHeight(300);
		m_window.setPosition(m_screenSize.x + m_window.getWidth(), m_screenSize.y / 2 - m_window.getHeight() / 2);
		
		Button resumeButton = new TextButton(stringBundle.get("resume_button"), uiSkin, "default");
		m_window.addActor(resumeButton);
		resumeButton.setWidth(220);
		resumeButton.setPosition(m_window.getWidth() / 2 - resumeButton.getWidth() / 2, m_window.getHeight() / 2 + resumeButton.getHeight() + 5.0f);
		resumeButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						getOwner().popScreen();
					}
				}, 0.2f);
		    }
		});
		
		Button resetButton = new TextButton(stringBundle.get("reset_button"), uiSkin, "default");
		m_window.addActor(resetButton);
		resetButton.setWidth(220);
		resetButton.setPosition(m_window.getWidth() / 2 - resetButton.getWidth() / 2, resumeButton.getY() - resetButton.getHeight() - 10.0f);
		resetButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						for(ActionListener listener: m_resetLevelListeners) {
							listener.handle();
						}
						getOwner().popScreen();
					}
				}, 0.2f);
		    }
		});
		
		Button skipButton = new TextButton(stringBundle.get("skip_button"), uiSkin, "default");
		m_window.addActor(skipButton);
		skipButton.setWidth(220);
		skipButton.setPosition(m_window.getWidth() / 2 - skipButton.getWidth() / 2, resetButton.getY() - skipButton.getHeight() - 10.0f);
		skipButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						for(ActionListener listener: m_skipLevelListeners) {
							listener.handle();
						}
						getOwner().popScreen();
					}
				}, 0.2f);
		    }
		});
		
		Button quitButton = new TextButton(stringBundle.get("quit_button"), uiSkin, "default");
		m_window.addActor(quitButton);
		quitButton.setWidth(220);
		quitButton.setPosition(m_window.getWidth() / 2 - quitButton.getWidth() / 2, skipButton.getY() - quitButton.getHeight() - 10.0f);
		quitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						for(ActionListener listener: m_quitGameListeners) {
							listener.handle();
						}
						getOwner().popScreen();
					}
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
	
	public void addResetLevelListener(ActionListener listener) {
		m_resetLevelListeners.add(listener);
	}
	
	public void addSkipLevelListener(ActionListener listener) {
		m_skipLevelListeners.add(listener);
	}
	
	public void addQuitGameListener(ActionListener listener) {
		m_quitGameListeners.add(listener);
	}
	
	private void slideIn() {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(m_screenSize.x / 2 - m_window.getWidth() / 2, m_screenSize.y / 2 - m_window.getHeight() / 2);
		moveAction.setDuration(0.3f);
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
