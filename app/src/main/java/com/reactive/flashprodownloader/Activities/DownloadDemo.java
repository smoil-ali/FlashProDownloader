package com.reactive.flashprodownloader.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reactive.flashprodownloader.Helper.Utils;
import com.reactive.flashprodownloader.R;

public class DownloadDemo extends AppCompatActivity {
  private static final String TAG = DownloadDemo.class.getSimpleName();
  private DownloadManager mgr=null;
  private long lastDownload=-1L;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
    registerReceiver(onComplete,
                     new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    registerReceiver(onNotificationClick,
                     new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    
    unregisterReceiver(onComplete);
    unregisterReceiver(onNotificationClick);
  }
  
  public void startDownload(View v) {
    Uri uri=Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

    lastDownload=
      mgr.enqueue(new DownloadManager.Request(uri)
                  .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                          DownloadManager.Request.NETWORK_MOBILE)
                  .setTitle("Diggy")
                  .setDescription("Something useful. No, really.")
              .setAllowedOverMetered(true)
                  .setDestinationInExternalFilesDir(this,
                          "Download",
                                                     "test.mp4")
                  .setNotificationVisibility(DownloadManager
                      .Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED));

    Log.i(TAG, "startDownload: "+lastDownload);
    v.setEnabled(false);
    findViewById(R.id.query).setEnabled(true);
  }
  
  @SuppressLint("Range")
  public void queryStatus(View v) {
    Cursor c=mgr.query(new DownloadManager.Query().setFilterById(lastDownload));
    
    if (c==null) {
      Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG).show();
    }
    else {
      c.moveToFirst();
      
      Log.i(getClass().getName(), "COLUMN_ID: "+
            c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
      long downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
      int total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
      Log.i(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: "+
            Utils.Companion.getProgress(downloaded,total));
      Log.i(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: "+
            c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
      Log.i(getClass().getName(), "COLUMN_LOCAL_URI: "+
            c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
      Log.i(getClass().getName(), "COLUMN_STATUS: "+
            c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
      Log.i(getClass().getName(), "COLUMN_REASON: "+
            c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
      Log.i(TAG, "Total bytes:"+total);
      
      Toast.makeText(this, statusMessage(c), Toast.LENGTH_LONG).show();
    }
  }
  
  public void viewLog(View v) {
    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
  }
  
  @SuppressLint("Range")
  private String statusMessage(Cursor c) {
    String msg="???";
    
    switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
      case DownloadManager.STATUS_FAILED:
        msg="Download failed!";
        break;
      
      case DownloadManager.STATUS_PAUSED:
        msg="Download paused!";
        break;
      
      case DownloadManager.STATUS_PENDING:
        msg="Download pending!";
        break;
      
      case DownloadManager.STATUS_RUNNING:
        msg="Download in progress!";
        break;
      
      case DownloadManager.STATUS_SUCCESSFUL:
        msg="Download complete!";
        break;
      
      default:
        msg="Download is nowhere in sight";
        break;
    }
    
    return(msg);
  }
  
  BroadcastReceiver onComplete=new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent intent) {
      findViewById(R.id.start).setEnabled(true);
    }
  };
  
  BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent intent) {
      Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show();
    }
  };
}