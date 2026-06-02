package View

import Model.AuthRepository
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.codelingo.R

@Composable
fun RegisterScreen6(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.provideFactory(AuthRepository())
    )
) {
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
                text = "¿Cuál es tu meta diaria de aprendizaje?",
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
                    viewModel.updateTiempoDedicacion("5")
                    navController.navigate("registro7") 
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background_app)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(2.dp, color = colorResource(R.color.seleccionado))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.width(15.dp))

                    Text(
                        text = "5 min/día",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(Modifier.width(130.dp))

                    Text(
                        text = "Relajado",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { 
                    viewModel.updateTiempoDedicacion("10")
                    navController.navigate("registro7") 
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background_app)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(2.dp, color = colorResource(R.color.seleccionado))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.width(15.dp))

                    Text(
                        text = "10 min/día",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(Modifier.width(130.dp))

                    Text(
                        text = "Normal",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { 
                    viewModel.updateTiempoDedicacion("15")
                    navController.navigate("registro7") 
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background_app)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(2.dp, color = colorResource(R.color.seleccionado))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.width(15.dp))

                    Text(
                        text = "15 min/día",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(Modifier.width(140.dp))

                    Text(
                        text = "Serio",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { 
                    viewModel.updateTiempoDedicacion("20")
                    navController.navigate("registro7") 
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background_app)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(2.dp, color = colorResource(R.color.seleccionado))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.width(15.dp))

                    Text(
                        text = "20 min/día",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(Modifier.width(130.dp))

                    Text(
                        text = "Intenso",
                        modifier = Modifier.padding(5.dp),
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

