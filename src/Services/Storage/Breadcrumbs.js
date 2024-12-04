import React from 'react';

import './Breadcrumbs.css';

const Breadcrumbs = ({ path, onNavigate }) => (
  <div className="breadcrumbs-kh">
    {path.map((folder, index) => (
      <span key={index} onClick={() => onNavigate(index)}>
        {folder}
      </span>
    ))}
  </div>
);

export default Breadcrumbs;
