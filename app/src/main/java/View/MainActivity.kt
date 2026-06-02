package View

import Model.AuthRepository
import Model.LessonRepository
import ViewModel.RegisterViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.codelingo.ui.theme.CodelingoTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val lessonRepository = LessonRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        
        val currentUser = auth.currentUser
        val initialDestination = if (currentUser != null) "home" else "start"
        
        setContent {
            CodelingoTheme {
                val navController = rememberNavController()
                val registerViewModel: RegisterViewModel = viewModel(
                    factory = RegisterViewModel.provideFactory(AuthRepository())
                )
                val scope = rememberCoroutineScope()
                
                // Estado para controlar la navegación inicial
                var startDestination by remember { mutableStateOf(initialDestination) }
                
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("home") { HomeScreen(navController = navController) }
                    composable("start") { StartScreen(navController = navController) }
                    composable("login") { LoginScreen(navController = navController) }
                    composable("register") { RegisterScreen(navController = navController, viewModel = registerViewModel) }
                    composable("registro2") { RegisterScreen2(navController = navController, viewModel = registerViewModel) }
                    composable("registro3") { RegisterScreen3(navController = navController, viewModel = registerViewModel) }
                    composable("registro4") { RegisterScreen4(navController = navController, viewModel = registerViewModel) }
                    composable("registro5") { RegisterScreen5(navController = navController, viewModel = registerViewModel) }
                    composable("registro6") { RegisterScreen6(navController = navController, viewModel = registerViewModel) }
                    composable("registro7") { RegisterScreen7(navController = navController, viewModel = registerViewModel) }
                    composable("registro8") { RegisterScreen8(navController = navController, viewModel = registerViewModel) }
                    composable("registro9") { RegisterScreen9(navController = navController, viewModel = registerViewModel) }
                    composable("profile") { ProfileScreen(navController = navController) }
                    composable("racha") { RachaScreen(navController = navController) }
                    
                    // Ruta para la pantalla de la tienda
                    composable("shop") { ShopScreen(navController = navController) }

                    // Ruta para la pantalla de teoría
                    composable(
                        route = "theory/{lessonId}/{uid}",
                        arguments = listOf(
                            navArgument("lessonId") { type = NavType.StringType },
                            navArgument("uid") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                        val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                        var theory by remember { mutableStateOf("") }
                        var isLoadingTheory by remember { mutableStateOf(true) }

                        LaunchedEffect(lessonId) {
                            scope.launch {
                                isLoadingTheory = true
                                val lesson = lessonRepository.getLessonWithQuestions(lessonId)
                                theory = lesson?.theory ?: "No hay teoría disponible para esta lección."
                                isLoadingTheory = false
                            }
                        }

                        TheoryScreen(
                            navController = navController,
                            lessonId = lessonId,
                            uid = uid,
                            theory = theory,
                            isLoading = isLoadingTheory
                        )
                    }
                    
                    // Ruta para la pantalla de preguntas
                    composable(
                        route = "question/{lessonId}/{uid}",
                        arguments = listOf(
                            navArgument("lessonId") { type = NavType.StringType },
                            navArgument("uid") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                        val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                        QuestionScreen(
                            navController = navController,
                            lessonId = lessonId,
                            uid = uid
                        )
                    }
                }
            }
        }
    }
}

