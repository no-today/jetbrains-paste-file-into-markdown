package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.impl

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.io.FileUtil
import io.github.notoday.jetbrains.paste.file.into.markdown.Flag
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.FileStorage
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.ClipboardContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.StorageContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.listener.StorageListener
import io.github.notoday.jetbrains.paste.file.into.markdown.isImage
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.IState
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.StoragePolicy
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

/**
 * @author no-today
 * @date 2023/05/24 09:48
 */
class LocalFileStorage(listener: StorageListener) : FileStorage(listener) {

    private val log = logger<LocalFileStorage>()

    override fun id() = StoragePolicy.LOCAL

    override fun saveBufferedImage(
        state: IState,
        clipboardContext: ClipboardContext,
        fileWrap: ClipboardContext.FileWrap,
        bufferedImage: BufferedImage,
    ): StorageContext.MarkdownLink {
        val (_, basic) = state

        val relativePath = Flag.relativePath(basic.savePath, basic.newFilename, clipboardContext, fileWrap.suffix)
        val newFile = File(relativePath.replaceFirst(".", clipboardContext.parentPath()))

        FileUtil.createIfDoesntExist(newFile)
        FileOutputStream(newFile).use { outputStream ->
            ImageIO.write(bufferedImage, fileWrap.suffix, outputStream)
            outputStream.flush()
        }

        return StorageContext.MarkdownLink(true, newFile.name, relativePath)
    }

    override fun saveFile(
        state: IState,
        clipboardContext: ClipboardContext,
        fileWrap: ClipboardContext.FileWrap,
        file: File,
    ): StorageContext.MarkdownLink {
        val (_, basic) = state

        val relativePath = Flag.relativePath(basic.savePath, file.name, clipboardContext, fileWrap.suffix)
        val newFile = File(relativePath.replaceFirst(".", clipboardContext.parentPath()))

        FileUtil.createIfDoesntExist(newFile)
        FileUtil.copy(file, newFile)

        return StorageContext.MarkdownLink(file.isImage(), newFile.name, relativePath)
    }
}
