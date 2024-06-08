package com.maxcred.orderservice.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.entity.PartEntity
import com.maxcred.orderservice.repository.ServiceOrderRepository
import com.maxcred.orderservice.views.part.ListPartsActivity
import com.maxcred.orderservice.views.serviceOrder.ListServiceOrderActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PartListAdapter(
    private var partList: List<PartEntity>,
    private val serviceOrderRepository: ServiceOrderRepository,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<PartListAdapter.PartViewHolder>() {

    // Define uma interface para o listener de edição
    interface OnEditClickListener {
        fun onEditClick(part: PartEntity)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(partId: Long)
    }


    // Listener de edição que será passado para o adaptador
    var onEditClickListener: OnEditClickListener? = null


    var onDeleteClickListener: OnDeleteClickListener? = null


    inner class PartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.textIdPartList)
        val serviceOrderIdTextView: TextView = itemView.findViewById(R.id.textOrderNumberPartList)
        val partQtyTextView: TextView = itemView.findViewById(R.id.textQtyPartList)
        val partCodeTextView: TextView = itemView.findViewById(R.id.textCodigoPartList)
        val partDescriptionTextView: TextView = itemView.findViewById(R.id.textDescricaoPartList)
        val partCostTextView: TextView = itemView.findViewById(R.id.textValorPartList)
        val totalPartCostTextView: TextView = itemView.findViewById(R.id.textTotalPartList)
        val btnEditar: ImageButton = itemView.findViewById(R.id.editButtonPartList)
        val btnDeletar: ImageButton = itemView.findViewById(R.id.deleteButtonPartList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parts, parent, false)
        return PartViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        val currentPart = partList[position]
        holder.idTextView.text = currentPart.id.toString()

        coroutineScope.launch(Dispatchers.Main) {
            val serviceOrder = serviceOrderRepository.getServiceOrderById(currentPart.serviceOrderId)
            val serviceOrderNumber = serviceOrder?.orderNumber ?: "N/A"

            holder.serviceOrderIdTextView.text = serviceOrderNumber.toString()
        }

        holder.partQtyTextView.text = currentPart.partQty
        holder.partCodeTextView.text = currentPart.partCode
        holder.partDescriptionTextView.text = currentPart.partDescription
        holder.partCostTextView.text = currentPart.partCost
        holder.totalPartCostTextView.text = currentPart.totalPartCostValue

        // Configura o listener do botão de edição
        holder.btnEditar.setOnClickListener {
            onEditClickListener?.onEditClick(currentPart)
        }

        // Configura o listener do botão de deletar
        holder.btnDeletar.setOnClickListener {
            val context = holder.itemView.context
            if (context is ListPartsActivity) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Confirmação")
                    .setMessage("Tem certeza de que deseja excluir esta peça?")
                    .setPositiveButton("Excluir") { dialog, _ ->
                        // Chama o método onDeleteClick do ouvinte de exclusão com o ID da parte
                        context.lifecycleScope.launch {
                            context.deleteById(currentPart.id)
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
        return partList.size
    }

    fun updatePartList(newPartList: List<PartEntity>) {
        partList = newPartList
        notifyDataSetChanged()
    }


}
