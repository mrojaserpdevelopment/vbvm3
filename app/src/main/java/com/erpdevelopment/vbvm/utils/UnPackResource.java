package com.erpdevelopment.vbvm.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.erpdevelopment.vbvm.activity.MainActivity;
import com.erpdevelopment.vbvm.db.VbvmDatabaseOpenHelper;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class UnPackResource {

    public static void unpackCatalogo(Context ctx, String zipFileName) throws FileNotFoundException, IOException {
        final int BUFFER = 8192;

        AssetManager am = ctx.getAssets();
        InputStream fis = am.open(zipFileName);
        
        
        if (fis == null)
            return;

        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis, BUFFER));
        ZipEntry entry;

        // crear el directorio de imagenes si no existe.
        
        File f = new File(Environment.getExternalStorageDirectory(), MainActivity.SETTING_IMAGES_DIRECTORY_NAME);
        if (!f.exists())
        	f.mkdir();
        
        String absPathCat = f.getAbsolutePath();
        
        while ((entry = zin.getNextEntry()) != null) {
            int count;

            FileOutputStream fos = new FileOutputStream(new File(absPathCat + "/" + entry.getName()));                        
            BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

            byte data[] = new byte[BUFFER];

            while ((count = zin.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
                        
            dest.flush();
            dest.close();
        }
        zin.close();

    }
    
    public static void unpackDatabase(Context ctx, String zipFileName) throws FileNotFoundException, IOException {
        final int BUFFER = 8192;

        AssetManager am = ctx.getAssets();
        InputStream fis = am.open(zipFileName);
                
        if (fis == null)
            return;

        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis, BUFFER));               
        zin.getNextEntry();

        File fGetDir = ctx.getDir(VbvmDatabaseOpenHelper.DATABASE_DIR, Context.MODE_PRIVATE);
		File f = new File(fGetDir, VbvmDatabaseOpenHelper.DATABASE_FILE_NAME);        			
		
		Log.i("ruta db", f.getAbsolutePath());
						
		FileOutputStream fos = new FileOutputStream(f);        
		BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

		byte data[] = new byte[BUFFER];
        int count;
       
		while ((count = zin.read(data, 0, BUFFER)) != -1)
			dest.write(data, 0, count);

               
		dest.flush();
		dest.close();
       
		zin.close();
		
		MainActivity.settings.edit().putString(MainActivity.SETTING_DATABASE_FILE_PATH, f.getAbsolutePath()).commit();
		Log.i("creacion DB", "OK!!");
    }    
}
