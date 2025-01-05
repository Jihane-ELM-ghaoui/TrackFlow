import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import { useAuth0 } from '@auth0/auth0-react';
import './Project.css';
import dotsImage from './dots.png';
import { Bar } from 'react-chartjs-2';
import { CircularProgressbar } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);


const Project = () => {
    const { id } = useParams();
    const { projectId } = useParams();
    const [project, setProject] = useState(null);
    const [selectedProject, setSelectedProject] = useState(null);
    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const { isAuthenticated, getIdTokenClaims } = useAuth0();
    const [email, setEmail] = useState('');
    const [role, setRole] = useState('');
    const [showTaskForm, setShowTaskForm] = useState(false);
    const [showRoleForm, setShowRoleForm] = useState(false);
    const [showMemberInput, setShowMemberInput] = useState(false);
    const [tasks, setTasks] = useState([]);
    const [taskDetails, setTaskDetails] = useState({
        taskName: '',
        taskDescription: '',
        taskPriority: 1,
        taskStartDate: '',
        taskEndDate: '',
        taskEstimatedEndDate: '',
        assignedUsers: ''
    });
    const [showUpdateForm, setShowUpdateForm] = useState(false);
    const [updatedProject, setUpdatedProject] = useState({
        name: '',
        description: ''
    });
    const [members, setMembers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [roleName, setRoleName] = useState('');
    const [kpiData, setKpiData] = useState(null);
    const usersList = ["user1", "user2", "user3"];


    const fetchProject = async (idToken) => {
      try {
          // Fetch the project data
          const response = await axios.get(`http://localhost:8091/api/projects/${projectId}`, {
              headers: { Authorization: `Bearer ${idToken}` },
          });
          console.log("Project data:", response.data);
  
          // Fetch the members of the project
          const membersResponse = await axios.get(`http://localhost:8091/api/projects/${projectId}/members`, {
              headers: { Authorization: `Bearer ${idToken}` },
          });
          console.log("Members data:", membersResponse.data);
  
          // Combine the project details and members into one object
          const project = {
              ...response.data,           // Project details
              members: membersResponse.data,  // Project members
          };
  
          // Optionally, update your state
          setProject(response.data);
          setSelectedProject(response.data);
          setUpdatedProject({
              name: response.data.name,
              description: response.data.description,
          });
          setMembers(membersResponse.data);
          console.log('project')
  
          // Return the combined object
          return project;
  
      } catch (error) {
          console.error('Error fetching project details:', error);
          setErrorMessage('An error occurred while fetching project details.');
      }
  };
  

    const fetchTasks = async (projectId, idToken) => {
      console.log("projectId, idToken", projectId, idToken);
        try {
          console.log("projectId, idToken", projectId, idToken);
            const tasksResponse = await axios.get(`http://localhost:8095/api/tasks/project/${projectId}`, {
                headers: { Authorization: `Bearer ${idToken}` },
            });
            setTasks(tasksResponse.data);
            console.log("taskresponsess:", tasksResponse.data);
        } catch (error) {
            setErrorMessage('An error occurred while fetching tasks.');
        }
    };

    const fetchData = async (idToken) => {
        try {
        
            setLoading(true);
            console.log("Ffff")
            const projectData = await fetchProject(idToken);


            if (projectData) {
                await fetchTasks(projectData.id, idToken);
            }
            console.log('Fetching tasks for project with ID:', projectData.id);
        } catch (error) {
            setErrorMessage('An error occurred while fetching data.');
        } finally {
            setLoading(false);
        }
    };


    useEffect(() => {
        if (isAuthenticated) {
            const fetchDataWrapper = async () => {
                try {
                    const idTokenClaims = await getIdTokenClaims();
                    const idToken = idTokenClaims.__raw;
                    fetchData(idToken);
                } catch (error) {
                    console.error('Error during authentication:', error);
                }
            };
            fetchDataWrapper();
        }
    }, [id, isAuthenticated, getIdTokenClaims]);

    const handleCreateTask = async () => {
      const { taskName, taskDescription, taskPriority, taskStartDate, taskEndDate, taskEstimatedEndDate, status, assignedUsers } = taskDetails;
  
      if (!taskName || !taskDescription) {
          alert('Please provide task name and description');
          return;
      }
  
      if (isAuthenticated && selectedProject) {
          try {
              setLoading(true);
  
              const idTokenClaims = await getIdTokenClaims();
              const idToken = idTokenClaims.__raw;
  
              const taskData = {
                  projectId: selectedProject.id,
                  taskName,
                  taskDescription,
                  taskPriority,
                  taskStartDate: taskStartDate || null,
                  taskEndDate: taskEndDate || null,     
                  taskEstimatedEndDate: taskEstimatedEndDate || null,
                  createdBy: idTokenClaims.email,
                  status: status || 'NOT_STARTED',
                  assignedUsers: assignedUsers || [],
              };
  
              // Send request to create task
              const response = await axios.post(`http://localhost:8095/api/tasks`, taskData, {
                  headers: {
                      Authorization: `Bearer ${idToken}`,
                  },
              });
  
              if (response.status === 200 || response.status === 201) {
                  alert('Task added successfully!');
  
                  // Immediately add the new task to the task list in state
                  setTasks((prevTasks) => [
                      ...prevTasks,
                      response.data,  // Assuming response.data contains the newly created task
                  ]);
  
                  // Reset the form
                  setTaskDetails({
                      taskName: '',
                      taskDescription: '',
                      taskPriority: 1,
                      taskStartDate: '',
                      taskEndDate: '',
                      taskEstimatedEndDate: '',
                      status: 'NOT_STARTED',
                      assignedUsers: [],
                  });
  
                  setShowTaskForm(false);
              } else {
                  alert(`Failed to create task. Status code: ${response.status}`);
              }
          } catch (error) {
              alert('Error creating task:', error.message);
          } finally {
              setLoading(false);
          }
      }
  };
  

const formatStatus = (status) => {
    switch (status) {
        case 'IN_PROGRESS':
            return 'In Progress';
        case 'NOT_STARTED':
            return 'Not Started';
        case 'DONE':
            return 'Done';
        default:
            return status;
    }
};

useEffect(() => {
    const fetchKpiData = async () => {
        try {
            const idTokenClaims = await getIdTokenClaims();
            const idToken = idTokenClaims.__raw;

            console.log('Fetching KPI data for project ID:', projectId);
            const response = await axios.get(`http://localhost:8010/api/kpi/project/${projectId}`, {
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });

        const data = response.data;

        if (data && Array.isArray(data.cycleTimes)) {
            data.cycleTimes = data.cycleTimes.map(task => ({
                taskId: task.taskId,
                cycleTime: Number(task.cycleTime),
            }));
        } else {
            data.cycleTimes = [];
        }

        console.log('KPI data fetched:', response.data);
        setKpiData(data);
        setLoading(false);
    } catch (error) {
        console.error('Error fetching KPI data:', error);
        setLoading(false);
    }
};

// Initial fetch on component mount
fetchKpiData();

// WebSocket setup
const createWebSocket = () => new SockJS('http://localhost:8095/ws');
let stompClient = Stomp.over(createWebSocket);

const connectWebSocket = () => {
    stompClient.connect(
        {},
        () => {
            console.log('Connected to WebSocket');

            stompClient.subscribe('/topic/kpiUpdates', () => {
                console.log('TaskService update detected. Fetching updated KPI data...');
                fetchKpiData(); // Trigger KPI fetch on task update
            });
        },
        (error) => {
            console.error('WebSocket connection error:', error);
            setTimeout(connectWebSocket, 5000); // Retry connection
        }
    );
};

connectWebSocket();

return () => {
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log('Disconnected from WebSocket');
        });
    }
};
}, [projectId, getIdTokenClaims]);

