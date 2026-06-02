package View

import Model.LessonRepository
import Model.QuestionData
import ViewModel.QuestionViewModel
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    navController: NavController,
    lessonId: String,
    uid: String,
    viewModel: QuestionViewModel = viewModel(
        factory = QuestionViewModel.Factory(
            repository = LessonRepository(),
            lessonId = lessonId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var leccionFinalizada by remember { mutableStateOf(false) }
    
    LaunchedEffect(lessonId) {
        // Log.d("QuestionScreen", "Pantalla de preguntas para lessonId: $lessonId")
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Lección: ${uiState.lesson?.title ?: lessonId}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF58CC02)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color(0xFF0A0E21))
        ) {
            when {
                uiState.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF58CC02)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            color = Color.White,
                            style = TextStyle(fontSize = 18.sp),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "ID de lección: $lessonId",
                            color = Color.White.copy(alpha = 0.7f),
                            style = TextStyle(fontSize = 14.sp),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF58CC02)
                            )
                        ) {
                            Text("Volver al menú principal")
                        }
                    }
                }
                else -> {
                    QuestionContent(
                        currentQuestionIndex = uiState.currentQuestionIndex,
                        totalQuestions = uiState.lesson?.questions?.size ?: 0,
                        question = viewModel.currentQuestion,
                        selectedOptionIndex = uiState.selectedOptionIndex,
                        hasAnswered = uiState.hasAnswered,
                        isCorrect = uiState.isCorrect,
                        onOptionSelected = { viewModel.selectOption(it) },
                        onNextQuestion = {
                            val isLast = uiState.currentQuestionIndex >= (uiState.lesson?.questions?.size ?: 1) - 1
                            if (isLast && !leccionFinalizada) {
                                // Log.d("QuestionScreen", "Finalizando lección. Guardando progreso para $uid")
                                viewModel.guardarProgresoUsuario(uid)
                                leccionFinalizada = true
                                navController.popBackStack("home", inclusive = false)
                            } else {
                                viewModel.nextQuestion()
                            }
                        },
                        score = uiState.score
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionContent(
    currentQuestionIndex: Int,
    totalQuestions: Int,
    question: QuestionData?,
    selectedOptionIndex: Int,
    hasAnswered: Boolean,
    isCorrect: Boolean,
    onOptionSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    score: Int
) {
    if (question == null) {
        Text(
            text = "No hay preguntas disponibles",
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Información de progreso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pregunta ${currentQuestionIndex + 1}/$totalQuestions",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Puntuación: $score",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Línea de progreso
        LinearProgressIndicator(
            progress = (currentQuestionIndex.toFloat() + 1) / totalQuestions,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            color = Color(0xFF58CC02)
        )
        
        // Enunciado de la pregunta
        Text(
            text = question.enunciado,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Opciones de respuesta
        question.opciones.forEachIndexed { index, opcion ->
            OptionItem(
                option = opcion,
                index = index,
                isSelected = selectedOptionIndex == index,
                isCorrect = hasAnswered && index == question.respuestaCorrecta,
                isIncorrect = hasAnswered && selectedOptionIndex == index && index != question.respuestaCorrecta,
                onClick = { if (!hasAnswered) onOptionSelected(index) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botón para continuar
        if (hasAnswered) {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58CC02)
                )
            ) {
                Text(
                    text = if (currentQuestionIndex < totalQuestions - 1) "Siguiente pregunta" else "Finalizar lección",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Mensaje de retroalimentación
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(
                        color = if (isCorrect) Color(0xFF58CC02) else Color(0xFFCC0202),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isCorrect) "Correcto" else "Incorrecto",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCorrect) "¡Correcto!" else "Incorrecto",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OptionItem(
    option: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isCorrect -> Color(0xFF58CC02)
        isIncorrect -> Color(0xFFCC0202)
        isSelected -> Color(0xFF3949AB)
        else -> Color(0xFF1F1F3D)
    }
    
    val borderColor = when {
        isSelected -> Color(0xFF3949AB)
        else -> Color(0xFF3F3F3F)
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index + 1}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = Color(0xFF3949AB),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = option,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            
            if (isCorrect) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correcto",
                    tint = Color.White
                )
            } else if (isIncorrect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Incorrecto",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionScreenPreview() {
    // Ejemplo de una pregunta para la vista previa
    val exampleQuestion = QuestionData(
        id = "q1",
        enunciado = "¿Cómo se dice 'hola' en inglés?",
        opciones = listOf("Hello", "Goodbye", "Thanks", "Sorry"),
        respuestaCorrecta = 0
    )

    QuestionContent(
        currentQuestionIndex = 0,
        totalQuestions = 10,
        question = exampleQuestion,
        selectedOptionIndex = 0,
        hasAnswered = true,
        isCorrect = true,
        onOptionSelected = {},
        onNextQuestion = {},
        score = 1
    )
}