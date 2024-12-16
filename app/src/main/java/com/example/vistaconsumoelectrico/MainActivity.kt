package com.example.vistaconsumoelectrico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var consumoActualText: TextView
    private lateinit var costoAproximadoText: TextView
    private lateinit var horasUsoText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRevisarConsumo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        consumoActualText = findViewById(R.id.consumo_actual)
        costoAproximadoText = findViewById(R.id.costo_aproximado)
        horasUsoText = findViewById(R.id.horas_uso)
        progressBar = findViewById(R.id.progressBar)
        btnRevisarConsumo = findViewById(R.id.btnConsumo)

        // Referencia a Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("arduino/temperatura")

        // Configurar botón para revisar consumo
        btnRevisarConsumo.setOnClickListener {
            revisarConsumo()
        }
    }

    private fun revisarConsumo() {
        // Escuchar cambios en tiempo real desde Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtener el valor del "consumo" (simulado como temperatura en Firebase)
                val consumo = snapshot.getValue(Float::class.java)
                if (consumo != null) {
                    // Mostrar el consumo en kWh
                    consumoActualText.text = "$consumo Kwh"

                    // Calcular el coste aproximado (150 por kWh)
                    val coste = consumo * 150
                    costoAproximadoText.text = "$${String.format("%.2f", coste)}"

                    // Simular las horas de uso (valor aleatorio entre 1 y 24)
                    val horasUso = Random.nextInt(1, 25)
                    horasUsoText.text = "$horasUso horas"

                    // Actualizar progreso en la barra de progreso
                    val progreso = (consumo / 10 * 100).toInt().coerceIn(0, 100)
                    progressBar.progress = progreso
                } else {
                    consumoActualText.text = "0 Kwh"
                    costoAproximadoText.text = "$0.00"
                    horasUsoText.text = "0 horas"
                    progressBar.progress = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                consumoActualText.text = "Error al obtener datos"
                costoAproximadoText.text = "--"
                horasUsoText.text = "--"
                progressBar.progress = 0
            }
        })
    }
}
