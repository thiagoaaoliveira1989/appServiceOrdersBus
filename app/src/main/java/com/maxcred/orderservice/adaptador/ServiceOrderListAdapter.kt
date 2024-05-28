package com.maxcred.orderservice.adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity
import com.maxcred.orderservice.repository.BusRepository
import com.maxcred.orderservice.views.serviceOrder.ListServiceOrderActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceOrderListAdapter(
    private var serviceOrderList: List<ServiceOrderEntity>,
    private val busRepository: BusRepository
) : RecyclerView.Adapter<ServiceOrderListAdapter.ServiceOrderViewHolder>() {

    inner class ServiceOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idOrderTextView: TextView = itemView.findViewById(R.id.textIdOrder)
        val orderNumberTextView: TextView = itemView.findViewById(R.id.textOrderNumber)
        val kmBusTextView: TextView = itemView.findViewById(R.id.textKmBus)
        val startDateTextView: TextView = itemView.findViewById(R.id.textStartDate)
        val endDateTextView: TextView = itemView.findViewById(R.id.textEndDate)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        val mechanicTextView: TextView = itemView.findViewById(R.id.textMechanic)
        val supervisorTextView: TextView = itemView.findViewById(R.id.textSupervisor)
        val vehicleTextView: TextView = itemView.findViewById(R.id.textVehicle)
        val licensePlateTextView: TextView = itemView.findViewById(R.id.textLicensePlate)
        val btnGerarPdf: ImageButton = itemView.findViewById(R.id.btnPdfButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicerorders, parent, false)
        return ServiceOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceOrderViewHolder, position: Int) {
        val currentOrder = serviceOrderList[position]

        holder.idOrderTextView.text = currentOrder.id.toString()
        holder.orderNumberTextView.text = currentOrder.orderNumber
        holder.kmBusTextView.text = currentOrder.kmBus
        holder.startDateTextView.text = currentOrder.startDate
        holder.endDateTextView.text = currentOrder.endDate
        holder.descriptionTextView.text = currentOrder.description
        holder.mechanicTextView.text = currentOrder.mechanic
        holder.supervisorTextView.text = currentOrder.supervisor

        CoroutineScope(Dispatchers.Main).launch {
            val bus = busRepository.getBusById(currentOrder.busId)
            val vehicle = bus?.vehicle ?: "N/A"
            val licensePlate = bus?.licensePlate ?: "N/A"

            holder.vehicleTextView.text = vehicle
            holder.licensePlateTextView.text = licensePlate
        }

        holder.btnGerarPdf.setOnClickListener {
            val context = holder.itemView.context
            if (context is ListServiceOrderActivity) {
                context.checkAndGeneratePdf(currentOrder)
            }
        }
    }

    fun updateServiceOrderList(newOrderList: List<ServiceOrderEntity>) {
        serviceOrderList = newOrderList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return serviceOrderList.size
    }
}
