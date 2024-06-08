package com.maxcred.orderservice.views.part

import android.annotation.SuppressLint
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
import com.maxcred.orderservice.data.db.entity.PartEntity
import com.maxcred.orderservice.repository.DatabaseDataSource
import kotlinx.coroutines.launch

class EditPartsActivity : AppCompatActivity() {

    private lateinit var edtQuantity: EditText
    private lateinit var edtCode: EditText
    private lateinit var edtDescriptionPart: EditText
    private lateinit var edtCost: EditText
    private lateinit var btnEditPart: Button
    private lateinit var selectEditPartsType: MaterialAutoCompleteTextView

    private lateinit var repository: DatabaseDataSource
    private var partId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_parts)

        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(
            database.registerDAO,
            database.busDAO,
            database.serviceOrderDAO,
            database.partDAO
        )

        edtQuantity = findViewById(R.id.edtEditQuantity)
        edtCode = findViewById(R.id.edtEditCode)
        edtDescriptionPart = findViewById(R.id.edtEditDescriptionPart)
        edtCost = findViewById(R.id.edtEditCost)
        btnEditPart = findViewById(R.id.btnEditPart)
        selectEditPartsType = findViewById(R.id.selectEditPartsType)

        partId = intent.getLongExtra("partId", -1)
        if (partId != -1L) {
            loadPartData(partId)
        }

        btnEditPart.setOnClickListener {
            if (validateFields()) {
                updatePart()
            }
        }
    }

    private fun loadPartData(partId: Long) {
        lifecycleScope.launch {
            val part = repository.getPartById(partId)
            part?.let {
                edtQuantity.setText(it.partQty)
                edtCode.setText(it.partCode)
                edtDescriptionPart.setText(it.partDescription)
                edtCost.setText(it.partCost)

                // Carregar o orçamento associado à peça
                loadServiceOrder(it.serviceOrderId)
            }
        }
    }

    private suspend fun loadServiceOrder(serviceOrderId: Long) {
        val serviceOrder = repository.getServiceOrderById(serviceOrderId)
        serviceOrder?.let {
            val budgets = listOf(it.orderNumber.toString())
            val adapter = ArrayAdapter(this@EditPartsActivity, android.R.layout.simple_dropdown_item_1line, budgets)
            selectEditPartsType.setAdapter(adapter)
            selectEditPartsType.setText(it.orderNumber.toString(), false)
        }
    }

    private fun validateFields(): Boolean {
        // Adicione a validação dos campos conforme necessário
        return true
    }

    @SuppressLint("ShowToast")
    private fun updatePart() {
        val part = PartEntity(
            id = partId,
            partQty = edtQuantity.text.toString(),
            partCode = edtCode.text.toString(),
            partDescription = edtDescriptionPart.text.toString(),
            partCost = edtCost.text.toString(),
            totalPartCostValue = "0.0", // Ajuste conforme necessário
            serviceOrderId = 1 // Exemplo de ID de orçamento, ajuste conforme necessário
        )

        lifecycleScope.launch {
            try {
                repository.updatePart(
                    part.id,
                    part.partQty,
                    part.partCode,
                    part.partDescription,
                    part.partCost.toString(),
                    part.totalPartCostValue.toString()
                )
                Toast.makeText(this@EditPartsActivity, "Peça atualizada com sucesso", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e("EditPartsActivity", "Erro ao atualizar a peça", e)
                Toast.makeText(this@EditPartsActivity, "Erro ao atualizar a peça", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
