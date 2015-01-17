package com.hotmail.daemon3000;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class LevelCompleteMenu {
	private Array<ActionListener> m_continueGameListeners;
	private Array<ActionListener> m_quitGameListeners;
	private Stage m_stage;
	private Window m_window;
	private Sound m_click;
	private I18NBundle m_stringBundle;
	private boolean m_isOpen = false;
	
	public LevelCompleteMenu(Skin uiSkin, Sound click, I18NBundle stringBundle) {
		m_stage = new Stage();
		m_click = click;
		m_stringBundle = stringBundle;
		m_continueGameListeners = new Array<ActionListener>();
		m_quitGameListeners = new Array<ActionListener>();
		
		createWidgets(uiSkin);
	}
	
	private void createWidgets(Skin uiSkin) {
		m_window = new Window(m_stringBundle.get("level_complete"), uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(250);
		m_window.setHeight(200);
		m_window.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		Button continueButton = new TextButton(m_stringBundle.get("continue_button"), uiSkin, "default");
		m_window.addActor(continueButton);
		continueButton.setWidth(220);
		continueButton.setPosition(m_window.getWidth() / 2 - continueButton.getWidth() / 2, m_window.getHeight() / 2 - 5.0f);
		continueButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				for(ActionListener listener: m_continueGameListeners) {
					close();
					listener.handle();
				}
		    }
		});
		
		Button quitButton = new TextButton(m_stringBundle.get("quit_button"), uiSkin, "default");
		m_window.addActor(quitButton);
		quitButton.setWidth(220);
		quitButton.setPosition(m_window.getWidth() / 2 - quitButton.getWidth() / 2, m_window.getHeight() / 2 - quitButton.getHeight() - 15.0f);
		quitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				for(ActionListener listener: m_quitGameListeners) {
					listener.handle();
				}
		    }
		});
		
		m_stage.addActor(m_window);
	}
	
	public void update(float delta) {
		m_stage.act(delta);
	}
	
	public void render() {
		m_stage.draw();
	}
	
	public boolean isOpen() {
		return m_isOpen;
	}
	
	public void open() {
		slideIn();
		m_isOpen = true;
		m_window.setVisible(true);
		Timer.schedule(new Task() {
			@Override
			public void run() {
				Gdx.input.setInputProcessor(m_stage);
			}
		}, 0.4f);
	}
	
	public void close() {
		slideOut();
		Timer.schedule(new Task() {
			@Override
			public void run() {
				m_isOpen = false;
				m_window.setVisible(false);
				Gdx.input.setInputProcessor(null);
			}
		}, 0.2f);
	}
	
	public void setNewRecord(boolean record) {
		if(record) {
			m_window.setTitle(m_stringBundle.get("new_level_record"));
		}
		else {
			m_window.setTitle(m_stringBundle.get("level_complete"));
		}
	}
	
	public void addContinueGameListener(ActionListener listener) {
		m_continueGameListeners.add(listener);
	}
	
	public void addQuitGameListener(ActionListener listener) {
		m_quitGameListeners.add(listener);
	}
	
	private void slideIn() {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(Gdx.graphics.getWidth() / 2 - m_window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		moveAction.setDuration(0.3f);
		m_window.addAction(moveAction);
	}
	
	private void slideOut() {
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		moveAction.setDuration(0.2f);
		m_window.addAction(moveAction);
	}
	
	public void dispose() {
		m_stage.dispose();
		m_click = null;
		m_stringBundle = null;
	}
}
