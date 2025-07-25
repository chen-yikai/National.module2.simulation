package com.example.nationalmodule2simulation.screens

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.nationalmodule2simulation.MainActivity
import com.example.nationalmodule2simulation.R

/**
 * Implementation of App Widget functionality.
 */
class mad_notebook_widget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.mad_notebook_widget)

    val newNote = Intent(context, MainActivity::class.java).apply {
        putExtra("note_action", "new")
    }
    val recentNote = Intent(context, MainActivity::class.java).apply {
        putExtra("note_action", "recent")
    }

    val recentNotePendingIntent = PendingIntent.getActivity(
        context,
        0,
        recentNote,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val newNotePendingIntent = PendingIntent.getActivity(
        context,
        1,
        newNote,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    views.setOnClickPendingIntent(R.id.open_recent, recentNotePendingIntent)

    views.setOnClickPendingIntent(R.id.new_note, newNotePendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}