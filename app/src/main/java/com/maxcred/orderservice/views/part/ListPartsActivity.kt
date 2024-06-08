package com.maxcred.orderservice.views.part

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.adapter.PartListAdapter
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.data.db.entity.PartEntity
import com.maxcred.orderservice.repository.DatabaseDataSource
import com.maxcred.orderservice.views.dashboard.DashboardActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListPartsActivity : AppCompatActivity(), PartListAdapter.OnDeleteClickListener {

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
        adapter = PartListAdapter(emptyList(), repository, lifecycleScope)
        recyclerView.adapter = adapter

        // Configurar o listener de edição
        adapter.onEditClickListener = object : PartListAdapter.OnEditClickListener {
            override fun onEditClick(part: PartEntity) {
                val intent = Intent(this@ListPartsActivity, EditPartsActivity::class.java)
                intent.putExtra("partId", part.id)
                startActivity(intent)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            repository.getAllParts().observe(this@ListPartsActivity) { partList ->
                partList?.let {
                    if (it.isNotEmpty()) {
                        adapter.updatePartList(partList)
                    }
                }
            }
        }
    }

    override fun onDeleteClick(id: Long) {
        lifecycleScope.launch {
            deleteById(id)
        }
    }

  suspend fun deleteById(id: Long) {
        try {
            repository.deletePart(id)
            Toast.makeText(this, "Peça Deletada", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@ListPartsActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()

        } catch (error: Exception) {
            Toast.makeText(this, "Erro ao Deletar Peça", Toast.LENGTH_SHORT).show()
        }
    }




}
