package View

import Model.AuthRepository
import Model.CompraResult
import Model.LessonRepository
import Model.UserData
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val lessonRepository = remember { LessonRepository() }
    val authRepository = remember { AuthRepository() } // Instanciar AuthRepository
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var userData by remember { mutableStateOf<UserData?>(null) } // Estado para los datos del usuario

    // Cargar datos del usuario al obtener el UID
    LaunchedEffect(uid) {
        if (uid != null) {
            authRepository.getUserData(uid).fold(
                onSuccess = { data -> userData = data },
                onFailure = { /* Manejar error si es necesario */ }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1824))
    ) {
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tienda",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Producto: Corona
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E2A3A)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star, // Usamos la estrella por ahora, si tienes un icono de corona mejor
                            contentDescription = "Corona",
                            tint = Color(0xFFFFC800),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Corona de Oro",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Muestra tu estatus",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            )
                        }
                        Text(
                            text = "5000 puntos",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFC800)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de compra/adquirido condicional
                    val hasCrown = userData?.hasCrown == true

                    Button(
                        onClick = {
                            if (uid != null && !hasCrown) { // Solo permitir comprar si no tiene la corona
                                scope.launch {
                                    when (lessonRepository.comprarCorona(uid)) {
                                        CompraResult.Success -> {
                                            snackbarHostState.showSnackbar("¡Corona comprada con éxito!")
                                            navController.navigate("profile")
                                        }
                                        CompraResult.InsufficientPoints -> {
                                            snackbarHostState.showSnackbar("¡No tienes suficientes puntos!")
                                        }
                                        CompraResult.AlreadyOwned -> {
                                            snackbarHostState.showSnackbar("¡Ya tienes esta corona!")
                                        }
                                        is CompraResult.Error -> {
                                            snackbarHostState.showSnackbar("Error al comprar la corona.")
                                        }
                                    }
                                }
                            } else if (hasCrown) {
                                // Si ya la tiene, no hace nada al hacer click o mostrar un mensaje
                                scope.launch { snackbarHostState.showSnackbar("Ya tienes esta corona.") }
                            } else {
                                scope.launch { snackbarHostState.showSnackbar("Usuario no autenticado.") }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasCrown) Color.Gray else Color(0xFF58CC02) // Cambia color si está adquirido
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !hasCrown // Deshabilita el botón si ya la tiene
                    ) {
                        if (hasCrown) {
                            // Envolvemos Icon y Text en un Row y centramos el Row verticalmente
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Adquirido",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Adquirido",
                                    style = TextStyle(
                                        color = Color.White, // Aseguramos el color blanco
                                        fontSize = 16.sp // Aseguramos el tamaño de fuente

                                    )
                                )
                            }
                        } else {
                            Text("Comprar")
                        }
                    }
                }
            }


        }
    }
} 