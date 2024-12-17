import React, { useEffect, useState } from 'react';
import { Bar } from 'react-chartjs-2';
import { CircularProgressbar } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';
import axios from 'axios';
import { Stomp } from '@stomp/stompjs';
import { useAuth0 } from '@auth0/auth0-react';

// Chart.js Registration
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const Project = () => {
  const { isAuthenticated, getIdTokenClaims } = useAuth0();
  const { projectId } = useParams();
  const [kpiData, setKpiData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchKpiData = async () => {
      try {
        const idTokenClaims = await getIdTokenClaims();
        const idToken = idTokenClaims.__raw;

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
    const createWebSocket = () => new SockJS('http://localhost:8011/ws');
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

  return (
    <div>
      {kpiData ? (
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-around', marginBottom: '30px' }}>
            <div style={{ width: '150px', height: '150px' }}>
              <h4>On-Time Completion Rate</h4>
              <CircularProgressbar
                value={kpiData.onTimeCompletionRate}
                text={`${kpiData.onTimeCompletionRate.toFixed(2)}%`}
                styles={{ path: { stroke: '#4caf50' }, text: { fill: '#4caf50' } }}
              />
            </div>
            <div style={{ width: '150px', height: '150px' }}>
              <h4>Project Progress</h4>
              <CircularProgressbar
                value={kpiData.progress}
                text={`${kpiData.progress.toFixed(2)}%`}
                styles={{ path: { stroke: '#ff9800' }, text: { fill: '#ff9800' } }}
              />
            </div>
          </div>
          <div style={{ width: '500px', height: '300px', margin: '0 auto' }}>
            <h4>Cycle Times per Task</h4>
            <Bar data={chartData} options={chartOptions} />
          </div>
        </div>
      ) : (
        <div>No data available</div>
      )}
    </div>
  );
};

export default Project;
