package com.example.data

import kotlinx.coroutines.flow.Flow

class EbonyRepository(
    private val iconMappingDao: IconMappingDao,
    private val projectMilestoneDao: ProjectMilestoneDao
) {
    val allMappings: Flow<List<IconMapping>> = iconMappingDao.getAllMappings()
    val allMilestones: Flow<List<ProjectMilestone>> = projectMilestoneDao.getAllMilestones()

    suspend fun insertMapping(mapping: IconMapping) {
        iconMappingDao.insert(mapping)
    }

    suspend fun deleteMapping(packageName: String) {
        iconMappingDao.deleteByPackage(packageName)
    }

    fun searchMappings(query: String): Flow<List<IconMapping>> {
        return iconMappingDao.search(query)
    }

    suspend fun updateMilestoneFeedback(id: String, approved: Boolean, feedback: String) {
        projectMilestoneDao.updateFeedback(id, approved, feedback)
    }

    suspend fun seedMilestones() {
        if (projectMilestoneDao.getCount() == 0) {
            // Seeding initial milestones for "Project Ebony Theme"
            val defaults = listOf(
                ProjectMilestone(
                    milestoneId = "upfront",
                    title = "Milestone 1: Upfront Design Deposit",
                    percentage = 30,
                    isApproved = true,
                    feedback = "Initial deposit approved and cleared. Design phase initiated."
                ),
                ProjectMilestone(
                    milestoneId = "draft_review",
                    title = "Milestone 2: Concept Draft Review",
                    percentage = 40,
                    isApproved = false,
                    feedback = "Under active editing. Pairings of Kente Glow and Sahara Sunset themes are drafted below."
                ),
                ProjectMilestone(
                    milestoneId = "final_review",
                    title = "Milestone 3: Final Approval & Handover",
                    percentage = 30,
                    isApproved = false,
                    feedback = "Pending final theme package review before compilation and submission."
                )
            )
            for (m in defaults) {
                projectMilestoneDao.insert(m)
            }
        }
    }

    suspend fun seedDefaultMappings() {
        // Let's pre-load some popular default package maps so the user has immediate rich data to explore, search, and edit
        val preloaded = listOf(
            IconMapping("com.whatsapp", "WhatsApp", "ic_whatsapp_ebony", "All Screens", "Mapped", "African Geometric border style"),
            IconMapping("com.instagram.android", "Instagram", "ic_instagram_ebony", "Quick Settings", "Mapped", "Terracotta Silhouette"),
            IconMapping("com.google.android.dialer", "Phone Dialer", "ic_dialer_ebony", "Home Screen", "Mapped", "Kente Gold pattern lines"),
            IconMapping("com.google.android.apps.messaging", "Messages", "ic_messages_ebony", "All Screens", "Reviewed", "Bronze circular stamp"),
            IconMapping("com.spotify.music", "Spotify", "ic_spotify_ebony", "All Screens", "Pending", "Requesting original tribal wave vector"),
            IconMapping("com.google.android.apps.maps", "Google Maps", "ic_maps_ebony", "Home Screen", "Mapped", "Ochre grid-line texture"),
            IconMapping("com.android.chrome", "Chrome Browser", "ic_chrome_ebony", "Quick Settings", "Pending", "Requires matching gradient check")
        )
        for (m in preloaded) {
            iconMappingDao.insert(m)
        }
    }
}
