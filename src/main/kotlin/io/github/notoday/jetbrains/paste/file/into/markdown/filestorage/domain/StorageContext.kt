package io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain

import java.time.LocalDateTime

/**
 * @author no-today
 * @date 2023/05/25 17:23
 */
data class StorageContext(
    val links: ArrayList<MarkdownLink>,
    val date: LocalDateTime
) {

    data class MarkdownLink(
        val isImage: Boolean,
        val title: String,
        val link: String
    ) {
        fun toLink(): String {
            val link = "[${title}](${link.replace(" ", "%20")})${System.lineSeparator()}"
            return if (isImage) "!$link" else link
        }
    }
}
