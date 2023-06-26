package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.listener

import com.intellij.openapi.vfs.VirtualFile

/**
 * @author no-today
 * @date 2023/06/20 09:50
 */
interface StorageListener {

    fun listener(currentEditedFile: VirtualFile, isImage: Boolean, link: String)
}