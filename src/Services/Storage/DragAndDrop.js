import React from 'react';

import './DragAndDrop.css';

const DragAndDrop = ({ onDrop }) => {
  const handleDrop = (event) => {
    event.preventDefault();
    const files = Array.from(event.dataTransfer.files);
    onDrop(files);
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  return (
    <div className="drag-drop-kh" onDrop={handleDrop} onDragOver={handleDragOver}>
      Drag and drop files here, or click to upload
    </div>
  );
};

export default DragAndDrop;
