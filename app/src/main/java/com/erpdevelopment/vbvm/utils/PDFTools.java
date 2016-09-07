package com.erpdevelopment.vbvm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.utils.imageloading.FileCache;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class PDFTools {
	private static final String GOOGLE_DRIVE_PDF_READER_PREFIX = "http://drive.google.com/viewer?url=";
	private static final String PDF_MIME_TYPE = "application/pdf";
	private static final String HTML_MIME_TYPE = "text/html";

	private Context mContext;
	private String mPdfUrl;

	/**
	 * If a PDF reader is installed, download the PDF file and open it in a reader. 
	 * Otherwise ask the user if he/she wants to view it in the Google Drive online PDF reader.<br />
	 */
	public void showPDFUrl( final Context context, final String pdfUrl ) {
		mContext = context;
		mPdfUrl = pdfUrl;
		if ( isPDFSupported( context ) ) {
			new asyncDownloadPdf().execute();
		} else {
			askToOpenPDFThroughGoogleDrive( context, pdfUrl );
		}
	}

	/**
	 * Downloads a PDF with the Android DownloadManager and opens it with an installed PDF reader app.
	 */
	public static void downloadAndOpenPDF(final Context context, final String pdfUrl) {
		final String filename = pdfUrl.substring( pdfUrl.lastIndexOf( "/" ) + 1 );
		// The place where the downloaded PDF file will be put
		final File tempFile = new File( FileCache.cacheDirAudio.getAbsolutePath(), filename );
		if ( tempFile.exists() ) {
			// If we have downloaded the file before, just go ahead and show it.
			openPDF( context, Uri.fromFile( tempFile ) );
			return;
		}
		// Show progress dialog while downloading
		final ProgressDialog progress = ProgressDialog.show( context, context.getString( R.string.pdf_show_local_progress_title ), context.getString( R.string.pdf_show_local_progress_content ), true );
		DownloadManager.Request r = new DownloadManager.Request( Uri.parse( pdfUrl ) );
		r.setDestinationInExternalFilesDir( context, FileCache.cacheDirAudio.getAbsolutePath(), filename );
		final DownloadManager dm = (DownloadManager) context.getSystemService( Context.DOWNLOAD_SERVICE );
		BroadcastReceiver onComplete = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if ( !progress.isShowing() ) {
					return;
				}
				context.unregisterReceiver( this );
				System.out.println("broadcast received - file downloaded...");
				progress.dismiss();
				long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, -1 );
				Cursor c = dm.query( new DownloadManager.Query().setFilterById( downloadId ) );

				if ( c.moveToFirst() ) {
					int status = c.getInt( c.getColumnIndex( DownloadManager.COLUMN_STATUS ) );
					if ( status == DownloadManager.STATUS_SUCCESSFUL ) {
						System.out.println("opening file...");
						openPDF( context, Uri.fromFile( tempFile ) );
					} else {
						System.out.println("status: " + status);
					}
				}
				c.close();
			}
		};
		context.registerReceiver( onComplete, new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );

		// Enqueue the request
		dm.enqueue( r );
	}

	/**
	 * Show a dialog asking the user if he wants to open the PDF through Google Drive
	 */
	public static void askToOpenPDFThroughGoogleDrive( final Context context, final String pdfUrl ) {
		new AlertDialog.Builder( context )
			.setTitle( R.string.pdf_show_online_dialog_title )
			.setMessage( R.string.pdf_show_online_dialog_question )
			.setNegativeButton( R.string.pdf_show_online_dialog_button_no, null )
			.setPositiveButton( R.string.pdf_show_online_dialog_button_yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openPDFThroughGoogleDrive(context, pdfUrl);	
				}
			})
			.show();
	}

	/**
	 * Launches a browser to view the PDF through Google Drive
	 */
	public static void openPDFThroughGoogleDrive(final Context context, final String pdfUrl) {
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.setDataAndType(Uri.parse(GOOGLE_DRIVE_PDF_READER_PREFIX + pdfUrl ), HTML_MIME_TYPE );
		context.startActivity( i );
	}
	/**
	 * Open a local PDF file with an installed reader
	 */
	public static final void openPDF(Context context, Uri localUri ) {
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.setDataAndType( localUri, PDF_MIME_TYPE );
		context.startActivity( i );
	}
	/**
	 * Checks if any apps are installed that supports reading of PDF files.
	 */
	public static boolean isPDFSupported( Context context ) {
		Intent i = new Intent( Intent.ACTION_VIEW );
		final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), "test.pdf" );
		i.setDataAndType( Uri.fromFile( tempFile ), PDF_MIME_TYPE );
		return context.getPackageManager().queryIntentActivities( i, PackageManager.MATCH_DEFAULT_ONLY ).size() > 0;
	}
	
	
	/************************* Alternative download code **************************************/
	
	private void tryDownloadingPDF(){
		// Get filename
		final String filename = mPdfUrl.substring( mPdfUrl.lastIndexOf( "/" ) + 1 );
		// The place where the downloaded PDF file will be put
//		final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), filename );
		final File tempFile = new File( FileCache.cacheDirAudio.getAbsolutePath(), filename );
		System.out.println("tempfile pdf: " + tempFile.getAbsolutePath());
		if ( tempFile.exists() ) {
			System.out.println("pdf file already exists...");
			// If we have downloaded the file before, just go ahead and show it.
			openPDF( mContext, Uri.fromFile( tempFile ) );
			return;
		}
	 
	    //Download the file
	    downloadFile(tempFile, mPdfUrl);
	     
	    //Show the downloaded file
//	    showPdf();
		openPDF( mContext, Uri.fromFile( tempFile ) );

	}
	 
	private void downloadFile(File filePath, String fileURL) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(fileURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(filePath);

            int read = 0;
            byte[] buffer = new byte[32768];
            while( (read = is.read(buffer)) > 0 )
                fos.write(buffer, 0, read);
            fos.close();
            is.close();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (conn != null) conn.disconnect();
        }
    }

	private class asyncDownloadPdf extends AsyncTask< String, String, String > {

		private ProgressDialog pDialog;

    	protected void onPreExecute() {
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Downloading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

		protected String doInBackground(String... params) {
            tryDownloadingPDF();
		    return null;
		}

        protected void onPostExecute(String listFiles) {
            pDialog.dismiss();
        }
    } // End asyncTask

}