if (loading) return <div>Loading...</div>;

if (!kpiData || !Array.isArray(kpiData.cycleTimes)) {
    return <div>No data available</div>;
}

const cycleTimes = kpiData.cycleTimes;
const taskIds = cycleTimes.map(task => task.taskId);

const chartData = {
    labels: taskIds,
    datasets: [
        {
            label: 'Cycle Time (days)',
            data: cycleTimes.map(task => task.cycleTime),
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1,
        },
    ],
};

const chartOptions = {
    responsive: true,
    scales: {
        x: {
            title: { display: true, text: 'Task ID' },
        },
        y: {
            title: { display: true, text: 'Cycle Time (Days)' },
            beginAtZero: true,
        },
    },
};

if (loading) return <div>Loading...</div>;
if (errorMessage) return <div>{errorMessage}</div>;

return (
    <div>
        {loading && <div>Loading...</div>}

        {errorMessage && <div>{errorMessage}</div>}

        {project ? (
            <div className="project-pageNB">
                <h1>{project.name}</h1>
                <p>{project.description}</p>

                <div className="button-rowNB">
                    <button onClick={() => setShowTaskForm(true)}>Create Task</button>
                    <button onClick={() => setShowMemberInput(true)}>Invite Members</button>
                    <button onClick={() => setShowRoleForm(true)}>Create Role</button>
                    <button onClick={() => setShowUpdateForm(true)}>Update Project</button>
                </div>

                <div className="members-sectionNB">
                    <h4>Joined Members</h4>
                    <ul>
                        {members.map((member, index) => (
                            <li key={index}>{member.name || member.email}</li>
                        ))}
                    </ul>
                </div>

                <div className="roles-sectionNB">
                    <h4>Project Roles</h4>
                    <ul>
                        {roles.length > 0 ? (
                            roles.map((role) => (
                                <li key={role.id}>{role.name}</li>
                            ))
                        ) : (
                            <li>No roles available</li>
                        )}
                    </ul>
                </div>

                {showTaskForm && (
                    <div className="modalNB">
                        <div className="modal-contentNB">
                            <span className="closeNB" onClick={() => setShowTaskForm(false)}>&times;</span>
                            <h2>Create Task</h2>
                            <form onSubmit={(e) => {
                                e.preventDefault();
                                handleCreateTask();
                            }}>
                                <label>Task Name:
                                    <input
                                        type="text"
                                        value={taskDetails.taskName}
                                        onChange={(e) => setTaskDetails({...taskDetails, taskName: e.target.value})}
                                        required
                                    />
                                </label>

                                <label>Task Description:
                                    <textarea
                                        value={taskDetails.taskDescription}
                                        onChange={(e) => setTaskDetails({
                                            ...taskDetails,
                                            taskDescription: e.target.value
                                        })}
                                        required
                                    />
                                </label>

                                <label>Priority:
                                    <input
                                        type="number"
                                        value={taskDetails.taskPriority}
                                        onChange={(e) => setTaskDetails({
                                            ...taskDetails,
                                            taskPriority: parseInt(e.target.value)
                                        })}
                                        min="1"
                                        max="5"
                                    />
                                </label>

                                <label>Start Date:
                                    <input
                                        type="date"
                                        value={taskDetails.taskStartDate}
                                        onChange={(e) => setTaskDetails({
                                            ...taskDetails,
                                            taskStartDate: e.target.value
                                        })}
                                        
                                    />
                                </label>

                                <label>End Date:
                                    <input
                                        type="date"
                                        value={taskDetails.taskEndDate}
                                        onChange={(e) => setTaskDetails({
                                            ...taskDetails,
                                            taskEndDate: e.target.value
                                        })}
                                    />
                                </label>

                                <label>Estimated End Date:
                                    <input
                                        type="date"
                                        value={taskDetails.taskEstimatedEndDate}
                                        onChange={(e) => setTaskDetails({
                                            ...taskDetails,
                                            taskEstimatedEndDate: e.target.value
                                        })}
                                    />
                                </label>

                                <label>Status:
                                    <select
                                        value={taskDetails.status}
                                        onChange={(e) => setTaskDetails({...taskDetails, status: e.target.value})}
                                    >
                                        <option value="NOT_STARTED">Not Started</option>
                                        <option value="IN_PROGRESS">In Progress</option>
                                        <option value="COMPLETED">Completed</option>
                                    </select>
                                </label>

                                <button type="submit" disabled={loading}>Add Task To Project</button>
                            </form>
                        </div>
                    </div>
                )}

                <div className="task-list-containerNB">
                    <div className="tasksNB">
                        {tasks.length > 0 ? (
                            tasks.map((task) => (
                                <div className="task-itemNB" key={task.id}>
                                    <div className="task-headerNB">
                                        <h3>{task.taskName} <span
                                            className="task-statusNB">| {formatStatus(task.status)}</span></h3>
                                        <img
                                            src={dotsImage}
                                            alt="Menu"
                                            className="dots-menuNB"
                                        />
                                    </div>
                                    <p className="task-descriptionNB">{task.taskDescription}</p>

                                    <div className="task-datesNB">
                                        <p><strong>Start Date:</strong> {task.taskStartDate ? new Date(task.taskStartDate).toLocaleDateString() : 'Not Set'}</p>
                                        <p><strong>Due Date:</strong> {task.taskEndDate ? new Date(task.taskEndDate).toLocaleDateString() : 'Not Set'}
                                        </p>
                                        <p><strong>Estimated Date
                                            Time:</strong> {task.taskEstimatedEndDate ? new Date(task.taskEstimatedEndDate).toLocaleDateString() : 'Not Set'}</p>
                                    </div>

                                    <div className={`task-priority priority-${task.taskPriority}`}>
                                        Priority: {task.taskPriority}
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p>No tasks available</p>
                        )}
                    </div>
                </div>
            </div>
        ) : (
            <div>No project found</div>
        )}

        {kpiData ? (
           <div className="project-pageNB">
           <div>
              
                <div style={{display: 'flex', justifyContent: 'space-around', marginBottom: '30px'}}>
                    <div style={{width: '150px', height: '150px'}}>
                        <h4>On-Time Completion Rate</h4>
                        <CircularProgressbar
                            value={kpiData.onTimeCompletionRate}
                            text={`${kpiData.onTimeCompletionRate.toFixed(2)}%`}
                            styles={{path: {stroke: '#4caf50'}, text: {fill: '#4caf50'}}}
                        />
                    </div>
                    <div style={{width: '150px', height: '150px'}}>
                        <h4>Project Progress</h4>
                        <CircularProgressbar
                            value={kpiData.progress}
                            text={`${kpiData.progress.toFixed(2)}%`}
                            styles={{path: {stroke: '#ff9800'}, text: {fill: '#ff9800'}}}
                        />
                    </div>
                </div>
                <br></br><br></br>
                <div style={{width: '500px', height: '300px', margin: '0 auto'}}>
                    <h4>Cycle Times per Task</h4>
                    <Bar data={chartData} options={chartOptions}/>
                </div>
            </div>
            </div>

        ) : (
            <div>No data available</div>
        )}
    </div>
);

};

export default Project;
