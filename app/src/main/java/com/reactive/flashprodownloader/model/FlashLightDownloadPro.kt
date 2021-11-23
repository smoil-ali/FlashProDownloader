package com.reactive.flashprodownloader.model

import android.provider.ContactsContract

data class FlashLightDownloadPro(
    var size: String?,
    var type: String?,
    var link: String?,
    var name: String?,
    var page: String?,
    var chunked: Boolean,
    var website: String?
)
