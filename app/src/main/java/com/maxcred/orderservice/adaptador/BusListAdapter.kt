package com.maxcred.orderservice.adaptador

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.entity.BusEntity
import com.maxcred.orderservice.views.bus.EditBusActivity
import com.maxcred.orderservice.views.bus.ListBusActivity
import com.maxcred.orderservice.views.serviceOrder.EditServiceOrderActivity
import com.maxcred.orderservice.views.serviceOrder.ListServiceOrderActivity
import kotlinx.coroutines.launch

class BusListAdapter(private var busList: List<BusEntity>) :
    RecyclerView.Adapter<BusListAdapter.BusViewHolder>() {

    inner class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.textId)
        val placaTextView: TextView = itemView.findViewById(R.id.textPlaca)
        val veiculoTextView: TextView = itemView.findViewById(R.id.textTipoVeiculo)
        val btnEditar: ImageButton = itemView.findViewById(R.id.editButtonBus)
        val btnDeletar: ImageButton = itemView.findViewById(R.id.deleteButtonBus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus, parent, false)
        return BusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val currentBus = busList[position]
        holder.idTextView.text = currentBus.id.toString()
        holder.placaTextView.text = currentBus.licensePlate
        holder.veiculoTextView.text = currentBus.vehicle



        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditBusActivity::class.java).apply {
                putExtra("BUS_ID", currentBus.id)
            }
            context.startActivity(intent)
        }

        holder.btnDeletar.setOnClickListener {
            val context = holder.itemView.context
            if (context is ListBusActivity) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Confirmação")
                    .setMessage("Tem certeza de que deseja excluir o Veículo?")
                    .setPositiveButton("Excluir") { dialog, _ ->
                        context.lifecycleScope.launch {
                            context.deleteById(currentBus.id)
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.show()
            }
        }


    }

    override fun getItemCount(): Int {
        return busList.size
    }

    // Função para atualizar a lista de ônibus
    fun updateBusList(newBusList: List<BusEntity>) {
        busList = newBusList
        notifyDataSetChanged()
    }
}
