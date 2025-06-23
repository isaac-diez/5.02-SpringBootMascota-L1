import React, { useEffect, useState } from 'react';

const Notification = ({ message, duration = 4000, onClose }) => {
  const [visible, setVisible] = useState(false);

  // This handles the fade-in and fade-out effect
  useEffect(() => {
    // Fade in
    setVisible(true);

    // Set a timer to automatically close the notification
    const timer = setTimeout(() => {
      setVisible(false);
      // Allow time for the fade-out animation before calling onClose
      setTimeout(onClose, 500);
    }, duration);

    // Clean up the timer if the component is unmounted early
    return () => clearTimeout(timer);
  }, [message, duration, onClose]);

  // Determine notification style based on keywords
  const getNotificationStyle = () => {
    const lowerCaseMessage = message.toLowerCase();
    if (lowerCaseMessage.includes('sick')) return 'bg-red-500';
    if (lowerCaseMessage.includes('hungry' || 'weak')) return 'bg-yellow-500';
    if (lowerCaseMessage.includes('sleeping')) return 'bg-blue-500';
    return 'bg-green-500'; // Default for positive messages
  };

  return (
    <div
      className={`fixed top-20 right-4 sm:right-6 md:right-8 transition-all duration-500 ease-in-out ${visible ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-full'}`}
    >
      <div className={`flex items-center justify-between gap-4 p-4 rounded-lg shadow-2xl text-white font-bold ${getNotificationStyle()}`}>
        <span>{message}</span>
        <button onClick={onClose} className="font-mono text-lg leading-none">&times;</button>
      </div>
    </div>
  );
};

export default Notification;