import React from 'react';
import './FileView.css';

const FileView = ({ files, view, onFileClick }) => {
  if (files.length === 0) {
    return (
      <div className="file-view-empty-kh">
        <div className="empty-message-kh">
          <p>No files uploaded yet.</p>
          <p>Drag and drop files above, or click to upload.</p>
        </div>
      </div>
    );
  }

  return (
    <div className={`file-view-kh ${view}`}>
      {files.map((file) => (
        <div
          key={file.id}
          className="file-item-kh"
          onClick={() => onFileClick(file)}
          title={`Open ${file.name}`}
        >
          <div className={`file-icon-kh ${file.type}`}></div>
          <span className="file-name-kh">{file.name}</span>
          <span className="file-size-kh">{file.size || 'Unknown size'}</span>
        </div>
      ))}
    </div>
  );
};

export default FileView;
