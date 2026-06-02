package Model

data class QuestionData(
    val id: String = "",
    val enunciado: String = "",
    val opciones: List<String> = listOf(),
    val respuestaCorrecta: Int = 0
) 