import React, { useState } from 'react';
import { Form, Input, InputNumber, Button, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const CreateOrder = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      await axios.post('/api/orders', values);
      message.success('Order created successfully');
      navigate('/'); // go back to orders list
    } catch (err) {
      console.error(err);
      message.error('Failed to create order');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form layout="vertical" onFinish={onFinish} style={{ maxWidth: 400, margin: '0 auto' }}>
      <Form.Item label="Item ID" name="itemId" rules={[{ required: true, message: 'Please input Item ID' }]}>
        <Input placeholder="e.g., A1" />
      </Form.Item>
      <Form.Item label="Quantity" name="quantity" rules={[{ required: true, type: 'number', min: 1, message: 'Quantity must be at least 1' }]}>
        <InputNumber min={1} style={{ width: '100%' }} />
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit" loading={loading}>Create Order</Button>
      </Form.Item>
    </Form>
  );
};

export default CreateOrder;
