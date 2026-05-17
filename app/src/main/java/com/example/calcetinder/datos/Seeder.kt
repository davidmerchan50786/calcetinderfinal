package com.example.calcetinder.datos

import android.content.Context
import com.example.calcetinder.modelo.Calcetin
import com.example.calcetinder.modelo.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object Seeder {

    fun poblarSiVacio(context: Context) {
        val prefs = context.getSharedPreferences("calcetinder", Context.MODE_PRIVATE)
        if (prefs.getBoolean("seeded", false)) return

        val pkg = context.packageName
        fun img(nombre: String) = "android.resource://$pkg/drawable/$nombre"

        val db = CalcetinderDB.obtenerDB(context)
        val repo = Repositorio(db.usuarioDAO(), db.calcetinDAO(), db.matchDAO())

        CoroutineScope(Dispatchers.IO).launch {
            val existentes = repo.obtenerTodosCalcetines().first()
            if (existentes.isNotEmpty()) {
                prefs.edit().putBoolean("seeded", true).apply()
                return@launch
            }

            // ── CARLOS, Madrid ────────────────────────────────────────────
            // Publica los calcetines de sus cajones que ya no tienen par
            val id1 = repo.insertarUsuario(
                Usuario(nombre = "Carlos", email = "carlos@calcetinder.com",
                        contrasena = "123456", ciudad = "Madrid")
            ).toInt()

            repo.insertarCalcetin(Calcetin(usuarioId = id1,
                nombre = "El Negro de Oficina",
                descripcion = "Sobrevivió a tres lavadoras. Su par no. " +
                        "Formal, discreto, sin manchas visibles. " +
                        "Busca otro calcetin tranquilo con quien compartir cajón.",
                color = "Negro", material = "Algodón peinado",
                imagenUri = img("sock_negro")))

            repo.insertarCalcetin(Calcetin(usuarioId = id1,
                nombre = "El Rojo Navideño",
                descripcion = "Regalo de empresa del 2021. Vinieron en pareja pero " +
                        "uno se perdió en el intercambio de Reyes. Este quedó solo. " +
                        "Sale poco, pero cuando sale lo da todo.",
                color = "Rojo", material = "Poliéster suave",
                imagenUri = img("sock_rojo")))

            // ── LUCIA, Barcelona ──────────────────────────────────────────
            val id2 = repo.insertarUsuario(
                Usuario(nombre = "Lucia", email = "lucia@calcetinder.com",
                        contrasena = "123456", ciudad = "Barcelona")
            ).toInt()

            repo.insertarCalcetin(Calcetin(usuarioId = id2,
                nombre = "El Lila del Tirón",
                descripcion = "Se separó de su par cuando alguien metió la mano " +
                        "al cajón con prisa y los dispersó. No se ha vuelto a ver. " +
                        "Este está listo para una nueva oportunidad. Sin rencores.",
                color = "Morado", material = "Lana suave",
                imagenUri = img("sock_morado")))

            repo.insertarCalcetin(Calcetin(usuarioId = id2,
                nombre = "El Verde de la Mudanza",
                descripcion = "Viajaron en la misma caja pero llegaron a destinos distintos. " +
                        "Lleva dos años en el cajón de los calcetines raros esperando. " +
                        "No exige que sean iguales. Solo que sean.",
                color = "Verde", material = "Algodón orgánico",
                imagenUri = img("sock_verde")))

            // ── MARCOS, Valencia ──────────────────────────────────────────
            val id3 = repo.insertarUsuario(
                Usuario(nombre = "Marcos", email = "marcos@calcetinder.com",
                        contrasena = "123456", ciudad = "Valencia")
            ).toInt()

            repo.insertarCalcetin(Calcetin(usuarioId = id3,
                nombre = "El Azul del Vestuario",
                descripcion = "Su par se quedó en el gimnasio. Alguien se lo llevó por error. " +
                        "O con intención. Marcos nunca lo sabrá. Este quedó solo " +
                        "en la bolsa de deporte. Sano, sin agujeros, listo para correr.",
                color = "Azul", material = "Microfibra técnica",
                imagenUri = img("sock_azul")))

            repo.insertarCalcetin(Calcetin(usuarioId = id3,
                nombre = "El Amarillo Maratón",
                descripcion = "Acabaron la carrera juntos pero en la bolsa de llegada " +
                        "uno de los dos desapareció. Este llegó a casa solo, " +
                        "con medalla y sin pareja. La ironía no se le escapa.",
                color = "Amarillo", material = "Algodón con elastano",
                imagenUri = img("sock_amarillo")))

            // ── ANA, Bilbao ───────────────────────────────────────────────
            val id4 = repo.insertarUsuario(
                Usuario(nombre = "Ana", email = "ana@calcetinder.com",
                        contrasena = "123456", ciudad = "Bilbao")
            ).toInt()

            repo.insertarCalcetin(Calcetin(usuarioId = id4,
                nombre = "El Blanco Invisible",
                descripcion = "Uno de veinte iguales. Cuando su par desapareció nadie supo " +
                        "cuál era cuál. Ana intenta que encuentre pareja aunque no sea " +
                        "el original. A estas alturas cualquier blanco sirve.",
                color = "Blanco", material = "Algodón básico",
                imagenUri = img("sock_verde")))

            repo.insertarCalcetin(Calcetin(usuarioId = id4,
                nombre = "El Gris Jubilado",
                descripcion = "Su par fue retirado del servicio activo por tener un agujero " +
                        "en el talón. Este sigue en perfecto estado pero se quedó sin destino. " +
                        "Ana no tiene corazón para tirarlo. Busca un nuevo compañero de cajón.",
                color = "Gris", material = "Algodón 80% Poliéster 20%",
                imagenUri = img("sock_negro")))

            prefs.edit().putBoolean("seeded", true).apply()
        }
    }
}
