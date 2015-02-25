package com.hotmail.daemon3000.Sokoban;

import com.badlogic.gdx.math.Vector2;

public abstract class Screen {
	private ScreenManager m_owner;
	private ScreenID m_id;
	private boolean m_updatePrevious;
	private boolean m_renderPrevious;
	
	public Screen(ScreenID id, ScreenManager owner, boolean updatePrevious, boolean renderPrevious) {
		m_owner = owner;
		m_id = id;
		m_updatePrevious = updatePrevious;
		m_renderPrevious = renderPrevious;
	}
	
	public ScreenManager getOwner() {
		return m_owner;
	}
	
	public ScreenID getID() {
		return m_id;
	}
	
	public abstract void onEnter();
	public abstract void onFocusEnter();
	public abstract void update(float delta);
	public abstract void render();
	public abstract void onFocusExit();
	public abstract void onExit();
	public abstract void resize(Vector2 screenSize, Vector2 virtualScreenSize);
	public abstract void dispose();
	
	public boolean getUpdatePrevious() {
		return m_updatePrevious;
	}
	
	public boolean getRenderPrevious() {
		return m_renderPrevious;
	}
}
