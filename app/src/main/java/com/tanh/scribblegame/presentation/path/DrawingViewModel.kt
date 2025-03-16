package com.tanh.scribblegame.presentation.path

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.scribblegame.data.resources.onError
import com.tanh.scribblegame.data.resources.onSuccess
import com.tanh.scribblegame.domain.model.Path
import com.tanh.scribblegame.domain.use_case.use_case_manager.PathManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val pathManager: PathManager
) : ViewModel() {

    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    val matchId = MutableStateFlow("")

    private val _path = MutableStateFlow<List<Path>>(emptyList())
    val path = _path.asStateFlow()

    init {
        viewModelScope.launch {
            matchId.collect { id ->
                if (id.isNotBlank()) {
                    Log.d("MAT2", "Drawing viewmodel $id")
                    pathManager.observePaths(id).collect { result ->
                        result.onSuccess {
                            _path.value = it
                        }
                        result.onError {
                            Log.e("MAT2", "Error fetching paths")
                        }
                    }
                } else {
                    Log.d("MAT2", "Drawing viewmodel null")
                }
            }
        }
    }

    fun setMatchId(id: String) {
        viewModelScope.launch {
            matchId.emit(id)
        }
    }

    fun onAction(action: DrawingAction) {
        when(action) {
            DrawingAction.OnClearCanvasClick -> onClearCanvasClick()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
        }
    }

    private fun onSelectColor(colorId: Int) {
        _state.update {
            it.copy(selectedColor = colorId)
        }
    }

    private fun onPathEnd() {
        val currentPath:  Path = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPath
            )
        }
        viewModelScope.launch {
            pathManager.updateNewPath(matchId.value ?: "", currentPath)
        }
    }

    private fun onDraw(offset: Offset) {
        val currentPath = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = currentPath.copy(
                    points = currentPath.points + offset
                )
            )
        }
    }

    private fun onNewPathStart() {
        _state.update {
            it.copy(
                currentPath = Path(
                    colorId = it.selectedColor,
                    points = emptyList()
                )
            )
        }
    }

    private fun onClearCanvasClick() {
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList()
            )
        }
    }

    fun clearPaths() {
        viewModelScope.launch {
            pathManager.clearPaths(matchId.value)
        }
    }


}