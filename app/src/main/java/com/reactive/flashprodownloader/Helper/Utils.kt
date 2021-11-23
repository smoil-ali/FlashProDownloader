package com.reactive.flashprodownloader.Helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat

class Utils {
    companion object{
        private val TAG: String = Utils::class.java.getSimpleName()
        private val SHARED_PREFERENCES_KEY = "com.reactive.FlashProDownloader"
        private val KEY = "SAVE"
        private val KEY2 = "TABCOUNT"
        private val KEY3 = "TAB_SELCTED"
        private val KEY4 = "SNAPSHOT"
        private val KEY5 = "SELECTED_ID"
        fun openDialog(manager: FragmentManager, fragment: DialogFragment) {
            val ft = manager.beginTransaction()
            val prev = manager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            fragment.show(ft, "dialog")
        }

        fun saveTabCount(context: Context, `val`: Int) {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt(KEY2, `val`)
            editor.apply()
        }

        fun getTabCount(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getInt(KEY2, 0)
        }

        fun saveSelectedTab(context: Context, `val`: Int) {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt(KEY3, `val`)
            editor.apply()
        }

        fun getSelectedTab(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getInt(KEY3, 0)
        }

        fun saveSelectedId(context: Context, `val`: Int) {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt(KEY5, `val`)
            editor.apply()
        }

        fun getSelectedId(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getInt(KEY5, 0)
        }

        fun isPathExists(path: String,context: Context): Boolean{
            val file = File(
                context.getExternalFilesDir(null).toString() +
                        "/" + "ScreenShot/" + path
            )
            return file.exists()
        }

        fun getDownloadVideoPath(context: Context):String{
            val file = File(
                context.getExternalFilesDir(null).toString() +
                        "/" + Environment.DIRECTORY_DOWNLOADS
            )
            return file.toString()
        }

        fun takeScreenShot(view: View, context: Context, folderName: String) {
            val file = File(
                context.getExternalFilesDir(null).toString() +
                        "/" + "ScreenShot/" + folderName
            )
            if (!file.exists()) {
                if (file.mkdirs()) {
                    takeSnap(file, view, context)
                    Log.i(
                        TAG,
                        "takeScreenShot: created folder"
                    )
                } else Log.i(
                    TAG,
                    "takeScreenShot: not created folder"
                )
            } else takeSnap(file, view, context)
        }

        private fun takeSnap(file: File, view: View, context: Context) {
            val bitmap = screenShot(view)
            try {
                val fileOutputStream = FileOutputStream(File(file.absolutePath + "/screenShot.jpeg"))
                val quality = 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
                Log.i(TAG, "takeScreenShot: taken")
                setSnapshotTaken(context, true)
            } catch (e: IOException) {
                Log.i(TAG, "takeScreenShot: " + e.message)
                e.printStackTrace()
            }
        }

        private fun screenShot(view: View): Bitmap {
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        fun isSnapShotTaken(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getBoolean(KEY4, false)
        }

        fun setSnapshotTaken(context: Context, `val`: Boolean) {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(KEY4, `val`)
            editor.apply()
        }

        fun hideKeyBoardEdt(editText: EditText, context: Context) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (editText.windowToken != null) {
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }

        fun showToast(context: Context,msg:String){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
        }

        fun getStringSizeLengthFile(size: Int): String {
            val df = DecimalFormat("0.00")
            val sizeKb = 1024.0f
            val sizeMb = sizeKb * sizeKb
            val sizeGb = sizeMb * sizeKb
            val sizeTerra = sizeGb * sizeKb
            if (size < sizeMb) return df.format((size / sizeKb).toDouble()) + " Kb" else if (size < sizeGb) return df.format(
                (size / sizeMb).toDouble()
            ) + " Mb" else if (size < sizeTerra) return df.format((size / sizeGb).toDouble()) + " Gb"
            return ""
        }

        fun saveAccept(context: Context, save: Boolean) {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(KEY, save)
            editor.apply()
        }

        fun getAccept(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getBoolean(
                KEY,
                false
            )
        }
    }

}