import React, { useRef } from 'react';
import './DragAndDrop.css';

const DragAndDrop = ({ onDrop }) => {
  const fileInputRef = useRef(null);

  const handleDrop = (event) => {
    event.preventDefault();
    const files = Array.from(event.dataTransfer.files);
    onDrop(files);
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleClick = () => {
    fileInputRef.current.click();
  };

  const handleFileChange = (event) => {
    const files = Array.from(event.target.files);
    onDrop(files);
  };

  return (
    <div
      className="drag-drop-kh"
      onDrop={handleDrop}
      onDragOver={handleDragOver}
      onClick={handleClick}
    >
      Drag and drop files here, or click to upload
      <input
        type="file"
        ref={fileInputRef}
        multiple
        style={{ display: 'none' }}
        onChange={handleFileChange}
      />
    </div>
  );
};

export default DragAndDrop;
