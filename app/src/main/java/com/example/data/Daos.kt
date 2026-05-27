package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IconMappingDao {
    @Query("SELECT * FROM icon_mappings ORDER BY appName ASC")
    fun getAllMappings(): Flow<List<IconMapping>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mapping: IconMapping)

    @Query("DELETE FROM icon_mappings WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("SELECT * FROM icon_mappings WHERE appName LIKE '%' || :query || '%' OR packageName LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<IconMapping>>
}

@Dao
interface ProjectMilestoneDao {
    @Query("SELECT * FROM project_milestones ORDER BY percentage ASC")
    fun getAllMilestones(): Flow<List<ProjectMilestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(milestone: ProjectMilestone)

    @Query("UPDATE project_milestones SET isApproved = :approved, feedback = :feedback, updatedAt = :updatedAt WHERE milestoneId = :id")
    suspend fun updateFeedback(id: String, approved: Boolean, feedback: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM project_milestones")
    suspend fun getCount(): Int
}
