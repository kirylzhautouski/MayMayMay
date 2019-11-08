package dev.kirillzhelt.maymaymay.dbtests

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.kirillzhelt.maymaymay.daysmodel.DayGrade
import dev.kirillzhelt.maymaymay.daysmodel.db.DayRoomDatabase
import dev.kirillzhelt.maymaymay.daysmodel.db.daos.DayDao
import dev.kirillzhelt.maymaymay.daysmodel.db.daos.DayTagJoinDao
import dev.kirillzhelt.maymaymay.daysmodel.db.daos.TagDao
import dev.kirillzhelt.maymaymay.daysmodel.db.entities.DayEntity
import dev.kirillzhelt.maymaymay.daysmodel.db.entities.DayTagJoin
import dev.kirillzhelt.maymaymay.daysmodel.db.entities.TagEntity
import dev.kirillzhelt.maymaymay.utils.observeOnce
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class DayTagJoinDaoTests {

    private lateinit var db: DayRoomDatabase
    private lateinit var dayTagJoinDao: DayTagJoinDao

    private lateinit var dayDao: DayDao
    private lateinit var tagDao: TagDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, DayRoomDatabase::class.java).build()

        dayTagJoinDao = db.dayTagJoinDao()

        dayDao = db.dayDao()
        tagDao = db.tagDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertOneTagForOneDay() = runBlocking {

        val day = DayEntity(Date(System.currentTimeMillis()), "day", DayGrade.EIGHT, 1)
        val tag = TagEntity("tag", 1)

        dayDao.insert(day)
        tagDao.insert(tag)

        val dayTagJoin = DayTagJoin(1, 1)
        dayTagJoinDao.insert(dayTagJoin)

        dayTagJoinDao.getTagsForDay(1).observeOnce {
            assertEquals(tag, it[0])
        }

    }

    @Test
    @Throws(Exception::class)
    fun insertManyTagsForOneDay() = runBlocking {

        val day = DayEntity(Date(System.currentTimeMillis()), "day", DayGrade.EIGHT, 1)

        val tags = mutableListOf<TagEntity>()
        for (i in 0..5) {
            tags.add(TagEntity("tag$i", i + 1))
        }

        dayDao.insert(day)

        for (tag in tags) {
            tagDao.insert(tag)

            val dayTagJoin = DayTagJoin(1, tag.id)
            dayTagJoinDao.insert(dayTagJoin)
        }

        dayTagJoinDao.getTagsForDay(1).observeOnce {
            assertEquals(tags, it)
        }

    }

    @Test
    @Throws(Exception::class)
    fun insertManyTagsForOneDayAndDeleteOneTag() = runBlocking {
        val day = DayEntity(Date(System.currentTimeMillis()), "day", DayGrade.EIGHT, 1)

        val tags = mutableListOf<TagEntity>()
        for (i in 0..5) {
            tags.add(TagEntity("tag$i", i + 1))
        }

        dayDao.insert(day)

        for (tag in tags) {
            tagDao.insert(tag)

            val dayTagJoin = DayTagJoin(1, tag.id)
            dayTagJoinDao.insert(dayTagJoin)
        }

        dayTagJoinDao.delete(DayTagJoin(1, 6))
        tags.removeAt(tags.size - 1)

        dayTagJoinDao.getTagsForDay(1).observeOnce {
            assertEquals(tags, it)
        }
    }
}