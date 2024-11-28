import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth0 } from '@auth0/auth0-react'; 
import "./AddProject.css";

const AddProject = () => {
    const [name, setProjectName] = useState("");
    const [description, setProjectDescription] = useState("");
    const [projects, setProjects] = useState([]); 
    const { isAuthenticated, getIdTokenClaims } = useAuth0(); 
    const [errorMessage, setErrorMessage] = useState('');
    const [loading, setLoading] = useState(false);


    const fetchProjects = async () => {
        if (isAuthenticated) {
            try {
                const idTokenClaims = await getIdTokenClaims();
                const idToken = idTokenClaims.__raw;

                const response = await axios.get(
                    'http://localhost:8091/api/projects',
                    {
                        headers: {
                            Authorization: `Bearer ${idToken}`,
                        },
                    }
                );

                setProjects(response.data); 
            } catch (error) {
                console.error('Error fetching projects:', error);
                setErrorMessage('An error occurred while fetching projects.');
            }
        } else {
            setErrorMessage("You need to be authenticated to view projects.");
        }
    };

    useEffect(() => {
        fetchProjects();
    }, [isAuthenticated, getIdTokenClaims]);

    const handleCreateProject = async () => {
        if (!name || !description) {
            setErrorMessage("Both project name and description are required.");
            return;
        }

        if (isAuthenticated) {
            try {
                setLoading(true);
                setErrorMessage('');

               
                const idTokenClaims = await getIdTokenClaims();
                const idToken = idTokenClaims.__raw;

            
                const projectData = {
                    name,
                    description,
                };

                const response = await axios.post(
                    'http://localhost:8091/api/projects',
                    projectData,
                    {
                        headers: {
                            Authorization: `Bearer ${idToken}`,
                        },
                    }
                );

                alert("Project added successfully!");

                
                fetchProjects();

            } catch (error) {
                if (error.response && error.response.status === 403) {
                    setErrorMessage("You don't have permission to perform this action.");
                } else {
                    console.error('Error adding project:', error);
                    setErrorMessage('An error occurred while adding the project.');
                }
            } finally {
                setLoading(false);
            }
        } else {
            setErrorMessage("You need to be authenticated to add a project.");
        }
    };

    return (
        <div className="project-form-container">
            <div className="project-form">
                <input
                    type="text"
                    placeholder="Enter Project Name"
                    value={name}
                    onChange={(e) => setProjectName(e.target.value)}
                />
                <textarea
                    placeholder="Enter Project Description"
                    value={description}
                    onChange={(e) => setProjectDescription(e.target.value)}
                />
                <button onClick={handleCreateProject} disabled={loading}>
                    {loading ? 'Submitting...' : 'Submit'}
                </button>
                {errorMessage && <p className="error-message">{errorMessage}</p>}
            </div>

            <div className="project-list">
                <h3>List of Projects:</h3>
                {projects.length === 0 ? (
                    <p>No projects found.</p>
                ) : (
                    <ul>
                        {projects.map((project, index) => (
                            <li key={index}>
                                <h4>{project.name}</h4>
                                <p>{project.description}</p>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default AddProject;
