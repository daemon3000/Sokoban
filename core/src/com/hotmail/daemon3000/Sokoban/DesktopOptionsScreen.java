package com.hotmail.daemon3000.Sokoban;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class DesktopOptionsScreen extends Screen {
	private SokobanGame m_game;
	private Stage m_stage;
	private Window m_window;
	private Label m_resolutionText;
	private CheckBox m_fullscreen;
	private CheckBox m_musicToggle;
	private Sound m_click;
	private Vector2[] m_resolutions;
	private Vector2 m_screenSize;
	private int m_currentResolution;
	private int m_lastResolution;
	private boolean m_lastFullscreen;
	private boolean m_lastMusicToggleState;
	
	public DesktopOptionsScreen(ScreenManager owner, SokobanGame game,  Skin uiSkin, Sound click) {
		super(ScreenID.Options, owner, false, false);
		m_game = game;
		m_stage = new Stage(game.getPlatformAdapter().createViewport());
		m_screenSize = game.getPlatformAdapter().getVirtualScreenSize();
		m_click = click;
		m_resolutions = GetResolutions();
		m_lastFullscreen = Gdx.graphics.isFullscreen();
		m_lastMusicToggleState = game.isMusicPlaying();
		sortDisplayModes();
		
		findCurrentResolution();
		createWidgets(uiSkin);
	}
	
	private void findCurrentResolution() {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		m_currentResolution = 0;
		m_lastResolution = 0;
		for(int i = 0; i < m_resolutions.length; i++) {
			if((int)m_resolutions[i].x == screenWidth && (int)m_resolutions[i].y == screenHeight) {
				m_currentResolution = i;
				m_lastResolution = i;
				break;
			}
		}
	}
	
	private void createWidgets(Skin uiSkin) {
		I18NBundle bundle = m_game.getStringBundle();
		
		m_window = new Window(bundle.get("options_menu_title"), uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(400);
		m_window.setHeight(260);
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
		
		//	RESOLUTION
		m_resolutionText = new Label(getResolutionString(m_currentResolution), uiSkin, "default");
		
		Button cycleLeftButton = createArrowButton(uiSkin, true);
		cycleLeftButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleResolutionsLeft();
		    }
		});
		
		Button cycleRightButton = createArrowButton(uiSkin, false);
		cycleRightButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleResolutionsRight();
		    }
		});
		
		HorizontalGroup resolutionWidget = new HorizontalGroup();
		m_window.addActor(resolutionWidget);
		resolutionWidget.space(20.0f);
		resolutionWidget.addActor(cycleLeftButton);
		resolutionWidget.addActor(m_resolutionText);
		resolutionWidget.addActor(cycleRightButton);
		resolutionWidget.pack();
		
		//	FULLSCREEN
		m_fullscreen = new CheckBox("", uiSkin, "default");
		m_fullscreen.setChecked(m_lastFullscreen);
		
		//	MUSIC
		m_musicToggle = new CheckBox("", uiSkin, "default");
		m_musicToggle.setChecked(m_lastMusicToggleState);
		
		//	LAYOUT TABLE
		Table layoutTable = new Table();
		m_window.addActor(layoutTable);
		layoutTable.left().top();
		layoutTable.setWidth(220.0f);
		layoutTable.setHeight(150.0f);
		layoutTable.setPosition(20.0f, m_window.getHeight() - layoutTable.getHeight() - 45.0f);
		layoutTable.add(new Label(bundle.get("resolution_label"), uiSkin, "default")).align(Align.left).width(175.0f).height(40.0f);
		layoutTable.add(resolutionWidget);
		layoutTable.row();
		layoutTable.add(new Label(bundle.get("fullscreen_label"), uiSkin, "default")).align(Align.left).width(175.0f).height(40.0f);
		layoutTable.add(m_fullscreen).align(Align.center);
		layoutTable.row();
		layoutTable.add(new Label(bundle.get("music_toggle_label"), uiSkin, "default")).align(Align.left).width(175.0f).height(40.0f);
		layoutTable.add(m_musicToggle).align(Align.center);
		
		m_stage.addActor(m_window);
	}
	
	private Button createArrowButton(Skin uiSkin, boolean left) {
		Button button = new ImageButton(uiSkin, "defaultSmall");
		Image arrowLeft = new Image(uiSkin, "arrow_small");
		button.addActor(arrowLeft);
		arrowLeft.setOrigin(arrowLeft.getWidth() / 2, arrowLeft.getHeight() / 2);
		arrowLeft.setPosition(button.getWidth() / 2 - arrowLeft.getWidth() / 2, 
								button.getHeight() / 2 - arrowLeft.getHeight() / 2);
		if(left)
			arrowLeft.setRotation(180.0f);
		
		return button;
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
		if(m_currentResolution != m_lastResolution || m_fullscreen.isChecked() != m_lastFullscreen) {
			m_lastResolution = m_currentResolution;
			m_lastFullscreen = m_fullscreen.isChecked();
			
			Vector2 resolution = m_resolutions[m_currentResolution];
			Gdx.graphics.setDisplayMode((int)resolution.x, (int)resolution.y, m_lastFullscreen);
			Preferences prefs = m_game.getPreferences();
			prefs.putInteger("window_width", (int)resolution.x);
			prefs.putInteger("window_height", (int)resolution.y);
			prefs.putBoolean("fullscreen", m_lastFullscreen);
		}
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
	
	private void cycleResolutionsRight() {
		if(m_currentResolution < m_resolutions.length - 1) {
			m_currentResolution++;
			m_resolutionText.setText(getResolutionString(m_currentResolution));
		}
	}
	
	private void cycleResolutionsLeft() {
		if(m_currentResolution > 0) {
			m_currentResolution--;
			m_resolutionText.setText(getResolutionString(m_currentResolution));
		}
	}
	
	private String getResolutionString(int currentResolution) {
		String text = String.format("%d X %d", (int)m_resolutions[currentResolution].x, 
											   (int)m_resolutions[currentResolution].y);
		
		return text;
	}
	
	private Vector2[] GetResolutions() {
		DisplayMode[] modes = Gdx.graphics.getDisplayModes();
		Array<Vector2> resolutions = new Array<Vector2>();
		boolean duplicate = false;

		for(DisplayMode mode : modes) {
			duplicate = false;
			for(int i = 0; i < resolutions.size; i++) {
				if(mode.width == (int)resolutions.get(i).x && mode.height == (int)resolutions.get(i).y) {
					duplicate = true;
					break;
				}
			}
			
			if(!duplicate)
				resolutions.add(new Vector2(mode.width, mode.height));
		}
		
		return resolutions.toArray(Vector2.class);
	}
	
	/**
	 * Sorts the display modes in ascending order based on resolution.
	 */
	private void sortDisplayModes() {
		Arrays.sort(m_resolutions, new Comparator<Vector2>() {
			public int compare(Vector2 a, Vector2 b) {
				long sizeA = (long)a.x * (long)a.y;
				long sizeB = (long)b.x * (long)b.y;
				
				return ((Long)sizeA).compareTo(sizeB);
			}
		});
	}
	
	@Override
	public void onEnter() {
		findCurrentResolution();
		m_resolutionText.setText(getResolutionString(m_currentResolution));
		
		m_lastFullscreen = Gdx.graphics.isFullscreen();
		m_fullscreen.setChecked(m_lastFullscreen);
		
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
