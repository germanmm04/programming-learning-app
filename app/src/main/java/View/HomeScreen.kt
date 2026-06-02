package View

import Model.LessonData
import Model.LessonRepository
import View.components.Lesson
import View.components.LessonPath
import View.components.sampleLessons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.codelingo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Inicio")
    object Shop : BottomNavItem("shop", Icons.Default.ShoppingCart, "Tienda")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
}

@Composable
fun HomeScreen(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Shop,
        BottomNavItem.Profile
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1824))
    ) {
        // Contenido principal (con padding para dejar espacio para la barra)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Renderizamos el contenido según la ruta actual
            when (val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route) {
                "home", null -> HomeContent(navController)
                "shop" -> ShopScreen(navController)
                "profile" -> ProfileScreen(navController)
            }
        }
        
        // Barra de navegación grande y personalizada
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(colorResource(id = R.color.background_app))
        ) {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            
            Row(
                modifier = Modifier.fillMaxSize().background(color = Color.Transparent),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon, 
                            contentDescription = item.title,
                            tint = if (currentRoute == item.route) Color(0xFF58CC02) else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Text(
                            text = item.title,
                            color = if (currentRoute == item.route) Color(0xFF58CC02) else Color.Gray,
                            style = TextStyle(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NextLessonCard(nextLesson: Lesson, onLessonClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color(0xFF58CC02),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = nextLesson.icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Próxima lección",
                        color = Color.White.copy(alpha = 0.8f),
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Text(
                        text = nextLesson.title,
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            IconButton(
                onClick = onLessonClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Ir a la lección",
                    tint = Color(0xFF58CC02),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun HomeContent(navController: NavController) {
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
    val scrollState = rememberScrollState()
    val lessonRepository = remember { LessonRepository() }
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: ""
    val firestore = FirebaseFirestore.getInstance()
    
    // Estado para almacenar las lecciones de Firestore
    var firestoreLessons by remember { mutableStateOf<List<LessonData>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Estado para almacenar el progreso del usuario
    var userProgress by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    
    // Estado para almacenar la puntuación total (ahora Flow)
    val puntuacionTotal by lessonRepository.getPuntuacionTotalFlow(uid).collectAsState(initial = 0L)
    
    // Estado para almacenar la racha (ahora Flow) y la última visita
    val racha by lessonRepository.getRachaFlow(uid).collectAsState(initial = 0L)
    var ultimaVisita by remember { mutableStateOf<com.google.firebase.Timestamp?>(null) }

    // Obtener la etapa inicial del usuario
    var etapaInicial by remember { mutableStateOf(1) }
    
    // Cargar lecciones de Firestore cuando se inicia la pantalla
    LaunchedEffect(true) {
        scope.launch {
            try {
                lessonRepository.getLessons().collect { lessons ->
                    firestoreLessons = lessons
                }
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // Cargar progreso del usuario, racha y última visita
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            isLoading = true
            try {
                // Obtener la etapa inicial del usuario
                val userDoc = firestore.collection("users")
                    .document(uid)
                    .get()
                    .await()
                etapaInicial = userDoc.getLong("etapaInicial")?.toInt() ?: 1
                ultimaVisita = userDoc.getTimestamp("ultimaVisita")

                // Obtener el progreso actual
                val progress = lessonRepository.getUserProgress(uid)
                userProgress = progress

                // Solo activar las lecciones si el usuario no tiene progreso guardado
                if (userProgress.isEmpty()) {
                    // Activar todas las lecciones hasta la etapa inicial
                    val lessonsToActivate = sampleLessons.filter { it.etapa < etapaInicial }
                    lessonsToActivate.forEach { lesson ->
                        val lessonId = "lesson_${lesson.id}"
                        lessonRepository.guardarProgresoLeccion(
                            uid = uid,
                            lessonId = lessonId,
                            puntuacion = 0,
                            leccionCompletada = true
                        )
                    }
                    // Actualizar el progreso después de activar las lecciones
                    userProgress = lessonRepository.getUserProgress(uid)
                }

                // Actualizar la racha y verificar si debemos mostrar la pantalla de racha
                lessonRepository.actualizarRacha(uid)
                val nuevaRacha = lessonRepository.getRacha(uid)
                
                // Si la racha es mayor o igual a 2 y es un nuevo día, mostrar la pantalla de racha
                if (nuevaRacha >= 2 && ultimaVisita != null) {
                    val ultimaVisitaDate = ultimaVisita?.toDate()
                    val hoyDate = com.google.firebase.Timestamp.now().toDate()
                    
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
                    
                    if (ultimaVisitaSoloDia?.before(hoySoloDia) == true) {
                        navController.navigate("racha") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            } catch (e: Exception) {
                // Manejar error
            } finally {
                isLoading = false
            }
        }
    }

    // Construir la lista de lecciones con el estado real
    val lessonsWithProgress = sampleLessons.mapIndexed { index, lesson ->
        val lessonFirestoreId = "lesson_${lesson.id}"
        val isCompleted = userProgress[lessonFirestoreId] == true
        val isActive = if (index == 0) {
            // La primera lección siempre está activa
            true
        } else {
            // Para las demás lecciones, verificamos si la lección anterior está completada
            val prevLessonId = "lesson_${sampleLessons[index - 1].id}"
            userProgress[prevLessonId] == true
        }
        lesson.copy(isCompleted = isCompleted, isActive = isActive)
    }
    
    // Encontrar la próxima lección activa
    val nextLesson = lessonsWithProgress.find { lesson ->
        val lessonFirestoreId = "lesson_${lesson.id}"
        val isCompleted = userProgress[lessonFirestoreId] ?: false
        !isCompleted && lesson.isActive
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sección superior con estadísticas
        StatsSection(puntuacionTotal = puntuacionTotal, racha = racha)
        
        // Mostrar la tarjeta de próxima lección si hay una lección activa
        nextLesson?.let { lesson ->
            NextLessonCard(
                nextLesson = lesson,
                onLessonClick = {
                    val lessonId = "lesson_${lesson.id}"
                    navController.navigate("theory/$lessonId/$uid")
                }
            )
        }
        
        // Indicador de carga o contenido
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF58CC02))
            }
        } else {
            // Camino de lecciones con scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                LessonPath(
                    lessons = lessonsWithProgress,
                    onLessonClick = { lesson ->
                        if (lesson.isActive) {
                            selectedLesson = lesson
                            val lessonId = "lesson_${lesson.id}"
                            navController.navigate("theory/$lessonId/$uid")
                        }
                    },
                    modifier = Modifier.verticalScroll(scrollState)
                )
            }
        }
    }
}

@Composable
fun StatsSection(puntuacionTotal: Long, racha: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Bandera de idioma
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = Color.Blue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ES",
                style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
            )
        }
        
        // Puntuación total
        Box(
            modifier = Modifier
                .height(40.dp)
                .background(color = Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Puntuación",
                    tint = Color(0xFFFFC800)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$puntuacionTotal",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
        
        // Racha
        Box(
            modifier = Modifier
                .height(40.dp)
                .background(color = Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.fuego),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Racha",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$racha",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}



