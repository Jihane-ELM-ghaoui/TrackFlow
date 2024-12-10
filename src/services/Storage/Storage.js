import React, { useState, useEffect, useCallback } from 'react';
import './Storage.css';
import DragAndDrop from './DragAndDrop';
import FileView from './FileView';
import Breadcrumbs from './Breadcrumbs';
import axios from 'axios';
import { useAuth0 } from '@auth0/auth0-react';
import { toast, ToastContainer } from 'react-toastify';
import listview from './assets/list_view.png';
import gridview from './assets/grid_view.png';
import 'react-toastify/dist/ReactToastify.css';

const notifyError = (message) => toast.error(message);
const notifySuccess = (message) => toast.success(message);

const Storage = () => {
  const [files, setFiles] = useState([]);
  const [view, setView] = useState('grid');
  const [path, setPath] = useState(['Root']);
  const { getIdTokenClaims, getAccessTokenSilently } = useAuth0();

  const getUserIdFromToken = useCallback(async () => {
    try {
      const idTokenClaims = await getIdTokenClaims();
      if (idTokenClaims) {
        const userMetadata = idTokenClaims['https://demo.app.com/user_metadata'];
        return userMetadata?.user_id || idTokenClaims.sub;
      } else {
        throw new Error('No ID token claims found');
      }
    } catch (error) {
      console.error('Error retrieving user ID from token:', error);
      throw new Error('Unable to retrieve user ID');
    }
  }, [getIdTokenClaims]);

  const fetchFiles = useCallback(async () => {
    try {
      const accessToken = await getAccessTokenSilently();
      const response = await axios.get('http://localhost:8090/api/files', {
        headers: { 'Authorization': `Bearer ${accessToken}` }
      });
      console.log("Fetched files:", response.data); // Debug log to ensure data is fetched
      setFiles(response.data);
    } catch (error) {
      console.error('Failed to fetch files', error);
      notifyError('Failed to fetch files');
    }
  }, [getAccessTokenSilently]);

  const handleUpload = async (uploadedFiles) => {
    try {
      const formData = new FormData();
      uploadedFiles.forEach((file) => {
        formData.append('file', file);
      });

      const accessToken = await getAccessTokenSilently();

      await axios.post('http://localhost:8090/api/files/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${accessToken}`,
        },
      });
      fetchFiles();
      notifySuccess('File uploaded successfully');
    } catch (error) {
      console.error('Error during file upload:', error);
      notifyError(`Upload failed: ${error.message}`);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, [fetchFiles]);

  return (
    <div className="storage-kh">
      <ToastContainer />
      <Breadcrumbs path={path} onNavigate={(index) => setPath(path.slice(0, index + 1))} />
      <DragAndDrop onDrop={handleUpload} />
      <div className="view-toggle-kh">
        <button onClick={() => setView('list')}>
          <img src={ listview } alt='List_view icon' style={{ width: '20px', height: '20px'}} />
        </button>
        <button onClick={() => setView('grid')}>
          <img src={ gridview } alt='Grid_view icon' style={{ width: '20px', height: '20px'}} />
        </button>
      </div>
      <FileView
        files={files}
        view={view}
        onFileClick={(file) => alert(`Clicked: ${file.name}`)}
        getAccessTokenSilently={getAccessTokenSilently}
        fetchFiles={fetchFiles}
      />
    </div>
  );
};

export default Storage;
