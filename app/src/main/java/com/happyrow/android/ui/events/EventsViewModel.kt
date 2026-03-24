package com.happyrow.android.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.usecases.events.CreateEvent
import com.happyrow.android.usecases.events.DeleteEvent
import com.happyrow.android.usecases.events.GetEventsByOrganizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EventsUiState {
    object Loading : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val message: String) : EventsUiState()
}

data class CreateEventUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val getEventsByOrganizer: GetEventsByOrganizer,
    private val createEvent: CreateEvent,
    private val deleteEvent: DeleteEvent
) : ViewModel() {

    private val _eventsState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val eventsState: StateFlow<EventsUiState> = _eventsState.asStateFlow()

    private val _createEventState = MutableStateFlow(CreateEventUiState())
    val createEventState: StateFlow<CreateEventUiState> = _createEventState.asStateFlow()

    fun loadEvents(organizerId: String) {
        viewModelScope.launch {
            _eventsState.value = EventsUiState.Loading
            try {
                val events = getEventsByOrganizer(organizerId)
                _eventsState.value = EventsUiState.Success(events)
            } catch (e: Exception) {
                _eventsState.value = EventsUiState.Error(e.message ?: "Failed to load events")
            }
        }
    }

    fun createNewEvent(request: EventCreationRequest) {
        viewModelScope.launch {
            _createEventState.value = CreateEventUiState(isLoading = true)
            try {
                createEvent(request)
                _createEventState.value = CreateEventUiState(success = true)
            } catch (e: Exception) {
                _createEventState.value = CreateEventUiState(error = e.message ?: "Failed to create event")
            }
        }
    }

    fun removeEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            try {
                deleteEvent(eventId, userId)
                val current = _eventsState.value
                if (current is EventsUiState.Success) {
                    _eventsState.value = EventsUiState.Success(
                        current.events.filter { it.id != eventId }
                    )
                }
            } catch (e: Exception) {
                _eventsState.value = EventsUiState.Error(e.message ?: "Failed to delete event")
            }
        }
    }

    fun resetCreateState() {
        _createEventState.value = CreateEventUiState()
    }
}
