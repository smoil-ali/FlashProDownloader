package com.reactive.flashprodownloader.Helper

import com.reactive.flashprodownloader.model.FlashLightDownloadsIds


class PR {
    companion object{
        private val list: MutableList<FlashLightDownloadsIds> = mutableListOf()

        fun addDownloadId(lightDownloadsIds: FlashLightDownloadsIds){
            list.add(lightDownloadsIds)
        }

        fun existId(subjectId: Int): FlashLightDownloadsIds?{
            list.forEach {
                if (it.subjectId == subjectId){
                    return it
                }
            }
            return null
        }

        fun deleteId(subjectId: Int){
            val lightDownloadsIds = existId(subjectId)
            list.remove(lightDownloadsIds)
        }

        fun getDownloadId(id: Int):FlashLightDownloadsIds?{
            return existId(id)
        }

    }
}