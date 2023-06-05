package dev.anilbeesetti.nextplayer.settings.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.anilbeesetti.nextplayer.core.data.repository.PreferencesRepository
import dev.anilbeesetti.nextplayer.core.model.DoubleTapGesture
import dev.anilbeesetti.nextplayer.core.model.FastSeek
import dev.anilbeesetti.nextplayer.core.model.PlayerPreferences
import dev.anilbeesetti.nextplayer.core.model.Resume
import dev.anilbeesetti.nextplayer.core.model.ScreenOrientation
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerPreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val preferencesFlow = preferencesRepository.playerPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PlayerPreferences()
        )

    private val _uiState = MutableStateFlow(PlayerPreferencesUIState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: PlayerPreferencesEvent) {
        if (event is PlayerPreferencesEvent.ShowDialog) {
            _uiState.update {
                it.copy(showDialog = event.value)
            }
        }
    }

    fun updatePlaybackResume(resume: Resume) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(
                    resume = resume
                )
            }
        }
    }

    fun updateDoubleTapGesture(gesture: DoubleTapGesture) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(
                    doubleTapGesture = gesture
                )
            }
        }
    }

    fun updateFastSeek(fastSeek: FastSeek) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(
                    fastSeek = fastSeek
                )
            }
        }
    }

    fun toggleDoubleTapGesture() {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(
                    doubleTapGesture = if (it.doubleTapGesture == DoubleTapGesture.NONE) {
                        DoubleTapGesture.FAST_FORWARD_AND_REWIND
                    } else {
                        DoubleTapGesture.NONE
                    }
                )
            }
        }
    }

    fun toggleFastSeek() {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(
                    fastSeek = if (it.fastSeek == FastSeek.DISABLE) FastSeek.AUTO else FastSeek.DISABLE
                )
            }
        }
    }

    fun toggleRememberBrightnessLevel() {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(rememberPlayerBrightness = !it.rememberPlayerBrightness)
            }
        }
    }

    fun toggleSwipeControls() {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(useSwipeControls = !it.useSwipeControls)
            }
        }
    }

    fun toggleRememberSelections() {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(rememberSelections = !it.rememberSelections)
            }
        }
    }

    fun toggleSeekControls() {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(useSeekControls = !it.useSeekControls)
            }
        }
    }

    fun updateAudioLanguage(value: String) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences { it.copy(preferredAudioLanguage = value) }
        }
    }

    fun updateSubtitleLanguage(value: String) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences {
                it.copy(
                    preferredSubtitleLanguage = value
                )
            }
        }
    }

    fun updatePreferredPlayerOrientation(value: ScreenOrientation) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences { it.copy(playerScreenOrientation = value) }
        }
    }
}

data class PlayerPreferencesUIState(
    val showDialog: PlayerPreferenceDialog = PlayerPreferenceDialog.None
)

sealed interface PlayerPreferenceDialog {
    object ResumeDialog : PlayerPreferenceDialog
    object DoubleTapDialog : PlayerPreferenceDialog
    object FastSeekDialog : PlayerPreferenceDialog
    object AudioLanguageDialog : PlayerPreferenceDialog
    object SubtitleLanguageDialog : PlayerPreferenceDialog
    object PlayerScreenOrientationDialog : PlayerPreferenceDialog
    object None : PlayerPreferenceDialog
}

sealed interface PlayerPreferencesEvent {
    data class ShowDialog(val value: PlayerPreferenceDialog) : PlayerPreferencesEvent
}

fun PlayerPreferencesViewModel.showDialog(dialog: PlayerPreferenceDialog) {
    onEvent(PlayerPreferencesEvent.ShowDialog(dialog))
}

fun PlayerPreferencesViewModel.hideDialog() {
    onEvent(PlayerPreferencesEvent.ShowDialog(PlayerPreferenceDialog.None))
}
