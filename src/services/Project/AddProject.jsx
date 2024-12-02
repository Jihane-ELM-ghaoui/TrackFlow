import React, { useState, useEffect, useCallback } from 'react'; // Importing necessary hooks from React
import axios from 'axios'; // Importing Axios for making HTTP requests
import { useAuth0 } from '@auth0/auth0-react'; // Importing Auth0 for user authentication
import { useNavigate } from 'react-router-dom';
import './AddProject.css'; // Importing the CSS file for styling


// Main component for adding and managing projects
const AddProject = () => {
    // State variables for managing form inputs and project data
    const [projects, setProjects] = useState([]); // List of projects fetched from the API
    const [name, setProjectName] = useState(''); // State for project name
    const [description, setProjectDescription] = useState(''); // State for project description
    const [showForm, setShowForm] = useState(false); // State to toggle the project creation form visibility
    const [selectedProject, setSelectedProject] = useState(null); // State for storing the selected project
    const { isAuthenticated, getIdTokenClaims } = useAuth0(); // Authentication data from Auth0
    const [errorMessage, setErrorMessage] = useState(''); // State for error messages
    const [loading, setLoading] = useState(false); // State for loading indicator


    const navigate = useNavigate();

    // Function to fetch projects from the backend API when authenticated
    const fetchProjects = useCallback(async () => {
        if (isAuthenticated) {
            try {
                // Fetching the Auth0 token for authentication
                const idTokenClaims = await getIdTokenClaims();
                const idToken = idTokenClaims.__raw;

                // Making GET request to fetch projects
                const response = await axios.get('http://localhost:8091/api/projects', {
                    headers: {
                        Authorization: `Bearer ${idToken}`,
                    },
                });

                // Update the state with the fetched projects
                setProjects(Array.isArray(response.data) ? response.data : []);
            } catch (error) {
                console.error('Error fetching projects:', error);
                setErrorMessage('An error occurred while fetching projects.');
            }
        } else {
            setErrorMessage('You need to be authenticated to view projects.');
        }
    }, [isAuthenticated, getIdTokenClaims]);

    // Fetch projects when the component mounts or when authentication status changes
    useEffect(() => {
        fetchProjects();
    }, [fetchProjects]);

    // Function to handle the creation of a new project
    const handleCreateProject = async () => {
        // Check if project name and description are provided
        if (!name || !description) {
            setErrorMessage('Both project name and description are required.');
            return;
        }

        if (isAuthenticated) {
            try {
                setLoading(true); // Start loading
                setErrorMessage(''); // Clear previous error messages

                // Fetch the Auth0 token for authentication
                const idTokenClaims = await getIdTokenClaims();
                const idToken = idTokenClaims.__raw;

                // Prepare the data for the new project
                const projectData = { name, description };

                // Make a POST request to create the project
                await axios.post('http://localhost:8091/api/projects', projectData, {
                    headers: {
                        Authorization: `Bearer ${idToken}`,
                    },
                });

                // Show success message and reset form fields
                alert('Project added successfully!');
                setProjectName('');
                setProjectDescription('');
                fetchProjects(); // Refresh the project list
                setShowForm(false); // Close the project creation form
            } catch (error) {
                console.error('Error adding project:', error);
                setErrorMessage('An error occurred while adding the project.');
            } finally {
                setLoading(false); // Stop loading
            }
        } else {
            setErrorMessage('You need to be authenticated to add a project.');
        }
    };

    // Function to handle project selection
    const handleProjectClick = (project) => {
        navigate(`/project/${project.id}`); // Navigate to Project.js with project ID
    };


    return (
        <div className="container">
            <div className="project-list-container">
                <h3 className="list-header">List of Projects</h3>

                <button className="create-new-btn" onClick={() => setShowForm(true)}>
                    Create New Project
                </button>

                <div className="projects">
                    {projects.map((project) => (
                        <div
                            key={project.id}
                            className="project-item"
                            onClick={() => handleProjectClick(project)}
                        >
                            <h4>{project.name}</h4>
                            <p>{project.description}</p>
                            <div className="created-at">
                                Created at: {new Date(project.createdAt).toLocaleDateString()}
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {showForm && (
                <div id="new-project-modal" className="modal">
                    <div className="modal-content">
                        <h3>Create New Project</h3>
                        <div className="project-form">
                            <label>Project Name:</label>
                            <input
                                type="text"
                                placeholder="Enter Project Name"
                                value={name}
                                onChange={(e) => setProjectName(e.target.value)}
                            />
                            <label>Description:</label>
                            <textarea
                                placeholder="Enter Project Description"
                                value={description}
                                onChange={(e) => setProjectDescription(e.target.value)}
                            />
                            <button onClick={handleCreateProject} disabled={loading}>
                                {loading ? 'Creating...' : 'Create Project'}
                            </button>
                            {errorMessage && <div className="error-message">{errorMessage}</div>}
                            <button className="close-btn" onClick={() => setShowForm(false)}>Close</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AddProject;
