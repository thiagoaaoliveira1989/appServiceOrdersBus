package com.maxcred.orderservice.views.part


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

class RegisterPartActivity: AppCompatActivity()  {
    private lateinit var repository: DatabaseDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_part)

        // Inicializando o banco de dados e o repositório
        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

        // Observe the LiveData from the repository to get the list of buses
        repository.getAllServiceOrders().observe(this) { orders ->
            try {
                val ordersTypes = orders.map { it.orderNumber }.toTypedArray()

                // Create an ArrayAdapter using the string array and a default dropdown layout
                val adapterOrders = ArrayAdapter(this@RegisterPartActivity, android.R.layout.simple_dropdown_item_1line, ordersTypes)

                // Find the MaterialAutoCompleteTextView and set the adapter
                val autoCompleteVehicleTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectPartsType)
                autoCompleteVehicleTextView.setAdapter(adapterOrders)

            } catch (e: Exception) {
                Log.e("RegisterPartsActivity", "Erro ao carregar peças", e)
            }
        }


        val btnRegister: Button = findViewById(R.id.btnRegisterPart)
        btnRegister.setOnClickListener {
            val orderNumberEditText = findViewById<EditText>(R.id.selectPartsType)
            val orderNumber = orderNumberEditText.text.toString()

            val partQtyEditText = findViewById<EditText>(R.id.edtQuantity)

            val partDescriptionEditText = findViewById<EditText>(R.id.edtDescriptionPart)
            val partDescription = partDescriptionEditText.text.toString()

            val partCodeEditText = findViewById<EditText>(R.id.edtCode)
            val partCode = partCodeEditText.text.toString()

            val partCostEditText = findViewById<EditText>(R.id.edtCost)

            val partQty = partQtyEditText.text.toString().toIntOrNull() ?: 0
            val partCost = partCostEditText.text.toString().toDoubleOrNull() ?: 0.0

            val totalPartCostValue = partQty * partCost

            // Converter o valor total de volta para uma string
            val totalPartCostValueString = totalPartCostValue.toString()

            if (orderNumber.isBlank() || partQty == 0 || partCode.isBlank() || partDescription.isBlank() ||  partCost == 0.0) {
                Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val isServiceOrderNumberExists = repository.isServiceOrderNumberExists(orderNumber)
                        if (!isServiceOrderNumberExists) {
                            runOnUiThread {
                                Toast.makeText(this@RegisterPartActivity, "Numero da ordem de serviço não existe", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val serviceOrderId = repository.getServiceOrderIdByPlate(orderNumber) // Pega o ID do ônibus pela placa
                            val result = repository.insertPart(partQty.toString(), partCode, partDescription, partCost.toString(), totalPartCostValueString, serviceOrderId)
                            runOnUiThread {
                                Toast.makeText(this@RegisterPartActivity, "Peças cadastradas", Toast.LENGTH_SHORT).show()
                                Log.i("RegisterPartsActivity", "Peças cadastrada: id: ${result.id}")

                                val intent = Intent(this@RegisterPartActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterPartsActivity", "Erro ao cadastrar Peças", e)
                        runOnUiThread {
                            Toast.makeText(this@RegisterPartActivity, "Erro ao cadastrar Peças", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}