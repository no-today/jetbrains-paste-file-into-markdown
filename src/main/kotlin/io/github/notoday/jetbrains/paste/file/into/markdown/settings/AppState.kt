package io.github.notoday.jetbrains.paste.file.into.markdown.settings

import com.google.gson.Gson
import io.github.notoday.jetbrains.paste.file.into.markdown.Flag

enum class StoragePolicy(val v: String) {
    LOCAL("local"),
    QI_NIU("qiniu"),
}

data class IState(
    var storagePolicy: StoragePolicy = StoragePolicy.LOCAL,
    var basic: Basic = Basic(),
    var qiniu: Qiniu = Qiniu()
) {
    fun deepCopy(): IState = Gson().fromJson(Gson().toJson(this), this.javaClass)
}

data class Basic(
    var savePath: String = optionsSavePath.first(),
    var newFilename: String = optionsNewFilename.first(),
    var imageQuality: Int = 100,
) {
    companion object {
        val optionsSavePath = listOf(
            "./assets/",
            "./assets/${Flag.MD_FILE.placeholder}/",
            "./${Flag.MD_FILE.placeholder}.assets/",
            "./",
        )

        val optionsNewFilename = listOf(
            "${Flag.MD_FILE.placeholder}_${Flag.DATETIME.placeholder}.${Flag.SUFFIX.placeholder}",
            "${Flag.DATETIME.placeholder}.${Flag.SUFFIX.placeholder}",
        )
    }
}

data class Qiniu(
    var accessKey: String = "",
    var secretKey: String = "",
    var bucketName: String = "",
    var bucketDomain: String = "",
    var protocol: String = optionsProtocol.first(),
    var domainList: ArrayList<String> = ArrayList()
) {
    companion object {
        val optionsProtocol = listOf("https://", "http://", "//")
    }
}