package com.happyrow.android.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.model.ParticipantStatus
import com.happyrow.android.domain.model.ParticipantUpdateRequest
import com.happyrow.android.usecases.participants.AddParticipant
import com.happyrow.android.usecases.participants.GetParticipants
import com.happyrow.android.usecases.participants.RemoveParticipant
import com.happyrow.android.usecases.participants.UpdateParticipantStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ParticipantsUiState {
    object Loading : ParticipantsUiState()
    data class Success(val participants: List<Participant>) : ParticipantsUiState()
    data class Error(val message: String) : ParticipantsUiState()
}

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val getParticipants: GetParticipants,
    private val addParticipant: AddParticipant,
    private val updateParticipantStatus: UpdateParticipantStatus,
    private val removeParticipant: RemoveParticipant
) : ViewModel() {

    private val _uiState = MutableStateFlow<ParticipantsUiState>(ParticipantsUiState.Loading)
    val uiState: StateFlow<ParticipantsUiState> = _uiState.asStateFlow()

    private val _addError = MutableStateFlow<String?>(null)
    val addError: StateFlow<String?> = _addError.asStateFlow()

    private var currentEventId: String = ""

    fun loadParticipants(eventId: String) {
        currentEventId = eventId
        viewModelScope.launch {
            _uiState.value = ParticipantsUiState.Loading
            try {
                val participants = getParticipants(eventId)
                _uiState.value = ParticipantsUiState.Success(participants)
            } catch (e: Exception) {
                _uiState.value = ParticipantsUiState.Error(e.message ?: "Failed to load participants")
            }
        }
    }

    fun addNewParticipant(eventId: String, email: String, status: ParticipantStatus) {
        viewModelScope.launch {
            _addError.value = null
            try {
                addParticipant(ParticipantCreationRequest(eventId, email, status))
                loadParticipants(eventId)
            } catch (e: Exception) {
                _addError.value = e.message ?: "Failed to add participant"
            }
        }
    }

    fun updateStatus(eventId: String, userEmail: String, status: ParticipantStatus) {
        viewModelScope.launch {
            try {
                updateParticipantStatus(eventId, userEmail, ParticipantUpdateRequest(status))
                loadParticipants(eventId)
            } catch (e: Exception) {
                _uiState.value = ParticipantsUiState.Error(e.message ?: "Failed to update status")
            }
        }
    }

    fun remove(eventId: String, userEmail: String) {
        viewModelScope.launch {
            try {
                removeParticipant(eventId, userEmail)
                loadParticipants(eventId)
            } catch (e: Exception) {
                _uiState.value = ParticipantsUiState.Error(e.message ?: "Failed to remove participant")
            }
        }
    }

    fun clearAddError() {
        _addError.value = null
    }
}
