package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class AndroidOptionsScreen extends Screen {
	private SokobanGame m_game;
	private Stage m_stage;
	private Window m_window;
	private CheckBox m_musicToggle;
	private Sound m_click;
	private Vector2 m_screenSize;
	private boolean m_lastMusicToggleState;
	
	public AndroidOptionsScreen(ScreenManager owner, SokobanGame game,  Skin uiSkin, Sound click) {
		super(ScreenID.Options, owner, false, false);
		m_game = game;
		m_stage = new Stage(game.getPlatformAdapter().createViewport());
		m_screenSize = game.getPlatformAdapter().getVirtualScreenSize();
		m_click = click;

		createWidgets(uiSkin);
	}
	
	private void createWidgets(Skin uiSkin) {
		I18NBundle bundle = m_game.getStringBundle();
		
		m_window = new Window(bundle.get("options_menu_title"), uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(400);
		m_window.setHeight(220);
		m_window.setPosition(m_screenSize.x + m_window.getWidth(), m_screenSize.y / 2 - m_window.getHeight() / 2);
		
		Button backButton = new TextButton(bundle.get("back_button"), uiSkin, "default");
		m_window.addActor(backButton);
		backButton.setWidth(150.0f);
		backButton.setPosition(15.0f, 10.0f);
		backButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						getOwner().changeScreen(ScreenID.Start);
					};
				}, 0.2f);
		    }
		});
		
		Button applyButton = new TextButton(bundle.get("apply_button"), uiSkin, "default");
		m_window.addActor(applyButton);
		applyButton.setWidth(150.0f);
		applyButton.setPosition(m_window.getWidth() - applyButton.getWidth() - 15.0f, 10.0f);
		applyButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				applyChanges();
		    }
		});
		
		//	MUSIC
		m_musicToggle = new CheckBox("", uiSkin, "default");
		m_musicToggle.setChecked(m_lastMusicToggleState);
		
		//	LAYOUT TABLE
		Table layoutTable = new Table();
		m_window.addActor(layoutTable);
		layoutTable.left().top();
		layoutTable.setWidth(m_window.getWidth() - 30.0f);
		layoutTable.setHeight(150.0f);
		layoutTable.setPosition(15.0f, m_window.getHeight() - layoutTable.getHeight() - 45.0f);
		layoutTable.row();
		layoutTable.add(new Label(bundle.get("music_toggle_label"), uiSkin, "default")).align(Align.left).width(m_window.getWidth() - applyButton.getWidth() - 30.0f).height(40.0f);
		layoutTable.add(m_musicToggle).align(Align.left);
		
		m_stage.addActor(m_window);
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
	
	private void applyChanges() {
		if(m_musicToggle.isChecked() != m_lastMusicToggleState) {
			m_lastMusicToggleState = m_musicToggle.isChecked();
			if(m_lastMusicToggleState) 
				m_game.playMusic();
			else
				m_game.stopMusic();
			
			Preferences prefs = m_game.getPreferences();
			prefs.putBoolean("music_on", m_lastMusicToggleState);
		}
	}
	
	@Override
	public void onEnter() {
		m_lastMusicToggleState = m_game.isMusicPlaying();
		m_musicToggle.setChecked(m_lastMusicToggleState);
		
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

	@Override
	public void dispose() {
		m_stage.dispose();
		m_click = null;
		m_game = null;
	}
}
