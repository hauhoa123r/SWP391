	package org.project.model.response;
	
	import java.math.BigDecimal;
	import java.util.List;
	
	public class FinancialSummaryDTO {
	    private BigDecimal totalIncome;
	    private BigDecimal totalExpense;
	    private BigDecimal netProfit;
	
	    private List<TopProductDTO> topProducts;
	    private List<LowStockProductDTO> lowStockProducts;
	
	    // Getters and Setters
	    public BigDecimal getTotalIncome() { return totalIncome; }
	    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
	
	    public BigDecimal getTotalExpense() { return totalExpense; }
	    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
	
	    public BigDecimal getNetProfit() { return netProfit; }
	    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }
	
	    public List<TopProductDTO> getTopProducts() { return topProducts; }
	    public void setTopProducts(List<TopProductDTO> topProducts) { this.topProducts = topProducts; }
	
	    public List<LowStockProductDTO> getLowStockProducts() { return lowStockProducts; }
	    public void setLowStockProducts(List<LowStockProductDTO> lowStockProducts) { this.lowStockProducts = lowStockProducts; }
	}
