package Model

data class UserData(
    val email: String = "",
    val password: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val lenguajeProgramacion: String = "",
    val nivelLenguaje: String = "",
    val tiempoDedicacion: String = "",
    val etapaInicial: Int = 1,
    val hasCrown: Boolean = false
) 