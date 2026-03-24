package com.happyrow.android.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.model.ContributionCreationRequest
import com.happyrow.android.domain.model.ContributionUpdateRequest
import com.happyrow.android.usecases.contributions.AddContribution
import com.happyrow.android.usecases.contributions.DeleteContribution
import com.happyrow.android.usecases.contributions.GetContributions
import com.happyrow.android.usecases.contributions.UpdateContribution
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ContributionsUiState {
    object Loading : ContributionsUiState()
    data class Success(val contributions: List<Contribution>) : ContributionsUiState()
    data class Error(val message: String) : ContributionsUiState()
}

@HiltViewModel
class ContributionsViewModel @Inject constructor(
    private val getContributions: GetContributions,
    private val addContribution: AddContribution,
    private val updateContribution: UpdateContribution,
    private val deleteContribution: DeleteContribution
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContributionsUiState>(ContributionsUiState.Loading)
    val uiState: StateFlow<ContributionsUiState> = _uiState.asStateFlow()

    fun loadContributions(eventId: String, resourceId: String) {
        viewModelScope.launch {
            _uiState.value = ContributionsUiState.Loading
            try {
                val contributions = getContributions(eventId, resourceId)
                _uiState.value = ContributionsUiState.Success(contributions)
            } catch (e: Exception) {
                _uiState.value = ContributionsUiState.Error(e.message ?: "Failed to load contributions")
            }
        }
    }

    fun contribute(request: ContributionCreationRequest, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                addContribution(request)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = ContributionsUiState.Error(e.message ?: "Failed to contribute")
            }
        }
    }

    fun update(eventId: String, resourceId: String, data: ContributionUpdateRequest) {
        viewModelScope.launch {
            try {
                updateContribution(eventId, resourceId, data)
                loadContributions(eventId, resourceId)
            } catch (e: Exception) {
                _uiState.value = ContributionsUiState.Error(e.message ?: "Failed to update contribution")
            }
        }
    }

    fun remove(eventId: String, resourceId: String) {
        viewModelScope.launch {
            try {
                deleteContribution(eventId, resourceId)
                loadContributions(eventId, resourceId)
            } catch (e: Exception) {
                _uiState.value = ContributionsUiState.Error(e.message ?: "Failed to delete contribution")
            }
        }
    }
}
