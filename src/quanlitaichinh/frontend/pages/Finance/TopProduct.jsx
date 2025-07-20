// src/pages/Finance/TopProducts.jsx
import { useEffect, useState } from "react";
import { fetchTopProducts } from "../../services/financeApi";

export default function TopProducts() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const today = new Date().toISOString().split("T")[0];
    const from = new Date();
    from.setDate(from.getDate() - 30);

    fetchTopProducts(from.toISOString().split("T")[0], today, 5)
      .then((res) => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="container my-5">
      <h2 className="fw-bold text-center mb-4">🔥 Top sản phẩm bán chạy</h2>

      <div className="alert alert-secondary text-center">
        📌 Danh sách Top 5 sản phẩm bán chạy (30 ngày gần nhất)
      </div>

      {loading ? (
        <div className="text-center py-5">Đang tải dữ liệu...</div>
      ) : (
        <div className="table-responsive">
          <table className="table table-bordered text-center align-middle">
            <thead className="table-light">
              <tr>
                <th>#</th>
                <th>Tên Sản Phẩm</th>
                <th>Doanh Thu</th>
              </tr>
            </thead>
            <tbody>
              {data?.length > 0 ? (
                data.map((p, idx) => (
                  <tr key={p.productId}>
                    <td>{idx + 1}</td>
                    <td>{p.productName}</td>
                    <td className="text-success fw-semibold">
                      {p.totalRevenue?.toLocaleString("vi-VN")} ₫
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="3" className="text-muted">
                    Không có dữ liệu
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
