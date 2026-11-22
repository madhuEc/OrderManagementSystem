import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import OrderList from './components/OrderList';
import CreateOrder from './components/CreateOrder';

const { Header, Content, Footer } = Layout;

const App = () => (
  <Router>
    <Layout className="layout" style={{ minHeight: '100vh' }}>
      <Header>
        <div className="logo" style={{ float: 'left', color: '#fff', fontSize: '1.2rem', marginRight: '1rem' }}>Order Management</div>
        <Menu theme="dark" mode="horizontal" defaultSelectedKeys={[window.location.pathname]}> 
          <Menu.Item key="/" >
            <Link to="/">Orders</Link>
          </Menu.Item>
          <Menu.Item key="/create">
            <Link to="/create">Create Order</Link>
          </Menu.Item>
        </Menu>
      </Header>
      <Content style={{ padding: '0 50px', marginTop: '24px' }}>
        <Routes>
          <Route path="/" element={<OrderList />} />
          <Route path="/create" element={<CreateOrder />} />
        </Routes>
      </Content>
      <Footer style={{ textAlign: 'center' }}>Order Management System ©2026</Footer>
    </Layout>
  </Router>
);

export default App;
