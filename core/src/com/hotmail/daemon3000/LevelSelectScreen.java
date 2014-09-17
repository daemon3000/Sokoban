package com.hotmail.daemon3000;

import java.io.IOException;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer.Task;

public class LevelSelectScreen implements Screen {
	private Skin m_uiSkin;
	private Stage m_stage;
	private Window m_window;
	private Label m_levelPackName;
	private Label m_levelPackSize;
	private Label m_levelPackAuthor;
	private Label m_levelPackDifficulty;
	private TextField m_startLevel;
	private Sound m_click;
	private Array<LevelPackData> m_levelPacks;
	private SokobanGame m_game;
	I18NBundle m_stringBundle;
	private int m_currentLevelPack = 0;
	
	public LevelSelectScreen(SokobanGame game) {
		m_uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		m_stage = new Stage();
		m_click = Gdx.audio.newSound(Gdx.files.internal("audio/click.ogg"));
		m_levelPacks = new Array<LevelPackData>();
		m_game = game;
		m_stringBundle = m_game.getStringBundle();
		
		loadInternalLevelPackList();
		loadAddonLevelPackList();
		createWidgets();
		Gdx.input.setInputProcessor(m_stage);
	}
	
	private void loadInternalLevelPackList() {
		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(Gdx.files.internal("levels/index.xml"));
			int childCount = root.getChildCount();
			
			for(int i = 0; i < childCount; i++) {
				XmlReader.Element elem = root.getChild(i);
				m_levelPacks.add(new LevelPackData(elem.getAttribute("name"), elem.getAttribute("id"), 
								 				   elem.getAttribute("file"), elem.getAttribute("author"), 
								 				   elem.getIntAttribute("levelCount"), elem.getIntAttribute("difficulty"), false));
			}
		}
		catch(GdxRuntimeException re) {
			m_levelPacks.clear();
		}
		catch(IOException ioe) {
			m_levelPacks.clear();
		}
	}
	
	private void loadAddonLevelPackList() {
		FileHandle fileHandle = Gdx.files.local("addons/levels/index.xml");
		if(!fileHandle.exists())
			return;
		
		Array<LevelPackData> levelPacks = new Array<LevelPackData>();
		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(fileHandle);
			int childCount = root.getChildCount();
			
			for(int i = 0; i < childCount; i++) {
				XmlReader.Element elem = root.getChild(i);
				levelPacks.add(new LevelPackData(elem.getAttribute("name"), elem.getAttribute("id"), 
								 				 elem.getAttribute("file"), elem.getAttribute("author"), 
								 				 elem.getIntAttribute("levelCount"), elem.getIntAttribute("difficulty"), true));
			}
		}
		catch(GdxRuntimeException re) {
			levelPacks.clear();
		}
		catch(IOException ioe) {
			levelPacks.clear();
		}
		
		if(levelPacks.size > 0)
			m_levelPacks.addAll(levelPacks);
	}
	
	private void createWidgets() {
		m_window = new Window(m_stringBundle.get("select_level_title"), m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(300);
		m_window.setHeight(350);
		m_window.setPosition(Gdx.graphics.getWidth() + m_window.getWidth(), Gdx.graphics.getHeight() / 2 - m_window.getHeight() / 2);
		
		String packName = m_levelPacks.size > 0 ? m_levelPacks.get(0).name : m_stringBundle.get("unknown");
		int packSize = m_levelPacks.size > 0 ? m_levelPacks.get(0).levelCount : 0;
		String packAuthor = m_levelPacks.size > 0 ? m_levelPacks.get(0).author : m_stringBundle.get("unknown");
		String packDifficulty = m_levelPacks.size > 0 ? convertLevelPackDifficulty(m_levelPacks.get(0).difficulty) : m_stringBundle.get("unknown");
		
		m_levelPackName = new Label(m_stringBundle.format("level_pack_name", packName), m_uiSkin, "default");
		m_window.addActor(m_levelPackName);
		m_levelPackName.setPosition(15.0f, m_window.getHeight() - 70.0f);
		
		m_levelPackSize = new Label(m_stringBundle.format("level_pack_size", packSize), m_uiSkin, "default");
		m_window.addActor(m_levelPackSize);
		m_levelPackSize.setPosition(15.0f, m_levelPackName.getY() - 25.0f);
		
		m_levelPackAuthor = new Label(m_stringBundle.format("level_pack_author", packAuthor), m_uiSkin, "default");
		m_window.addActor(m_levelPackAuthor);
		m_levelPackAuthor.setPosition(15.0f, m_levelPackSize.getY() - 25.0f);
		
		m_levelPackDifficulty = new Label(m_stringBundle.format("level_pack_difficulty", packDifficulty), m_uiSkin, "default");
		m_window.addActor(m_levelPackDifficulty);
		m_levelPackDifficulty.setPosition(15.0f, m_levelPackAuthor.getY() - 25.0f);
		
		Label startAtLabel = new Label(m_stringBundle.get("level_pack_start_at"), m_uiSkin, "default");
		m_window.addActor(startAtLabel);
		startAtLabel.setWidth(90);
		startAtLabel.setPosition(15.0f,m_levelPackDifficulty.getY() - 45.0f);
		
		m_startLevel = new TextField("1", m_uiSkin, "default");
		m_window.addActor(m_startLevel);
		m_startLevel.setWidth(175);
		m_startLevel.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		m_startLevel.setPosition(110.0f, m_levelPackDifficulty.getY() - 60.0f);
		
		
		Button cancelButton = new TextButton(m_stringBundle.get("cancel_button"), m_uiSkin, "default");
		m_window.addActor(cancelButton);
		cancelButton.setWidth(m_window.getWidth() - 20.0f);
		cancelButton.setPosition(10.0f, 10.0f);
		cancelButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				slideOut();
				Timer.schedule(new Task() {
					@Override
					public void run() {
						m_game.setScreen(new StartScreen(m_game));
						dispose();
					};
				}, 0.2f);
		    }
		});
		
		Button startGameButton = new TextButton(m_stringBundle.get("play_button"), m_uiSkin, "default");
		m_window.addActor(startGameButton);
		startGameButton.setWidth(160.0f);
		startGameButton.setPosition(70.0f, cancelButton.getX() + startGameButton.getHeight() + 10.0f);
		startGameButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if(m_levelPacks.size > 0) {
					m_click.play();
					slideOut();
					Timer.schedule(new Task() {
						@Override
						public void run() {
							LevelPackData levelPackData = m_levelPacks.get(m_currentLevelPack);
							FileHandle fileHandle = levelPackData.isAddon ? Gdx.files.local(levelPackData.file) :
																			Gdx.files.internal(levelPackData.file);
							m_game.setScreen(new GameScreen(m_game, levelPackData.id, fileHandle, parseStartLevel()));
							dispose();
						}
					}, 0.2f);
				}
		    }
		});
		
		Button cycleLeftButton = new TextButton("<", m_uiSkin, "default");
		m_window.addActor(cycleLeftButton);
		cycleLeftButton.setPosition(15.0f, cancelButton.getX() + cycleLeftButton.getHeight() + 10.0f);
		cycleLeftButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleLevelPacksLeft();
		    }
		});
		
		Button cycleRightButton = new TextButton(">", m_uiSkin, "default");
		m_window.addActor(cycleRightButton);
		cycleRightButton.setPosition(m_window.getWidth() - 65.0f, cancelButton.getX() + cycleRightButton.getHeight() + 10.0f);
		cycleRightButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleLevelPacksRight();
		    }
		});
		
		m_stage.addActor(m_window);
		slideIn();
	}
	
	private void cycleLevelPacksLeft() {
		if(m_currentLevelPack > 0) {
			m_currentLevelPack--;
			resetPackInfo();
		}
	}
	
	private void cycleLevelPacksRight() {
		if(m_currentLevelPack < m_levelPacks.size - 1) {
			m_currentLevelPack++;
			resetPackInfo();
		}
	}
	
	private void resetPackInfo() {
		I18NBundle bundle = m_game.getStringBundle();
		m_levelPackName.setText(bundle.format("level_pack_name", m_levelPacks.get(m_currentLevelPack).name));
		m_levelPackSize.setText(bundle.format("level_pack_size", m_levelPacks.get(m_currentLevelPack).levelCount));
		m_levelPackAuthor.setText(bundle.format("level_pack_author", m_levelPacks.get(m_currentLevelPack).author));
		m_levelPackDifficulty.setText(bundle.format("level_pack_difficulty", convertLevelPackDifficulty(m_levelPacks.get(m_currentLevelPack).difficulty)));
	}
	
	private int parseStartLevel() {
		try {
			int number = Integer.parseInt(m_startLevel.getText());
			return MathUtils.clamp(number - 1, 0, m_levelPacks.get(m_currentLevelPack).levelCount - 1);
		}
		catch(NumberFormatException ex) {
			return 0;
		}
	}
	
	private String convertLevelPackDifficulty(int difficulty) {
		if(difficulty == LevelPackData.DIFFICULTY_EASY)
			return m_stringBundle.get("difficulty_easy");
		else if(difficulty == LevelPackData.DIFFICULTY_NORMAL)
			return m_stringBundle.get("difficulty_normal");
		else if(difficulty == LevelPackData.DIFFICULTY_HARD)
			return m_stringBundle.get("difficulty_hard");
		else
			return m_stringBundle.get("unknown");
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
