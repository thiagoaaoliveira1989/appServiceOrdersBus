package com.maxcred.orderservice.views.part

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.adapter.PartListAdapter
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.repository.DatabaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListPartsActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PartListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parts_list)

        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(
            database.registerDAO,
            database.busDAO,
            database.serviceOrderDAO,
            database.partDAO
        )

        recyclerView = findViewById<RecyclerView>(R.id.recycle_view_partlist) ?: throw IllegalStateException("RecyclerView not found")
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PartListAdapter(emptyList(), repository, lifecycleScope) // Passa o lifecycleScope para o adaptador
        recyclerView.adapter = adapter

        // Obtenha a lista de partes do repositório
        lifecycleScope.launch(Dispatchers.Main) {
            repository.getAllParts().observe(this@ListPartsActivity) { partList ->
                partList?.let {
                    if (it.isNotEmpty()) {
                        println("Part list size: ${partList.size}")

                        // Atualiza os itens do adaptador com a nova lista de partes
                        adapter.updatePartList(partList)
                    } else {
                        // Se a lista estiver vazia, não faz nada ou mostra uma mensagem para o usuário
                    }
                }
            }
        }
    }

}
