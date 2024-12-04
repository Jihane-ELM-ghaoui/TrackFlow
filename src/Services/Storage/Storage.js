import React, { useState } from 'react';
import './Storage.css';
import DragAndDrop from './DragAndDrop';
import FileView from './FileView';
import Breadcrumbs from './Breadcrumbs';

const Storage = () => {
  const [files, setFiles] = useState([]); // No files by default
  const [view, setView] = useState('grid'); // Default view is grid
  const [path, setPath] = useState(['Root']); // Default root path

  const handleUpload = (uploadedFiles) => {
    const newFiles = uploadedFiles.map((file) => ({
      id: Date.now() + file.name,
      name: file.name,
      type: 'file',
    }));
    setFiles([...files, ...newFiles]);
  };

  const handleNavigate = (index) => {
    setPath(path.slice(0, index + 1)); // Navigate to the selected folder level
  };

  return (
    <div className="storage-kh">
      {/* Breadcrumbs for navigation */}
      <Breadcrumbs path={path} onNavigate={handleNavigate} />

      {/* Drag and drop area */}
      <DragAndDrop onDrop={handleUpload} />

      {/* View toggle buttons */}
      <div className="view-toggle-kh">
        <button onClick={() => setView('grid')}>Grid View</button>
        <button onClick={() => setView('list')}>List View</button>
      </div>

      {/* File display section */}
      <FileView files={files} view={view} onFileClick={(file) => alert(`Clicked: ${file.name}`)} />
    </div>
  );
};

export default Storage;
