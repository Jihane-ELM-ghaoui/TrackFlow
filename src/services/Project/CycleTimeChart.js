import React from 'react';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const CycleTimeChart = ({ data }) => {
  const chartData = {
    labels: data.cycleTimes.map(item => `Task ${item.taskid}`),
    datasets: [
      {
        label: 'Cycle Time (Days)',
        data: data.cycleTimes.map(item => item.cycleTime),
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
    ],
  };  

  const options = {
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
        min: 0,
      },
    },
  };

  return (
    <div style={{ width: '80%', margin: '40px auto' }}>
      <Bar data={chartData} options={options} />
    </div>
  );
};

export default CycleTimeChart;
