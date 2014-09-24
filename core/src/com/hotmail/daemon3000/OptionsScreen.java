package com.hotmail.daemon3000;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class OptionsScreen implements Screen {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Label m_resolutionText;
	private CheckBox m_fullscreen;
	private Sound m_click;
	private SokobanGame m_game;
	private DisplayMode m_displayModes[];
	private int m_currentResolution;
	private int m_lastResolution;
	private boolean m_lastFullscreen;
	
	public OptionsScreen(SokobanGame game, boolean slideIn) {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_game = game;
		m_lastFullscreen = Gdx.graphics.isFullscreen();
		m_displayModes = Gdx.graphics.getDisplayModes().clone();
		sortDisplayModes();
		
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		m_currentResolution = 0;
		m_lastResolution = 0;
		for(int i = 0; i < m_displayModes.length; i++) {
			if(m_displayModes[i].width == screenWidth && m_displayModes[i].height == screenHeight) {
				m_currentResolution = i;
				m_lastResolution = i;
				break;
			}
		}
		
		createWidgets(slideIn);
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createWidgets(boolean slideIn) {
		I18NBundle bundle = m_game.getStringBundle();
		
		m_window = new Window(bundle.get("options_menu_title"), m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(350);
		m_window.setHeight(260);
		if(slideIn) {
			m_window.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), 
								 Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		}
		else {
			m_window.setPosition(Gdx.graphics.getWidth() / 2 - m_window.getWidth() / 2, 
					 			 Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		}
		
		String text = String.format("%d X %d", m_displayModes[m_currentResolution].width, 
											   m_displayModes[m_currentResolution].height);
		m_resolutionText = new Label(text, m_uiSkin, "default");
		
		Button cycleLeftButton = new ImageButton(m_uiSkin, "defaultSmall");
		cycleLeftButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleResolutionsLeft();
		    }
		});
		Image arrowLeft = new Image(m_uiSkin, "arrow_small");
		cycleLeftButton.addActor(arrowLeft);
		arrowLeft.setOrigin(arrowLeft.getWidth() / 2, arrowLeft.getHeight() / 2);
		arrowLeft.setPosition(cycleLeftButton.getWidth() / 2 - arrowLeft.getWidth() / 2, 
								cycleLeftButton.getHeight() / 2 - arrowLeft.getHeight() / 2);
		arrowLeft.setRotation(180.0f);
		
		Button cycleRightButton = new ImageButton(m_uiSkin, "defaultSmall");
		cycleRightButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleResolutionsRight();
		    }
		});
		Image arrowRight = new Image(m_uiSkin, "arrow_small");
		cycleRightButton.addActor(arrowRight);
		arrowRight.setOrigin(arrowRight.getWidth() / 2, arrowRight.getHeight() / 2);
		arrowRight.setPosition(cycleRightButton.getWidth() / 2 - arrowRight.getWidth() / 2, 
								cycleRightButton.getHeight() / 2 - arrowRight.getHeight() / 2);
		
		HorizontalGroup resolutionGroup = new HorizontalGroup();
		m_window.addActor(resolutionGroup);
		resolutionGroup.space(20.0f);
		resolutionGroup.addActor(new Label(bundle.get("resolution_label"), m_uiSkin, "default"));
		resolutionGroup.addActor(cycleLeftButton);
		resolutionGroup.addActor(m_resolutionText);
		resolutionGroup.addActor(cycleRightButton);
		resolutionGroup.pack();
		resolutionGroup.setPosition(15.0f, m_window.getHeight() - 70.0f);
		
		m_fullscreen = new CheckBox("", m_uiSkin, "default");
		m_fullscreen.setChecked(m_lastFullscreen);
		
		HorizontalGroup fullscreenGroup = new HorizontalGroup();
		m_window.addActor(fullscreenGroup);
		fullscreenGroup.space(10.0f);
		fullscreenGroup.addActor(new Label(bundle.get("fullscreen_label"), m_uiSkin, "default"));
		fullscreenGroup.addActor(m_fullscreen);
		fullscreenGroup.pack();
		fullscreenGroup.setPosition(15.0f, m_window.getHeight() - 125.0f);
		
		Button backButton = new TextButton(bundle.get("back_button"), m_uiSkin, "default");
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
						dispose();
						m_game.setScreen(new StartScreen(m_game));
					};
				}, 0.2f);
		    }
		});
		
		Button applyButton = new TextButton(bundle.get("apply_button"), m_uiSkin, "default");
		m_window.addActor(applyButton);
		applyButton.setWidth(150.0f);
		applyButton.setPosition(m_window.getWidth() - applyButton.getWidth() - 15.0f, 10.0f);
		applyButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				applyGraphicsChanges();
		    }
		});
		
		m_stage.addActor(m_window);
		if(slideIn) {
			slideIn();
		}
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
	
	private void applyGraphicsChanges() {
		if(m_currentResolution != m_lastResolution || m_fullscreen.isChecked() != m_lastFullscreen) {
			m_lastResolution = m_currentResolution;
			m_lastFullscreen = m_fullscreen.isChecked();
			
			DisplayMode mode = m_displayModes[m_currentResolution];
			Gdx.graphics.setDisplayMode(mode.width, mode.height, m_lastFullscreen);
			
			//	I reload the Options screen because the widgets get messed up
			//	when you change the resolution and I have no idea why it happens.
			dispose();
			m_game.setScreen(new OptionsScreen(m_game, false));
		}
	}
	
	private void cycleResolutionsRight() {
		if(m_currentResolution < m_displayModes.length - 1) {
			m_currentResolution++;
			
			String text = String.format("%d X %d", m_displayModes[m_currentResolution].width, 
					   							   m_displayModes[m_currentResolution].height);
			m_resolutionText.setText(text);
		}
	}
	
	private void cycleResolutionsLeft() {
		if(m_currentResolution > 0)
		{
			m_currentResolution--;
			
			String text = String.format("%d X %d", m_displayModes[m_currentResolution].width, 
					   m_displayModes[m_currentResolution].height);
			m_resolutionText.setText(text);
		}
	}
	
	/**
	 * Sorts the display modes in ascending order based on resolution.
	 */
	private void sortDisplayModes() {
		Arrays.sort(m_displayModes, new Comparator<DisplayMode>() {
			public int compare(DisplayMode a, DisplayMode b) {
				long sizeA = a.width * a.height;
				long sizeB = b.width * b.height;
				
				if(sizeA > sizeB) {
					return 1;
				}
				else if(sizeB > sizeA) {
					return -1;
				}
				
				return 0;
			}
		});
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
