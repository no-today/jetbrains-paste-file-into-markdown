package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain

import com.intellij.openapi.vfs.VirtualFile
import io.github.notoday.jetbrains.paste.file.into.markdown.imageFileSuffixes
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import java.util.*

/**
 * @author no-today
 * @date 2023/05/19 09:57
 */
data class ClipboardContext(
    val currentEditedFile: VirtualFile,
    val fileWraps: List<FileWrap>,
    val date: LocalDateTime = LocalDateTime.now()
) {

    data class FileWrap(val file: File?, val image: BufferedImage?, val suffix: String)

    companion object {
        private val defaultImageSuffix = imageFileSuffixes.first()

        private fun getFilesFromSystemClipboard(): List<FileWrap> {
            val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)

            // Copy multiple files at once
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return (transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>).map {
                    FileWrap(it, null, it.extension)
                }
            } else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                // Copy the picture directly, without suffix
                return (transferable.getTransferData(DataFlavor.imageFlavor) as Image).let {
                    Collections.singletonList(
                        FileWrap(
                            null,
                            toBufferedImage(it),
                            defaultImageSuffix
                        )
                    )
                }
            }

            return Collections.emptyList()
        }

        private fun toBufferedImage(image: Image): BufferedImage {
            if (image is BufferedImage) {
                return image
            }

            val imageWidth = image.getWidth(null)
            val imageHeight = image.getHeight(null)

            // Create a buffered image
            val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
            val defaultScreenDevice = graphicsEnvironment.defaultScreenDevice
            val defaultConfiguration = defaultScreenDevice.defaultConfiguration
            val bufferedImage = defaultConfiguration.createCompatibleImage(imageWidth, imageHeight)

            // Draw the image on the buffered image
            val graphics = bufferedImage.createGraphics()
            graphics.drawImage(image, 0, 0, null)
            graphics.dispose()

            // Return the buffered image
            return bufferedImage
        }

        fun parseFromSystemClipboard(currentEditedFile: VirtualFile) =
            ClipboardContext(currentEditedFile, getFilesFromSystemClipboard())
    }

    fun isNotEmpty(): Boolean = fileWraps.isNotEmpty()
    fun parentPath(): String = currentEditedFile.parent.toString().replace("file://", "")
    fun mdFile(): String = currentEditedFile.nameWithoutExtension
}