package dev.kirillzhelt.maymaymay.daysmodel.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.kirillzhelt.maymaymay.daysmodel.db.entities.DayEntity
import java.util.*

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(day: DayEntity)

    @Query("""
        SELECT *
        FROM days
    """)
    fun getDays(): LiveData<List<DayEntity>>

    @Update
    fun update(day: DayEntity)

    @Delete
    fun delete(day: DayEntity)

    @Query("""
        SELECT days.day_date
        FROM days
    """)
    fun getDates(): LiveData<List<Date>>

}