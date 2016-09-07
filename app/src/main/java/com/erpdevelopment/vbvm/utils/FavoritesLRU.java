package com.erpdevelopment.vbvm.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;

import com.erpdevelopment.vbvm.db.DBHandleFavorites;
import com.erpdevelopment.vbvm.db.DBHandleStudies;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.model.Lesson;

public class FavoritesLRU {

	private static final int MAX_CAPACITY = 24;
	
	public static Map<String, ItemInfo> lruMap = (Map<String, ItemInfo>) Collections.synchronizedMap(new LRUMap<String, ItemInfo>(MAX_CAPACITY));
	public static Map<String, ItemInfo> tempHashMap = new HashMap<>();
	
    public static ArrayList<ItemInfo> lruArrayList = new ArrayList<ItemInfo>();
    
    public static boolean restoreLRUMap = false;
    
	public FavoritesLRU() {
	}
	
	public static void addLruItem(String key, ItemInfo value){
		lruMap.put(key, value);
		tempHashMap.put(key, value);		
		convertLruToList();
	}
	
	public static ItemInfo getLruItem(String key){
		ItemInfo item = lruMap.get(key);
		convertLruToList();
		return item;
	}
	
	public static void deleteLruItem(String key){
		lruMap.remove(key);
		tempHashMap.remove(key);		
		convertLruToList();
	}
		
	//parse LruMap, get value from HashMap and add it to ArrayList
	private static void convertLruToList(){		
		lruArrayList.clear();
		DBHandleFavorites.removeAllFavorites();
		String k = "";
		Iterator<String> entries = lruMap.keySet().iterator();
		while (entries.hasNext()){
			k = entries.next();					
			ItemInfo item = tempHashMap.get(k);			
			lruArrayList.add(item);	
		}
		Collections.reverse(lruArrayList);
		for ( ItemInfo item : lruArrayList ) {
			if (item.getType().equals("lesson")){
				String thumbnailSource = ((Lesson) item.getItem()).getStudyThumbnailSource();
				DBHandleStudies.createFavorite(item.getId(), "lesson", thumbnailSource);
			} else if (item.getType().equals("article")) {
				DBHandleStudies.createFavorite(item.getId(), "article", "");
			} else if (item.getType().equals("event")) {
				DBHandleStudies.createFavorite(item.getId(), "event", "");
			} else if (item.getType().equals("post")) {
				DBHandleStudies.createFavorite(item.getId(), "post", "");
			} else if (item.getType().equals("video")) {
				DBHandleStudies.createFavorite(item.getId(), "video", "");
			}
		}
	}
}
