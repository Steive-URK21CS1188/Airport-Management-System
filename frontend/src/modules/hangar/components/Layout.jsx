import React from "react";
import "../styles/layout.css";

const Layout = ({ children }) => {
  return (
    <div className="layout-container">
      <div className="layout-inner">
        {children}
      </div>
    </div>
  );
};

export default Layout;
