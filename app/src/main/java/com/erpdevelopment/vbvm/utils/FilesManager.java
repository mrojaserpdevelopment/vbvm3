package com.erpdevelopment.vbvm.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.VideoChannel;
import com.erpdevelopment.vbvm.model.ItemInfo;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Study;

import android.os.Environment;

public class FilesManager {
	    private static final int DOWNLOAD_BUFFER_SIZE = 4096;
//	    public static int positionLessonInList = 0;
	    public static int seekBarProgress = 0;
	    public static String lastLessonId = "";
	    public static String lastStudyTitle = ""; 
	    public static long totalDuration = 0; 
	    public static ArrayList<ItemInfo> listRecentItems = new ArrayList<ItemInfo>();
	    public static List<Study> listStudies = new ArrayList<Study>();
	    public static List<Answer> listAnswers = new ArrayList<Answer>();
	    public static List<Article> listArticles = new ArrayList<Article>();
	    public static List<VideoChannel> listVideoChannels = new ArrayList<VideoChannel>();
		public static List<Study> listStudiesTypeOld = new ArrayList<Study>(); // Old Testament books
		public static List<Study> listStudiesTypeNew = new ArrayList<Study>(); // New Testament books
		public static List<Study> listStudiesTypeSingle = new ArrayList<Study>(); // Single Teachings
		private URL url;
		private URLConnection conn;
		private int lastSlash;
		private String fileName;
		private BufferedInputStream inStream;
		private BufferedOutputStream outStream;
		private File outFile;
		private FileOutputStream fileStream;
//		public static boolean changeLesson = false;

		// Constructor
		public FilesManager(){
			
		}
		
		public static void addItemSortList(ItemInfo item){
			boolean isContained = false;
			int pos = -1;
			for ( int i=0; i<listRecentItems.size(); i++ ) {
				if (listRecentItems.get(i).getId().equals(item.getId())) {
					isContained = true;
					pos = i;
					break;
				}
			}			
			//Checks if the item is already in the list and sorts the list after adding it
			if ( isContained ){				
				ArrayList<ItemInfo> tempList = new ArrayList<ItemInfo>();
				tempList.add(item);
				listRecentItems.remove(pos);
				for ( int i=0; i<listRecentItems.size(); i++) {
					tempList.add(listRecentItems.get(i));
				}
				listRecentItems.clear();
				listRecentItems.addAll(tempList);

				tempList.clear();
			}else{
				listRecentItems.add(item);
				Collections.reverse(listRecentItems);				
			}
		}
		
		/**
		 * Function to read all mp3 files from sdcard
		 * and store the details in ArrayList
		 * */
		public boolean checkSavedFile(String dirname, String urlname){
			// get the filename
	        lastSlash = urlname.lastIndexOf('/');
	        if(lastSlash >=0 && !urlname.equals(""))
	        {
                fileName = urlname.toString().substring(lastSlash + 1);
	        }	  
	        //check if filename is saved in directory
			File f = new File(Environment.getExternalStorageDirectory(), dirname);
            if (!f.exists()) {
            	return false;
            } 	
            
            if (f.listFiles().length > 0){
	            for (File file : f.listFiles()) {
	                if (file.isFile()) {
	                    // make something with the name
	                    if (file.getName().equals(fileName)) {
	                    	System.out.println("archivo encontrado!!!");
	                    	return true;
	                    }	
	                }
	            }
            }
            
            return false;			
		}
		
		public void saveFile(String dirname, String urlname){
			try {
				
				  url = new URL(urlname);
		          conn = url.openConnection();
		          conn.setUseCaches(true);
		          // get the filename
		          lastSlash = url.toString().lastIndexOf('/');
		          fileName = "file.bin";
		          if(lastSlash >=0)
		          {
		                  fileName = url.toString().substring(lastSlash + 1);
		          }
		          if(fileName.equals(""))
		          {
		                  fileName = "file.bin";
		          }
		          File f = new File(Environment.getExternalStorageDirectory(), MainActivity.DIRECTORY_IMAGES);
		          if (!f.exists()) {
		          	f.mkdir();
		          }	
		          	
		          String absPathCat = f.getAbsolutePath();
		          // start download
		          inStream = new BufferedInputStream(conn.getInputStream());
		          outFile = new File(absPathCat + "/" + fileName);
		          fileStream = new FileOutputStream(outFile);
		          outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
		          byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
		          int bytesRead = 0;
		          while((bytesRead = inStream.read(data, 0, data.length)) >= 0)
		          {
		                  outStream.write(data, 0, bytesRead);
		          }	          
		          outStream.close();
		          fileStream.close();
		          inStream.close();
			}
	        catch(MalformedURLException e)
	        {
	        		System.out.println("El error es: MalformedURLException");
	        }
	        catch(FileNotFoundException e)
	        {
	        		System.out.println("El error es: FileNotFoundException");
	        }
	        catch(Exception e)
	        {
        		e.printStackTrace();
	        }

		}
		
		public static File createDirectory(String fileName) {
			File fileDirectory = new File(
		            android.os.Environment.getExternalStorageDirectory(), fileName );  
			
			if(!fileDirectory.exists()){
		        // create directory in your application context
				fileDirectory.mkdirs();
		    }
			
			return fileDirectory;
		}

		/**
		 * Class to filter files which are having .mp3 extension
		 * */
		class FileExtensionFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
//				return (name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".jpeg") || name.endsWith(".JPEG") );
				return name.equals(fileName);
			}
		}
		
		public static boolean isFileDownloaded(String filename, String audioDir)	        
	    {
	        File fileDir = new File(audioDir);
	        if(!fileDir.exists() || !fileDir.isDirectory()){
	            return false;
	        }
	
	        String[] files = fileDir.list();
	
	        if(files.length == 0){
	            return false;
	        }
	        for (int i = 0; i < files.length; i++) {
	        	if (files[i].equals(filename)) {
	        		System.out.println("found file: " + filename + " in " + audioDir);
	        		return true;
	        	}
	        }
	      
	        return false;
	    }
	
}
