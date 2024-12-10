import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';


import Calendar from './Calendar';
import TaskCard from './TaskCard';
import CircularProgressBar from './CircularProgressBar';
import TaskStatusChart from './TaskStatusChart';

import './Dashboard.css'; 


const Dashboard = () => {
  const { isAuthenticated, getIdTokenClaims, getAccessTokenSilently } = useAuth0();
  const [userMetadata, setUserMetadata] = useState(null);
  const [token, setToken] = useState(null); 
  const [error, setError] = useState(null);
 
  const [data, setData] = useState('');
  const [idToken, setIdToken] = useState(''); 
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');


  useEffect(() => {
    const fetchData = async () => {
      if (isAuthenticated) {
        try {
          const idTokenClaims = await getIdTokenClaims(); 
          const idToken = idTokenClaims.__raw; 
          setIdToken(idToken); 

          const response = await axios.get('http://localhost:8080/api/protected', {
            headers: {
              Authorization: `Bearer ${idToken}`, 
            },
          });

          setData(response.data);
          setErrorMessage('');
        
        } catch (error) {
          if (error.response && error.response.status === 403) {
            setErrorMessage("You don't have permission.");
          } else {
            console.error('Error fetching data:', error);
            setErrorMessage('An error occurred while fetching data.'); 
          }
        } finally {
          setLoading(false); 
        }
      } else {
        setLoading(false); 
      }
    };

    fetchData();
  }, [getIdTokenClaims, isAuthenticated]);





  useEffect(() => {
    if (!isAuthenticated) {
      return;
    }

    const getUserMetadataAndToken = async () => {
      try {
        // Fetch the ID token claims
        const idTokenClaims = await getIdTokenClaims();
        console.log('ID token claims:', idTokenClaims);

        if (idTokenClaims) {
          const metadata = idTokenClaims['https://demo.app.com/user_metadata'];
          console.log('User metadata:', metadata);
          setUserMetadata(metadata);
        } else {
          console.error('No ID token claims available');
        }

        // Fetch the access token silently
        const accessToken = await getAccessTokenSilently();
        console.log('Access Token:', accessToken);
        setToken(accessToken);  // Set token in state

      } catch (error) {
        console.error('Error getting token or metadata:', error);
        setError('Error fetching user metadata or token');
      } finally {
        setLoading(false);
      }
    };

    getUserMetadataAndToken();
  }, [getIdTokenClaims, getAccessTokenSilently, isAuthenticated]);



  const [kpiData, setKpiData] = useState({
    taskCompletionRate: 0,
    incompleteTaskRate: 0,
    taskStatusCount: { completed: 0, inProgress: 0, notStarted: 0 },
  });

  useEffect(() => {
    const fetchKpi = async () => {
      if (isAuthenticated) {
        try {
          const idTokenClaims = await getIdTokenClaims();
          const idToken = idTokenClaims.__raw; 
          setIdToken(idToken);
  
          const response = await axios.get('http://localhost:8015/api/kpi/user', {
            headers: {
              Authorization: `Bearer ${idToken}`,
            },
          });
  
          setKpiData({
            taskCompletionRate: response.data.taskCompletionRate,
            incompleteTaskRate: response.data.incompleteTaskRate,
            taskStatusCount: response.data.taskStatusCount,
          });
        } catch (error) {
          console.error('Error fetching KPI data:', error);
        }
      }
    };
  
    fetchKpi();
  }, [getIdTokenClaims, isAuthenticated]);


  return (
    <div className="dashboard-container-kh">
      {/* Header */}
      <div className="dashboard-header-kh">
        <h1>{userMetadata?.Full_Name}'s Dashboard</h1>
        <div className="header-buttons-kh">
          <button className="create-new-project-btn-kh">+ Create new task</button>
          <button className="add-date-btn-kh">+ Create new project</button>
        </div>
      </div>

      {/* Main Content */}
      <div className="main-content-kh">
        {/* Calendar Section */}
        <div className="calendar-container-kh">
          <Calendar />
          <button className="create-task-btn-kh">+ Add date to Calendar</button>
        </div>

        {/* Task Section */}
        <div className="task-section-kh">
            <TaskCard title="Management App" />
            <TaskCard title="Core Task Management and User Access" />
        </div>
      </div>

      {/* Charts Section */}
      <div className="charts-section-kh">
        <div className="chart-item-kh">
          <div className="chart-label-kh">Task Completion Rate</div>
          <CircularProgressBar
            progress={kpiData.taskCompletionRate}
            label="Task Completion Rate"
            color="#4CAF50"
          />
        </div>
        <div className="chart-item-kh">
          <div className="chart-label-kh">Incomplete Task Rate</div>
          <CircularProgressBar
            progress={kpiData.incompleteTaskRate}
            label="Incomplete Task Rate"
            color="#F44336"
          />
        </div>
        <div className="chart-item-kh">
          <div className="chart-label-kh">Task Status</div>
          <div className="task-status-chart-container">
            <TaskStatusChart
              statusData={[ 
                kpiData.taskStatusCount?.completed || 0,
                kpiData.taskStatusCount?.inProgress || 0,
                kpiData.taskStatusCount?.notStarted || 0,
              ]}
            />
          </div>
        </div>
      </div> 
    </div> 
  );
}

export default Dashboard;
