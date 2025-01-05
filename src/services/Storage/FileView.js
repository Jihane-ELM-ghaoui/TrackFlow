/* Container for the file view */
.file-view-kh {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 20px;
}

/* Styling for each file item */
.file-item-kh {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
  margin: 5px 0;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

/* Change background and shadow on hover */
.file-item-kh:hover {
  background-color: #f1f1f1;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* Styling for the file name */
.file-item-kh .file-name-kh {
  font-weight: bold;
  color: #333;
  flex-grow: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Styling for buttons */
.file-item-kh button {
  /* background-color: #007bff; */
  color: black;
  border: none;
  border-radius: 4px;
  padding: 5px 10px;
  cursor: pointer;
  margin-left: 10px;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

/* Button hover effects */
.file-item-kh button:hover {
  /* background-color: #0056b3; */
  transform: scale(1.05);
}

/* Button focus effects for accessibility */
.file-item-kh button:focus {
  outline: 2px solid #277298;
  outline-offset: 2px;
}

/* Styling for empty file view message */
.file-view-empty-kh {
  text-align: center;
  font-size: 16px;
  color: #666;
  padding: 20px;
  font-style: italic;
}

/* Toggle view buttons */
.view-toggle-kh {
  margin: 10px;
  display: flex;
  gap: 10px;
}

/* Styling for toggle buttons */
.view-toggle-kh button {
  /* background-color: #007bff; */
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 15px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

/* Toggle button hover effects */
.view-toggle-kh button:hover {
  /* background-color: #0056b3; */
  transform: scale(1.05);
}

/* Toggle button focus effects */
.view-toggle-kh button:focus {
  outline: 2px solid #277298;
  outline-offset: 2px;
}

/* Drag and drop area styling */
.drag-drop-area {
  border: 2px dashed #ddd;
  padding: 20px;
  text-align: center;
  color: #666;
  margin-bottom: 20px;
  cursor: pointer;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

/* Hover effect for drag and drop area */
.drag-drop-area:hover {
  background-color: #f1f1f1;
  border-color: #bbb;
}

/* Responsive design for smaller screens */
@media (max-width: 600px) {
  .file-item-kh {
    flex-direction: column;
    align-items: flex-start;
  }

  .file-item-kh button {
    margin-left: 0;
    margin-top: 5px;
  }
}

/* List View */
.file-view-kh.list {
  flex-direction: column;
}

.file-view-kh.list .file-item-kh {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

/* Grid View Specific Styling */
.file-view-kh.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  justify-content: start;
}

.file-view-kh.grid .file-item-kh {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  text-align: center;
  overflow: hidden;
}

/* File name styling specific to grid view */
.file-view-kh.grid .file-item-kh .file-name-kh {
  font-weight: bold;
  font-size: 14px;
  color: #333;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

/* Button layout in grid view */
.file-view-kh.grid .file-item-kh button {
  width: 100%;
  margin: 4px 0;
  font-size: 12px;
}

/* Grid View Hover Effect */
.file-view-kh.grid .file-item-kh:hover {
  background-color: #f1f1f1;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* Responsive adjustments for small screens in grid view */
@media (max-width: 600px) {
  .file-view-kh.grid .file-item-kh {
    grid-template-columns: 1fr;
  }
}
