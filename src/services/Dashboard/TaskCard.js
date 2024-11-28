import React from 'react';
import './TaskCard.css'; // Import the specific CSS for TaskCard

function TaskCard({ title }) {
  return (
    <div className="task-card-kh">
      <div className="task-card-header-kh">
        <h3>{title}</h3>
      </div>
      <div className="task-card-content-kh">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit...</p>
        <div className="tasks-kh">
          <div className="task-kh">
            <p>Set up project repository & CI/CD pipeline</p>
            <small>Assigned to Jihane, User78, Othmane</small>
          </div>
          <div className="task-kh">
            <p>Project structure with Spring Boot and React</p>
            <small>Assigned to ...</small>
          </div>
        </div>
      </div>
    </div>
  );
}

export default TaskCard;
