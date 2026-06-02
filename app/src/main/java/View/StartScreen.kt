package View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.codelingo.R

@Composable
fun StartScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background_app))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .background(color = colorResource(id = R.color.background_app), shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            // Texto "¿Ya tienes una cuenta?"
            Text(
                text = "¿Ya tienes una cuenta?",
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally).padding(bottom = 10.dp),
                color = Color.White,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Botón "Ingresar"
            Button(
                onClick = {
                    // Acción para ingresar
                    navController.navigate("login") // Cambia la ruta según corresponda
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.bkgn_button)), shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Ingresar", color = Color.White,
                    style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ))
            }

            // Línea separadora
            HorizontalDivider(
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Texto "¿Es tu primera vez?"
            Text(
                text = "¿Es tu primera vez?",
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally).padding(bottom = 10.dp),
                color = Color.White,
                style = TextStyle(

                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Botón "Empieza ahora"
            Button(
                onClick = {
                    // Acción para registrarse
                    navController.navigate("register") // Cambia la ruta según corresponda
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.bkgn_button)), shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Empieza ahora",
                    style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ))
            }
        }
    }
}


