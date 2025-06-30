import { useEffect, useState } from "react";
import OrderCard from "../components/Orders/OrderCard";
import authApiClient from "../services/auth-api-client";

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [validationMessage, setValidationMessage] = useState("");

  useEffect(() => {
    authApiClient.get("/orders/").then((res) => setOrders(res.data));
  }, []);

  const handleCancelOrder = async (orderId) => {
    try {
      const response = await authApiClient.post(`/orders/${orderId}/cancel/`);
      console.log(response);
      if (response.status === 200) {
        setOrders((prevOrder) =>
          prevOrder.map((order) =>
            order.id === orderId ? { ...order, status: "Canceled" } : order
          )
        );
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="container mx-auto py-8 px-4">
      {validationMessage && (
        <div role="alert" className="alert alert-info mb-3">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            className="h-6 w-6 shrink-0 stroke-current"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            ></path>
          </svg>
          <span>{validationMessage}</span>
        </div>
      )}
      <h1 className="text-2xl font-bold mb-6">Order Details</h1>
      {orders.map((order) => (
        <OrderCard
          key={order.id}
          order={order}
          onCancel={handleCancelOrder}
          setValidationMessage={setValidationMessage}
        />
      ))}
    </div>
  );
};

export default Orders;
