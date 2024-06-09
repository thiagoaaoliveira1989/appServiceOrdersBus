package com.maxcred.orderservice.views.part

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

class RegisterPartActivity : AppCompatActivity() {
    private lateinit var repository: DatabaseDataSource
    private var orderNumber: Long = -1 // Inicializa como -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_part)

        // Inicializando o banco de dados e o repositório
        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

        // Observe the LiveData from the repository to get the list of service orders
        repository.getAllServiceOrders().observe(this) { orders ->
            try {
                val ordersTypes = orders.map { it.orderNumber }.toTypedArray()

                // Create an ArrayAdapter using the string array and a default dropdown layout
                val adapterOrders = ArrayAdapter(this@RegisterPartActivity, android.R.layout.simple_dropdown_item_1line, ordersTypes)

                // Find the MaterialAutoCompleteTextView and set the adapter
                val autoCompleteOrderNumberTextView = findViewById<MaterialAutoCompleteTextView>(R.id.selectPartsType)
                autoCompleteOrderNumberTextView.setAdapter(adapterOrders)

                // Set onItemClickListener to retrieve the selected order number
                autoCompleteOrderNumberTextView.setOnItemClickListener { _, _, position, _ ->
                    orderNumber = orders[position].orderNumber
                }
            } catch (e: Exception) {
                Log.e("RegisterPartsActivity", "Erro ao carregar ordens de serviço", e)
            }
        }

        val btnRegister: Button = findViewById(R.id.btnRegisterPart)
        btnRegister.setOnClickListener {
            val partQtyEditText = findViewById<EditText>(R.id.edtQuantity)
            val partDescriptionEditText = findViewById<EditText>(R.id.edtDescriptionPart)
            val partCodeEditText = findViewById<EditText>(R.id.edtCode)
            val partCostEditText = findViewById<EditText>(R.id.edtCost)

            val partQty = partQtyEditText.text.toString().toIntOrNull() ?: 0
            val partDescription = partDescriptionEditText.text.toString().uppercase(Locale.getDefault())
            val partCode = partCodeEditText.text.toString()
            val partCost = partCostEditText.text.toString().toDoubleOrNull() ?: 0.0

            val totalPartCostValue = partQty * partCost
            val totalPartCostValueString = totalPartCostValue.toString()

            if (orderNumber == null || partQty == 0 || partDescription.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos de Quantidade e Descrição", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val isServiceOrderNumberExists = repository.isServiceOrderNumberExists(orderNumber)
                        if (!isServiceOrderNumberExists) {
                            runOnUiThread {
                                Toast.makeText(this@RegisterPartActivity, "Número da ordem de serviço não existe", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val serviceOrderId = repository.getServiceOrderIdByPlate(orderNumber)
                            val result = repository.insertPart(partQty.toString(), partCode, partDescription, partCost.toString(), totalPartCostValueString, serviceOrderId)

                            runOnUiThread {
                                AlertDialog.Builder(this@RegisterPartActivity)
                                    .setMessage("Deseja adicionar outra peça?")
                                    .setPositiveButton("Sim") { _: DialogInterface, _: Int ->
                                        // Limpa os campos após cadastrar a peça
                                        partQtyEditText.text.clear()
                                        partDescriptionEditText.text.clear()
                                        partCodeEditText.text.clear()
                                        partCostEditText.text.clear()
                                    }
                                    .setNegativeButton("Não") { _: DialogInterface, _: Int ->
                                        // Se o usuário disser não, volta para o dashboard
                                        val intent = Intent(this@RegisterPartActivity, DashboardActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterPartsActivity", "Erro ao cadastrar peças", e)
                        runOnUiThread {
                            Toast.makeText(this@RegisterPartActivity, "Erro ao cadastrar peças", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
