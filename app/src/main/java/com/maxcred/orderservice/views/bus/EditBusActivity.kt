package com.maxcred.orderservice.views.bus

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.data.db.entity.BusEntity
import com.maxcred.orderservice.repository.DatabaseDataSource
import com.google.android.material.textfield.MaterialAutoCompleteTextView // Importação necessária
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditBusActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource
    private lateinit var busEntity: BusEntity

    private lateinit var edtVehicleType: MaterialAutoCompleteTextView
    private lateinit var edtLicensePlate: EditText
    private lateinit var edtNumberCar: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_bus)

        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

        edtVehicleType = findViewById(R.id.selectVehicleType)
        edtLicensePlate = findViewById(R.id.edtLicensePlate)
        edtNumberCar = findViewById(R.id.edtNumberCar)
        btnSave = findViewById(R.id.btnSaveBusEdit)

        val busId = intent.getLongExtra("BUS_ID", -1)
        if (busId == -1L) {
            Toast.makeText(this, "Erro ao carregar informações do ônibus", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Obtenha as informações do ônibus pelo ID
        GlobalScope.launch(Dispatchers.Main) {
            busEntity = repository.getBusById(busId) ?: run {
                Toast.makeText(this@EditBusActivity, "Ônibus não encontrado", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            // Preencha os campos com as informações do ônibus
            edtLicensePlate.setText(busEntity.licensePlate)
            edtNumberCar.setText(busEntity.numberCar)

            // Obtenha a lista de tipos de veículos
            val vehicleTypes = arrayOf("Rodoviario", "Urbano", "MicroOnibus") // Substitua isso pela sua lógica para obter os tipos de veículos do banco de dados

            // Crie um adapter com a lista de tipos de veículos
            val adapter = ArrayAdapter(this@EditBusActivity, android.R.layout.simple_dropdown_item_1line, vehicleTypes)

            // Defina o adapter para o campo de seleção de veículo
            edtVehicleType.setAdapter(adapter)

            // Defina o valor padrão para o campo de seleção de veículo como o tipo de veículo do ônibus
            val vehicleIndex = vehicleTypes.indexOf(busEntity.vehicle)
            if (vehicleIndex != -1) {
                edtVehicleType.setText(vehicleTypes[vehicleIndex], false) // O segundo parâmetro indica que a alteração não deve ser notificada ao usuário
            }
        }

        // Configurar o botão para salvar as alterações
        btnSave.setOnClickListener {
            val vehicleType = edtVehicleType.text.toString()
            val licensePlate = edtLicensePlate.text.toString()
            val numberCar = edtNumberCar.text.toString()

            // Verificar se algum campo está em branco
            if (vehicleType.isBlank() || licensePlate.isBlank() || numberCar.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Atualizar as informações do ônibus no banco de dados
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    repository.updateBus(busEntity.id, vehicleType, licensePlate, numberCar)
                    Toast.makeText(this@EditBusActivity, "Ônibus atualizado com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@EditBusActivity, "Erro ao atualizar ônibus", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
