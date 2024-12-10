import React from 'react';
import axios from 'axios';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import emptyfolder from './assets/empty-folder.png';
import download from './assets/download.png';
import deleteicon from './assets/delete.png';
import shareicon from './assets/share.png';
import openicon from './assets/open-icon.png';
import './FileView.css';

const notifyError = (message) => toast.error(message);
const notifySuccess = (message) => toast.success(message);
const notifySuccessWithCopy = (message, url) => {
  toast.success(
    <div style={{ fontFamily: 'Arial, sans-serif', color: '#333' }}>
      <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>{message}</span>
      <br />
      <span 
        style={{ 
          cursor: 'pointer', 
          color: '#007BFF', 
          textDecoration: 'underline', 
          marginTop: '0.5rem', 
          display: 'inline-block', 
          transition: 'color 0.2s ease' 
        }}
        onClick={() => navigator.clipboard.writeText(url)}
        onMouseOver={(e) => e.target.style.color = '#0056b3'} // Darker blue on hover
        onMouseOut={(e) => e.target.style.color = '#007BFF'}  // Reset to original color
      >
        Copy Link
      </span>
    </div>,
    { 
      autoClose: false, 
      closeOnClick: true 
    }
  );
};

const FileView = ({ files, view, getAccessTokenSilently, fetchFiles }) => {

  //Download file
  const handleDownload = async (file) => {
    try {
      const accessToken = await getAccessTokenSilently();
      const response = await axios.get(
        `http://localhost:8090/api/files/download/${file.name}`,
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
      notifySuccess('File downloaded successfully');
    } catch (error) {
      console.error("Error downloading file:", error);
      notifyError('Failed to download file');
    }
  };  

  //Open file
  const handleOpen = async (file) => {
    try {
      const accessToken = await getAccessTokenSilently();
      const response = await axios.get(
        `http://localhost:8090/api/files/open/${file.name}`, 
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
      notifyError('Failed to open file');
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
      fetchFiles(); // Refresh the file list after deletion
      notifySuccess('File removed successfully');
    } catch (error) {
      console.error("Error deleting file:", error);
      notifyError('Failed to delete file');
    }
  };

  //Share file
  const handleShare = async (file) => {
    try {
      const accessToken = await getAccessTokenSilently();

      const response = await axios.post(
        `http://localhost:8090/api/files/share/${file.name}`,
        null, // No request body
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      const shareableLink = response.data.split("Shareable link: ")[1];
      navigator.clipboard.writeText(shareableLink); // Copy link to clipboard
      notifySuccessWithCopy("Shareable link copied to clipboard: " + shareableLink);
    } catch (error) {
      console.error("Error sharing file:", error);
      notifyError("Failed to share file");
    }
  };

  
  if (!files || files.length === 0) {
    return (
      <div className="file-view-empty-kh">
        <div className="empty-message-kh">
        <img src={emptyfolder} alt="Empty folder" />
          <p>No files uploaded yet.</p>
        </div>
      </div>
    );
  }



  return (
    <div className={`file-view-kh ${view}`}>
      <ToastContainer />
      {files.map((file) => (
        <div key={file.name} className="file-item-kh">
          <span className="file-name-kh">{file.name}</span>
          <button onClick={() => handleOpen(file)}>
            <img src={ openicon } alt='Open icon' style={{ width: '20px', height: '20px'}} />
          </button>
          <button onClick={() => handleDownload(file)}>
            <img src={ download } alt='Download icon' style={{ width: '20px', height: '20px'}} />
          </button>
          <button onClick={() => handleDelete(file)}>
            <img src={ deleteicon } alt='Delete icon' style={{ width: '20px', height: '20px'}} />
          </button>
          <button onClick={() => handleShare(file)}>
            <img src={ shareicon } alt='Share icon' style={{ width: '20px', height: '20px'}} />
          </button>
        </div>
      ))}
    </div>
  );
};

export default FileView;
