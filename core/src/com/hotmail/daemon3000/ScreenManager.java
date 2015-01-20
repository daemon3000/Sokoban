package com.hotmail.daemon3000;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ScreenManager {
	private HashMap<ScreenID, Screen> m_screens;
	private Array<Screen> m_screenStack;
	private int m_firstScreenToUpdate;
	private int m_firstScreenToRender;
	
	public ScreenManager() {
		m_screens = new HashMap<ScreenID, Screen>();
		m_screenStack = new Array<Screen>();
	}
	
	public void addScreen(Screen screen) {
		if(!m_screens.containsKey(screen.getID()))
			m_screens.put(screen.getID(), screen);
	}
	
	public Screen getScreen(ScreenID id) {
		return m_screens.get(id);
	}
	
	public void changeScreen(ScreenID id) {
		Iterator<?> iter = m_screenStack.iterator();
		while(iter.hasNext()) {
			Screen screen = (Screen)iter.next();
			screen.onExit();
			iter.remove();
		}
		
		Screen screen = m_screens.get(id);
		if(screen != null)
		{
			m_screenStack.add(screen);
			m_firstScreenToUpdate = 0;
			m_firstScreenToRender = 0;
			screen.onEnter();
			screen.onFocusEnter();
		}
	}
	
	public void pushScreen(ScreenID id) {
		Screen screen = m_screens.get(id);
		if(screen != null)
		{
			for(int i = m_firstScreenToUpdate; i < m_screenStack.size; i++)
				m_screenStack.get(i).onFocusExit();
			
			m_screenStack.add(screen);
			findFirstScreenToUpdate();
			findFirstScreenToRender();
			screen.onEnter();
			screen.onFocusEnter();
		}
	}
	
	public void popScreen() {
		if(m_screenStack.size > 0)
		{
			Screen screen = m_screenStack.pop();
			screen.onExit();
			findFirstScreenToUpdate();
			findFirstScreenToRender();
			
			for(int i = m_firstScreenToUpdate; i < m_screenStack.size; i++)
				m_screenStack.get(i).onFocusEnter();
		}
	}
	
	private void findFirstScreenToUpdate() {
		m_firstScreenToUpdate = 0;
		for(int i = m_screenStack.size - 1; i >= 0; i--)
		{
			if(!m_screenStack.get(i).getUpdatePrevious())
			{
				m_firstScreenToUpdate = i;
				break;
			}
		}
	}
	
	private void findFirstScreenToRender() {
		m_firstScreenToRender = 0;
		for(int i = m_screenStack.size - 1; i >= 0; i--)
		{
			if(!m_screenStack.get(i).getRenderPrevious())
			{
				m_firstScreenToRender = i;
				break;
			}
		}
	}
	
	public void update(float delta) {
		for(int i = m_firstScreenToUpdate; i < m_screenStack.size; i++)
			m_screenStack.get(i).update(delta);
	}
	
	public void render() {
		for(int i = m_firstScreenToRender; i < m_screenStack.size; i++)
			m_screenStack.get(i).render();
	}
	
	public void resize(Vector2 screenSize, Vector2 virtualScreenSize) {
		for(Map.Entry<ScreenID, Screen> entry : m_screens.entrySet()) {
			entry.getValue().resize(screenSize, virtualScreenSize);
		}
	}
	
	public void dispose() {
		for(Map.Entry<ScreenID, Screen> entry : m_screens.entrySet()) {
			entry.getValue().dispose();
		}
		m_screenStack.clear();
		m_screens.clear();
	}
}
