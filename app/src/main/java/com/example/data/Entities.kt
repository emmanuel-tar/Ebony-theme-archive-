package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_mappings")
data class IconMapping(
    @PrimaryKey val packageName: String,
    val appName: String,
    val mappedIconName: String,
    val targetPlatform: String, // "Samsung", "Xiaomi", "Both"
    val status: String, // "Mapped", "Pending", "Reviewed"
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "project_milestones")
data class ProjectMilestone(
    @PrimaryKey val milestoneId: String, // "upfront", "draft_review", "final_review", "distribution"
    val title: String,
    val percentage: Int, // 30, 40, 30 etc.
    val isApproved: Boolean,
    val feedback: String,
    val costMultiplier: Double = 1.0, // Used for live pricing calculators
    val updatedAt: Long = System.currentTimeMillis()
)
