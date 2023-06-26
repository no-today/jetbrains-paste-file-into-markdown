package io.github.notoday.jetbrains.paste.file.into.markdown

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import java.io.File

/**
 * @author no-today
 * @date 2023/05/14 21:33
 */
val imageFileSuffixes = listOf("png", "jpg", "jpeg", "gif")

fun Editor?.isMarkdownFile(): Boolean {
    if (this == null) return false
    if (this !is EditorEx) return false
    if (this.virtualFile == null) return false
    return this.virtualFile.fileType.name.lowercase() == "markdown"
}

fun File.isImage(): Boolean = imageFileSuffixes.contains(this.extension)

fun StringBuilder.replace(substr: String, replace: String) {
    while (true) {
        val start = indexOf(substr)
        if (start < 0) break

        replace(start, start + substr.length, replace)
    }
}