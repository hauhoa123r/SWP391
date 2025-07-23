package org.project.service.base;

import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface cơ sở cho các service liên quan đến giao dịch nhà cung cấp.
 * 
 * @param <T> Kiểu DTO sẽ được sử dụng (SupplierInDTO hoặc SupplierOutDTO)
 */
public interface BaseSupplierTransactionService<T> {

    /**
     * Lấy tất cả các giao dịch
     * 
     * @return Danh sách các DTO
     */
    List<T> getAllTransactions();
    
    /**
     * Lấy trang các giao dịch với lọc tùy chọn
     * 
     * @param page Số trang (0-based)
     * @param size Kích thước trang
     * @param keyword Từ khóa tìm kiếm (tùy chọn)
     * @param status Lọc theo trạng thái (tùy chọn)
     * @return Trang kết quả
     */
    Page<T> getAllTransactions(int page, int size, String keyword, String status);
    
    /**
     * Lấy trang các giao dịch với các tùy chọn lọc nâng cao
     * 
     * @param page Số trang (0-based)
     * @param size Kích thước trang
     * @param status Lọc theo trạng thái (tùy chọn)
     * @param search Từ khóa tìm kiếm (tùy chọn)
     * @param type Lọc theo loại (tùy chọn)
     * @return Trang kết quả
     */
    Page<T> getFilteredTransactions(int page, int size, String status, String search, String type);
    
    /**
     * Lấy trang các giao dịch được lọc theo danh sách trạng thái cụ thể
     * 
     * @param page Số trang (0-based)
     * @param size Kích thước trang
     * @param status Lọc theo một trạng thái cụ thể (tùy chọn)
     * @param search Từ khóa tìm kiếm (tùy chọn)
     * @param type Lọc theo loại (tùy chọn)
     * @param allowedStatuses Danh sách các trạng thái cho phép
     * @return Trang kết quả
     */
    Page<T> getFilteredTransactionsForView(int page, int size, String status, String search, 
                                         String type, List<SupplierTransactionStatus> allowedStatuses);
    
    /**
     * Lấy các giao dịch được lọc theo loại và trạng thái (dùng cho hiển thị hóa đơn)
     * 
     * @param page Số trang (0-based)
     * @param size Kích thước trang
     * @param keyword Từ khóa tìm kiếm (tùy chọn)
     * @param status Lọc theo trạng thái (tùy chọn)
     * @param allowedStatuses Danh sách các trạng thái cho phép
     * @param transactionType Loại giao dịch
     * @return Trang kết quả
     */
    Page<T> getFilteredTransactions(int page, int size, String keyword, String status, 
                                  List<SupplierTransactionStatus> allowedStatuses,
                                  SupplierTransactionType transactionType);
    
    /**
     * Lấy giao dịch theo ID
     * 
     * @param id ID giao dịch
     * @return DTO giao dịch hoặc null nếu không tìm thấy
     */
    T getTransactionById(Long id);
    
    /**
     * Tạo giao dịch mới
     * 
     * @param dto DTO chứa thông tin giao dịch cần tạo
     * @return DTO của giao dịch đã tạo
     */
    T createTransaction(T dto);
    
    /**
     * Cập nhật giao dịch
     * 
     * @param id ID giao dịch cần cập nhật
     * @param dto DTO chứa thông tin cập nhật
     * @return DTO của giao dịch đã cập nhật
     */
    T updateTransaction(Long id, T dto);
    
    /**
     * Cập nhật trạng thái giao dịch
     * 
     * @param id ID giao dịch cần cập nhật
     * @param status Trạng thái mới
     * @return DTO của giao dịch đã cập nhật
     */
    T updateTransactionStatus(Long id, String status);
    
    /**
     * Xóa giao dịch
     * 
     * @param id ID giao dịch cần xóa
     */
    void deleteTransaction(Long id);
} 