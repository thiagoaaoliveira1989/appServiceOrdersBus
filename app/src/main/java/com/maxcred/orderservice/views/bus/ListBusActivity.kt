package com.maxcred.orderservice.views.bus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.adaptador.BusListAdapter
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.repository.DatabaseDataSource
import com.maxcred.orderservice.views.dashboard.DashboardActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListBusActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_list)

        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

        recyclerView = findViewById(R.id.recycle_view_buslist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BusListAdapter(emptyList()) // Inicializa o adaptador com uma lista vazia
        recyclerView.adapter = adapter

        // Obtenha a lista de ônibus do repositório
        GlobalScope.launch(Dispatchers.Main) {
            repository.getAllBuses().observe(this@ListBusActivity) { busList ->
                busList?.let {
                    if (it.isNotEmpty()) {
                        println("Bus list size: ${busList.size}")

                        // Atualiza os itens do adaptador com a nova lista de ônibus
                        adapter.updateBusList(busList)
                    } else {
                        // Se a lista estiver vazia, não faz nada ou mostra uma mensagem para o usuário
                    }
                }
            }
        }
    }

    suspend fun deleteById(id: Long) {
        try {
            repository.deleteBus(id)
            Toast.makeText(this, "Veículo Deletado", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@ListBusActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()

        } catch (error: Exception) {
            Toast.makeText(this, "Erro ao Deletar Veículo", Toast.LENGTH_SHORT).show()
        }
    }
}
