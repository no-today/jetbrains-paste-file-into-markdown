package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.listener

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vfs.VirtualFile

/**
 * @author no-today
 * @date 2023/06/20 10:27
 */
object StorageLogListener : StorageListener {
    private val log = logger<StorageLogListener>()

    override fun listener(currentEditedFile: VirtualFile, isImage: Boolean, link: String) {
        log.info("Storage: file: ${currentEditedFile.name}, isImage: $isImage, link: $link")
    }
}