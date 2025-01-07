import React, { useState, useRef, useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';

import CustomCalendar from './Calendar';
import TaskCard from './TaskCard';
import ProjectCard from './ProjectCard';
import CircularProgressBar from './CircularProgressBar';
import TaskStatusChart from './TaskStatusChart';
import './Dashboard.css';

const Dashboard = () => {
  const { isAuthenticated, getIdTokenClaims, getAccessTokenSilently } = useAuth0();
  const [userMetadata, setUserMetadata] = useState(null);
  const [data, setData] = useState('');
  const [idToken, setIdToken] = useState(''); 
  const [token, setToken] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
  const [kpiData, setKpiData] = useState({
    taskCompletionRate: 0,
    incompleteTaskRate: 0,
    taskStatusCount: { completed: 0, inProgress: 0, notStarted: 0 },
  });


  useEffect(() => {
    const fetchData = async () => {
      if (isAuthenticated) {
        try {
          const idTokenClaims = await getIdTokenClaims(); 
          const idToken = idTokenClaims.__raw; 
          setIdToken(idToken); 

          const response = await axios.get('http://localhost:8888/user-service/api/protected', {
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


  // Fetch KPI data
  useEffect(() => {
    const fetchKpiData = async () => {
      if (!isAuthenticated) return;

      try {
        const idTokenClaims = await getIdTokenClaims();
        const idToken = idTokenClaims.__raw;

        const response = await axios.get('http://localhost:8010/api/kpi/user', {
          headers: { Authorization: `Bearer ${idToken}` },
        });

        setKpiData({
          taskCompletionRate: response.data.taskCompletionRate || 0,
          incompleteTaskRate: response.data.incompleteTaskRate || 0,
          taskStatusCount: response.data.taskStatusCount || { completed: 0, inProgress: 0, notStarted: 0 },
        });
      } catch (error) {
        console.error('Error fetching KPI data:', error);
      }
    };

    fetchKpiData();
  }, [getIdTokenClaims, isAuthenticated]);

    // Load events from localStorage when component mounts
    const [events, setEvents] = useState([]);
    useEffect(() => {
      const savedEvents = JSON.parse(localStorage.getItem('events')) || [];
      // Ensure date is a Date object
      const formattedEvents = savedEvents.map(event => ({
        ...event,
        date: new Date(event.date), // Convert date to Date object
      }));
      setEvents(formattedEvents);
    }, []);
  
    // Save events to localStorage whenever events array changes
    useEffect(() => {
      if (events.length > 0) {
        localStorage.setItem('events', JSON.stringify(events));
      }
    }, [events]);
  
    const [showEventForm, setShowEventForm] = useState(false);
    const [newEvent, setNewEvent] = useState({
      date: new Date(),
      time: '',
      name: '',
    });
    const formRef = useRef();
  
    useEffect(() => {
      function handleClickOutside(event) {
        if (formRef.current && !formRef.current.contains(event.target)) {
          setShowEventForm(false);
        }
      }
      document.addEventListener('mousedown', handleClickOutside);
      return () => {
        document.removeEventListener('mousedown', handleClickOutside);
      };
    }, []);
  
    const handleAddEvent = () => {
      const updatedEvents = [
        ...events,
        {
          date: new Date(newEvent.date),
          time: newEvent.time,
          description: newEvent.name,
        },
      ];
      setEvents(updatedEvents);
      setNewEvent({ date: new Date(), time: '', name: '' });
      setShowEventForm(false);
      alert('Event added!');
    };
  
  
    const handleFormChange = (field, value) => {
      setNewEvent({
        ...newEvent,
        [field]: field === 'date' ? new Date(value) : value,
      });
    };

  return (
    <div className="dashboard-container-kh">
      {/* Header */}
      <div className="dashboard-header-kh">
        <h1>{userMetadata?.Full_Name || 'User'}'s Dashboard</h1>
        <div className="header-buttons-kh">
        <button className="create-new-project-btn-kh">
  <a href="/user/add-project" style={{ textDecoration: 'none', color: 'inherit' }}>
    View Projects
  </a>
</button>

        </div>
      </div>

      {/* Main Content */}
      <div className="main-content-kh">
        {/* Calendar Section */}
        <div className="calendar-container-kh">
          <CustomCalendar events={events} />
          
          <button
            className="create-task-btn-kh"
            onClick={() => setShowEventForm(!showEventForm)}
          >
            + Add date to Calendar
          </button>
          {showEventForm && (
            <div className="event-form" ref={formRef}>
              <h4>Add New Event</h4>
              <label>
                Date:
                <input
                  type="date"
                  value={newEvent.date.toISOString().split('T')[0]}
                  onChange={(e) => handleFormChange('date', e.target.value)}
                />
              </label>
              <label>
                Time:
                <input
                  type="time"
                  value={newEvent.time}
                  onChange={(e) => handleFormChange('time', e.target.value)}
                />
              </label>
              <label>
                Event Name:
                <input
                  type="text"
                  value={newEvent.name}
                  onChange={(e) => handleFormChange('name', e.target.value)}
                />
              </label>
              <div className="form-buttons">
                <button onClick={handleAddEvent}>Submit</button>
                <button onClick={() => setShowEventForm(false)}>Cancel</button>
              </div>
            </div>
          )}
        </div>

        {/* Task Section */}
        <div className="task-section-kh">
          <ProjectCard title="Projects"
            fetchUrl="http://localhost:8091/api/projects/users/recent"/>
          <TaskCard title="Tasks" 
          fetchUrl="http://localhost:8095/api/tasks/users/recent"/>
        </div>
      </div>

      {/* Charts Section */}
      <div className="charts-section-kh">
        <div className="chart-item-kh">
          <div className="chart-label-kh">Task Completion Rate</div>
          <CircularProgressBar
            progress={Number(kpiData.taskCompletionRate).toFixed(2)}
            label="Task Completion Rate"
            color="#4CAF50"
          />
        </div>
        <div className="chart-item-kh">
          <div className="chart-label-kh">Incomplete Task Rate</div>
          <CircularProgressBar
            progress={Number(kpiData.incompleteTaskRate).toFixed(2)}
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
};

export default Dashboard;
