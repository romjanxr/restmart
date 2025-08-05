import formatDate from "../utils/formatDate";

const Order = ({ orders }) => {
  // Define the mapping from status to badge class
  const orderBadgeColors = {
    Completed: "badge-success",
    "Ready To Ship": "badge-warning",
    Processing: "badge-info",
    Canceled: "badge-error",
    "Not Paid": "badge-primary",
  };

  return (
    <div className="mt-6 card bg-base-100 shadow-sm">
      <div className="card-body">
        <h3 className="card-title text-lg">Recent Orders</h3>
        <div className="overflow-x-auto">
          <table className="table table-zebra">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Status</th>
                <th>Date</th>
                <th>Amount</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td>{`#${order.id}`}</td>
                  <td>
                    {order.user.first_name} {order.user.last_name}
                  </td>
                  <td>
                    {/* Dynamically set the badge class */}
                    <div
                      className={`badge ${
                        orderBadgeColors[order.status] || "badge-neutral"
                      }`}
                    >
                      {order.status}
                    </div>
                  </td>
                  <td>{formatDate(order.created_at)}</td>
                  <td>${order.total_price}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Order;
