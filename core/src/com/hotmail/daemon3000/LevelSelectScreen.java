package com.hotmail.daemon3000;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer.Task;

public class LevelSelectScreen extends Screen {
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
	private I18NBundle m_stringBundle;
	private Vector2 m_screenSize;
	private int m_currentLevelPack = 0;
	
	public LevelSelectScreen(ScreenManager owner, SokobanGame game, Skin uiSkin, Sound click) {
		super(ScreenID.LevelSelect, owner);
		m_stage = new Stage(game.getPlatformSettings().createViewport());
		m_click = click;
		m_levelPacks = new Array<LevelPackData>();
		m_game = game;
		m_stringBundle = game.getStringBundle();
		m_screenSize = game.getPlatformSettings().getVirtualScreenSize();
		
		loadLevelPackList(Gdx.files.internal("levels/index.json"), true);
		if(m_game.getPlatformSettings().allowsAddonLevels()) {
			loadLevelPackList(m_game.getPlatformSettings().getAddonLevelPath(), false);
		}
		createWidgets(uiSkin);
	}
	
	private void loadLevelPackList(FileHandle fileHandle, boolean internal) {
		if(!fileHandle.exists())
			return;
		
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(fileHandle);
		String name = null, id = null, file = null, author = null;
		int levelCount = 0, difficulty = -1;
				
		for(JsonValue entry = root.child; entry != null; entry = entry.next) {
			name = entry.getString("name", null);
			id = entry.getString("id", null);
			file = entry.getString("file", null);
			author = entry.getString("author", m_stringBundle.get("unknown"));
			levelCount = entry.getInt("levelCount", 0);
			difficulty = entry.getInt("difficulty", -1);
			if(name != null && id != null && file != null && levelCount > 0) {
				m_levelPacks.add(new LevelPackData(name, id, file, author, levelCount, difficulty, internal));
			}
		}
	}
	
	private void createWidgets(Skin m_uiSkin) {
		m_window = new Window(m_stringBundle.get("select_level_title"), m_uiSkin, "default");
		m_window.setMovable(false);
		m_window.setKeepWithinStage(false);
		m_window.setWidth(300);
		m_window.setHeight(325);
		m_window.setPosition(m_screenSize.x + m_window.getWidth(), m_screenSize.y / 2 - m_window.getHeight() / 2);
		
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
		startAtLabel.setPosition(15.0f,m_levelPackDifficulty.getY() - 32.0f);
		
		m_startLevel = new TextField("1", m_uiSkin, "default");
		m_window.addActor(m_startLevel);
		m_startLevel.setWidth(175);
		m_startLevel.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		m_startLevel.setPosition(110.0f, m_levelPackDifficulty.getY() - 40.0f);
		
		
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
						getOwner().changeScreen(ScreenID.Start);
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
							FileHandle fileHandle = levelPackData.isInternal ? Gdx.files.internal(levelPackData.file) :
																				Gdx.files.local(levelPackData.file);
																			
							m_game.setScene(new GameScene(m_game, levelPackData.id, fileHandle, parseStartLevel()));
						}
					}, 0.2f);
				}
		    }
		});
		
		Button cycleLeftButton = new ImageButton(m_uiSkin, "default");
		m_window.addActor(cycleLeftButton);
		cycleLeftButton.setPosition(15.0f, cancelButton.getX() + cycleLeftButton.getHeight() + 10.0f);
		cycleLeftButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleLevelPacksLeft();
		    }
		});
		Image arrowLeft = new Image(m_uiSkin, "arrow");
		cycleLeftButton.addActor(arrowLeft);
		arrowLeft.setOrigin(arrowLeft.getWidth() / 2, arrowLeft.getHeight() / 2);
		arrowLeft.setPosition(cycleLeftButton.getWidth() / 2 - arrowLeft.getWidth() / 2, 
								cycleLeftButton.getHeight() / 2 - arrowLeft.getHeight() / 2);
		arrowLeft.setRotation(180.0f);
		
		Button cycleRightButton = new ImageButton(m_uiSkin, "default");
		m_window.addActor(cycleRightButton);
		cycleRightButton.setPosition(m_window.getWidth() - 65.0f, cancelButton.getX() + cycleRightButton.getHeight() + 10.0f);
		cycleRightButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				m_click.play();
				cycleLevelPacksRight();
		    }
		});
		Image arrowRight = new Image(m_uiSkin, "arrow");
		cycleRightButton.addActor(arrowRight);
		arrowRight.setOrigin(arrowRight.getWidth() / 2, arrowRight.getHeight() / 2);
		arrowRight.setPosition(cycleRightButton.getWidth() / 2 - arrowRight.getWidth() / 2, 
								cycleRightButton.getHeight() / 2 - arrowRight.getHeight() / 2);
		
		m_stage.addActor(m_window);
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
		m_game = null;
		m_stringBundle = null;
	}
}
