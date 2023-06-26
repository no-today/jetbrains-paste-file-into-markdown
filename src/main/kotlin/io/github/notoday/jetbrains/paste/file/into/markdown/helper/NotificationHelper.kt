package io.github.notoday.jetbrains.paste.file.into.markdown.helper

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.ProjectManager

/**
 * @author no-today
 * @date 2023/06/26 09:21
 */
object NotificationHelper {

    private fun notify(content: String, notificationType: NotificationType) {
        ProjectManager.getInstance()?.openProjects?.let {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("FileStorageNotification")
                .createNotification(content, notificationType)
                .notify(it[0])
        }
    }

    fun info(content: String) {
        notify(content, NotificationType.INFORMATION)
    }

    fun warn(content: String) {
        notify(content, NotificationType.WARNING)
    }

    fun err(content: String) {
        notify(content, NotificationType.ERROR)
    }
}