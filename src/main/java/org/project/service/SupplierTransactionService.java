package org.project.service;

import org.project.model.dto.StocksDTO;

import java.util.List;

public interface SupplierTransactionService {
    List<StocksDTO> loadMedicineStockInOrders();
}
