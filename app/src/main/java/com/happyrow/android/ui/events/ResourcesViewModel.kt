package com.happyrow.android.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCreationRequest
import com.happyrow.android.domain.model.ResourceUpdateRequest
import com.happyrow.android.usecases.resources.CreateResource
import com.happyrow.android.usecases.resources.DeleteResource
import com.happyrow.android.usecases.resources.GetResources
import com.happyrow.android.usecases.resources.UpdateResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ResourcesUiState {
    object Loading : ResourcesUiState()
    data class Success(val resources: List<Resource>) : ResourcesUiState()
    data class Error(val message: String) : ResourcesUiState()
}

@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val getResources: GetResources,
    private val createResource: CreateResource,
    private val updateResource: UpdateResource,
    private val deleteResource: DeleteResource
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResourcesUiState>(ResourcesUiState.Loading)
    val uiState: StateFlow<ResourcesUiState> = _uiState.asStateFlow()

    private val _addError = MutableStateFlow<String?>(null)
    val addError: StateFlow<String?> = _addError.asStateFlow()

    private var currentEventId: String = ""

    fun loadResources(eventId: String) {
        currentEventId = eventId
        viewModelScope.launch {
            _uiState.value = ResourcesUiState.Loading
            try {
                val resources = getResources(eventId)
                _uiState.value = ResourcesUiState.Success(resources)
            } catch (e: Exception) {
                _uiState.value = ResourcesUiState.Error(e.message ?: "Failed to load resources")
            }
        }
    }

    fun createNewResource(request: ResourceCreationRequest) {
        viewModelScope.launch {
            _addError.value = null
            try {
                createResource(request)
                loadResources(currentEventId)
            } catch (e: Exception) {
                _addError.value = e.message ?: "Failed to create resource"
            }
        }
    }

    fun updateExistingResource(id: String, data: ResourceUpdateRequest) {
        viewModelScope.launch {
            try {
                updateResource(id, data)
                loadResources(currentEventId)
            } catch (e: Exception) {
                _uiState.value = ResourcesUiState.Error(e.message ?: "Failed to update resource")
            }
        }
    }

    fun removeResource(id: String) {
        viewModelScope.launch {
            try {
                deleteResource(id)
                loadResources(currentEventId)
            } catch (e: Exception) {
                _uiState.value = ResourcesUiState.Error(e.message ?: "Failed to delete resource")
            }
        }
    }

    fun clearAddError() {
        _addError.value = null
    }
}
