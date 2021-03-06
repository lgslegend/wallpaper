package com.moe.LiveVisualizer.duang;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import java.util.ArrayList;
import java.util.List;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.app.Service;
import android.graphics.Canvas;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;

public class Engine
{
	private Class duang;
	private int wind,speed,maxSize,minSize;
	private List<Duang> list;
	private LiveWallpaper.WallpaperEngine engine;
	private DisplayMetrics display=new DisplayMetrics();
	private Bitmap buffer;
	private Engine(LiveWallpaper.WallpaperEngine engine){
		this.engine=engine;
		wind=engine.getPreference().getInt("duang_wind",2);
		speed=engine.getPreference().getInt("duang_speed",30);
		maxSize=engine.getPreference().getInt("duang_maxSize",50);
		minSize=engine.getPreference().getInt("duang_minSize",10);
		changed();
		list=new ArrayList<>();
		setDuang(Integer.parseInt(engine.getPreference().getString("duang_screen","0")));
	
	}

	public int getSpeed()
	{
		// TODO: Implement this method
		return speed;
	}

	public DisplayMetrics getDisplay()
	{
		// TODO: Implement this method
		return display;
	}

	public int getWind()
	{
		// TODO: Implement this method
		return wind;
	}

	public int getMxSize()
	{
		// TODO: Implement this method
		return maxSize;
	}

	public int getMinSize()
	{
		// TODO: Implement this method
		return minSize;
	}

	
	public Bitmap getBuffer()
	{
		return buffer;
	}
		public Context getContext(){
			return engine.getContext();
		}
	public static Engine init(LiveWallpaper.WallpaperEngine engine){
		return new Engine(engine);
	}
	public void draw(Canvas canvas){
		synchronized(list){
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().draw(canvas);
			}
	}
	public void reset(){
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().reset(false);
	}
	public void changed(){
		((WindowManager)engine.getContext().getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(display);
	}
	public void setSizeChanged(int size){
		synchronized(list){
		if(size>list.size()){
			for(int i=list.size();i<size;i++){
				try
				{
					Duang duang_item=(Duang)duang.newInstance();
					duang_item.setEngine(this);
					duang_item.reset(true);
					list.add(duang_item);
				}
				catch (InstantiationException e)
				{}
				catch (IllegalAccessException e)
				{}
				catch (IllegalArgumentException e)
				{}
			}
		}else{
			int listSize=list.size();
			for(;size<listSize;size++)
			list.remove(0).release();
		}
		}
	}
	public void setMaxSize(int size){
		maxSize=size;
		notifyChanged();
	}
	public void setMinSize(int size){
		minSize=size;
		notifyChanged();
	}
	public void setMaxSpeed(int speed){
		this.speed=speed;
		notifyChanged();
	}
	public void setWind(int wind){
		this.wind=wind;
		notifyChanged();
	}
	public void setDuang(int mode){
		clear();
		if(buffer!=null){
			buffer.recycle();
			buffer=null;
			}
		int size=engine.getPreference().getInt("duang_size",50);
		Class class_=null;
		switch(mode){
			case 0:
				class_=Snow.class;
				//duang=new Snow(display,maxSize,minSize,wind,speed);
				break;
			case 1:
				class_=Rain.class;
				//duang=new Rain(display,maxSize,minSize,wind,speed);
				break;
			case 2:
				buffer=BitmapFactory.decodeResource(engine.getContext().getResources(),com.moe.LiveVisualizer.R.raw.sakura_leave);
				class_=Sakura.class;
				break;
			case 3:
				buffer=BitmapFactory.decodeResource(engine.getContext().getResources(),com.moe.LiveVisualizer.R.raw.bubble_mould);
				class_=Bubble.class;
				break;
			case 4:
				buffer=BitmapFactory.decodeFile(new File(engine.getContext().getExternalFilesDir(null),"duang").getAbsolutePath());
				class_=Image.class;
				break;
			case 5:
				class_=Graph.class;
				break;
		}
		try
		{
			duang = class_;
			//(Displayclass_Metrics.class, Integer.class, Integer.class, Integer.class, Integer.class);
		}
		catch (SecurityException e)
		{}

		setSizeChanged(size);
	}
	public void clear(){
		synchronized(list){
		int size=list.size();
		for(int i=0;i<size;i++)
			list.remove(0).release();
			}
	}
	private void notifyChanged(){
		Iterator<Duang> iterator=list.iterator();
		while(iterator.hasNext())
			iterator.next().reset(true);
	}
}
