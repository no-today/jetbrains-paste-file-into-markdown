package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage

import com.intellij.openapi.diagnostic.logger
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.ClipboardContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.StorageContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.impl.LocalFileStorage
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.impl.QiniuFileStorage
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.listener.StorageLogListener
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.AppSettingsState
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.StoragePolicy

/**
 * @author no-today
 * @date 2023/06/13 09:47
 */
object FileStorageSet {
    private val log = logger<FileStorageSet>()

    private val storages: Map<StoragePolicy, FileStorage> = mutableMapOf(
        of(LocalFileStorage(StorageLogListener)),
        of(QiniuFileStorage(StorageLogListener))
    )

    private fun of(fileStorage: FileStorage): Pair<StoragePolicy, FileStorage> = Pair(fileStorage.id(), fileStorage)

    fun of(key: StoragePolicy): FileStorage = storages[key]!!

    fun save(clipboardContext: ClipboardContext): StorageContext {
        val (storagePolicy) = AppSettingsState.instance.state
        log.info("[StoragePolicy]: $storagePolicy, $clipboardContext")

        return storages[storagePolicy]!!.save(clipboardContext)
    }
}