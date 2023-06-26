package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.impl

import com.intellij.openapi.diagnostic.logger
import io.github.notoday.jetbrains.paste.file.into.markdown.Flag
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.FileStorage
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.FileStorageSet
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.ClipboardContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.StorageContext
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.listener.StorageListener
import io.github.notoday.jetbrains.paste.file.into.markdown.helper.NotificationHelper
import io.github.notoday.jetbrains.paste.file.into.markdown.helper.QiniuStorageHelper
import io.github.notoday.jetbrains.paste.file.into.markdown.isImage
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.IState
import io.github.notoday.jetbrains.paste.file.into.markdown.settings.StoragePolicy
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Path
import javax.imageio.ImageIO

/**
 * @author no-today
 * @date 2023/06/06 11:41
 */
class QiniuFileStorage(listener: StorageListener) : FileStorage(listener) {

    private val log = logger<FileStorageSet>()

    override fun id() = StoragePolicy.QI_NIU

    override fun saveBufferedImage(
        state: IState,
        clipboardContext: ClipboardContext,
        fileWrap: ClipboardContext.FileWrap,
        bufferedImage: BufferedImage,
    ): StorageContext.MarkdownLink {
        val (_, basic, qiniu) = state

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, fileWrap.suffix, outputStream)
        val cloudPath = Flag.relativePath(basic.savePath, basic.newFilename, clipboardContext, fileWrap.suffix)
            .removePrefix("./")

        QiniuStorageHelper.asyncPut(outputStream.toByteArray(), cloudPath) { key, res ->
            if (!res.isOK) {
                NotificationHelper.err("Qiniu cloud upload failed: $key [${res.statusCode}] ${res.error}")
            }
        }

        return StorageContext.MarkdownLink(
            true,
            Path.of(cloudPath).fileName.toString(),
            "${qiniu.protocol}${qiniu.bucketDomain}/${cloudPath}"
        )
    }

    override fun saveFile(
        state: IState,
        clipboardContext: ClipboardContext,
        fileWrap: ClipboardContext.FileWrap,
        file: File,
    ): StorageContext.MarkdownLink {
        val (_, basic, qiniu) = state

        val cloudPath = Flag.relativePath(basic.savePath, file.name, clipboardContext, fileWrap.suffix)
            .removePrefix("./")

        val markdownLink = StorageContext.MarkdownLink(
            file.isImage(),
            file.name,
            "${qiniu.protocol}${qiniu.bucketDomain}/${cloudPath}"
        )

        QiniuStorageHelper.asyncPut(file, cloudPath) { key, res ->
            if (!res.isOK) {
                NotificationHelper.err("Qiniu cloud upload failed: $key [${res.statusCode}] ${res.error}")
            }
        }

        return markdownLink
    }
}
