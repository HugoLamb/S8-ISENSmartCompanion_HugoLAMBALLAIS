package fr.isen.hugo.isensmartcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntities)

    @Query("SELECT * FROM ChatEntities ORDER BY date DESC")
    suspend fun getAllChats(): List<ChatEntities>

    @Delete
    suspend fun deleteChat(chat: ChatEntities)

    @Query("DELETE FROM ChatEntities")
    suspend fun clearChats()
}
