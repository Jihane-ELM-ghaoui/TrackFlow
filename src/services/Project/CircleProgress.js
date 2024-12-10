import React from 'react';
import { CircularProgressbar } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';

const CircleProgress = ({ value, text }) => {
  return (
    <div style={{ width: '150px', height: '150px', margin: '20px auto' }}>
      <CircularProgressbar
        value={value}
        text={`${value}%`}
        styles={{
          path: {
            stroke: '#4caf50',
            strokeWidth: 8,
          },
          text: {
            fill: '#4caf50',
            fontSize: '20px',
            fontWeight: 'bold',
          },
        }}
      />
      <h3 style={{ textAlign: 'center', marginTop: '10px' }}>{text}</h3>
    </div>
  );
};

export default CircleProgress;
