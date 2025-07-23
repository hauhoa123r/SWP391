package org.project.service.base;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.repository.SupplierTransactionItemRepository;
import org.project.repository.SupplierTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Lớp cơ sở cung cấp các phương thức chung cho các service liên quan đến SupplierTransaction.
 */
public abstract class BaseTransactionService {

    protected final SupplierTransactionRepository transactionRepository;
    protected final SupplierEntityRepository supplierRepository;
    protected final InventoryManagerRepository inventoryManagerRepository;
    protected final ProductRepository productRepository;
    protected final SupplierTransactionItemRepository itemRepository;

    protected BaseTransactionService(SupplierTransactionRepository transactionRepository,
                                   SupplierEntityRepository supplierRepository,
                                   InventoryManagerRepository inventoryManagerRepository,
                                   ProductRepository productRepository,
                                   SupplierTransactionItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryManagerRepository = inventoryManagerRepository;
        this.productRepository = productRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Kiểm tra việc chuyển đổi trạng thái có hợp lệ không
     *
     * @param currentStatus Trạng thái hiện tại
     * @param newStatus     Trạng thái mới
     * @return true nếu việc chuyển đổi là hợp lệ, false nếu không
     */
    protected boolean isValidStatusTransition(SupplierTransactionStatus currentStatus, SupplierTransactionStatus newStatus) {
        // Logic kiểm tra chuyển đổi trạng thái chung
        if (currentStatus == newStatus) {
            return true; // Cho phép cập nhật với cùng trạng thái
        }

        switch (currentStatus) {
            // StockIn status transitions
            case WAITING_FOR_DELIVERY:
                return newStatus == SupplierTransactionStatus.RECEIVED || 
                       newStatus == SupplierTransactionStatus.REJECTED;
            
            case RECEIVED:
                return newStatus == SupplierTransactionStatus.INSPECTED || 
                       newStatus == SupplierTransactionStatus.REJECTED;
                       
            case INSPECTED:
                return newStatus == SupplierTransactionStatus.COMPLETED || 
                       newStatus == SupplierTransactionStatus.REJECTED;
            
            // StockOut status transitions
            case PREPARE_DELIVERY:
                return newStatus == SupplierTransactionStatus.DELIVERING || 
                       newStatus == SupplierTransactionStatus.REJECTED;
            
            case DELIVERING:
                return newStatus == SupplierTransactionStatus.DELIVERED || 
                       newStatus == SupplierTransactionStatus.REJECTED;
                       
            case DELIVERED:
                return newStatus == SupplierTransactionStatus.PENDING || 
                       newStatus == SupplierTransactionStatus.PAID ||
                       newStatus == SupplierTransactionStatus.REJECTED;
                       
            case PENDING:
                return newStatus == SupplierTransactionStatus.PAID || 
                       newStatus == SupplierTransactionStatus.REJECTED;
                       
            case PAID:
                return newStatus == SupplierTransactionStatus.COMPLETED || 
                       newStatus == SupplierTransactionStatus.REJECTED;

            case COMPLETED:
                return false; // Không thể thay đổi từ trạng thái hoàn thành
                
            case REJECTED:
                return false; // Không thể thay đổi từ trạng thái bị từ chối
                
            default:
                return false;
        }
    }

    /**
     * Lấy các giao dịch được lọc theo loại và danh sách trạng thái cho phép
     *
     * @param page           Số trang
     * @param size           Số lượng mục trên mỗi trang
     * @param keyword        Từ khóa tìm kiếm (tùy chọn)
     * @param status         Trạng thái cụ thể (tùy chọn)
     * @param allowedStatuses Danh sách trạng thái cho phép
     * @param transactionType Loại giao dịch
     * @return Trang kết quả SupplierTransactionsEntity
     */
    @Transactional(readOnly = true)
    protected Page<SupplierTransactionsEntity> getFilteredTransactionsEntities(int page, int size, String keyword,
                                                     String status, List<SupplierTransactionStatus> allowedStatuses,
                                                     SupplierTransactionType transactionType) {
        PageRequest pageRequest = PageRequest.of(page, size);
        
        // Nếu không có trạng thái được phép, trả về trang trống
        if (allowedStatuses == null || allowedStatuses.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        // Convert status string to enum if provided
        SupplierTransactionStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = SupplierTransactionStatus.valueOf(status);
                // Kiểm tra xem trạng thái có trong danh sách cho phép hay không
                if (!allowedStatuses.contains(statusEnum)) {
                    statusEnum = null; // Reset nếu trạng thái không được cho phép
                }
            } catch (IllegalArgumentException e) {
                statusEnum = null;
            }
        }

        // Get transactions with filters
        Page<SupplierTransactionsEntity> transactions;
        
        if (keyword != null && !keyword.isEmpty()) {
            if (statusEnum != null) {
                // Search by keyword and filter by specific status
                transactions = transactionRepository.findByTransactionTypeAndStatusAndSupplierEntityNameContainingIgnoreCase(
                        transactionType, statusEnum, keyword, pageRequest);
            } else {
                // Search by keyword and filter by list of allowed statuses
                transactions = transactionRepository.findByTransactionTypeAndStatusInAndSupplierEntityNameContainingIgnoreCase(
                        transactionType, allowedStatuses, keyword, pageRequest);
            }
        } else {
            if (statusEnum != null) {
                // Filter by specific status only
                transactions = transactionRepository.findByTransactionTypeAndStatus(
                        transactionType, statusEnum, pageRequest);
            } else {
                // Filter by list of allowed statuses only
                transactions = transactionRepository.findByTransactionTypeAndStatusIn(
                        transactionType, allowedStatuses, pageRequest);
            }
        }
        
        return transactions;
    }
    
    /**
     * Kiểm tra trạng thái trong danh sách trạng thái cho phép
     *
     * @param status Trạng thái cần kiểm tra
     * @param allowedStatuses Danh sách trạng thái cho phép
     * @return true nếu trạng thái trong danh sách cho phép, false nếu không
     */
    protected boolean isStatusAllowed(SupplierTransactionStatus status, Collection<SupplierTransactionStatus> allowedStatuses) {
        return allowedStatuses != null && allowedStatuses.contains(status);
    }
} 