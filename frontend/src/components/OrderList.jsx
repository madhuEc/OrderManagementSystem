import React, { useEffect, useState } from 'react';
import { Table, message, Spin } from 'antd';
import axios from 'axios';

const columns = [
  { title: 'Order ID', dataIndex: 'orderId', key: 'orderId' },
  { title: 'Item ID', dataIndex: 'itemId', key: 'itemId' },
  { title: 'Quantity', dataIndex: 'quantity', key: 'quantity' },
];

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchOrders = async () => {
      setLoading(true);
      try {
        const resp = await axios.get('/api/orders'); // proxy to order-service
        setOrders(resp.data);
      } catch (err) {
        console.error(err);
        message.error('Failed to load orders');
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  if (loading) {
    return <Spin tip="Loading orders..." />;
  }

  return <Table dataSource={orders} columns={columns} rowKey="orderId" />;
};

export default OrderList;
