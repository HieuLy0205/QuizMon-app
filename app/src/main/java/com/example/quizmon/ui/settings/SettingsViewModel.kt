package com.example.quizmon.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizmon.ui.notification.NotificationHelper

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application)
    private val sharedPref = application.getSharedPreferences("settings", Application.MODE_PRIVATE)

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    private val _notificationEnabled = MutableLiveData<Boolean>()
    val notificationEnabled: LiveData<Boolean> = _notificationEnabled

    private val _notificationHour = MutableLiveData<Int>()
    val notificationHour: LiveData<Int> = _notificationHour

    private val _notificationMinute = MutableLiveData<Int>()
    val notificationMinute: LiveData<Int> = _notificationMinute

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _nickname.value = sharedPref.getString("nickname", "")
        _notificationEnabled.value = notificationHelper.isNotificationEnabled()
        _notificationHour.value = notificationHelper.getNotificationHour()
        _notificationMinute.value = notificationHelper.getNotificationMinute()
    }

    fun saveNickname(nickname: String) {
        sharedPref.edit().putString("nickname", nickname).apply()
        _nickname.value = nickname
    }

    fun setNotificationEnabled(enabled: Boolean) {
        notificationHelper.setNotificationEnabled(enabled)
        _notificationEnabled.value = enabled
    }

    fun saveNotificationTime(hour: Int, minute: Int) {
        notificationHelper.saveNotificationTime(hour, minute)
        _notificationHour.value = hour
        _notificationMinute.value = minute
        if (_notificationEnabled.value == true) {
            notificationHelper.scheduleDaily(hour, minute)
        }
    }

    fun getNickname(): String {
        return sharedPref.getString("nickname", "") ?: ""
    }
}