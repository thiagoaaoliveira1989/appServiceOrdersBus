package com.maxcred.orderservice.views.serviceOrder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.repository.DatabaseDataSource
import com.maxcred.orderservice.views.dashboard.DashboardActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class RegisterServiceOrderActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource
    private var selectedVehicleId: Long = -1 // Inicializa como -1 para indicar que nenhum veículo foi selecionado ainda

    @SuppressLint("MissingInflatedId", "LongLogTag", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_serviceorder_form)

        // Inicializando o banco de dados e o repositório
        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

        // Observe the LiveData from the repository to get the list of buses
        repository.getAllBuses().observe(this) { vehicles ->
            try {
                val vehicleTypes = vehicles.map { it.numberCar }.toTypedArray()

                // Create an ArrayAdapter using the string array and a default dropdown layout
                val adapterVehicle = ArrayAdapter(this@RegisterServiceOrderActivity, android.R.layout.simple_dropdown_item_1line, vehicleTypes)

                // Find the MaterialAutoCompleteTextView and set the adapter
                val autoCompleteVehicleTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectVehicleTypeServiceOrder)
                autoCompleteVehicleTextView.setAdapter(adapterVehicle)

                // Set onItemClickListener to retrieve the selected vehicle ID
                autoCompleteVehicleTextView.setOnItemClickListener { _, _, position, _ ->
                    selectedVehicleId = vehicles[position].id // Obtém o ID do veículo selecionado
                }
            } catch (e: Exception) {
                Log.e("RegisterServiceOrderActivity", "Erro ao carregar veículos", e)
            }
        }

        // Observe the LiveData from the repository to get the list of users
        repository.getAllRegister().observe(this) { users ->
            try {
                val supervisorTypes = users.map { it.username }.toTypedArray()

                // Create an ArrayAdapter using the string array and a default dropdown layout
                val adapterSupervisor = ArrayAdapter(this@RegisterServiceOrderActivity, android.R.layout.simple_dropdown_item_1line, supervisorTypes)

                // Find the MaterialAutoCompleteTextView and set the adapter
                val autoCompleteSupervisorTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectSupervisor)
                autoCompleteSupervisorTextView.setAdapter(adapterSupervisor)

            } catch (e: Exception) {
                Log.e("RegisterServiceOrderActivity", "Erro ao carregar supervisores", e)
            }
        }

        val btnRegister: Button = findViewById(R.id.btnRegisterServiceOrder)
        btnRegister.setOnClickListener {
            // Check if a vehicle has been selected
            if (selectedVehicleId != -1L) {
                // Continue com o processamento usando selectedVehicleId
                val kmBusEditText = findViewById<EditText>(R.id.edtKm)
                val kmBus = kmBusEditText.text.toString()

                val startDateEditText = findViewById<EditText>(R.id.edtStartDate)
                val startDate = startDateEditText.text.toString()

                val endDateEditText = findViewById<EditText>(R.id.edtEndDate)
                val endDate = endDateEditText.text.toString()

                val descriptionEditText = findViewById<EditText>(R.id.edtDescriptionServiceOrder)
                val description = descriptionEditText.text.toString().uppercase(Locale.getDefault())

                val mechanicEditText = findViewById<EditText>(R.id.edtMechanic)
                val mechanic = mechanicEditText.text.toString().uppercase(Locale.getDefault())

                val supervisorEditText = findViewById<EditText>(R.id.selectSupervisor)
                val encarregado = supervisorEditText.text.toString().uppercase(Locale.getDefault())

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val result = repository.insertServiceOrder(kmBus, startDate, endDate, description, mechanic, encarregado, selectedVehicleId)
                        runOnUiThread {
                            Toast.makeText(this@RegisterServiceOrderActivity, "Ordem de Serviço cadastrada", Toast.LENGTH_SHORT).show()
                            Log.i("RegisterServiceOrderActivity", "Ordem de Serviço cadastrada: id: ${result}, Nº Ordem de Serviço: ${result.orderNumber}")

                            val intent = Intent(this@RegisterServiceOrderActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterServiceOrderActivity", "Erro ao cadastrar ordem de serviço", e)
                        runOnUiThread {
                            Toast.makeText(this@RegisterServiceOrderActivity, "Erro ao cadastrar ordem de serviço", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@RegisterServiceOrderActivity, "Por favor, selecione um veículo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


