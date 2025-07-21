package org.project.repository;

import org.project.entity.SupplierTransactionItem;
import org.project.entity.SupplierTransactionItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SupplierTransactionItemRepository extends JpaRepository<SupplierTransactionItem, SupplierTransactionItemId> {

	@Query("SELECT COALESCE(SUM(st.unitPrice * st.quantity), 0) " +
		       "FROM SupplierTransactionItem st " +
		       "WHERE st.transaction.transactionDate BETWEEN :fromDate AND :toDate")
		BigDecimal sumExpensesBetween(@Param("fromDate") LocalDate fromDate,
		                               @Param("toDate") LocalDate toDate);


	List<SupplierTransactionItem> findByTransaction_TransactionDateBetween(LocalDate from, LocalDate to);

}
