import React, { useState, useEffect } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import './Calendar.css';

function CustomCalendar({ events, onDateClick }) {
  const [selectedEvent, setSelectedEvent] = useState(null);

  const handleDayClick = (date) => {
    const event = events.find((e) => e.date instanceof Date && e.date.toDateString() === date.toDateString());
    setSelectedEvent(event);
    if (onDateClick) {
      onDateClick(date);
    }
  };

  const handleClickOutside = (event) => {
    // Hide the popup if the click is outside the calendar or the popup
    if (!event.target.closest('.calendar-container-kh')) {
      setSelectedEvent(null);
    }
  };

  useEffect(() => {
    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const getTileClassName = ({ date, view }) => {
    if (view === 'month') {
      return events.some((e) => e.date instanceof Date && e.date.toDateString() === date.toDateString())
        ? 'highlighted-date'
        : '';
    }
    return '';
  };

  return (
    <div className="calendar-container-kh">
      <Calendar onClickDay={handleDayClick} tileClassName={getTileClassName} />
      {selectedEvent && (
        <div className="event-popup">
          <h4>Event Details</h4>
          <p>
            <strong>Date:</strong> {selectedEvent.date.toDateString()}
          </p>
          <p><strong>Time:</strong> {selectedEvent.time}</p>
          <p>
            <strong>Description:</strong> {selectedEvent.description}
          </p>
        </div>
      )}
    </div>
  );
}

export default CustomCalendar;
