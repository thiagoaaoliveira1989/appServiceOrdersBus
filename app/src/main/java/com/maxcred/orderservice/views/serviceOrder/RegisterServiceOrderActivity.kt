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

class RegisterServiceOrderActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource

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
                val vehicleTypes = vehicles.map { it.licensePlate }.toTypedArray()

                // Create an ArrayAdapter using the string array and a default dropdown layout
                val adapterVehicle = ArrayAdapter(this@RegisterServiceOrderActivity, android.R.layout.simple_dropdown_item_1line, vehicleTypes)

                // Find the MaterialAutoCompleteTextView and set the adapter
                val autoCompleteVehicleTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectVehicleTypeServiceOrder)
                autoCompleteVehicleTextView.setAdapter(adapterVehicle)
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

            val vehicleEditText = findViewById<EditText>(R.id.selectVehicleTypeServiceOrder)
            val vehicle = vehicleEditText.text.toString()

            val orderNumberEditText = findViewById<EditText>(R.id.edtNumberServiceOrder)
            val orderNumber = orderNumberEditText.text.toString()

            val kmBusEditText = findViewById<EditText>(R.id.edtKm)
            val kmBus = kmBusEditText.text.toString()

            val startDateEditText = findViewById<EditText>(R.id.edtStartDate)
            val startDate = startDateEditText.text.toString()

            val endDateEditText = findViewById<EditText>(R.id.edtEndDate)
            val endDate = endDateEditText.text.toString()

            val descriptionEditText = findViewById<EditText>(R.id.edtDescriptionServiceOrder)
            val description = descriptionEditText.text.toString()

            val mechanicEditText = findViewById<EditText>(R.id.edtMechanic)
            val mechanic = mechanicEditText.text.toString()

            val supervisorEditText = findViewById<EditText>(R.id.selectSupervisor)
            val supervisor = supervisorEditText.text.toString()

            if (vehicle.isBlank() || orderNumber.isBlank() || kmBus.isBlank() || startDate.isBlank() || endDate.isBlank() || description.isBlank() || mechanic.isBlank() || supervisor.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val isServiceOrderNumberExists = repository.isServiceOrderNumberExists(orderNumber)
                        if (isServiceOrderNumberExists) {
                            runOnUiThread {
                                Toast.makeText(this@RegisterServiceOrderActivity, "Essa Ordem de Serviço já está cadastrada!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val busId = repository.getBusIdByPlate(vehicle) // Pega o ID do ônibus pela placa
                            val result = repository.insertServiceOrder(orderNumber, kmBus, startDate, endDate, description, mechanic, supervisor, busId)
                            runOnUiThread {
                                Toast.makeText(this@RegisterServiceOrderActivity, "Ordem de Serviço cadastrada", Toast.LENGTH_SHORT).show()
                                Log.i("RegisterServiceOrderActivity", "Ordem de Serviço cadastrada: id: ${result.id}, Nº Ordem de Serviço: ${result.orderNumber}")

                                val intent = Intent(this@RegisterServiceOrderActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterServiceOrderActivity", "Erro ao cadastrar ordem de serviço", e)
                        runOnUiThread {
                            Toast.makeText(this@RegisterServiceOrderActivity, "Erro ao cadastrar ordem de serviço", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
