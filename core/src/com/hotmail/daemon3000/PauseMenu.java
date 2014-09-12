package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class PauseMenu {
	private Array<ActionListener> m_resetLevelListeners;
	private Array<ActionListener> m_skipLevelListeners;
	private Array<ActionListener> m_quitLevelListeners;
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private boolean m_isOpen = false;
	
	public PauseMenu() {
		m_resetLevelListeners = new Array<ActionListener>();
		m_skipLevelListeners = new Array<ActionListener>();
		m_quitLevelListeners = new Array<ActionListener>();
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		
		createWidgets();
	}
	
	private void createWidgets() {
		m_window = new Window("Pause Menu", m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setWidth(200);
		m_window.setHeight(200);
		m_window.setPosition(Gdx.graphics.getWidth() / 2 - m_window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		Button resumeButton = new TextButton("Resume", m_uiSkin, "default");
		m_window.addActor(resumeButton);
		resumeButton.setWidth(150);
		resumeButton.setHeight(30);
		resumeButton.setPosition(m_window.getWidth() / 2 - resumeButton.getWidth() / 2, m_window.getHeight() / 2 + 35);
		resumeButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				close();
		    }
		});
		
		Button resetButton = new TextButton("Reset", m_uiSkin, "default");
		m_window.addActor(resetButton);
		resetButton.setWidth(150);
		resetButton.setHeight(30);
		resetButton.setPosition(m_window.getWidth() / 2 - resetButton.getWidth() / 2, m_window.getHeight() / 2 - 5);
		resetButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				for(ActionListener listener: m_resetLevelListeners) {
					listener.handle();
				}
		    }
		});
		
		Button skipButton = new TextButton("Skip", m_uiSkin, "default");
		m_window.addActor(skipButton);
		skipButton.setWidth(150);
		skipButton.setHeight(30);
		skipButton.setPosition(m_window.getWidth() / 2 - skipButton.getWidth() / 2, m_window.getHeight() / 2 - 45);
		skipButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				for(ActionListener listener: m_skipLevelListeners) {
					listener.handle();
				}
		    }
		});
		
		Button quitButton = new TextButton("Quit", m_uiSkin, "default");
		m_window.addActor(quitButton);
		quitButton.setWidth(150);
		quitButton.setHeight(30);
		quitButton.setPosition(m_window.getWidth() / 2 - quitButton.getWidth() / 2, m_window.getHeight() / 2 - 85);
		quitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				for(ActionListener listener: m_quitLevelListeners) {
					listener.handle();
				}
		    }
		});
		
		m_stage.addActor(m_window);
	}
	
	public void act(float delta) {
		m_stage.act(delta);
	}
	
	public void render() {
		m_stage.draw();
	}
	
	public boolean isOpen() {
		return m_isOpen;
	}
	
	public void open() {
		m_isOpen = true;
		m_window.setVisible(true);
		Gdx.input.setInputProcessor(m_stage);
	}
	
	public void close() {
		m_isOpen = false;
		m_window.setVisible(false);
		Gdx.input.setInputProcessor(null);
	}
	
	public void addResetLevelListener(ActionListener listener) {
		m_resetLevelListeners.add(listener);
	}
	
	public void addSkipLevelListener(ActionListener listener) {
		m_skipLevelListeners.add(listener);
	}
	
	public void addQuitLevelListener(ActionListener listener) {
		m_quitLevelListeners.add(listener);
	}
	
	public void dispose() {
		m_window = null;
		m_uiSkin.dispose();
		m_stage.dispose();
	}
}
