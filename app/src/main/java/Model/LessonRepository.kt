package Model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class LessonRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Obtener todas las lecciones
    fun getLessons(): Flow<List<LessonData>> = flow {
        try {
            val snapshot = firestore.collection("lessons")
                .get()
                .await()
            
            val lessons = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LessonData::class.java)?.also {
                    // Log.d("LessonRepo", "Loaded lesson: ${it.id}") // Opcional: Log para cada lección cargada
                }
            }
            
            emit(lessons)
            
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error getting all lessons", e)
            emit(emptyList())
        }
    }

    // Obtener una lección específica con sus preguntas y teoría
    suspend fun getLessonWithQuestions(lessonId: String): LessonData? {
        return try {
            Log.d("LessonRepo", "Attempting to get lesson with ID: $lessonId")
            val lessonRef = firestore.collection("lessons").document(lessonId)
            
            val lessonDoc = lessonRef.get().await()
            
            if (!lessonDoc.exists()) {
                Log.w("LessonRepo", "Lesson document with ID $lessonId does not exist.")
                return null
            }
            
            // Convertir el documento a LessonData
            val lesson = lessonDoc.toObject(LessonData::class.java)
            
            if (lesson == null) {
                Log.e("LessonRepo", "Failed to convert lesson document $lessonId to LessonData.")
                return null
            }
            Log.d("LessonRepo", "Successfully loaded lesson data for $lessonId: ${lesson.title}")
            
            // Obtener las preguntas de la lección
            val questionsRef = lessonRef.collection("questions")
            
            val questionsSnapshot = questionsRef.get().await()
            
            val questions = questionsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuestionData::class.java)
            }
            Log.d("LessonRepo", "Loaded ${questions.size} questions for lesson $lessonId.")
            
            // Obtener la teoría de la lección
            val theoryRef = lessonRef.collection("theory")
            val theorySnapshot = theoryRef.get().await()
            val theory = theorySnapshot.documents.firstOrNull()?.getString("content") ?: ""
            Log.d("LessonRepo", "Loaded theory for lesson $lessonId: \"$theory\"")
            
            val finalLessonData = lesson.copy(
                questions = questions,
                theory = theory
            )
            Log.d("LessonRepo", "Returning LessonData for $lessonId: theory=${finalLessonData.theory.length} chars, questions=${finalLessonData.questions.size}")
            finalLessonData
            
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error getting lesson with questions for ID: $lessonId", e)
            null
        }
    }

    // Guardar el progreso de una lección para un usuario usando el UID
    suspend fun guardarProgresoLeccion(
        uid: String,
        lessonId: String,
        puntuacion: Int,
        leccionCompletada: Boolean
    ) {
        try {
            val progreso = hashMapOf(
                "leccionCompletada" to leccionCompletada,
                "puntuacion" to puntuacion
            )
            firestore.collection("users")
                .document(uid)
                .collection("progreso")
                .document(lessonId)
                .set(progreso)
                .await()
            
            // Actualizar la puntuación total del usuario
            val userRef = firestore.collection("users").document(uid)
            val userDoc = userRef.get().await()
            val puntuacionActual = userDoc.getLong("puntuacionTotal") ?: 0L
            userRef.update("puntuacionTotal", puntuacionActual + puntuacion).await()
            
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error saving lesson progress for user $uid, lesson $lessonId", e)
        }
    }

    // Obtener la puntuación total del usuario como un Flow
    fun getPuntuacionTotalFlow(uid: String): Flow<Long> = callbackFlow {
        if (uid.isEmpty()) {
            // Si el UID está vacío, emitir 0 y cerrar el flow
            trySend(0L)
            channel.close()
            return@callbackFlow
        }

        val userRef = firestore.collection("users").document(uid)

        val subscription = userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("LessonRepo", "Error listening for total score changes for user $uid", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val puntuacion = snapshot.getLong("puntuacionTotal") ?: 0L
                Log.d("LessonRepo", "Total score updated for user $uid: $puntuacion")
                trySend(puntuacion)
            } else {
                 // Documento no existe o ha sido eliminado
                 Log.w("LessonRepo", "User document for $uid does not exist or is empty.")
                 trySend(0L)
            }
        }

        awaitClose { subscription.remove() }
    }

    // Obtener la racha del usuario como un Flow
    fun getRachaFlow(uid: String): Flow<Long> = callbackFlow {
        if (uid.isEmpty()) {
            // Si el UID está vacío, emitir 0 y cerrar el flow
            trySend(0L)
            channel.close()
            return@callbackFlow
        }

        val userRef = firestore.collection("users").document(uid)

        val subscription = userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("LessonRepo", "Error listening for streak changes for user $uid", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val racha = snapshot.getLong("racha") ?: 0L
                Log.d("LessonRepo", "Streak updated for user $uid: $racha")
                trySend(racha)
            } else {
                 // Documento no existe o ha sido eliminado
                 Log.w("LessonRepo", "User document for $uid does not exist or is empty.")
                 trySend(0L)
            }
        }

        awaitClose { subscription.remove() }
    }

    // Obtener la puntuación total del usuario
    suspend fun getPuntuacionTotal(uid: String): Long {
        return try {
            val userRef = firestore.collection("users").document(uid)
            val userDoc = userRef.get().await()
            val puntuacion = userDoc.getLong("puntuacionTotal") ?: 0L
            Log.d("LessonRepo", "Total score for user $uid: $puntuacion")
            puntuacion
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error getting total score for user $uid", e)
            0L
        }
    }

    // Actualizar la racha del usuario
    suspend fun actualizarRacha(uid: String) {
        try {
            val userRef = firestore.collection("users").document(uid)
            val userDoc = userRef.get().await()
            
            val ultimaVisita = userDoc.getTimestamp("ultimaVisita")
            val rachaActual = userDoc.getLong("racha") ?: 0L
            
            val hoy = com.google.firebase.Timestamp.now()
            
            // Convertir timestamps a fechas para comparar solo el día
            val ultimaVisitaDate = ultimaVisita?.toDate()
            val hoyDate = hoy.toDate()
            
            // Crear fechas solo con año, mes y día (sin hora)
            val ultimaVisitaSoloDia = ultimaVisitaDate?.let {
                java.util.Calendar.getInstance().apply {
                    time = it
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.time
            }
            
            val hoySoloDia = java.util.Calendar.getInstance().apply {
                time = hoyDate
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.time
            
            val nuevaRacha = when {
                ultimaVisita == null -> {
                    1L
                }
                ultimaVisitaSoloDia?.before(hoySoloDia) == true -> {
                    // Verificar si han pasado más de 24 horas
                    val horasPasadas = (hoy.seconds - ultimaVisita.seconds) / 3600
                    if (horasPasadas > 24) {
                        Log.d("LessonRepo", "Streak reset for user $uid due to >24 hours since last visit.")
                        1L
                    } else {
                        Log.d("LessonRepo", "Streak increased for user $uid.")
                        rachaActual + 1L
                    }
                }
                else -> {
                    Log.d("LessonRepo", "Streak maintained for user $uid (same day visit).")
                    rachaActual
                }
            }
            
            // Solo actualizamos si es un nuevo día o es la primera visita
            if (ultimaVisita == null || ultimaVisitaSoloDia?.before(hoySoloDia) == true) {
                 Log.d("LessonRepo", "Updating streak and last visit for user $uid: newRacha=$nuevaRacha")
                userRef.update(
                    mapOf(
                        "racha" to nuevaRacha,
                        "ultimaVisita" to hoy
                    )
                ).await()
            }
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error updating streak for user $uid", e)
        }
    }

    // Obtener la racha actual del usuario
    suspend fun getRacha(uid: String): Long {
        return try {
            val userDoc = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            val racha = userDoc.getLong("racha") ?: 0L
            Log.d("LessonRepo", "Streak for user $uid: $racha")
            racha
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error getting streak for user $uid", e)
            0L
        }
    }

    // Obtener el progreso del usuario
    suspend fun getUserProgress(uid: String): Map<String, Boolean> {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("progreso")
                .get()
                .await()
            val progressMap = snapshot.documents.associate { doc ->
                doc.id to (doc.getBoolean("leccionCompletada") ?: false)
            }
             Log.d("LessonRepo", "User progress for $uid: ${progressMap.size} items")
            progressMap
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error getting user progress for user $uid", e)
            emptyMap()
        }
    }

    // Guardar la teoría de una lección
    suspend fun guardarTeoriaLeccion(
        lessonId: String,
        teoria: String
    ) {
        try {
            val theoryRef = firestore.collection("lessons")
                .document(lessonId)
                .collection("theory")
                .document("theory1")

            val theoryData = hashMapOf(
                "content" to teoria
            )

            theoryRef.set(theoryData).await()
            Log.d("LessonRepo", "Successfully saved theory for lesson $lessonId.")
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error al guardar la teoría: ${e.message}")
        }
    }

    // Función para comprar la corona
    suspend fun comprarCorona(uid: String): CompraResult {
        val userRef = firestore.collection("users").document(uid)
        val costoCorona = 5000L

        return try {
            firestore.runTransaction { transaction ->
                val userSnapshot = transaction.get(userRef)
                val puntuacionActual = userSnapshot.getLong("puntuacionTotal") ?: 0L
                val tieneCorona = userSnapshot.getBoolean("hasCrown") ?: false

                when {
                    tieneCorona -> {
                        Log.d("LessonRepo", "User $uid already has the crown.")
                        CompraResult.AlreadyOwned
                    }
                    puntuacionActual >= costoCorona -> {
                        val nuevaPuntuacion = puntuacionActual - costoCorona
                        transaction.update(userRef, "puntuacionTotal", nuevaPuntuacion)
                        transaction.update(userRef, "hasCrown", true)
                        Log.d("LessonRepo", "User $uid successfully bought the crown. New score: $nuevaPuntuacion")
                        CompraResult.Success
                    }
                    else -> {
                        Log.d("LessonRepo", "User $uid does not have enough points to buy the crown. Score: $puntuacionActual")
                        CompraResult.InsufficientPoints
                    }
                }
            }.await()
        } catch (e: Exception) {
            Log.e("LessonRepo", "Error buying crown for user $uid", e)
            CompraResult.Error("Error en la transacción de compra: ${e.message}")
        }
    }
}

// Sellado de clases para el resultado de la compra
sealed class CompraResult {
    object Success : CompraResult()
    object InsufficientPoints : CompraResult()
    object AlreadyOwned : CompraResult()
    data class Error(val message: String) : CompraResult()
} 