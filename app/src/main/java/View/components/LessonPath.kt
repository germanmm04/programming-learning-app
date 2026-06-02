package View.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codelingo.R

data class Lesson(
    val id: Int,
    val title: String,
    val icon: Int,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false,
    val etapa: Int = 1
)

data class EtapaHeader(
    val etapa: Int,
    val title: String
)

@Composable
fun LessonPath(
    lessons: List<Lesson>,
    onLessonClick: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var currentEtapa = 0
        
        lessons.forEach { lesson ->
            // Si cambiamos de etapa, mostramos el encabezado
            if (lesson.etapa != currentEtapa) {
                currentEtapa = lesson.etapa
                // Si no es la primera etapa, añadimos un espacio
                if (currentEtapa > 1) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
                // Mostramos el encabezado de la etapa
                EtapaHeaderComponent(etapa = currentEtapa)
                
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            LessonButton(
                lesson = lesson,
                onClick = { onLessonClick(lesson) }
            )
            
            // Dibuja la línea conectora si no es la última lección de la etapa
            if (lesson.id % 10 != 0) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .background(
                            color = if (lesson.isCompleted) Color(0xFF58CC02)
                            else Color.Gray.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
fun EtapaHeaderComponent(etapa: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color(0xFF58CC02),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "ETAPA $etapa",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

@Composable
fun LessonButton(
    lesson: Lesson,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        lesson.isActive -> Color(0xFF58CC02)
        lesson.isCompleted -> Color(0xFF58CC02).copy(alpha = 0.7f)
        else -> Color.Gray.copy(alpha = 0.3f)
    }

    Surface(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape),
        color = backgroundColor,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = lesson.icon),
                contentDescription = lesson.title,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

// Lista de 50 lecciones organizadas en 5 etapas (10 por etapa)
val sampleLessons = listOf(
    // ETAPA 1 - Fundamentos de Kotlin
    Lesson(1, "Introducción a Kotlin", R.drawable.icon1, etapa = 1),
    Lesson(2, "Variables y Tipos", R.drawable.icon2, etapa = 1),
    Lesson(3, "Operadores", R.drawable.icon3, etapa = 1),
    Lesson(4, "Strings y Arrays", R.drawable.icon4, etapa = 1),
    Lesson(5, "Control de Flujo", R.drawable.icon5, etapa = 1),
    Lesson(6, "Bucles y Rangos", R.drawable.icon6, etapa = 1),
    Lesson(7, "Funciones Básicas", R.drawable.icon7, etapa = 1),
    Lesson(8, "Null Safety", R.drawable.icon8, etapa = 1),
    Lesson(9, "Colecciones", R.drawable.icon1, etapa = 1),
    Lesson(10, "Lambdas", R.drawable.icon2, etapa = 1),
    
    // ETAPA 2 - Programación Orientada a Objetos
    Lesson(11, "Clases y Objetos", R.drawable.icon3, etapa = 2),
    Lesson(12, "Constructores", R.drawable.icon4, etapa = 2),
    Lesson(13, "Herencia", R.drawable.icon5, etapa = 2),
    Lesson(14, "Interfaces", R.drawable.icon6, etapa = 2),
    Lesson(15, "Data Classes", R.drawable.icon7, etapa = 2),
    Lesson(16, "Object y Companion", R.drawable.icon8, etapa = 2),
    Lesson(17, "Sealed Classes", R.drawable.icon1, etapa = 2),
    Lesson(18, "Extensiones", R.drawable.icon2, etapa = 2),
    Lesson(19, "Delegación", R.drawable.icon3, etapa = 2),
    Lesson(20, "Genéricos", R.drawable.icon4, etapa = 2),
    
    // ETAPA 3 - Características Avanzadas
    Lesson(21, "Corrutinas", R.drawable.icon5, etapa = 3),
    Lesson(22, "Flows", R.drawable.icon6, etapa = 3),
    Lesson(23, "Channels", R.drawable.icon7, etapa = 3),
    Lesson(24, "Suspensión", R.drawable.icon8, etapa = 3),
    Lesson(25, "Contexto de Corrutinas", R.drawable.icon1, etapa = 3),
    Lesson(26, "Scope Builders", R.drawable.icon2, etapa = 3),
    Lesson(27, "Supervisión", R.drawable.icon3, etapa = 3),
    Lesson(28, "Exception Handling", R.drawable.icon4, etapa = 3),
    Lesson(29, "Testing", R.drawable.icon5, etapa = 3),
    Lesson(30, "Debugging", R.drawable.icon6, etapa = 3),
    
    // ETAPA 4 - Android con Kotlin
    Lesson(31, "Activities", R.drawable.icon7, etapa = 4),
    Lesson(32, "Fragments", R.drawable.icon8, etapa = 4),
    Lesson(33, "ViewModels", R.drawable.icon1, etapa = 4),
    Lesson(34, "LiveData", R.drawable.icon2, etapa = 4),
    Lesson(35, "Room Database", R.drawable.icon3, etapa = 4),
    Lesson(36, "Navigation", R.drawable.icon4, etapa = 4),
    Lesson(37, "Dependency Injection", R.drawable.icon5, etapa = 4),
    Lesson(38, "WorkManager", R.drawable.icon6, etapa = 4),
    Lesson(39, "Jetpack Compose", R.drawable.icon7, etapa = 4),
    Lesson(40, "Material Design", R.drawable.icon8, etapa = 4),
    
    // ETAPA 5 - Patrones y Arquitectura
    Lesson(41, "MVVM", R.drawable.icon1, etapa = 5),
    Lesson(42, "Clean Architecture", R.drawable.icon2, etapa = 5),
    Lesson(43, "Repository Pattern", R.drawable.icon3, etapa = 5),
    Lesson(44, "Use Cases", R.drawable.icon4, etapa = 5),
    Lesson(45, "State Management", R.drawable.icon5, etapa = 5),
    Lesson(46, "Error Handling", R.drawable.icon6, etapa = 5),
    Lesson(47, "Caching", R.drawable.icon7, etapa = 5),
    Lesson(48, "Performance", R.drawable.icon8, etapa = 5),
    Lesson(49, "CI/CD", R.drawable.icon1, etapa = 5),
    Lesson(50, "Publicación en Play Store", R.drawable.icon2, etapa = 5)
) 