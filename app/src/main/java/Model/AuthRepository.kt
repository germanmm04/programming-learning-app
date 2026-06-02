package Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        return try {
            if (email.isEmpty() || password.isEmpty()) {
                return Result.failure(Exception("Por favor, complete todos los campos"))
            }
            
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo electrónico"
                is FirebaseAuthInvalidCredentialsException -> "La contraseña es incorrecta"
                else -> "Error al iniciar sesión. Por favor, inténtelo de nuevo"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun register(userData: UserData): Result<FirebaseUser?> {
        return try {
            // Validar campos obligatorios
            if (userData.email.isEmpty() || userData.password.isEmpty()) {
                return Result.failure(Exception("El correo y la contraseña son obligatorios"))
            }

            // Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(userData.email, userData.password).await()
            
            // Si se creó exitosamente, guardar datos adicionales en Firestore
            authResult.user?.let { user ->
                try {
                    // Crear un mapa con los datos del usuario excluyendo la contraseña
                    val userDataMap = mapOf(
                        "email" to userData.email,
                        "nombre" to userData.nombre,
                        "apellidos" to userData.apellidos,
                        "lenguajeProgramacion" to userData.lenguajeProgramacion,
                        "nivelLenguaje" to userData.nivelLenguaje,
                        "tiempoDedicacion" to userData.tiempoDedicacion,
                        "etapaInicial" to userData.etapaInicial
                    )

                    // Guardar en Firestore
                    firestore.collection("users")
                        .document(user.uid)
                        .set(userDataMap)
                        .await()
                } catch (e: Exception) {
                    // Si falla al guardar en Firestore, eliminamos el usuario creado
                    auth.currentUser?.delete()
                    return Result.failure(Exception("Error al guardar datos en Firestore: ${e.message}"))
                }
            }

            Result.success(authResult.user)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "El correo electrónico no es válido"
                is com.google.firebase.firestore.FirebaseFirestoreException -> {
                    when (e.code) {
                        com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED -> 
                            "Error de permisos: Verifica las reglas de seguridad de Firestore"
                        else -> "Error de Firestore: ${e.message}"
                    }
                }
                else -> "Error al registrar usuario: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun getUserData(userId: String): Result<UserData> {
        return try {
            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.toObject(UserData::class.java)
                userData?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al obtener los datos del usuario"))
            } else {
                Result.failure(Exception("No se encontraron datos del usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
