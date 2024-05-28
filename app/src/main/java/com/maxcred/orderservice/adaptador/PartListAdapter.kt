package com.maxcred.orderservice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.entity.PartEntity
import com.maxcred.orderservice.repository.ServiceOrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PartListAdapter(
    private var partList: List<PartEntity>,
    private val serviceOrderRepository: ServiceOrderRepository,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<PartListAdapter.PartViewHolder>() {

    inner class PartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.textIdPartList)
        val serviceOrderIdTextView: TextView = itemView.findViewById(R.id.textOrderNumberPartList) // queria que ao inves do id pegar o numero da ordem de seriço direto que esta relacionada a esse numero, tabela = order_service
        val partQtyTextView: TextView = itemView.findViewById(R.id.textQtyPartList)
        val partCodeTextView: TextView = itemView.findViewById(R.id.textCodigoPartList)
        val partDescriptionTextView: TextView = itemView.findViewById(R.id.textDescricaoPartList)
        val partCostTextView: TextView = itemView.findViewById(R.id.textValorPartList)
        val totalPartCostTextView: TextView = itemView.findViewById(R.id.textTotalPartList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parts, parent, false)
        return PartViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        val currentPart = partList[position]
        holder.idTextView.text = currentPart.id.toString()

        // Obter a ordem de serviço correspondente à parte atual
        coroutineScope.launch(Dispatchers.Main) {
            val serviceOrder = serviceOrderRepository.getServiceOrderById(currentPart.serviceOrderId)
            val serviceOrderNumber = serviceOrder?.orderNumber ?: "N/A"

            holder.serviceOrderIdTextView.text = serviceOrderNumber
        }

        holder.partQtyTextView.text = currentPart.partQty
        holder.partCodeTextView.text = currentPart.partCode
        holder.partDescriptionTextView.text = currentPart.partDescription
        holder.partCostTextView.text = currentPart.partCost
        holder.totalPartCostTextView.text = currentPart.totalPartCostValue
    }

    override fun getItemCount(): Int {
        return partList.size
    }

    fun updatePartList(newPartList: List<PartEntity>) {
        partList = newPartList
        notifyDataSetChanged()
    }
}
