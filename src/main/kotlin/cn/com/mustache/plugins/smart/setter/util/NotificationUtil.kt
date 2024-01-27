package cn.com.mustache.plugins.smart.setter.util

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

class NotificationUtil {

    companion object {
        private const val GROUP_ID = "Smart Setter Notification Group"
        fun notify(project: Project, title: String, content: String, notificationType: NotificationType) {
            val notification = Notification(GROUP_ID, title, content, notificationType)
            Notifications.Bus.notify(notification, project)
        }

        fun notify(project: Project, content: String, notificationType: NotificationType) {
            notify(project, "", content, notificationType)
        }

    }

}