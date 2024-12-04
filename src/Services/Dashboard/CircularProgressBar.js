import React, { useEffect, useRef } from 'react';
import { Chart, ArcElement, Tooltip, Legend } from 'chart.js';

// Register necessary components
Chart.register(ArcElement, Tooltip, Legend);

function CircularProgressBar({ progress, label, color }) {
  const chartRef = useRef(null);

  useEffect(() => {
    if (chartRef.current.chartInstance) {
      chartRef.current.chartInstance.destroy();
    }

    const ctx = chartRef.current.getContext('2d');
    const chartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        datasets: [
          {
            data: [progress, 100 - progress],
            backgroundColor: [color, '#E0E0E0'],
            borderWidth: 0,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          tooltip: { enabled: false },
        },
        cutout: '70%', // Makes it look like a circular progress bar
      },
    });

    chartRef.current.chartInstance = chartInstance;

    return () => {
      chartInstance.destroy();
    };
  }, [progress, color]);

  return (
    <div className="circular-progress-container">
      <canvas ref={chartRef}></canvas>
      <div className="circular-progress-label">{progress}%</div>
    </div>
  );
}

export default CircularProgressBar;
