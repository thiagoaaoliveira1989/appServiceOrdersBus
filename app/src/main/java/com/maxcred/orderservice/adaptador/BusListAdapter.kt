package com.maxcred.orderservice.adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.entity.BusEntity

class BusListAdapter(private var busList: List<BusEntity>) :
    RecyclerView.Adapter<BusListAdapter.BusViewHolder>() {

    inner class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.textId)
        val placaTextView: TextView = itemView.findViewById(R.id.textPlaca)
        val veiculoTextView: TextView = itemView.findViewById(R.id.textTipoVeiculo)
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
