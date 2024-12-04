import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import CustomCalendar from './Calendar';
import TaskCard from './TaskCard';
import CircularProgressBar from './CircularProgressBar';
import TaskStatusChart from './TaskStatusChart';
import './Dashboard.css';

function Dashboard({ userName }) {
  const [kpiData, setKpiData] = useState({
    taskCompletionRate: 0,
    incompleteTaskRate: 0,
    taskStatusCount: { completed: 0, inProgress: 0, notStarted: 0 },
  });

  // Fetch all KPI data from backend
  useEffect(() => {
    axios.get('http://localhost:8080/api/kpi/user/2')
      .then(response => {
        setKpiData({
          taskCompletionRate: response.data.taskCompletionRate,
          incompleteTaskRate: response.data.incompleteTaskRate,
          taskStatusCount: response.data.taskStatusCount,
        });
      })
      .catch(error => console.error('Error fetching KPI data:', error));
  }, []);

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
      <div className="dashboard-header-kh">
        <h1>{userName}'s Dashboard</h1>
        <div className="header-buttons-kh">
          <button className="create-new-project-btn-kh">+ Create new task</button>
          <button className="add-date-btn-kh">+ Create new project</button>
        </div>
      </div>

      <div className="main-content-kh">
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
            color="#D20062 "
          />
        </div>
        <div className="chart-item-kh">
          <div className="chart-label-kh">Incomplete Task Rate</div>
          <CircularProgressBar
            progress={kpiData.incompleteTaskRate}
            label="Incomplete Task Rate"
            color="#243642"
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
