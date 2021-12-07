package com.reactive.flashprodownloader.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;

import com.reactive.flashprodownloader.Helper.Utils;
import com.reactive.flashprodownloader.databinding.ActivityWhatsAppBinding;

import java.util.List;

public class WhatsAppActivity extends AppCompatActivity {
    private static final String TAG = WhatsAppActivity.class.getSimpleName();
    ActivityWhatsAppBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWhatsAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        Intent intent =  storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();

        String targetDirectory = "WhatsApp%2FMedia%2F.Statuses"; // add your directory to be selected by the user
        Uri uri =(Uri) intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
        String scheme = uri.toString();
        Log.i(TAG, "onCreate: "+scheme);
        scheme = scheme.replace("/root/", "/document/");
        scheme += "%3A"+targetDirectory;
        Log.i(TAG, "onCreate: " + scheme);
        uri = Uri.parse(scheme);
        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        startActivityForResult(intent, 11);
    }




    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 11) {
            if (data != null) {
                Log.i(TAG, "onActivityResult: not null $data");
                getContentResolver().takePersistableUriPermission(
                        data.getData(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
                readSDK30(data.getData());
            }else{
                Log.i(TAG, "onActivityResult: data null");
            }
        }
    }

    private void readSDK30(Uri treeUri) {
        DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: ");
                listFiles(tree);
            }
        }).start();

    }

    void listFiles(DocumentFile folder) {
        if (folder.isDirectory()) {
            DocumentFile[] documentFiles = folder.listFiles();
            for (DocumentFile documentFile:documentFiles) {
                Log.i(TAG, "listFiles: "+documentFile.getUri());
            }
        }
    }
}