package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.EbonyRepository
import com.example.data.IconMapping
import com.example.data.ProjectMilestone
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// High-fidelity preloaded Theme metadata
data class PremiumTheme(
    val id: String,
    val name: String,
    val platform: String, // "Android Phone (Material)" etc.
    val conceptName: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val description: String,
    val details: String,
    val status: String,
    val iconStyle: String
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class EbonyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = EbonyRepository(
        database.iconMappingDao(),
        database.projectMilestoneDao()
    )

    // Seeding initial data on start
    init {
        viewModelScope.launch {
            repository.seedMilestones()
            // Check if mappings are empty, if so, seed them
            repository.allMappings.first().let {
                if (it.isEmpty()) {
                    repository.seedDefaultMappings()
                }
            }
        }
    }

    // Static Pre-loaded African Themes Catalog for Android Mobile Phones
    val themesList = listOf(
        PremiumTheme(
            id = "kente_glow",
            name = "Kente Glow",
            platform = "Android Phone (Material UI)",
            conceptName = "Kente Weaving Heritage",
            primaryColorHex = 0xFFEAA627, // Canary Yellow
            accentColorHex = 0xFFD35230, // Terracotta Red
            description = "Rich golden motif structured with authentic horizontal lines from historic Ghanaian Kente weaving styles.",
            details = "Designed specifically to map standard Android Launcher, QuickSettings & System UI. Utilizes custom gold-on-black geometric buttons.",
            status = "Draft Ready - Complete",
            iconStyle = "Kente Glow Geometric Outline"
        ),
        PremiumTheme(
            id = "sahara_sunset",
            name = "Sahara Sunset",
            platform = "Android Phone (Themed Icons)",
            conceptName = "Tuareg Ochre Dust",
            primaryColorHex = 0xFFD35230, // Terracotta
            accentColorHex = 0xFFF2CC8F, // Sahara Sand
            description = "Minimalist warm ochre sands and crimson sunset landscapes framing custom Tuareg camel-inspired icons.",
            details = "Cohesive color palettes applied to the Home and Lockscreen background, fully adaptable for modern Android 13+ adaptive theme standards.",
            status = "Draft Ready - Complete",
            iconStyle = "Tuareg Ochre Sand Monochromatic"
        ),
        PremiumTheme(
            id = "nile_dusk",
            name = "Nile Dusk",
            platform = "Android Phone (AMOLED Black)",
            conceptName = "Cobalt papyrus & Gold",
            primaryColorHex = 0xFF1C6E8C, // Nile Blue
            accentColorHex = 0xFFEAA627, // Gold
            description = "A royal deep-slate cobalt blue design utilizing high-contrast gold chevrons, optimized for premium AMOLED power efficiency.",
            details = "Tuned with the latest Android XML configurations. Features responsive home screen widget previews and tribal vector chevron accents.",
            status = "Draft Ready - Under Review",
            iconStyle = "Nile Cobalt & Gold Chevron Minimal"
        ),
        PremiumTheme(
            id = "ananse_gold",
            name = "Ananse Gold",
            platform = "Android Phone (Adaptive Pack)",
            conceptName = "Spidery Maze & Geometric Bronze",
            primaryColorHex = 0xFF81B29A, // Sage Green
            accentColorHex = 0xFFEAA627, // Bronze/Gold
            description = "Folklore-inspired intricate maze borders wrapped around bronze badges, delivering a bold, hand-drawn look.",
            details = "Perfect for experimental layouts. Comes with 200 custom high-definition adaptive icon files conforming to Google-defined standards.",
            status = "Active Drafting",
            iconStyle = "Ananse Gold Maze Print"
        )
    )

    // Interactive simulator choices
    private val _selectedThemeId = MutableStateFlow("kente_glow")
    val selectedThemeId: StateFlow<String> = _selectedThemeId.asStateFlow()

    val currentTheme: StateFlow<PremiumTheme> = _selectedThemeId.map { id ->
        themesList.firstOrNull { it.id == id } ?: themesList[0]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), themesList[0])

    private val _isLockscreenPreview = MutableStateFlow(false)
    val isLockscreenPreview: StateFlow<Boolean> = _isLockscreenPreview.asStateFlow()

    // Invoice pricing states
    private val _baseDesignFee = MutableStateFlow(2500) // $2500 design fee base
    val baseDesignFee: StateFlow<Int> = _baseDesignFee.asStateFlow()

    // Database states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredMappings: StateFlow<List<IconMapping>> = _searchQuery
        .debounce(100)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allMappings
            } else {
                repository.searchMappings(query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestones: StateFlow<List<ProjectMilestone>> = repository.allMilestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation control strings
    private val _currentScreen = MutableStateFlow("dashboard") // "dashboard", "showcase", "mappings", "milestones"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Actions
    fun selectTheme(id: String) {
        _selectedThemeId.value = id
    }

    fun togglePreviewScreen() {
        _isLockscreenPreview.value = !_isLockscreenPreview.value
    }

    fun setBaseDesignFee(fee: Int) {
        if (fee >= 0) {
            _baseDesignFee.value = fee
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun addMapping(packageName: String, appName: String, iconStyle: String, platform: String, note: String) {
        viewModelScope.launch {
            val newMapping = IconMapping(
                packageName = packageName.trim(),
                appName = appName.trim(),
                mappedIconName = "ic_${appName.lowercase().replace(" ", "_")}_ebony",
                targetPlatform = platform,
                status = "Mapped",
                note = note.trim()
            )
            repository.insertMapping(newMapping)
        }
    }

    fun deleteMapping(packageName: String) {
        viewModelScope.launch {
            repository.deleteMapping(packageName)
        }
    }

    fun approveMilestone(id: String, feedback: String) {
        viewModelScope.launch {
            repository.updateMilestoneFeedback(id, approved = true, feedback = feedback)
        }
    }

    fun resetMilestones() {
        viewModelScope.launch {
            repository.updateMilestoneFeedback("upfront", true, "Initial deposit approved and cleared. Design phase initiated.")
            repository.updateMilestoneFeedback("draft_review", false, "Under active editing. Pairings of Kente Glow and Sahara Sunset themes are drafted below.")
            repository.updateMilestoneFeedback("final_review", false, "Pending final theme package review before compilation and submission.")
        }
    }
}
