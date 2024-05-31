package com.maxcred.orderservice.adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.dao.BusDAO
import com.maxcred.orderservice.data.db.dao.PartDAO
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity
import com.maxcred.orderservice.utils.OrderServiceReport
import com.maxcred.orderservice.utils.PdfUtils
import com.maxcred.orderservice.views.serviceOrder.ListServiceOrderActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceOrderListAdapter(
    private var serviceOrderList: List<ServiceOrderEntity>,
    private var busRepository: BusDAO,
    private var partRepository: PartDAO,

    ) : RecyclerView.Adapter<ServiceOrderListAdapter.ServiceOrderViewHolder>() {

    inner class ServiceOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idOrderTextView: TextView = itemView.findViewById(R.id.textOrderNumber)
        val kmBusTextView: TextView = itemView.findViewById(R.id.textKmBus)
        val startDateTextView: TextView = itemView.findViewById(R.id.textStartDate)
        val endDateTextView: TextView = itemView.findViewById(R.id.textEndDate)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        val mechanicTextView: TextView = itemView.findViewById(R.id.textMechanic)
        val supervisorTextView: TextView = itemView.findViewById(R.id.textSupervisor)
        val vehicleTextView: TextView = itemView.findViewById(R.id.textVehicle)
        val licensePlateTextView: TextView = itemView.findViewById(R.id.textLicensePlate)
        val numberCarTextView: TextView = itemView.findViewById(R.id.textNumberCar)
        val btnGerarPdf: ImageButton = itemView.findViewById(R.id.btnPdfButton)
        val btnEditar: ImageButton = itemView.findViewById(R.id.editButtonOrder)
        val btnDeletar: ImageButton = itemView.findViewById(R.id.deleteButtonOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicerorders, parent, false)
        return ServiceOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceOrderViewHolder, position: Int) {
        val currentOrder = serviceOrderList[position]

        holder.idOrderTextView.text = currentOrder.orderNumber.toString()
        holder.kmBusTextView.text = currentOrder.kmBus
        holder.startDateTextView.text = currentOrder.startDate
        holder.endDateTextView.text = currentOrder.endDate
        holder.descriptionTextView.text = currentOrder.description
        holder.mechanicTextView.text = currentOrder.mechanic
        holder.supervisorTextView.text = currentOrder.encarregado

        CoroutineScope(Dispatchers.Main).launch {
            val bus = busRepository.getBusById(currentOrder.busId)
            val vehicle = bus?.vehicle ?: "N/A"
            val licensePlate = bus?.licensePlate ?: "N/A"
            val numberCar = bus?.numberCar ?: "N/A"

            holder.vehicleTextView.text = vehicle
            holder.licensePlateTextView.text = licensePlate
            holder.numberCarTextView.text = numberCar
        }

        holder.btnGerarPdf.setOnClickListener {
            val context = holder.itemView.context
            if (context is ListServiceOrderActivity) {
                context.checkAndGeneratePdf(currentOrder)
            }

            // Aqui chamamos a função para gerar o PDF dentro de um bloco de coroutine
            CoroutineScope(Dispatchers.Main).launch {
                // Agora, dentro do bloco de coroutine, podemos chamar funções suspensas
                val bus = withContext(Dispatchers.IO) {
                    busRepository.getBusById(currentOrder.busId)
                }
                val vehicle = bus?.vehicle ?: "N/A"
                val licensePlate = bus?.licensePlate ?: "N/A"
                val numberCar = bus?.numberCar ?: "N/A"



                val partsLiveData = partRepository.getPartsForServiceOrder(currentOrder.orderNumber)
                partsLiveData.observe(context as ListServiceOrderActivity) { parts ->
                    val partsList = parts.map {
                        OrderServiceReport.Peca(
                            quantidade = it.partQty.toInt(),
                            codigo = it.partCode ?: "",
                            descricao = it.partDescription ?: "",
                            custo = (it.partCost),
                            custoTotal = it.totalPartCostValue,
                        )
                    }

                    val orderServiceReport = OrderServiceReport(
                        orderNumber = currentOrder.orderNumber,
                        veiculo = vehicle,
                        km = currentOrder.kmBus,
                        placa = licensePlate,
                        numberCar = numberCar,
                        dataAbertura = currentOrder.startDate ?: "",
                        dataFechamento = currentOrder.endDate ?: "",
                        descricaoServico = currentOrder.description ?: "",
                        pecasUtilizadas = partsList,
                        mecanico = currentOrder.mechanic ?: "",
                        encarregado = currentOrder.encarregado ?: ""
                    )

                    PdfUtils.generatePdf(holder.itemView.context, orderServiceReport)
                }
            }
        }

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context
        }


        holder.btnDeletar.setOnClickListener {
            val context = holder.itemView.context
            if (context is ListServiceOrderActivity) {
                context.lifecycleScope.launch {
                    context.deleteByOrderNumber(currentOrder.orderNumber)
                }
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
