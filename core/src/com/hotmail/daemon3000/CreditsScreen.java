package com.hotmail.daemon3000;

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

public class CreditsScreen extends Screen {
	private Stage m_stage;
	private Window m_window;
	private Sound m_click;
	private Vector2 m_screenSize;
	
	public CreditsScreen(ScreenManager owner, SokobanGame game,  Skin uiSkin, Sound click) {
		super(ScreenID.Credits, owner);
		m_click = click;
		m_stage = new Stage(game.getPlatformSettings().createViewport());
		m_screenSize = game.getPlatformSettings().getVirtualScreenSize();
		
		createWidgets(game, uiSkin);
	}
	
	private void createWidgets(SokobanGame game, Skin uiSkin) {
		I18NBundle bundle = game.getStringBundle();
		
		m_window = new Window(bundle.get("credits_menu_title"), uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(300);
		m_window.setHeight(260);
		m_window.setPosition(m_screenSize.x + m_window.getWidth(), m_screenSize.y / 2 - m_window.getHeight() / 2);
		
		Label artCreditsLabel = new Label(bundle.format("tiles_credits", "1001.com"), uiSkin, "default");
		m_window.addActor(artCreditsLabel);
		artCreditsLabel.setPosition(15.0f, m_window.getHeight() - 80.0f);
		
		Label uiCreditsLabel = new Label(bundle.format("ui_credits", "www.kenney.nl"), uiSkin, "default");
		m_window.addActor(uiCreditsLabel);
		uiCreditsLabel.setPosition(15.0f, artCreditsLabel.getY() - 25.0f);
		
		Label musicCreditsLabel = new Label(bundle.format("music_credits", "estudiocafofo"), uiSkin, "default");
		m_window.addActor(musicCreditsLabel);
		musicCreditsLabel.setPosition(15.0f, uiCreditsLabel.getY() - 25.0f);
		
		Button backButton = new TextButton(bundle.get("back_button"), uiSkin, "default");
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
						getOwner().changeScreen(ScreenID.Start);
					};
				}, 0.2f);
		    }
		});
		
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
	
	@Override
	public void onEnter() {
		slideIn();
		Gdx.input.setInputProcessor(m_stage);
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
	}	
}
