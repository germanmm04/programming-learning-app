package View

import Model.AuthRepository
import ViewModel.RegisterUiState
import ViewModel.RegisterViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.codelingo.R

@Composable
fun RegisterScreen8(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.provideFactory(AuthRepository())
    )
) {
    // Observar el estado de la UI
    val uiState = viewModel.uiState.collectAsState()

    // Efecto para manejar la navegación después del registro exitoso
    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            is RegisterUiState.Success -> {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background_app))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "¿Desde dónde quieres comenzar?",
                modifier = Modifier
                    .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
                color = Color.White,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { 
                    navController.navigate("registro9") 
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background_app)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(2.dp, color = colorResource(R.color.seleccionado))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.java),
                        contentDescription = "Java Icon",
                        modifier = Modifier
                            .size(50.dp),
                        tint = Color.White,
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Desde el principio",
                            modifier = Modifier.padding(start = 10.dp),
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Completa la lección más fácil del curso",
                            modifier = Modifier.padding(start = 10.dp),
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { 
                    navController.navigate("registro9") 
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background_app)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(2.dp, color = colorResource(R.color.seleccionado))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.java),
                        contentDescription = "Java Icon",
                        modifier = Modifier
                            .size(50.dp),
                        tint = Color.White,
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Descubrir mi nivel",
                            modifier = Modifier.padding(start = 10.dp),
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Te recomendaremos desde donde empezar a aprender",
                            modifier = Modifier.padding(start = 10.dp),
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            // Mostrar error si existe
            if (uiState.value is RegisterUiState.Error) {
                Text(
                    text = (uiState.value as RegisterUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Mostrar loading si está cargando
            if (uiState.value is RegisterUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White
                )
            }
        }
    }
}

