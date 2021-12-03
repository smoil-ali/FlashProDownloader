package com.reactive.flashprodownloader.Helper

import com.reactive.flashprodownloader.model.FlashLightDownloadsIds


class PR {
    companion object{
        private val list: MutableList<FlashLightDownloadsIds> = mutableListOf()

        fun addDownloadId(lightDownloadsIds: FlashLightDownloadsIds){
            list.add(lightDownloadsIds)
        }

        fun Available(subjectId: Int): Boolean{
            list.forEach {
                if (it.subjectId == subjectId){
                    return true
                }
            }
            return false
        }

        fun existId(subjectId: Int): FlashLightDownloadsIds {
            list.forEach {
                if (it.subjectId == subjectId){
                    return it
                }
            }
            throw NullPointerException()
        }

        fun deleteId(subjectId: Int){
            val lightDownloadsIds = existId(subjectId)
            list.remove(lightDownloadsIds)
        }

        fun getDownloadId(id: Int):FlashLightDownloadsIds{
            return existId(id)
        }

        fun getSize():Int{
            return list.size
        }

    }
}