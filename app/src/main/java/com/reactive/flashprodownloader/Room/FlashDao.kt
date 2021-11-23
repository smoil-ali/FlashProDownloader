package com.reactive.flashprodownloader.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.reactive.flashprodownloader.model.FlashBookmark
import com.reactive.flashprodownloader.model.FlashHistory
import com.reactive.flashprodownloader.model.FlashLightDownload
import com.reactive.flashprodownloader.model.FlashWindow


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

    @Query("SELECT * FROM FlashLightDownload WHERE status=:status")
    fun getProgressDownloads(status: Boolean): LiveData<List<FlashLightDownload>>

    @Query("SELECT * FROM FlashLightDownload WHERE status=:status")
    fun getCompleteDownloads(status: Boolean): LiveData<List<FlashLightDownload>>

    @Query("UPDATE FlashLightDownload SET status=:status,path=:path WHERE id=:id")
    suspend fun updateDownload(status: Boolean,id : Int,path: String)

    @Query("UPDATE FlashWindow SET title=:title,url=:url,path=:path WHERE id=:id")
    suspend fun updateFlashWindow(id:Int,title:String,url:String,path:String)

    @Query("DELETE FROM FlashLightDownload WHERE id=:id")
    suspend fun deleteLightDownload(id: Int)

    @Insert
    suspend fun createNewFlashWindow(FlashWindow: FlashWindow)

    @Insert
    suspend fun addBookmark(bookmark: FlashBookmark)

    @Insert
    suspend fun addHistory(history: FlashHistory)

    @Insert
    suspend fun addDownload(lightDownload: FlashLightDownload)

    @Delete
    suspend fun deleteFlashWindow(FlashWindow: FlashWindow)

    @Delete
    suspend fun deleteBookmark(bookmark: FlashBookmark)

    @Delete
    suspend fun deleteHistory(history: FlashHistory)


}