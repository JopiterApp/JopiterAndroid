/*
* Jopiter App
* Copyright (C) 2026 Leonardo Colman Lopes
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package app.jopiter.notification

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import app.jopiter.MainActivity
import app.jopiter.R
import app.jopiter.common.TimeFormat

/**
 * Creates the notification channels and posts presence/appointment reminders. Posting is guarded by
 * the runtime [POST_NOTIFICATIONS] permission so it is a silent no-op when the user declined it.
 */
object ReminderNotifications {

  const val PRESENCE_CHANNEL_ID = "presence_reminders"
  const val APPOINTMENT_CHANNEL_ID = "appointment_reminders"

  fun createChannels(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NotificationManager::class.java) ?: return
    manager.createNotificationChannel(
      channel(context, PRESENCE_CHANNEL_ID, R.string.presence_channel_name, R.string.presence_channel_description)
    )
    manager.createNotificationChannel(
      channel(
        context,
        APPOINTMENT_CHANNEL_ID,
        R.string.appointment_channel_name,
        R.string.appointment_channel_description
      )
    )
  }

  fun notifyPresence(context: Context, reminder: PresenceReminder) {
    post(
      context,
      PRESENCE_CHANNEL_ID,
      reminder.uniqueName.hashCode(),
      context.getString(R.string.presence_notification_title),
      context.getString(R.string.presence_notification_text, reminder.subjectName, reminder.startAt.format(TimeFormat))
    )
  }

  fun notifyAppointment(context: Context, reminder: AppointmentReminder) {
    post(
      context,
      APPOINTMENT_CHANNEL_ID,
      reminder.uniqueName.hashCode(),
      appointmentTitle(context, reminder),
      appointmentText(context, reminder)
    )
  }

  private fun channel(context: Context, id: String, nameRes: Int, descriptionRes: Int): NotificationChannel {
    require(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    return NotificationChannel(id, context.getString(nameRes), IMPORTANCE_DEFAULT).apply {
      description = context.getString(descriptionRes)
    }
  }

  private fun post(context: Context, channelId: String, id: Int, title: String, text: String) {
    if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PERMISSION_GRANTED) return

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE)
    val notification = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentText(text)
      .setStyle(NotificationCompat.BigTextStyle().bigText(text))
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .build()

    NotificationManagerCompat.from(context).notify(id, notification)
  }

  private fun appointmentTitle(context: Context, reminder: AppointmentReminder): String = when (reminder.daysLeft) {
    0 -> context.getString(R.string.appointment_notification_title_today, reminder.appointmentName)
    1 -> context.getString(R.string.appointment_notification_title_tomorrow, reminder.appointmentName)
    else -> context.getString(R.string.appointment_notification_title_days, reminder.daysLeft, reminder.appointmentName)
  }

  private fun appointmentText(context: Context, reminder: AppointmentReminder): String {
    val subject = reminder.subjectName
    val name = reminder.appointmentName
    return if (subject != null) {
      when (reminder.daysLeft) {
        0 -> context.getString(R.string.appointment_notification_text_today, name, subject)
        1 -> context.getString(R.string.appointment_notification_text_tomorrow, name, subject)
        else -> context.getString(R.string.appointment_notification_text_days, reminder.daysLeft, name, subject)
      }
    } else {
      when (reminder.daysLeft) {
        0 -> context.getString(R.string.appointment_notification_text_today_no_subject, name)
        1 -> context.getString(R.string.appointment_notification_text_tomorrow_no_subject, name)
        else -> context.getString(R.string.appointment_notification_text_days_no_subject, reminder.daysLeft, name)
      }
    }
  }
}
