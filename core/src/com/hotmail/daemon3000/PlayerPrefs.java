package com.hotmail.daemon3000;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerPrefs {
	public static class Data {
		public ObjectMap<String, Object> settings = new ObjectMap<String, Object>();
	}
	
	private Data m_data = new Data();
	
	public void setString(String key, String value) {
		m_data.settings.put(key, value);
	}
	
	public String getString(String key, String defValue) {
		Object obj = m_data.settings.get(key);
		if(obj != null && obj instanceof String) {
			return (String)obj;
		}
		else
			return defValue;
	}
	
	public void setInt(String key, int value) {
		m_data.settings.put(key, value);
	}
	
	public int getInt(String key, int defValue) {
		Object obj = m_data.settings.get(key);
		if(obj != null && obj instanceof Integer) {
			return (Integer)obj;
		}
		else
			return defValue;
	}
	
	public void setFloat(String key, float value) {
		m_data.settings.put(key, value);
	}
	
	public float getFloat(String key, float defValue) {
		Object obj = m_data.settings.get(key);
		if(obj != null && obj instanceof Float) {
			return (Float)obj;
		}
		else
			return defValue;
	}
	
	public void setBoolean(String key, boolean value) {
		m_data.settings.put(key, value);
	}
	
	public boolean getBoolean(String key, boolean defValue) {
		Object obj = m_data.settings.get(key);
		if(obj != null && obj instanceof Boolean) {
			return (Boolean)obj;
		}
		else
			return defValue;
	}
	
	public void load(FileHandle fileHandle) {
		if(!fileHandle.exists()) {
			m_data = new Data();
			return;
		}
		
		Json json = new Json();
		json.addClassTag("string", String.class);
		json.addClassTag("int", Integer.class);
		json.addClassTag("float", Float.class);
		json.addClassTag("boolean", Boolean.class);
		m_data = json.fromJson(Data.class, fileHandle);
		if(m_data == null)
			m_data = new Data();
	}
	
	public void load(String file) {
		try {
			Json json = new Json();
			json.addClassTag("string", String.class);
			json.addClassTag("int", Integer.class);
			json.addClassTag("float", Float.class);
			json.addClassTag("boolean", Boolean.class);
			
			FileInputStream stream = new FileInputStream(file);
			m_data = json.fromJson(Data.class, stream);
			if(m_data == null)
				m_data = new Data();
			stream.close();
		} 
		catch (FileNotFoundException e) {
			m_data = new Data();
		} 
		catch (IOException e) {
			m_data = new Data();
		}
	}
	
	public void save(FileHandle fileHandle) {
		Json json = new Json();
		json.addClassTag("string", String.class);
		json.addClassTag("int", Integer.class);
		json.addClassTag("float", Float.class);
		json.addClassTag("boolean", Boolean.class);
		json.setOutputType(OutputType.javascript);
		fileHandle.writeString(json.prettyPrint(m_data), false);
	}
	
	public void save(String file) {
		try {
			FileOutputStream stream = new FileOutputStream(file);
			Json json = new Json();
			json.addClassTag("string", String.class);
			json.addClassTag("int", Integer.class);
			json.addClassTag("float", Float.class);
			json.addClassTag("boolean", Boolean.class);
			json.setOutputType(OutputType.javascript);
			stream.write(json.prettyPrint(m_data).getBytes(Charset.forName("UTF-8")));
			stream.close();
		} 
		catch (FileNotFoundException e) {
		} 
		catch (IOException e) {
		}
	}
}
