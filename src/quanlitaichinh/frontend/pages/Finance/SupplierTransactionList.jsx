import { useEffect, useState } from "react";
import { fetchSupplierTransactions } from "../../services/financeApi";

export default function SupplierTransactionList() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");

  const loadData = (fromDate, toDate) => {
    setLoading(true);
    fetchSupplierTransactions(fromDate, toDate)
      .then((res) => setItems(res.data))
      .catch((err) => console.error("❌ Lỗi khi tải danh sách chi phí:", err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData(); // Mặc định 30 ngày gần nhất
  }, []);

  const handleFilter = () => {
    loadData(from, to);
  };

  return (
    <div className="container my-5">
      <div className="card border-0 shadow-sm">
        <div
          className="card-header text-white fw-bold d-flex align-items-center justify-content-between"
          style={{ backgroundColor: "#17a2b8", height: "60px", fontSize: "18px" }}
        >
          <div>
            <i className="fas fa-truck-loading me-2"></i> Danh sách chi phí
          </div>
          <div className="d-flex align-items-center gap-2">
            <input
              type="date"
              value={from}
              onChange={(e) => setFrom(e.target.value)}
              className="form-control form-control-sm"
            />
            <span className="mx-1">→</span>
            <input
              type="date"
              value={to}
              onChange={(e) => setTo(e.target.value)}
              className="form-control form-control-sm"
            />
            <button className="btn btn-sm btn-light border" onClick={handleFilter}>
              Lọc
            </button>
          </div>
        </div>

        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">Đang tải dữ liệu...</div>
          ) : items.length === 0 ? (
            <div className="text-center text-muted py-5">
              Không có dữ liệu chi phí.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-bordered table-hover m-0">
                <thead className="table-light text-center align-middle">
                  <tr>
                    <th style={{ width: "50px" }}>#</th>
                    <th>Nhà cung cấp</th>
                    <th>Tổng chi phí</th>
                    <th>Ngày giao dịch</th>
                  </tr>
                </thead>
                <tbody className="text-center align-middle">
                  {items.map((item, index) => (
                    <tr key={item.transactionId || index}>
                      <td>{index + 1}</td>
                      <td>{item.supplierName || "Không rõ"}</td>
                      <td className="text-danger fw-semibold">
                        {item.totalAmount?.toLocaleString("vi-VN", {
                          style: "currency",
                          currency: "VND",
                          minimumFractionDigits: 0,
                        })}
                      </td>
                      <td>
                        {item.transactionDate
                          ? new Date(item.transactionDate).toLocaleDateString("vi-VN")
                          : "Không rõ"}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
