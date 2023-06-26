package io.github.notoday.jetbrains.paste.file.into.markdown.action

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.ex.EditorEx
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.FileStorageSet
import io.github.notoday.jetbrains.paste.file.into.markdown.filestorage.domain.ClipboardContext
import io.github.notoday.jetbrains.paste.file.into.markdown.isMarkdownFile

/**
 * Listen for paste events
 *
 * @author no-today
 * @date 2023/05/14 21:33
 */
class MarkdownPasteFileActionHandler(private val actionHandler: EditorActionHandler?) : EditorActionHandler() {

    private val log = logger<MarkdownPasteFileActionHandler>()

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        var handled = false

        // The current edit is not a markdown file, ignore
        if (editor.isMarkdownFile()) {
            ClipboardContext.parseFromSystemClipboard((editor as EditorEx).virtualFile).apply {
                if (this.isNotEmpty()) {
                    handled = true
                    val markdownContext = FileStorageSet.save(this)

                    ApplicationManager.getApplication().runWriteAction {
                        WriteCommandAction.runWriteCommandAction(editor.project) {
                            markdownContext.links.forEach {
                                // edit markdown content, insert link: ![title](./assert/1.png)
                                EditorModificationUtil.insertStringAtCaret(editor, it.toLink())
                            }
                        }
                    }
                }
            }
        }

        // If the current processor cannot handle it, it will be handled by other processors
        if (!handled) {
            actionHandler?.execute(editor, caret, dataContext)
        }
    }
}