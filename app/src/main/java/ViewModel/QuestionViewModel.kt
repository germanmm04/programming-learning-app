package ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import Model.LessonData
import Model.LessonRepository
import Model.QuestionData

data class QuestionUiState(
    val loading: Boolean = true,
    val lesson: LessonData? = null,
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int = -1,
    val hasAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val error: String? = null
)

class QuestionViewModel(
    private val repository: LessonRepository,
    private val lessonId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuestionUiState())
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()
    
    val currentQuestion: QuestionData?
        get() = uiState.value.lesson?.questions?.getOrNull(uiState.value.currentQuestionIndex)
    
    init {
        loadLesson()
    }
    
    private fun loadLesson() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = null) }
                
                val lesson = repository.getLessonWithQuestions(lessonId)
                
                if (lesson != null) {
                    _uiState.update { 
                        it.copy(
                            loading = false,
                            lesson = lesson,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            loading = false,
                            error = "No se pudo cargar la lección. Por favor, inténtalo de nuevo."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        loading = false,
                        error = "Error al cargar la lección: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun selectOption(index: Int) {
        if (uiState.value.hasAnswered) return
        
        val currentQuestion = currentQuestion ?: return
        val isCorrect = index == currentQuestion.respuestaCorrecta
        
        _uiState.update { 
            it.copy(
                selectedOptionIndex = index,
                hasAnswered = true,
                isCorrect = isCorrect,
                score = it.score + if (isCorrect) 10 else 0
            )
        }
    }
    
    fun nextQuestion() {
        val currentIndex = uiState.value.currentQuestionIndex
        val totalQuestions = uiState.value.lesson?.questions?.size ?: 0
        
        if (currentIndex < totalQuestions - 1) {
            _uiState.update { 
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionIndex = -1,
                    hasAnswered = false,
                    isCorrect = false
                )
            }
        }
    }
    
    // Guardar el progreso del usuario al finalizar la lección usando el UID
    fun guardarProgresoUsuario(uid: String) {
        val puntuacion = uiState.value.score
        val leccionCompletada = true
        viewModelScope.launch {
            repository.guardarProgresoLeccion(
                uid = uid,
                lessonId = lessonId,
                puntuacion = puntuacion,
                leccionCompletada = leccionCompletada
            )
        }
    }
    
    class Factory(
        private val repository: LessonRepository,
        private val lessonId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
                return QuestionViewModel(repository, lessonId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

