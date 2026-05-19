package com.jnetaol.screenai.data.db

import androidx.room.*
import com.jnetaol.screenai.data.model.ScreenAnalysis
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM screen_analyses ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<ScreenAnalysis>>

    @Query("SELECT * FROM screen_analyses WHERE id = :id")
    suspend fun getAnalysisById(id: Long): ScreenAnalysis?

    @Query("SELECT * FROM screen_analyses WHERE extractedText LIKE '%' || :query || '%' OR analysis LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR analysisType LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchAnalyses(query: String): Flow<List<ScreenAnalysis>>

    @Query("SELECT * FROM screen_analyses WHERE analysisType = :type ORDER BY timestamp DESC")
    fun getAnalysesByType(type: String): Flow<List<ScreenAnalysis>>

    @Query("SELECT COUNT(*) FROM screen_analyses")
    fun getAnalysisCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: ScreenAnalysis): Long

    @Delete
    suspend fun deleteAnalysis(analysis: ScreenAnalysis)

    @Query("DELETE FROM screen_analyses")
    suspend fun deleteAll()
}
