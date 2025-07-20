import { useEffect, useState } from "react";
import { fetchLowStockProducts } from "../../services/financeApi";

export default function LowStockProducts() {
  const [data, setData] = useState([]);

  useEffect(() => {
    fetchLowStockProducts(10)
      .then((res) => setData(res.data))
      .catch((err) => console.error("❌ Lỗi khi tải dữ liệu:", err));
  }, []);

  return (
    <div className="container my-5">
      <div className="card border-0 shadow-sm">
        <div
          className="card-header text-white fw-bold d-flex align-items-center"
          style={{ backgroundColor: "#4e73df", height: "60px", fontSize: "18px" }}
        >
          <i className="fas fa-boxes me-2"></i> Sản phẩm tồn kho thấp
        </div>
        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-bordered table-hover m-0">
              <thead className="table-light text-center align-middle">
                <tr>
                  <th style={{ width: "60px" }}>#</th>
                  <th>Tên Sản Phẩm</th>
                  <th>Số Lượng Còn Lại</th>
                </tr>
              </thead>
              <tbody className="text-center align-middle">
                {data.length === 0 ? (
                  <tr>
                    <td colSpan="3" className="text-muted py-4">
                      Không có sản phẩm nào dưới ngưỡng tồn kho.
                    </td>
                  </tr>
                ) : (
                  data.map((p, idx) => (
                    <tr key={idx}>
                      <td>{idx + 1}</td>
                      <td className="fw-semibold">{p.name}</td>
                      <td>
                        <span className="badge bg-danger fs-6 px-3 py-2 shadow-sm">
                          {p.stockQuantities}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
