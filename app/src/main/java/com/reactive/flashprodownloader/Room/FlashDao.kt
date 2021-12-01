package com.reactive.flashprodownloader.Room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reactive.flashprodownloader.model.*


@Dao
interface FlashDao {
    @Query("SELECT * FROM FlashWindow")
    fun getAllFlashWindows(): LiveData<List<FlashWindow>>

    @Query("SELECT * FROM FlashWindow")
    suspend fun getFlashWindows(): List<FlashWindow>

    @Query("SELECT * FROM FlashWindow WHERE id =:id")
    suspend fun getFlashWindowById(id: Int): FlashWindow

    @Query("SELECT * FROM FlashBookmark")
    fun getBookmarks(): LiveData<List<FlashBookmark>>

    @Query("SELECT * FROM FlashHistory")
    fun getHistory(): LiveData<MutableList<FlashHistory>>

    @Query("SELECT * FROM FlashLightDownload WHERE status=:progress")
    fun getProgressDownloads(progress: String): LiveData<List<FlashLightDownload>>

    @Query("SELECT * FROM FlashLightDownload WHERE status=:status")
    fun getCompleteDownloads(status: String): LiveData<List<FlashLightDownload>>

    @Query("UPDATE FlashLightDownload SET status=:status,path=:path WHERE id=:id")
    suspend fun updateDownload(status: String,id : Int,path: String)

    @Query("UPDATE FlashWindow SET title=:title,url=:url,path=:path WHERE id=:id")
    suspend fun updateFlashWindow(id:Int,title:String,url:String,path:String)

    @Query("DELETE FROM FlashLightDownload WHERE id=:id")
    suspend fun deleteLightDownload(id: Int)

    @Query("UPDATE FlashProgress SET progress=:progress WHERE videoId=:videoId")
    suspend fun updateProgress(videoId: Int,progress: Int)

    @Query("SELECT * FROM FlashProgress WHERE videoId=:videoId")
    fun getProgressById(videoId: Int): LiveData<FlashProgress>

    @Query("SELECT * FROM FlashProgress WHERE videoId=:videoId")
    fun getSimpleProgressById(videoId: Int): FlashProgress

    @Query("DELETE FROM FlashProgress WHERE videoId=:videoId")
    suspend fun deleteFlashProgress(videoId: Int)

    @Insert
    suspend fun createNewFlashWindow(FlashWindow: FlashWindow)

    @Insert
    suspend fun addBookmark(bookmark: FlashBookmark)

    @Insert
    suspend fun addHistory(history: FlashHistory)

    @Insert
    suspend fun addDownload(lightDownload: FlashLightDownload)

    @Insert
    suspend fun addProgress(flashProgress: FlashProgress)

    @Delete
    suspend fun deleteFlashWindow(FlashWindow: FlashWindow)

    @Delete
    suspend fun deleteBookmark(bookmark: FlashBookmark)

    @Delete
    suspend fun deleteHistory(history: FlashHistory)


}