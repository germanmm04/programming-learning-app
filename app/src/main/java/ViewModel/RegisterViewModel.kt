package ViewModel

import Model.AuthRepository
import Model.UserData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    // Estado del formulario de registro
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData

    // Funciones para actualizar cada campo del UserData
    fun updateEmail(email: String) {
        _userData.value = _userData.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _userData.value = _userData.value.copy(password = password)
    }

    fun updateNombre(nombre: String) {
        _userData.value = _userData.value.copy(nombre = nombre)
    }

    fun updateApellidos(apellidos: String) {
        _userData.value = _userData.value.copy(apellidos = apellidos)
    }

    fun updateLenguajeProgramacion(lenguaje: String) {
        _userData.value = _userData.value.copy(lenguajeProgramacion = lenguaje)
    }

    fun updateNivelLenguaje(nivel: String) {
        val etapaInicial = when (nivel) {
            "Principiante" -> 1
            "Intermedio" -> 3
            "Avanzado" -> 4
            else -> 1
        }
        _userData.value = _userData.value.copy(
            nivelLenguaje = nivel,
            etapaInicial = etapaInicial
        )
    }

    fun updateTiempoDedicacion(tiempo: String) {
        _userData.value = _userData.value.copy(tiempoDedicacion = tiempo)
    }

    // Función para realizar el registro
    fun register() {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            try {
                val result = authRepository.register(_userData.value)
                if (result.isSuccess) {
                    _uiState.value = RegisterUiState.Success
                } else {
                    _uiState.value = RegisterUiState.Error(
                        result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    companion object {
        fun provideFactory(
            authRepository: AuthRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterViewModel(authRepository) as T
            }
        }
    }
} 