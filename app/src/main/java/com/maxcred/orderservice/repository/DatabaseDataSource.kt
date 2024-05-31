package com.maxcred.orderservice.repository

import androidx.lifecycle.LiveData
import com.maxcred.orderservice.data.db.dao.BusDAO
import com.maxcred.orderservice.data.db.dao.PartDAO
import com.maxcred.orderservice.data.db.dao.RegisterDAO
import com.maxcred.orderservice.data.db.dao.ServiceOrderDAO
import com.maxcred.orderservice.data.db.entity.BusEntity
import com.maxcred.orderservice.data.db.entity.PartEntity
import com.maxcred.orderservice.data.db.entity.RegisterEntity
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity

class DatabaseDataSource(
    private val registerDAO: RegisterDAO,
    private val busDAO: BusDAO,
    private val serviceOrderDAO: ServiceOrderDAO,
    private val partDAO: PartDAO,
) : RegisterRepository, BusRepository, ServiceOrderRepository, PartRepository {

    // Métodos para Register User
    override suspend fun isEmailExists(email: String): Boolean {
        return registerDAO.isEmailExists(email)
    }

    override suspend fun insertRegister(username: String, email: String, password: String): RegisterResult {
        val register = RegisterEntity(username = username, email = email, password = password)
        val id = registerDAO.insert(register)
        return RegisterResult(id, username, email, password)
    }

    override suspend fun updateRegister(id: Long, username: String, email: String, password: String) {
        val register = RegisterEntity(id = id, username = username, email = email, password = password)
        registerDAO.update(register)
    }

    override suspend fun deleteRegister(id: Long) {
        registerDAO.delete(id)
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun getAllRegister(): LiveData<List<RegisterEntity>> {
        return registerDAO.getAllRegister()
    }

    // Métodos para BusEntity
    override suspend fun isLicensePlateExists(licensePlate: String): Boolean {
        return busDAO.isLicensePlateExists(licensePlate)
    }

    override suspend fun getBusById(id: Long): BusEntity? {
        return busDAO.getBusById(id)
    }

    override suspend fun getBusIdByPlate(licensePlate: String): Long {
        return busDAO.getBusIdByPlate(licensePlate)
    }

    override suspend fun getBusByPlate(licensePlate: String): BusEntity? {
        return busDAO.getBusByPlate(licensePlate)
    }

    override suspend fun insertBus(vehicle: String, licensePlate: String, numberCar: String): Long {
        val busEntity = BusEntity(vehicle = vehicle, licensePlate = licensePlate, numberCar = numberCar)
        val id = busDAO.insertBus(busEntity)
        return id
    }

    override suspend fun updateBus(id: Long, vehicle: String, licensePlate: String, numberCar: String) {
        val busEntity = BusEntity(id = id, vehicle = vehicle, licensePlate = licensePlate, numberCar = numberCar)
        busDAO.updateBus(busEntity)
    }

    override suspend fun deleteBus(id: Long) {
        busDAO.deleteBus(id)
    }

    override suspend fun deleteAllBuses() {
        TODO("Not yet implemented")
    }

    override fun getAllBuses(): LiveData<List<BusEntity>> {
        return busDAO.getAllBus()
    }

    // Métodos para PartEntity
    override suspend fun insertPart(
        partQty: String, partCode: String?, partDescription: String?, partCost: String, totalPartCostValue: String,
        serviceOrderId: Long
    ): PartResult {
        val part = PartEntity(
            partQty = partQty,
            partCode = partCode,
            partDescription = partDescription,
            partCost = partCost,
            totalPartCostValue = totalPartCostValue,
            serviceOrderId = serviceOrderId
        )
        val id = partDAO.insert(part)
        return PartResult(id, partQty, partCode, partDescription, partCost, totalPartCostValue)
    }

    override suspend fun updatePart(
        id: Long, partQty: String, partCode: String?, partDescription: String?, partCost: String, totalPartCostValue: String
    ) {
        // Busca a parte pelo ID
        val existingPart = partDAO.getPartById(id)

        // Verifica se a parte existe
        if (existingPart != null) {
            // Atualiza os valores da parte existente
            val updatedPart = existingPart.copy(
                partQty = partQty,
                partCode = partCode,
                partDescription = partDescription,
                partCost = partCost,
                totalPartCostValue = totalPartCostValue
            )

            // Chama o método update com a parte atualizada
            partDAO.update(updatedPart)
        } else {
            // Trate o caso em que a parte não é encontrada (opcional)
            // Por exemplo, lançar uma exceção ou registrar um erro
        }
    }

    override suspend fun deletePart(id: Long) {
        partDAO.deleteById(id)
    }

    override suspend fun deleteAllParts() {
        TODO("Not yet implemented")
    }

    override suspend fun getPartById(id: Long): PartEntity? {
        return partDAO.getPartById(id)
    }

    override fun getAllParts(): LiveData<List<PartEntity>> {
        return partDAO.getAllParts()
    }

    override suspend fun getPartsForServiceOrder(serviceOrderId: Long): LiveData<List<PartEntity>> {
        return partDAO.getPartsForServiceOrder(serviceOrderId)
    }

    // Métodos para ServiceOrderEntity
    // Implementação do ServiceOrderRepository

    override suspend fun isServiceOrderNumberExists(orderNumber: Long): Boolean {
        return serviceOrderDAO.isServiceOrderNumberExists(orderNumber)
    }

    override suspend fun getServiceOrderById(id: Long): ServiceOrderResult? {
        val serviceOrder = serviceOrderDAO.getServiceOrderByOrderNumber(id)
        return serviceOrder?.let {
            ServiceOrderResult(
                orderNumber = it.orderNumber,
                kmBus = it.kmBus,
                startDate = it.startDate,
                endDate = it.endDate,
                description = it.description,
                mechanic = it.mechanic,
                encarregado = it.encarregado,
                busId = it.busId
            )
        }
    }

    override suspend fun insertServiceOrder(
        kmBus: String,
        startDate: String,
        endDate: String,
        description: String?,
        mechanic: String,
        encarregado: String,
        busId: Long
    ): ServiceOrderResult {
        val serviceOrder = ServiceOrderEntity(
            kmBus = kmBus,
            startDate = startDate,
            endDate = endDate,
            description = description,
            mechanic = mechanic,
            encarregado = encarregado,
            busId = busId
        )
        val id = serviceOrderDAO.insert(serviceOrder)
        return ServiceOrderResult(
            orderNumber = id,
            kmBus = kmBus,
            startDate = startDate,
            endDate = endDate,
            description = description,
            mechanic = mechanic,
            encarregado = encarregado,
            busId = busId
        )
    }

    override suspend fun updateServiceOrder(
        orderNumber: Long,
        kmBus: String,
        startDate: String,
        endDate: String,
        description: String?,
        mechanic: String,
        encarregado: String,
        busId: Long
    ) {
        val serviceOrder = ServiceOrderEntity(
            orderNumber = orderNumber,
            kmBus = kmBus,
            startDate = startDate,
            endDate = endDate,
            description = description,
            mechanic = mechanic,
            encarregado = encarregado,
            busId = busId
        )
        serviceOrderDAO.update(serviceOrder)
    }


    override suspend fun deleteByOrderNumber(orderNumber: Long) {
        serviceOrderDAO.deleteByOrderNumber(orderNumber)
    }

    override fun getAllServiceOrders(): LiveData<List<ServiceOrderEntity>> {
        return serviceOrderDAO.getAllServiceOrders()
    }

    override suspend fun getServiceOrderIdByPlate(orderNumber: Long): Long {
        return serviceOrderDAO.getServiceOrderIdByOrderNumber(orderNumber)
    }

}
