package com.maxcred.orderservice.views.serviceOrder

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity
import com.maxcred.orderservice.repository.DatabaseDataSource
import com.maxcred.orderservice.views.dashboard.DashboardActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class EditServiceOrderActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource
    private var serviceOrderId: Long = -1
    private var selectedVehicleId: Long = -1
    private var supervisor: String? = null

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service_order)

        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

        serviceOrderId = intent.getLongExtra("SERVICE_ORDER_ID", -1)
        if (serviceOrderId == -1L) {
            Toast.makeText(this, "Erro ao carregar ordem de serviço", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configure the adapter for the vehicle selection field
        val autoCompleteVehicleTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectVehicleTypeEditServiceOrder)
        repository.getAllBuses().observe(this) { vehicles ->
            try {
                val vehicleTypes = vehicles.map { it.numberCar }.toTypedArray()
                val adapterVehicle = ArrayAdapter(this@EditServiceOrderActivity, android.R.layout.simple_dropdown_item_1line, vehicleTypes)
                autoCompleteVehicleTextView.setAdapter(adapterVehicle)
                autoCompleteVehicleTextView.setOnItemClickListener { _, _, position, _ ->
                    selectedVehicleId = vehicles[position].id
                }
            } catch (e: Exception) {
                Log.e("EditServiceOrderActivity", "Erro ao carregar veículos", e)
            }
        }

        // Initialize date fields
        val startDateEditText = findViewById<EditText>(R.id.edtStartDateEdit)
        val endDateEditText = findViewById<EditText>(R.id.edtEndDateEdit)

        startDateEditText.setOnClickListener {
            showDatePickerDialog(startDateEditText)
        }

        endDateEditText.setOnClickListener {
            showDatePickerDialog(endDateEditText)
        }

        // Load service order details
        loadServiceOrderDetails(serviceOrderId)

        val btnSave: Button = findViewById(R.id.btnSaveServiceOrderEdit)
        btnSave.setOnClickListener {
            if (selectedVehicleId != -1L) {
                val kmBusEditText = findViewById<EditText>(R.id.edtKmEdit)
                val kmBus = kmBusEditText.text.toString()
                val startDate = startDateEditText.text.toString()
                val endDate = endDateEditText.text.toString()
                val descriptionEditText = findViewById<EditText>(R.id.edtDescriptionServiceOrderEdit)
                val description = descriptionEditText.text.toString()
                val mechanicEditText = findViewById<EditText>(R.id.edtMechanicEdit)
                val mechanic = mechanicEditText.text.toString()

                supervisor?.let { supervisorName ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val serviceOrder = ServiceOrderEntity(
                                orderNumber = serviceOrderId,
                                kmBus = kmBus,
                                startDate = startDate,
                                endDate = endDate,
                                description = description,
                                mechanic = mechanic,
                                encarregado = supervisorName,
                                busId = selectedVehicleId
                            )
                            repository.updateServiceOrder(
                                serviceOrderId,
                                kmBus,
                                startDate,
                                endDate,
                                description.uppercase(Locale.getDefault()),
                                mechanic.uppercase(Locale.getDefault()),
                                supervisorName.uppercase(Locale.getDefault()),
                                selectedVehicleId
                            )
                            runOnUiThread {
                                Toast.makeText(this@EditServiceOrderActivity, "Ordem de Serviço atualizada", Toast.LENGTH_SHORT).show()
                                Log.i("EditServiceOrderActivity", "Ordem de Serviço atualizada: id: ${serviceOrderId}")
                                val intent = Intent(this@EditServiceOrderActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } catch (e: Exception) {
                            Log.e("EditServiceOrderActivity", "Erro ao atualizar ordem de serviço", e)
                            runOnUiThread {
                                Toast.makeText(this@EditServiceOrderActivity, "Erro ao atualizar ordem de serviço", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } ?: run {
                    Toast.makeText(this@EditServiceOrderActivity, "Erro ao obter o supervisor", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@EditServiceOrderActivity, "Por favor, selecione um veículo", Toast.LENGTH_SHORT).show()
            }
        }

        // Observar o LiveData do usuário logado
        repository.getAllRegister().observe(this, Observer { users ->
            users?.let {
                // Quando o usuário logado é atualizado, atualize o nome do supervisor
                if (users.isNotEmpty()) {
                    supervisor = users.first().username
                } else {
                    Log.e("EditServiceOrderActivity", "Nenhum usuário encontrado")
                }
            }
        })
    }

    @SuppressLint("LongLogTag")
    private fun loadServiceOrderDetails(serviceOrderId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val serviceOrder = repository.getServiceOrderById(serviceOrderId)
                serviceOrder?.let { order ->
                    runOnUiThread {
                        findViewById<EditText>(R.id.edtKmEdit).setText(order.kmBus)
                        findViewById<EditText>(R.id.edtStartDateEdit).setText(order.startDate)
                        findViewById<EditText>(R.id.edtEndDateEdit).setText(order.endDate)
                        findViewById<EditText>(R.id.edtDescriptionServiceOrderEdit).setText(order.description)
                        findViewById<EditText>(R.id.edtMechanicEdit).setText(order.mechanic)

                        // Find the vehicle by id and set the text in the autoCompleteVehicleTextView
                        val autoCompleteVehicleTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectVehicleTypeEditServiceOrder)
                        repository.getAllBuses().observe(this@EditServiceOrderActivity) { vehicles ->
                            val selectedVehicle = vehicles.find { it.id == order.busId }
                            selectedVehicle?.let {
                                autoCompleteVehicleTextView.setText(it.numberCar, false)
                                selectedVehicleId = it.id
                            }
                        }
                    }
                } ?: run {
                    runOnUiThread {
                        Toast.makeText(this@EditServiceOrderActivity, "Ordem de serviço não encontrada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                Log.e("EditServiceOrderActivity", "Erro ao carregar detalhes da ordem de serviço", e)
                runOnUiThread {
                    Toast.makeText(this@EditServiceOrderActivity, "Erro ao carregar detalhes da ordem de serviço", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                editText.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}
