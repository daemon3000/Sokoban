package com.hotmail.daemon3000;

public abstract class Screen {
	private ScreenManager m_owner;
	private ScreenID m_id;
	
	public Screen(ScreenID id, ScreenManager owner) {
		m_owner = owner;
		m_id = id;
	}
	
	public ScreenManager getOwner() {
		return m_owner;
	}
	
	public ScreenID getID() {
		return m_id;
	}
	
	public abstract void onEnter();
	public abstract void update(float delta);
	public abstract void render();
	public abstract void onExit();
	public abstract void resize(int width, int height);
	public abstract void dispose();
}
