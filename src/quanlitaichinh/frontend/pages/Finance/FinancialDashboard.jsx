import { useEffect, useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { fetchSummary } from "../../services/financeApi";

export default function FinancialDashboard() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [fromDate, setFromDate] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() - 30); // Mặc định: 30 ngày trước
    return d;
  });

  const [toDate, setToDate] = useState(new Date());

  const loadSummary = () => {
    const fromStr = fromDate.toISOString().split("T")[0];
    const toStr = toDate.toISOString().split("T")[0];

    setLoading(true);
    fetchSummary(fromStr, toStr, 5, 10)
      .then((res) => {
        setSummary(res.data);
        setError(null);
      })
      .catch((err) => {
        console.error("❌ Lỗi khi tải dữ liệu:", err);
        setError("Không thể tải dữ liệu từ server");
        setSummary(null);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadSummary();
  }, []);

  const handleFilter = () => {
    loadSummary();
  };

  return (
    <div className="container mt-5">
      <h2 className="mb-4 fw-bold">📊 Tổng quan tài chính</h2>

      {/* Bộ lọc thời gian */}
      <div className="row mb-4 align-items-end">
        <div className="col-md-4">
          <label className="form-label">Từ ngày</label>
          <DatePicker
            className="form-control"
            selected={fromDate}
            onChange={(date) => setFromDate(date)}
            dateFormat="yyyy-MM-dd"
          />
        </div>
        <div className="col-md-4">
          <label className="form-label">Đến ngày</label>
          <DatePicker
            className="form-control"
            selected={toDate}
            onChange={(date) => setToDate(date)}
            dateFormat="yyyy-MM-dd"
          />
        </div>
        <div className="col-md-4">
          <button className="btn btn-primary w-100" onClick={handleFilter}>
            Lọc dữ liệu
          </button>
        </div>
      </div>

      {/* Thông báo loading / error */}
      {loading && <div className="text-center mt-5">Đang tải dữ liệu...</div>}
      {error && <div className="text-danger text-center mt-5">{error}</div>}

      {/* Tổng quan tài chính */}
      {!loading && summary && (
        <>
          <div className="row mb-4">
            <div className="col-md-4">
              <div className="card text-white bg-success shadow">
                <div className="card-body">
                  <h5 className="card-title">Tổng thu</h5>
                  <p className="card-text fs-4 fw-semibold">
                    {summary.totalIncome?.toLocaleString("vi-VN")} ₫
                  </p>
                </div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="card text-white bg-danger shadow">
                <div className="card-body">
                  <h5 className="card-title">Tổng chi</h5>
                  <p className="card-text fs-4 fw-semibold">
                    {summary.totalExpense?.toLocaleString("vi-VN")} ₫
                  </p>
                </div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="card text-white bg-primary shadow">
                <div className="card-body">
                  <h5 className="card-title">Lợi nhuận</h5>
                  <p className="card-text fs-4 fw-semibold">
                    {summary.netProfit?.toLocaleString("vi-VN")} ₫
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Top sản phẩm bán chạy */}
          <div className="card mb-4">
            <div className="card-header bg-light text-center fw-bold">
              🔥 Top sản phẩm bán chạy
            </div>
            <div className="card-body p-0">
              <table className="table table-bordered m-0">
                <thead className="table-light">
                  <tr>
                    <th className="text-center">#</th>
                    <th className="text-center">Tên Sản Phẩm</th>
                    <th className="text-center">Doanh Thu</th>
                  </tr>
                </thead>
                <tbody>
                  {summary.topProducts?.map((p, idx) => (
                    <tr key={idx}>
                      <td className="text-center">{idx + 1}</td>
                      <td className="text-center">{p.productName}</td>
                      <td className="text-center">
                        {p.totalRevenue?.toLocaleString("vi-VN")} ₫
                      </td>
                    </tr>
                  ))}
                  {summary.topProducts?.length === 0 && (
                    <tr>
                      <td colSpan="3" className="text-center">
                        Không có dữ liệu
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Sản phẩm tồn kho thấp */}
          <div className="card">
            <div className="card-header bg-light text-center fw-bold">
              📦 Sản phẩm tồn kho thấp
            </div>
            <div className="card-body p-0">
              <table className="table table-bordered m-0">
                <thead className="table-light">
                  <tr>
                    <th className="text-center">#</th>
                    <th className="text-center">Tên Sản Phẩm</th>
                    <th className="text-center">Số Lượng Còn Lại</th>
                  </tr>
                </thead>
                <tbody>
                  {summary.lowStockProducts?.map((p, idx) => (
                    <tr key={idx}>
                      <td className="text-center">{idx + 1}</td>
                      <td className="text-center">{p.name}</td>
                      <td className="text-center">{p.stockQuantities}</td>
                    </tr>
                  ))}
                  {summary.lowStockProducts?.length === 0 && (
                    <tr>
                      <td colSpan="3" className="text-center">
                        Không có sản phẩm nào tồn kho thấp
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
