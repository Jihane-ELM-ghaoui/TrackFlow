import React from 'react';
import Calendar from './Calendar';
import TaskCard from './TaskCard';
import './Dashboard.css'; // Ensure this imports the correct styles

function Dashboard({ userName }) {
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
          <Calendar />
          <button className="create-task-btn-kh">+ Add date to Calendar</button> {/* New button below the calendar */}
        </div>
        <div className="task-section-kh">
          <TaskCard title="Management App" />
          <TaskCard title="Core Task Management and User Access" />
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
