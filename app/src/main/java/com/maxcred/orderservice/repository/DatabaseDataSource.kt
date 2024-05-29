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
import com.maxcred.orderservice.data.db.entity.ServiceOrderWithParts

class DatabaseDataSource (
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
        registerDAO.deleteAll()
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

    override suspend fun insertBus(vehicle: String, licensePlate: String): BusResult {
        val busEntity = BusEntity(0, vehicle, licensePlate)
        val id = busDAO.insertBus(busEntity)
        return BusResult(id, vehicle, licensePlate)
    }

    override suspend fun updateBus(id: Long, vehicle: String, licensePlate: String) {
        val busEntity = BusEntity(id, vehicle, licensePlate)
        busDAO.updateBus(busEntity)
    }

    override suspend fun deleteBus(id: Long) {
        busDAO.deleteBus(id)
    }

    override suspend fun deleteAllBuses() {
        busDAO.deleteAllBuses()
    }

    override fun getAllBuses(): LiveData<List<BusEntity>> {
        return busDAO.getAllBus()
    }

    // Métodos para PartEntity
    override suspend fun insertPart(
        partQty: String, partCode: String?, partDescription: String?, partCost: String, totalPartCostValue: String,
        serviceOrderId: Long): PartResult {
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
        partDAO.deleteAll()
    }

    override suspend fun getPartById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getServiceOrderById(id: Long): ServiceOrderResult? {
        val serviceOrder = serviceOrderDAO.getServiceOrderById(id)

        return serviceOrder?.let {
            ServiceOrderResult(
                it.id,
                it.orderNumber ?: "", // Se it.orderNumber for nulo, use uma string vazia
                it.kmBus ?: "", // Se it.kmBus for nulo, use uma string vazia
                it.startDate ?: "", // Se it.startDate for nulo, use uma string vazia
                it.endDate ?: "", // Se it.endDate for nulo, use uma string vazia
                it.description, // Não adicionamos verificação para description, pois já é permitido ser nulo
                it.mechanic ?: "", // Se it.mechanic for nulo, use uma string vazia
                it.supervisor ?: "", // Se it.supervisor for nulo, use uma string vazia
                it.busId
            )
        }
    }

    override fun getAllParts(): LiveData<List<PartEntity>> {
        return partDAO.getAllParts()
    }

    override suspend fun getPartsForServiceOrder(serviceOrderId: Long): LiveData<List<PartEntity>> {
        return partDAO.getPartsForServiceOrder(serviceOrderId)
    }

    // Métodos para ServiceOrderEntity
    override suspend fun isServiceOrderNumberExists(orderNumber: String): Boolean {
        return serviceOrderDAO.isServiceOrderNumberExists(orderNumber)
    }



    override suspend fun insertServiceOrder(
        orderNumber: String, kmBus: String, startDate: String, endDate: String, description: String?,
        mechanic: String, supervisor: String, busId: Long): ServiceOrderResult {
        val serviceOrder = ServiceOrderEntity(
            orderNumber = orderNumber,
            kmBus = kmBus,
            startDate = startDate,
            endDate = endDate,
            description = description,
            mechanic = mechanic,
            supervisor = supervisor,
            busId = busId
        )
        val id = serviceOrderDAO.insert(serviceOrder)
        return ServiceOrderResult(id, orderNumber, kmBus, startDate, endDate, description, mechanic, supervisor, busId)
    }

    override suspend fun updateServiceOrder(
        id: Long, orderNumber: String, kmBus: String, startDate: String, endDate: String, description: String?,
        mechanic: String, supervisor: String, busId: Long) {
        val serviceOrder = ServiceOrderEntity(
            id = id,
            orderNumber = orderNumber,
            kmBus = kmBus,
            startDate = startDate,
            endDate = endDate,
            description = description,
            mechanic = mechanic,
            supervisor = supervisor,
            busId = busId
        )
        serviceOrderDAO.update(serviceOrder)
    }

    override suspend fun deleteServiceOrder(id: Long) {
        serviceOrderDAO.deleteById(id)
    }

    override  fun getAllServiceOrders(): LiveData<List<ServiceOrderEntity>> {
        return serviceOrderDAO.getAllServiceOrders()
    }


    override suspend fun getServiceOrderIdByPlate(orderNumber: String): Long {
        return serviceOrderDAO.getServiceOrderIdByPlate(orderNumber)
    }
}
