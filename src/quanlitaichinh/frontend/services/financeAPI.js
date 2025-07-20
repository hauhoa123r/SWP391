import axios from "axios";

const BASE_URL = "http://localhost:8080/api/finance";

// ⚙️ Hàm tạo khoảng ngày mặc định: 90 ngày gần nhất
const getDefaultDateRange = () => {
  const today = new Date();
  const to = today.toISOString().split("T")[0];

  const fromDate = new Date();
  fromDate.setDate(today.getDate() - 90);
  const from = fromDate.toISOString().split("T")[0];

  return { from, to };
};

// 1. Dashboard tổng quan tài chính
export const fetchSummary = (from, to, topN = 5, stockThreshold = 10) => {
  const dateRange = from && to ? { from, to } : getDefaultDateRange();
  return axios.get(`${BASE_URL}/summary`, {
    params: {
      ...dateRange,
      top: topN,
      threshold: stockThreshold,
    },
  });
};

// 2. Danh sách thanh toán (payments)
export const fetchPayments = (from, to) => {
  const dateRange = from && to ? { from, to } : getDefaultDateRange();
  return axios.get(`${BASE_URL}/payments`, {
    params: dateRange,
  });
};

// 3. Danh sách chi phí nhập hàng (expenses)
export const fetchSupplierTransactions = (from, to) => {
  const dateRange = from && to ? { from, to } : getDefaultDateRange();
  return axios.get(`${BASE_URL}/expenses`, {
    params: dateRange,
  });
};

// 4. Top sản phẩm bán chạy
export const fetchTopProducts = (from, to, topN = 5) => {
  const dateRange = from && to ? { from, to } : getDefaultDateRange();
  return axios.get(`${BASE_URL}/top-products`, {
    params: {
      ...dateRange,
      top: topN,
    },
  });
};

// 5. Sản phẩm tồn kho thấp
export const fetchLowStockProducts = (threshold = 10) => {
  return axios.get(`${BASE_URL}/low-stock`, {
    params: { threshold },
  });
};
