import React, { useEffect, useState } from 'react';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { CircularProgressbar } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const Project = () => {
  const { projectId } = useParams();
  const [kpiData, setKpiData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [socketConnected, setSocketConnected] = useState(false);

  useEffect(() => {
    const createWebSocket = () => new SockJS('http://localhost:8010/ws');
    let stompClient = Stomp.over(createWebSocket);

    const connectWebSocket = () => {
      stompClient.connect(
        {
          'heart-beat': '10000,10000',
        },
        (frame) => {
          console.log('Connected: ' + frame);
          setSocketConnected(true);

          stompClient.subscribe(`/topic/kpiProjectUpdates`, (message) => {
            const data = JSON.parse(message.body);
            setKpiData(prevData => ({
              ...prevData,
              ...data,
            }));
          });
        },
        (error) => {
          console.error('WebSocket Error:', error);
          setSocketConnected(false);
          setTimeout(connectWebSocket, 5000);
        }
      );

      stompClient.onclose = () => {
        setSocketConnected(false);
        setTimeout(connectWebSocket, 5000);
      };
    };

    connectWebSocket();

    return () => {
      if (stompClient) {
        stompClient.disconnect(() => {
          console.log('Disconnected from WebSocket');
        });
      }
    };
  }, [projectId]);

  useEffect(() => {
    const fetchKpiData = async () => {
      try {
        const response = await fetch(`http://localhost:8010/api/kpi/project/${projectId}`);
        if (!response.ok) {
          throw new Error(`Error fetching KPI data: ${response.statusText}`);
        }
        const data = await response.json();

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
        console.error(error);
        setLoading(false);
      }
    };

    fetchKpiData();
  }, [projectId]);

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
        title: {
          display: true,
          text: 'Task ID',
        },
      },
      y: {
        title: {
          display: true,
          text: 'Cycle Time (Days)',
        },
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
              <h4 class>On-Time Completion Rate</h4>
              <CircularProgressbar
                value={kpiData.onTimeCompletionRate}
                text={`${kpiData.onTimeCompletionRate.toFixed(2)}%`}
                styles={{
                  path: { stroke: '#4caf50' },
                  text: { fill: '#4caf50' },
                }}
              />
            </div>

            <div style={{ width: '150px', height: '150px' }}>
              <h4>Project Progress</h4>
              <CircularProgressbar
                value={kpiData.progress}
                text={`${kpiData.progress.toFixed(2)}%`}
                styles={{
                  path: { stroke: '#ff9800' },
                  text: { fill: '#ff9800' },
                }}
              />
            </div>
          </div>
          <div style={{ width: '500px', height: '300px', margin: '0 auto' }}>
            <br/><br/><br/>
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
