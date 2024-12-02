import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import { useAuth0 } from '@auth0/auth0-react';
import './Project.css';

const Project = () => {
    const { id } = useParams(); // Get project ID from URL
    const [project, setProject] = useState(null);
    const { isAuthenticated, getIdTokenClaims } = useAuth0();
    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [showForm, setShowForm] = useState(false); // State to toggle the project creation form visibility
    const [selectedProject, setSelectedProject] = useState(null); // State for storing the selected project
    const [email, setEmail] = useState(''); // State for email input when inviting members
    const [role, setRole] = useState(''); // State for role creation input
    const [showTaskForm, setShowTaskForm] = useState(false); // State to toggle the task creation form visibility
    const [showRoleForm, setShowRoleForm] = useState(false); // State to toggle the role creation form visibility
    const [showMemberInput, setShowMemberInput] = useState(false); // State to toggle the member invitation input visibility

    // State for task details object
    const [taskDetails, setTaskDetails] = useState({
        taskName: '',
        taskDescription: '',
        taskPriority: 1,
        taskStartDate: '',
        taskEndDate: '',
        taskEstimatedEndDate: ''
    });

    // Fetch project details
    useEffect(() => {
        const fetchProject = async () => {
            if (isAuthenticated) {
                try {
                    setLoading(true);
                    const idTokenClaims = await getIdTokenClaims();
                    const idToken = idTokenClaims.__raw;

                    // API call to fetch the project using the correct URL
                    const response = await axios.get(`http://localhost:8091/api/projects/${id}`, {
                        headers: { Authorization: `Bearer ${idToken}` },
                    });

                    setProject(response.data);
                    setSelectedProject(response.data); 
                } catch (error) {
                    console.error('Error fetching project:', error);
                    setErrorMessage('An error occurred while fetching project details.');
                } finally {
                    setLoading(false);
                }
            }
        };
        fetchProject();
    }, [id, isAuthenticated, getIdTokenClaims]);

    // Handle adding a project member
    const handleAddMember = async () => {
        if (!email) {
            alert('Please provide a valid email');
            return;
        }

        try {
            const response = await axios.post('http://localhost:8091/api/projectmembers/add', null, {
                params: {
                    projectName: selectedProject.name,
                    email: email,
                },
            });

            if (response.status === 200) {
                alert(`Invitation sent to ${email}`);
                setEmail('');
                setShowMemberInput(false);
            }
        } catch (error) {
            console.error('Error sending invitation:', error);
            alert('Failed to send invitation. Please try again.');
        }
    };

    // Handle creating a role
    const handleCreateTask = async () => {
        const { taskName, taskDescription, taskPriority, taskStartDate, taskEndDate, taskEstimatedEndDate, status } = taskDetails;

        // Check if task name and description are provided
        if (!taskName || !taskDescription) {
            alert('Please provide task name and description');
            return;
        }

        if (isAuthenticated && selectedProject) {
            try {
                setLoading(true); // Start loading

                // Fetch the Auth0 token for authentication
                const idTokenClaims = await getIdTokenClaims();
                if (!idTokenClaims) {
                    throw new Error('Unable to retrieve authentication token.');
                }

                const idToken = idTokenClaims.__raw;

                // Prepare the task data
                const taskData = {
                    projectId: selectedProject.id, // Use the selected project ID
                    taskName,
                    taskDescription,
                    taskPriority,
                    taskStartDate,
                    taskEndDate,
                    taskEstimatedEndDate,
                    createdBy: idTokenClaims.email, // Pass the user's email
                    status,
                };

                // Send a POST request to create the task
                const response = await axios.post('http://localhost:8095/api/tasks', taskData, {
                    headers: {
                        Authorization: `Bearer ${idToken}`,
                    },
                });

                // Check for successful creation
                if (response.status === 200 || response.status === 201) {
                    alert('Task added successfully!');
                    // Reset form values after successful task creation
                    setTaskDetails({
                        taskName: '',
                        taskDescription: '',
                        taskPriority: 1,
                        taskStartDate: '',
                        taskEndDate: '',
                        taskEstimatedEndDate: '',
                        status: 'NOT_STARTED',
                    });
                    setShowTaskForm(false); // Close the task form
                } else {
                    alert(`Failed to create task. Status code: ${response.status}`);
                }
            } catch (error) {
                // Handle various errors here

                // Check for authentication token fetch errors
                if (error.message === 'Unable to retrieve authentication token.') {
                    alert('Authentication failed. Please try logging in again.');
                }
                // Handle network or API request errors
                else if (error.response) {
                    // API response error (e.g., 400, 500 status codes)
                    if (error.response.status === 400) {
                        alert('Bad Request: Please check the task details and try again.');
                    } else if (error.response.status === 401) {
                        alert('Unauthorized: Please log in to create a task.');
                    } else if (error.response.status === 403) {
                        alert('Forbidden: You do not have permission to create a task for this project.');
                    } else if (error.response.status === 500) {
                        alert('Server error: Something went wrong on the server. Please try again later.');
                    } else {
                        alert(`Error: ${error.response.statusText}`);
                    }
                } 
                // Handle token-related errors or unexpected issues
                else if (error.request) {
                    alert('Network error: Unable to connect to the server.');
                } else {
                    // Any other errors not caught above
                    alert(`Unexpected error: ${error.message}`);
                }

                // Log the error for debugging purposes
                console.error('Error creating task:', error);
            } finally {
                setLoading(false); // Stop loading
            }
        } else {
            // If user is not authenticated or project is not selected
            if (!isAuthenticated) {
                alert('You need to be authenticated to create a task.');
            } else if (!selectedProject) {
                alert('You need to select a project to create a task.');
            }
        }
    };

    if (loading) return <div>Loading...</div>;
    if (errorMessage) return <div>{errorMessage}</div>;

    return project ? (
        <div className="project-page">
            <h1>{project.name}</h1>
            <p>{project.description}</p>
            <div className="button-row">
                <button onClick={() => setShowTaskForm(true)}>Create Task</button>
                <button onClick={() => setShowMemberInput(true)}>Invite Members</button>
                <button onClick={() => setShowRoleForm(true)}>Create Role</button>
            </div>
{/* Task Creation Modal */}
{showTaskForm && (
                <div className="modal">
                    <div className="modal-content">
                        <span className="close" onClick={() => setShowTaskForm(false)}>&times;</span>
                        <h2>Create Task</h2>
                        <form
                            onSubmit={(e) => {
                                e.preventDefault();
                                handleCreateTask();
                            }}
                        >
                            <label>
                                Task Name:
                                <input
                                    type="text"
                                    value={taskDetails.taskName}
                                    onChange={(e) => setTaskDetails({ ...taskDetails, taskName: e.target.value })}
                                    required
                                />
                            </label>
                            <label>
                                Task Description:
                                <textarea
                                    value={taskDetails.taskDescription}
                                    onChange={(e) => setTaskDetails({ ...taskDetails, taskDescription: e.target.value })}
                                    required
                                />
                            </label>
                            <label>
                                Priority:
                                <input
                                    type="number"
                                    min="1"
                                    max="5"
                                    value={taskDetails.taskPriority}
                                    onChange={(e) => setTaskDetails({ ...taskDetails, taskPriority: e.target.value })}
                                />
                            </label>
                            <label>
                                Start Date:
                                <input
                                    type="date"
                                    value={taskDetails.taskStartDate}
                                    onChange={(e) => setTaskDetails({ ...taskDetails, taskStartDate: e.target.value })}
                                />
                            </label>
                            <label>
                                End Date:
                                <input
                                    type="date"
                                    value={taskDetails.taskEndDate}
                                    onChange={(e) => setTaskDetails({ ...taskDetails, taskEndDate: e.target.value })}
                                />
                            </label>
                            <label>
                                Estimated End Date:
                                <input
                                    type="date"
                                    value={taskDetails.taskEstimatedEndDate}
                                    onChange={(e) => setTaskDetails({ ...taskDetails, taskEstimatedEndDate: e.target.value })}
                                />
                            </label>
                            <label>
                                Status:
                                <select
                                    value={taskDetails.status}
                                    onChange={(e) => setTaskDetails({ ...taskDetails, status: e.target.value })}
                                >
                                    <option value="NOT_STARTED">Not Started</option>
                                    <option value="IN_PROGRESS">In Progress</option>
                                    <option value="COMPLETED">Completed</option>
                                </select>
                            </label>
                            <button type="submit" disabled={loading}>Create Task</button>
                        </form>
                    </div>
                </div>
            )}
            
            {/* Similar modals for inviting members and creating roles */}
        </div>
    ) : (
        <div>No project found</div>
    );
};

export default Project;
