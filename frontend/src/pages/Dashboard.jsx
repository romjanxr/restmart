import {
  FiPackage,
  FiShoppingCart,
  FiStar,
  FiTruck,
  FiUsers,
} from "react-icons/fi";
import StatCard from "../components/Dashboard/StatCard";
import Order from "../components/Dashboard/Order";
import { useEffect, useMemo, useState } from "react";
import authApiClient from "../services/auth-api-client";
import useAuthContext from "../hooks/useAuthContext";
import apiClient from "../services/api-client";

export default function Dashboard() {
  const { user } = useAuthContext();
  const [orders, setOrders] = useState([]);
  const [totalUsers, setTotalUsers] = useState(0);
  const [totalProducts, setTotalProducts] = useState(0);

  useEffect(() => {
    authApiClient.get("/orders/").then((res) => setOrders(res.data));
  }, []);

  useEffect(() => {
    authApiClient
      .get("/auth/users/")
      .then((res) => setTotalUsers(res.data.length));
  }, []);

  useEffect(() => {
    apiClient.get("/products/").then((res) => setTotalProducts(res.data.count));
  }, []);

  const userOrderStats = useMemo(() => {
    let orderInProgressCount = 0;
    let canceledOrdersCount = 0;
    let totalSpendingAmount = 0;

    orders.forEach((order) => {
      const inprogressStatus = ["Processing", "Ready To Ship", "Not Paid"];
      const spendingStatus = ["Canceled", "Not Paid"];

      if (inprogressStatus.includes(order.status)) orderInProgressCount++;
      if (order.status === "Canceled") canceledOrdersCount++;
      if (!spendingStatus.includes(order.status))
        totalSpendingAmount += parseFloat(order.total_price || 0);
    });

    return {
      orderInProgress: orderInProgressCount,
      canceledOrders: canceledOrdersCount,
      totalSpending: totalSpendingAmount,
    };
  }, [orders]);

  return (
    <div>
      {user.is_staff ? (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          <StatCard
            icon={FiPackage}
            title="Total Products"
            value={totalProducts}
          />
          <StatCard
            icon={FiShoppingCart}
            title="Total Orders"
            value={orders.length}
          />
          <StatCard icon={FiUsers} title="Total Users" value={totalUsers} />
          <StatCard icon={FiStar} title="Average Rating" value={4.8} />
        </div>
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          <StatCard
            icon={FiShoppingCart}
            title="My Total Orders"
            value={orders.length}
          />
          <StatCard
            icon={FiTruck}
            title="Order in Progress"
            value={userOrderStats.orderInProgress}
          />
          <StatCard
            icon={FiUsers}
            title="Canceled Orders"
            value={userOrderStats.canceledOrders}
          />
          <StatCard
            icon={FiStar}
            title="Total Spending"
            value={`$${userOrderStats.totalSpending.toFixed(2)}`}
          />
        </div>
      )}
      <Order orders={orders} />
    </div>
  );
}
