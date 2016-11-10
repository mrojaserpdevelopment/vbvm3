package com.erpdevelopment.vbvm.utils;

import com.erpdevelopment.vbvm.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckConnectivity {
	
//	Context context;	
	static ProgressDialog progress;
	
//	public CheckConnectivity(Context context) {
//		super();
//		this.context = context;
//	}

	public static boolean isOnline(Context context) {		
	    ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	public static void showMessage(final Context context){
		
		Activity a = (Activity) context; a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
		  		builder.setMessage(R.string.message_dialog_error_connection)
		  		        .setTitle(context.getResources().getString(R.string.title_dialog_error_connection))
		  		        .setCancelable(false)
		  		        .setIcon(R.drawable.error_dialog)
		  		        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
		  					@Override
		  					public void onClick(DialogInterface dialog, int which) {
		  						dialog.cancel();
		  					}
		  				});     
		  		AlertDialog alert = builder.create();
		  		alert.show();
			}
		});
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//  		builder.setMessage("No internet connection")
//  		        .setTitle(context.getResources().getString(R.string.dialog_title_error))
//  		        .setCancelable(false)
//  		        .setIcon(R.drawable.error_dialog)
//  		        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
//  					@Override
//  					public void onClick(DialogInterface dialog, int which) {
//  						dialog.cancel();
//  					}
//  				});     
//  		AlertDialog alert = builder.create();
//  		alert.show();
	}
	
	public static boolean connectionPresent(final ConnectivityManager cMgr) {
      if (cMgr != null) {
         NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
         if ((netInfo != null) && (netInfo.getState() != null)) {
            return netInfo.getState().equals(State.CONNECTED);
         } else {
            return false;
         }
      }
      return false;
	}

	public static void isNetworkAvailable(final Handler handler, final int timeout, final Context context) {
		
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send within the 'timeout' (in milliseconds)

		progress = ProgressDialog.show(context, "", "Please wait...");
		
        new Thread() {

            private boolean responded = false;

            @Override
            public void run() {

                // set 'responded' to TRUE if it is able to connect with google mobile (responds fast)

                new Thread() {

                    @Override
                    public void run() {
//                      HttpGet requestForTest = new HttpGet("http://m.google.com");
//                        HttpGet requestForTest = new HttpGet("http://erpdevelopment.dyndns.biz:9005/NavService.svc");
//                        try {
//                            new DefaultHttpClient().execute(requestForTest); // can last...
//                            responded = true;
//                        } catch (Exception e) {}

                        HttpURLConnection conn = null;
                        try {
                            URL url = new URL("http://erpdevelopment.dyndns.biz:9005/NavService.svc");
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.getInputStream();
                            responded = true;
                        }catch (MalformedURLException e) {
                            e.printStackTrace();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            if (conn != null) conn.disconnect();
                        }

                    }

                }.start();

                try {
                    int waited = 0;
                    while(!responded && (waited < timeout)) {
                        sleep(1000);
                        if(!responded ) { 
                            waited += 1000;
                        }
                    }
                } 
                catch(InterruptedException e) {} // do nothing 
                finally { 
                	progress.dismiss();
                    if (!responded) { handler.sendEmptyMessage(0); } 
                    else { handler.sendEmptyMessage(1); }
                }

            }

        }.start();
	}
	
//	public static void showProgress() {
//		progress = ProgressDialog.show(context, "", "Checking connectivity...");
//	}
	
}
