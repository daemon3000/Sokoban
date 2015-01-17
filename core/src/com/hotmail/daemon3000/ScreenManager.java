package com.hotmail.daemon3000;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ScreenManager {
	private HashMap<ScreenID, Screen> m_screens;
	private Screen m_currentScreen;
	
	public ScreenManager() {
		m_screens = new HashMap<ScreenID, Screen>();
		m_currentScreen = null;
	}
	
	public void addScreen(Screen screen) {
		if(!m_screens.containsKey(screen.getID()))
			m_screens.put(screen.getID(), screen);
	}
	
	public void removeScreen(Screen screen) {
		m_screens.remove(screen.getID());
	}
	
	public void changeScreen(ScreenID id) {
		if(m_currentScreen != null) {
			m_currentScreen.onExit();
			m_currentScreen = null;
		}
		
		m_currentScreen = m_screens.get(id);
		if(m_currentScreen != null) {
			m_currentScreen.onEnter();
		}
	}
	
	public void update(float delta) {
		if(m_currentScreen != null)
			m_currentScreen.update(delta);
	}
	
	public void render() {
		if(m_currentScreen != null)
			m_currentScreen.render();
	}
	
	public void resize(int width, int height) {
		Iterator<?> iter = m_screens.entrySet().iterator();
		while(iter.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, Screen> entry = (Map.Entry<String, Screen>)iter.next();
			entry.getValue().resize(width, height);
		}
	}
	
	public void dispose() {
		Iterator<?> iter = m_screens.entrySet().iterator();
		while(iter.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, Screen> entry = (Map.Entry<String, Screen>)iter.next();
			entry.getValue().dispose();
			iter.remove();
		}
	}
}
