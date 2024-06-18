package com.maxcred.orderservice.views.bus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.repository.DatabaseDataSource
import com.maxcred.orderservice.views.dashboard.DashboardActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterBusActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_bus)

        // Inicializando o banco de dados e o repositório
        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)


        // Define the options for the dropdown
        val vehicleTypes = arrayOf("Rodoviário", "Urbano", "Micro-ônibus")

        // Create an ArrayAdapter using the string array and a default dropdown layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, vehicleTypes)

        // Find the MaterialAutoCompleteTextView and set the adapter
        val autoCompleteTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectVehicleType)
        autoCompleteTextView.setAdapter(adapter)


        val btnRegister: Button = findViewById(R.id.btnRegisterBus)
        btnRegister.setOnClickListener {

            val vehicleEditText = findViewById<EditText>(R.id.selectVehicleType)
            val vehicle = vehicleEditText.text.toString()

            val licensePlateEditText = findViewById<EditText>(R.id.edtLicensePlate)
            val licensePlate = licensePlateEditText.text.toString()

            val numberCarEditText = findViewById<EditText>(R.id.edtNumberCar)
            val numberCar = numberCarEditText.text.toString()

            if (vehicle.isBlank() || licensePlate.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                // Aqui você pode chamar o método do repositório para verificar se a placa já está cadastrada
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val isLicensePlateExists = repository.isLicensePlateExists(licensePlate)
                        if (isLicensePlateExists) {
                            runOnUiThread {
                                Toast.makeText(this@RegisterBusActivity, "Veiculo já cadastrado!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val result = repository.insertBus(vehicle, licensePlate, numberCar)
                            runOnUiThread {
                                Toast.makeText(this@RegisterBusActivity, "Ônibus cadastrado", Toast.LENGTH_SHORT).show()
                                Log.i("RegisterBusActivity", "Ônibus cadastrado: ${result}")

                                // Retornar para a tela inicial após o cadastro bem-sucedido
                                val intent = Intent(this@RegisterBusActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish() // Opcional: finaliza a atividade atual para que não possa ser retornada com o botão de voltar
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("RegisterBusActivity", "Erro ao cadastrar ônibus", e)
                        runOnUiThread {
                            Toast.makeText(this@RegisterBusActivity, "Erro ao cadastrar ônibus", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
