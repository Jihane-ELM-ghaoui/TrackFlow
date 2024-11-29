import React from 'react';
import axios from 'axios';
import { useAuth0 } from "@auth0/auth0-react";
import './FileView.css';

const FileView = ({ files, view, getAccessTokenSilently, fetchFiles }) => {

  //Download file
  const handleDownload = async (file) => {
    try {
      const accessToken = await getAccessTokenSilently();
      const response = await axios.get(
        `http://localhost:8090/api/files/download/${file.name}`, // Change from POST to GET
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
          responseType: "blob",
        }
      );
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", file.name);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("Error downloading file:", error);
      alert("Failed to download file");
    }
  };  

  //Open file
  const handleOpen = async (file) => {
    try {
      const accessToken = await getAccessTokenSilently();
      const response = await axios.get(
        `http://localhost:8090/api/files/open/${file.name}`, // Change endpoint to 'open'
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
          responseType: "blob", // This ensures the response is a Blob object
        }
      );
      const url = window.URL.createObjectURL(response.data);
      window.open(url, "_blank"); // Opens the file in a new tab
    } catch (error) {
      console.error("Error opening file:", error);
      alert("Failed to open file");
    }
  };
  
  //Delete file
  const handleDelete = async (file) => {
    try {
      const accessToken = await getAccessTokenSilently();
      await axios.delete(`http://localhost:8090/api/files/delete/${file.name}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      alert('File deleted successfully');
      fetchFiles(); // Refresh the file list after deletion
    } catch (error) {
      console.error("Error deleting file:", error);
      alert("Failed to delete file");
    }
  };
  
  if (!files || files.length === 0) {
    return (
      <div className="file-view-empty-kh">
        <div className="empty-message-kh">
          <p>No files uploaded yet.</p>
        </div>
      </div>
    );
  }

  return (
    <div className={`file-view-kh ${view}`}>
      {files.map((file) => (
        <div key={file.name} className="file-item-kh">
          <span className="file-name-kh">{file.name}</span>
          <button onClick={() => handleOpen(file)}>Open</button>
          <button onClick={() => handleDownload(file)}>Download</button>
          <button onClick={() => handleDelete(file)}>Delete</button>
        </div>
      ))}
    </div>
  );
};

export default FileView;
