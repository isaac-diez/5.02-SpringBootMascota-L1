import React from 'react';

const PetStatBar = ({ label, value, hexColor }) => {
    const percentage = Math.max(0, Math.min(100, value));
    console.log(label, value);

    return (
        <div className="mb-2">
            <div className="flex justify-between mb-1">
                <span className="text-base font-bold">{label}</span>
                <span className="text-sm font-bold">{Math.round(percentage)}%</span>
            </div>
<div className="w-full bg-gray-200 rounded-full h-8 border-2 border-black" style={{ height: '12px' }}>
  <div
    className="h-full rounded-full transition-all duration-300"
                    style={{
                        width: `${percentage}%`,
                        backgroundColor: hexColor
                    }}>
                </div>
            </div>
        </div>
    );
};

export default PetStatBar;