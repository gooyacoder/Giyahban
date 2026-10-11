package com.ahm.giyahban

import androidx.room.*

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY triggerAt ASC")
    fun getAll(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE triggerAt > :now ORDER BY triggerAt ASC")
    fun getFuture(now: Long): List<Reminder>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    fun getById(id: Long): Reminder?

    @Insert
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}
