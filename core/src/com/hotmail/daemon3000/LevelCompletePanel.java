package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class LevelCompletePanel {
	private Array<ActionListener> m_continueGameListeners;
	private Array<ActionListener> m_quitGameListeners;
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private boolean m_isOpen = false;
	
	public LevelCompletePanel(Skin uiSkin) {
		m_uiSkin = uiSkin;
		m_stage = new Stage();
		m_continueGameListeners = new Array<ActionListener>();
		m_quitGameListeners = new Array<ActionListener>();
		
		createWidgets();
	}
	
	private void createWidgets() {
		m_window = new Window("Level Complete!", m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setWidth(200);
		m_window.setHeight(150);
		m_window.setPosition(Gdx.graphics.getWidth() / 2 - m_window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		Button continueButton = new TextButton("Continue", m_uiSkin, "default");
		m_window.addActor(continueButton);
		continueButton.setWidth(150);
		continueButton.setHeight(30);
		continueButton.setPosition(m_window.getWidth() / 2 - continueButton.getWidth() / 2, m_window.getHeight() / 2 + 5);
		continueButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				for(ActionListener listener: m_continueGameListeners) {
					close();
					listener.handle();
				}
		    }
		});
		
		Button quitButton = new TextButton("Quit", m_uiSkin, "default");
		m_window.addActor(quitButton);
		quitButton.setWidth(150);
		quitButton.setHeight(30);
		quitButton.setPosition(m_window.getWidth() / 2 - quitButton.getWidth() / 2, m_window.getHeight() / 2 - 35);
		quitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				for(ActionListener listener: m_quitGameListeners) {
					listener.handle();
				}
		    }
		});
		
		m_stage.addActor(m_window);
	}
	
	public void render(float delta) {
		m_stage.act(delta);
		m_stage.draw();
	}
	
	public boolean isOpen() {
		return m_isOpen;
	}
	
	public void open() {
		m_isOpen = true;
		Timer.schedule(new Task() {
			@Override
			public void run() {
				m_window.setVisible(true);
				Gdx.input.setInputProcessor(m_stage);
			}
		}, 1.0f);
	}
	
	public void close() {
		m_isOpen = false;
		m_window.setVisible(false);
		Gdx.input.setInputProcessor(null);
	}
	
	public void addContinueGameListener(ActionListener listener) {
		m_continueGameListeners.add(listener);
	}
	
	public void addQuitGameListener(ActionListener listener) {
		m_quitGameListeners.add(listener);
	}
	
	public void dispose() {
		m_stage.dispose();
	}
}
