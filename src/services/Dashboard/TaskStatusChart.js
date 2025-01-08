import React, { useEffect, useRef } from 'react';
import { Chart, ArcElement, Tooltip, Legend, PieController } from 'chart.js';

Chart.register(PieController, ArcElement, Tooltip, Legend);

function TaskStatusChart({ statusData }) {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  useEffect(() => {
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    const ctx = chartRef.current.getContext('2d');

    chartInstance.current = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: ['Completed', 'In Progress', 'Not Started'],
        datasets: [
          {
            data: statusData || [0, 0, 0],
            backgroundColor: ['#36A2EB', '#FFCE56', '#FF6384'],
            borderWidth: 0,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'top' },
          tooltip: { enabled: true },
        },
      },
    });


    console.log('TaskStatusChart data:', statusData);
    if (!chartInstance.current) console.log('Chart instance not created!');

    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [statusData]);

  return (
    <div className="task-status-chart-container">
      <canvas ref={chartRef}></canvas>
    </div>
  );

}


export default TaskStatusChart;
