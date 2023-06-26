package io.github.notoday.jetbrains.paste.file.into.markdown.helper

import com.qiniu.common.QiniuException
import com.qiniu.http.Response
import com.qiniu.storage.BucketManager
import com.qiniu.storage.Configuration
import com.qiniu.storage.UploadManager
import com.qiniu.util.Auth
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.AppSettingsState
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.Qiniu
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.time.Instant

/**
 * @author no-today
 * @date 2023/06/09 16:34
 */
object QiniuStorageHelper {

    /**
     * one hour
     */
    private const val EXPIRED_SECONDS: Long = 60 * 60

    private val configuration = Configuration()
    private val uploadManager = UploadManager(configuration)
    private lateinit var auth: Auth

    private var expire = Instant.MIN
    private var token: String = ""

    fun isAvailable(): Boolean = expire.isAfter(Instant.now())

    fun refreshToken(config: Qiniu, force: Boolean = false): QiniuStorageHelper {
        if (StringUtils.isBlank(config.accessKey)) throw RuntimeException("accessKey is cannot empty")
        if (StringUtils.isBlank(config.secretKey)) throw RuntimeException("secretKey is cannot empty")
        if (StringUtils.isBlank(config.bucketName)) throw RuntimeException("bucketName is cannot empty")

        if (force || !isAvailable()) {
            auth = Auth.create(config.accessKey, config.secretKey)
            token = auth.uploadToken(config.bucketName, null, EXPIRED_SECONDS, null)
            expire = Instant.now().plusSeconds(EXPIRED_SECONDS - 60)

            if (force) {
                val bucketDomains = getBucketDomains(config.bucketName)
                if (bucketDomains.isEmpty()) throw RuntimeException("bucket[${config.bucketName}] does not exist, or no domain")
            }
        }

        return this
    }

    fun getBucketDomains(bucketName: String): List<String> =
        BucketManager(auth, configuration).domainList(bucketName).toList()

    @Throws(QiniuException::class)
    fun asyncPut(data: ByteArray, key: String, callback: (key: String, res: Response) -> Unit) {
        refreshToken(AppSettingsState.instance.state.qiniu)
        uploadManager.asyncPut(data, key, token, null, null, false, callback)
    }

    @Throws(QiniuException::class)
    fun asyncPut(file: File, key: String, callback: (key: String, res: Response) -> Unit) {
        refreshToken(AppSettingsState.instance.state.qiniu)
        uploadManager.asyncPut(file.readBytes(), key, token, null, null, false, callback)
    }
}