package Model

data class LessonData(
    val id: String = "",
    val title: String = "",
    val etapa: Int = 1,
    val icon: String = "star",
    val isCompleted: Boolean = false,
    val isActive: Boolean = false,
    val theory: String = "", // Teoría de la lección
    val questions: List<QuestionData> = listOf()
) 