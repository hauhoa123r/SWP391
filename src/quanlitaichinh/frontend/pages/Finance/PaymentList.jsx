import { useEffect, useState } from "react";
import { fetchPayments } from "../../services/financeApi";

export default function PaymentList() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);

  const today = new Date().toISOString().split("T")[0];
  const thirtyDaysAgo = new Date();
  thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
  const defaultFrom = thirtyDaysAgo.toISOString().split("T")[0];

  const [fromDate, setFromDate] = useState(defaultFrom);
  const [toDate, setToDate] = useState(today);

  const loadPayments = () => {
    setLoading(true);
    fetchPayments(fromDate, toDate)
      .then((res) => setPayments(res.data))
      .catch((err) =>
        console.error("❌ Lỗi khi tải danh sách thanh toán:", err)
      )
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadPayments();
  }, []);

  return (
    <div className="container my-5">
      <div className="card border-0 shadow-sm">
        <div
          className="card-header text-white fw-bold d-flex justify-content-between align-items-center"
          style={{
            backgroundColor: "#20c997",
            height: "60px",
            fontSize: "18px",
          }}
        >
          <span>
            <i className="fas fa-money-check-alt me-2"></i> Danh sách thanh toán
          </span>
          <div className="d-flex align-items-center">
            <input
              type="date"
              value={fromDate}
              max={toDate}
              onChange={(e) => setFromDate(e.target.value)}
              className="form-control form-control-sm me-2"
            />
            <span className="me-2">–</span>
            <input
              type="date"
              value={toDate}
              min={fromDate}
              onChange={(e) => setToDate(e.target.value)}
              className="form-control form-control-sm me-2"
            />
            <button className="btn btn-sm btn-light" onClick={loadPayments}>
              Lọc
            </button>
          </div>
        </div>

        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">Đang tải dữ liệu...</div>
          ) : payments.length === 0 ? (
            <div className="text-center text-muted py-5">
              Không có dữ liệu thanh toán.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-bordered table-hover m-0">
                <thead className="table-light text-center align-middle">
                  <tr>
                    <th style={{ width: "50px" }}>#</th>
                    <th>Ngày thanh toán</th>
                    <th>Số tiền</th>
                    <th>Bệnh nhân</th>
                  </tr>
                </thead>
                <tbody className="text-center align-middle">
                  {payments.map((p, i) => (
                    <tr key={i}>
                      <td>{i + 1}</td>
                      <td>
                        {p.paymentTime
                          ? new Date(p.paymentTime).toLocaleDateString("vi-VN")
                          : "Không rõ"}
                      </td>
                      <td className="text-success fw-semibold">
                        {p.amount?.toLocaleString("vi-VN", {
                          style: "currency",
                          currency: "VND",
                          minimumFractionDigits: 0,
                        })}
                      </td>
                      <td>{p.customerName || "Không rõ"}</td>
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

