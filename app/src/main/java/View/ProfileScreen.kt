package View

import Model.AuthRepository
import Model.UserData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.codelingo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import Model.LessonRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val authRepository = remember { AuthRepository() }
    val lessonRepository = remember { LessonRepository() }
    val scope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    var userData by remember { mutableStateOf<UserData?>(null) }
    var racha by remember { mutableStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user?.uid) {
        if (user?.uid != null) {
            try {
                authRepository.getUserData(user.uid).fold(
                    onSuccess = { data ->
                        userData = data
                        scope.launch {
                            racha = lessonRepository.getRacha(user.uid)
                            isLoading = false
                        }
                    },
                    onFailure = { e ->
                        error = e.message
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Perfil",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.background_app)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.background_app))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.bkgn_button)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        ProfileField("Nombre") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userData?.nombre ?: "",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (userData?.hasCrown == true) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Corona",
                                        tint = Color(0xFFFFC800),
                                        modifier = Modifier.size(24.dp).padding(start = 4.dp)
                                    )
                                }
                            }
                        }

                        ProfileField("Apellidos") { 
                            Text(
                                text = userData?.apellidos ?: "",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        ProfileField("Email") { 
                             Text(
                                text = userData?.email ?: "",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                         }
                        ProfileField("Lenguaje de Programación") { 
                            Text(
                                text = userData?.lenguajeProgramacion ?: "",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                         }
                        ProfileField("Nivel") { 
                            Text(
                                text = userData?.nivelLenguaje ?: "",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                         }
                        ProfileField("Racha") { 
                            Text(
                                text = racha.toString(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                         }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("start") {
                                popUpTo("start") { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.bkgn_button)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Cerrar Sesión",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, valueContent: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
        valueContent()
    }
} 