package com.hotmail.daemon3000;

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
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class OptionsScreen extends Screen {
	private SokobanGame m_game;
	private Stage m_stage;
	private Window m_window;
	private Label m_resolutionText;
	private CheckBox m_fullscreen;
	private Sound m_click;
	private DisplayMode m_displayModes[];
	private Vector2 m_screenSize;
	private int m_currentResolution;
	private int m_lastResolution;
	private boolean m_lastFullscreen;
	
	public OptionsScreen(ScreenManager owner, SokobanGame game,  Skin uiSkin, Sound click) {
		super(ScreenID.Options, owner);
		m_game = game;
		m_stage = new Stage(game.getPlatformSettings().createViewport());
		m_screenSize = game.getPlatformSettings().getVirtualScreenSize();
		m_click = click;
		m_displayModes = Gdx.graphics.getDisplayModes().clone();
		m_lastFullscreen = Gdx.graphics.isFullscreen();
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
		
		createWidgets(uiSkin);
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void createWidgets(Skin uiSkin) {
		I18NBundle bundle = m_game.getStringBundle();
		
		m_window = new Window(bundle.get("options_menu_title"), uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(350);
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
				applyGraphicsChanges();
		    }
		});
		
		//	RESOLUTION
		String text = String.format("%d X %d", m_displayModes[m_currentResolution].width, 
											   m_displayModes[m_currentResolution].height);
		m_resolutionText = new Label(text, uiSkin, "default");
		
		Button cycleLeftButton = new ImageButton(uiSkin, "defaultSmall");
		cycleLeftButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleResolutionsLeft();
		    }
		});
		Image arrowLeft = new Image(uiSkin, "arrow_small");
		cycleLeftButton.addActor(arrowLeft);
		arrowLeft.setOrigin(arrowLeft.getWidth() / 2, arrowLeft.getHeight() / 2);
		arrowLeft.setPosition(cycleLeftButton.getWidth() / 2 - arrowLeft.getWidth() / 2, 
								cycleLeftButton.getHeight() / 2 - arrowLeft.getHeight() / 2);
		arrowLeft.setRotation(180.0f);
		
		Button cycleRightButton = new ImageButton(uiSkin, "defaultSmall");
		cycleRightButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleResolutionsRight();
		    }
		});
		Image arrowRight = new Image(uiSkin, "arrow_small");
		cycleRightButton.addActor(arrowRight);
		arrowRight.setOrigin(arrowRight.getWidth() / 2, arrowRight.getHeight() / 2);
		arrowRight.setPosition(cycleRightButton.getWidth() / 2 - arrowRight.getWidth() / 2, 
								cycleRightButton.getHeight() / 2 - arrowRight.getHeight() / 2);
		
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
		
		//	LAYOUT TABLE
		Table layoutTable = new Table();
		m_window.addActor(layoutTable);
		layoutTable.left().top();
		layoutTable.setWidth(220.0f);
		layoutTable.setHeight(150.0f);
		layoutTable.setPosition(20.0f, m_window.getHeight() - layoutTable.getHeight() - 45.0f);
		layoutTable.add(new Label(bundle.get("resolution_label"), uiSkin, "default")).align(Align.left).width(125.0f).height(40.0f);
		layoutTable.add(resolutionWidget);
		layoutTable.row();
		layoutTable.add(new Label(bundle.get("fullscreen_label"), uiSkin, "default")).align(Align.left).width(125.0f).height(40.0f);
		layoutTable.add(m_fullscreen).align(Align.left);
		
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
	
	private void applyGraphicsChanges() {
		if(m_currentResolution != m_lastResolution || m_fullscreen.isChecked() != m_lastFullscreen) {
			m_lastResolution = m_currentResolution;
			m_lastFullscreen = m_fullscreen.isChecked();
			
			DisplayMode mode = m_displayModes[m_currentResolution];
			m_game.getPlatformSettings().changeResolution(mode.width, mode.height, m_lastFullscreen);
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
				long sizeA = (long)a.width * (long)a.height;
				long sizeB = (long)b.width * (long)b.height;
				
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
		m_game = null;
	}
}
