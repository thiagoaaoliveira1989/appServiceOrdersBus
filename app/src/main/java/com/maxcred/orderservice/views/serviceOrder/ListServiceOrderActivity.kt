package com.maxcred.orderservice.views.serviceOrder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.adaptador.ServiceOrderListAdapter
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity
import com.maxcred.orderservice.repository.DatabaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListServiceOrderActivity : AppCompatActivity() {
    private lateinit var repository: DatabaseDataSource
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServiceOrderListAdapter

    companion object {
        const val STORAGE_PERMISSION_CODE = 23
    }

    private lateinit var storageActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serviceorders_list)

        // Inicialização do RecyclerView
        recyclerView = findViewById(R.id.recycle_view_serviceorderlist)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialização do ActivityResultLauncher para gerenciamento de permissões de armazenamento
        storageActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        Log.d("listServiceActivity", "Gerenciar permissões de armazenamento externo concedidas")
                        // Gera o PDF após a concessão das permissões de armazenamento
                    } else {
                        Toast.makeText(this, "Permissões de armazenamento negadas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        // Inicialização do repositório
        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(
            database.registerDAO,
            database.busDAO,
            database.serviceOrderDAO,
            database.partDAO
        )

        // Inicialização do adaptador com a lógica para gerar PDF
        adapter = ServiceOrderListAdapter(emptyList(), repository)
        recyclerView.adapter = adapter

        // Observa as alterações na lista de ordens de serviço
        lifecycleScope.launch(Dispatchers.Main) {
            repository.getAllServiceOrders().observe(this@ListServiceOrderActivity) { serviceOrderList ->
                serviceOrderList?.let {
                    if (it.isNotEmpty()) {
                        println("Service order list size: ${serviceOrderList.size}")
                        // Atualiza os itens do adaptador com a nova lista de ordens de serviço
                        adapter.updateServiceOrderList(serviceOrderList)
                    } else {
                        // Se a lista estiver vazia, não faz nada ou mostra uma mensagem para o usuário
                    }
                }
            }
        }
    }

    // Verifica permissões de armazenamento e gera o PDF
    fun checkAndGeneratePdf(serviceOrder: ServiceOrderEntity) {
        if (checkStoragePermissions()) {
            generatePdf(serviceOrder) // Gera o PDF se as permissões de armazenamento estiverem concedidas
        } else {
            requestForStoragePermissions() // Solicita permissões de armazenamento se não estiverem concedidas
        }
    }

    // Verifica se as permissões de armazenamento estão concedidas
    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    // Solicita permissões de armazenamento ao usuário
    private fun requestForStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                ListServiceOrderActivity.STORAGE_PERMISSION_CODE
            )
        }
    }

    // Manipula o resultado da solicitação de permissões
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ListServiceOrderActivity.STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Gera o PDF se as permissões forem concedidas
            } else {
                Toast.makeText(this, "Permissões de armazenamento negadas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para gerar o PDF
    private fun generatePdf(serviceOrder: ServiceOrderEntity) {
        Log.d("GERAR PDF", "Gerando PDF para a ordem de serviço: ${serviceOrder.orderNumber}")
        // Aqui você pode acessar as informações necessárias da ordem de serviço usando serviceOrder
        // e gerar o PDF conforme necessário
    }
}
