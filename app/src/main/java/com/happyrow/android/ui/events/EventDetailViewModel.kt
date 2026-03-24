package com.happyrow.android.ui.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.usecases.events.DeleteEvent
import com.happyrow.android.usecases.events.GetEventById
import com.happyrow.android.usecases.events.UpdateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EventDetailUiState {
    object Loading : EventDetailUiState()
    data class Success(val event: Event) : EventDetailUiState()
    data class Error(val message: String) : EventDetailUiState()
    object Deleted : EventDetailUiState()
}

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEventById: GetEventById,
    private val updateEvent: UpdateEvent,
    private val deleteEvent: DeleteEvent
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    init {
        loadEvent()
    }

    fun loadEvent() {
        viewModelScope.launch {
            _uiState.value = EventDetailUiState.Loading
            try {
                val event = getEventById(eventId)
                if (event != null) {
                    _uiState.value = EventDetailUiState.Success(event)
                } else {
                    _uiState.value = EventDetailUiState.Error("Event not found")
                }
            } catch (e: Exception) {
                _uiState.value = EventDetailUiState.Error(e.message ?: "Failed to load event")
            }
        }
    }

    fun update(request: EventCreationRequest) {
        viewModelScope.launch {
            try {
                val updated = updateEvent(eventId, request)
                _uiState.value = EventDetailUiState.Success(updated)
            } catch (e: Exception) {
                _uiState.value = EventDetailUiState.Error(e.message ?: "Failed to update event")
            }
        }
    }

    fun delete(userId: String) {
        viewModelScope.launch {
            try {
                deleteEvent(eventId, userId)
                _uiState.value = EventDetailUiState.Deleted
            } catch (e: Exception) {
                _uiState.value = EventDetailUiState.Error(e.message ?: "Failed to delete event")
            }
        }
    }
}
