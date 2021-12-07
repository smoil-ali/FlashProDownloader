package com.reactive.flashprodownloader.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.ActivityTestBinding
import java.util.*
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.util.Util
import com.reactive.flashprodownloader.Helper.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TestActivity : AppCompatActivity() {
    private val TAG = TestActivity::class.simpleName
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var binding: ActivityTestBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent =  storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()

        val targetDirectory = "WhatsApp%2FMedia%2F.Statuses" // add your directory to be selected by the user
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
        var scheme = uri.toString()
        Log.i(TAG, "onCreate: $scheme")
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$targetDirectory"
        Log.i(TAG, "onCreate: $scheme")
        uri = Uri.parse(scheme)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        startActivityForResult(intent, 11)






    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 11) {
            if (data != null) {
                Log.i(TAG, "onActivityResult: not null $data")
                Utils.showToast(this,"not null")
                data.data?.let { treeUri ->


                    contentResolver.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    readSDK30(treeUri)
                }
            }else{
                Utils.showToast(this,"null")
                Log.i(TAG, "onActivityResult: data null")
            }
        }
    }

    private fun readSDK30(treeUri: Uri) {
        val tree = DocumentFile.fromTreeUri(this, treeUri)!!

        coroutineScope.launch(Dispatchers.Default) {
            val uriList = arrayListOf<Uri>()
            listFiles(tree).forEach { uri ->

                val path = uri.toString()
                coroutineScope.launch(Dispatchers.Main) {
                    delay(1000)
                    binding.path.text = path
                }
                Log.i(TAG, "readSDK30: $uri")

            }
        }
    }

    fun listFiles(folder: DocumentFile): List<Uri> {
            return if (folder.isDirectory) {
                folder.listFiles().mapNotNull { file ->
                    if (file.name != null) file.uri else null
                }
            } else {
                emptyList()
            }
    }


}