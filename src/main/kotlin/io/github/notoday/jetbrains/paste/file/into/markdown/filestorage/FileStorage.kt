package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage

import com.intellij.openapi.vfs.VirtualFileManager
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.ClipboardContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.StorageContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.listener.StorageListener
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.AppSettingsState
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.IState
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.StoragePolicy
import java.awt.image.BufferedImage
import java.io.File

/**
 * @author no-today
 * @date 2023/05/19 11:34
 */
abstract class FileStorage(protected val listener: StorageListener) {

    abstract fun id(): StoragePolicy

    fun save(clipboardContext: ClipboardContext): StorageContext {
        val state = AppSettingsState.instance.state
        val markdownContext = StorageContext(arrayListOf(), clipboardContext.date)

        for (fileWrap in clipboardContext.fileWraps) {
            fileWrap.image?.let { bufferedImage ->
                saveBufferedImage(
                    state,
                    clipboardContext,
                    fileWrap,
                    bufferedImage
                ).let {
                    markdownContext.links.add(it)
                    listener.listener(clipboardContext.currentEditedFile, it.isImage, it.link)
                }
            }

            fileWrap.file?.let { file ->
                saveFile(
                    state,
                    clipboardContext,
                    fileWrap,
                    file,
                ).let {
                    markdownContext.links.add(it)
                    listener.listener(clipboardContext.currentEditedFile, it.isImage, it.link)
                }
            }
        }

        // refresh vfs
        VirtualFileManager.getInstance().syncRefresh()

        return markdownContext
    }

    protected abstract fun saveBufferedImage(
        state: IState,
        clipboardContext: ClipboardContext,
        fileWrap: ClipboardContext.FileWrap,
        bufferedImage: BufferedImage
    ): StorageContext.MarkdownLink

    protected abstract fun saveFile(
        state: IState,
        clipboardContext: ClipboardContext,
        fileWrap: ClipboardContext.FileWrap,
        file: File
    ): StorageContext.MarkdownLink
}