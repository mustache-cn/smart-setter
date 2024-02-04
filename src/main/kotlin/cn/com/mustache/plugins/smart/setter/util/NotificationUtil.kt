package cn.com.mustache.plugins.smart.setter.util

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

/**
 * @author Steven
 * Utility class for displaying notifications in the IDE.
 */
class NotificationUtil {

    companion object {
        /**
         * The group ID for notifications.
         */
        private const val GROUP_ID = "Smart Setter Notification Group"

        /**
         * Displays a notification with the specified title, content, and type.
         *
         * @param project The current project.
         * @param title The title of the notification.
         * @param content The content of the notification.
         * @param notificationType The type of the notification (e.g., INFO, WARNING, ERROR).
         */
        fun notify(project: Project, title: String, content: String, notificationType: NotificationType) {
            val notification = Notification(GROUP_ID, title, content, notificationType)
            Notifications.Bus.notify(notification, project)
        }

        /**
         * Displays a notification with the specified content and type, using an empty title.
         *
         * @param project The current project.
         * @param content The content of the notification.
         * @param notificationType The type of the notification (e.g., INFO, WARNING, ERROR).
         */
        fun notify(project: Project, content: String, notificationType: NotificationType) {
            notify(project, "", content, notificationType)
        }

    }

}