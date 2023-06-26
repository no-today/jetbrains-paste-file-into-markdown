package io.github.notoday.jetbrains.paste.file.into.markdown

import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.ClipboardContext
import java.nio.file.Path
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")

enum class Flag(val placeholder: String, private val func: (str: String) -> String, val desc: String) {
    MD_FILE("\${MD_FILE}", { it }, "Current markdown file name, such as <b>'README.md'</b>"),
    SUFFIX("\${SUFFIX}", { it }, "Current file suffix, such as <b>'png'</b>"),
    TIMESTAMP(
        "\${TIMESTAMP}",
        { System.currentTimeMillis().toString() },
        "Current timestamp, such as <b>'946655999000'</b>"
    ),
    DATETIME(
        "\${DATETIME}",
        { java.time.LocalDateTime.now().format(dateTimeFormatter) },
        "Current datetime, such as <b>'yyyy-MM-dd-HH:mm:ss'</b>"
    ),
    UUID(
        "\${UUID}",
        { java.util.UUID.randomUUID().toString() },
        "Random 32-bit string, such as <b>'188033f4-fb9f-11ed-be56-0242ac120002'</b>"
    );

    fun replaceVariable(expressions: StringBuilder, arg: String) {
        if (expressions.contains(placeholder)) expressions.replace(placeholder, func.invoke(arg))
    }

    companion object {
        private fun replaceVariable(
            expression: String,
            mdFile: String,
            fileSuffix: String
        ): String {
            val sb = StringBuilder(expression)

            for (flag in values()) {
                when (flag) {
                    SUFFIX -> flag.replaceVariable(sb, fileSuffix)
                    MD_FILE -> flag.replaceVariable(sb, mdFile)
                    else -> flag.replaceVariable(sb, "")
                }
            }

            return sb.toString()
        }

        fun relativePath(
            savePath: String,
            filename: String,
            clipboard: ClipboardContext,
            fileSuffix: String
        ): String {
            val relativePath = Flag.replaceVariable(savePath, clipboard.mdFile(), fileSuffix)
            val newFilename = Flag.replaceVariable(filename, clipboard.mdFile(), fileSuffix)
            return Path.of(relativePath, newFilename).toString()
        }
    }
}