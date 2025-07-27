package org.project.service.impl;

import org.project.entity.MedicineEntity;
import org.project.entity.SupplierTransactionItemEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.StocksDTO;
import org.project.repository.SupplierTransactionsRepository;
import org.project.service.SupplierTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierTransactionServiceImpl implements SupplierTransactionService {
    //inject repo
    @Autowired
    private SupplierTransactionsRepository supplierTransactionsRepository;

    @Override
    public List<StocksDTO> loadMedicineStockInOrders() {
        // Bước 1: Truy xuất danh sách giao dịch loại IN
        List<SupplierTransactionsEntity> transactions =
                supplierTransactionsRepository.findAllByTransactionType(SupplierTransactionType.STOCK_IN)
                        .stream().toList(); // Đảm bảo có List

        // Bước 2: Xử lý từng transaction và item của nó
        return transactions.stream()
                .flatMap(transaction ->
                        transaction.getSupplierTransactionItemEntities().stream()
                                .filter(item -> item.getProductEntity().getMedicineEntity() != null)
                                .map(item -> new AbstractMap.SimpleEntry<>(transaction, item))
                )
                .map(entry -> {
                    SupplierTransactionsEntity transaction = entry.getKey();
                    SupplierTransactionItemEntity item = entry.getValue();
                    MedicineEntity medicine = (item.getProductEntity()).getMedicineEntity();

                    return new StocksDTO(
                            medicine.getId(),
                            medicine.getProductEntity().getName(),
                            "IN",
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getExpirationDate().toLocalDate(),
                            transaction.getTransactionDate().toLocalDateTime().toLocalDate(),
                            transaction.getSupplierEntity().getName()
                    );
                })
                .collect(Collectors.toList());
    }
}
