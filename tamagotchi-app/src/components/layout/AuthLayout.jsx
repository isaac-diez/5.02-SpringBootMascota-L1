import React from 'react';

const AuthLayout = ({ children }) => {

  const backgroundImageUrl = '/background_tamagotchi.png';

  return (

    <div
      className="min-h-screen flex items-center justify-center bg-gray-100 p-4"

      style={{ backgroundImage: `url(${backgroundImageUrl})` }}
    >

      <div className="bg-white/80 backdrop-blur-sm p-8 rounded-xl shadow-2xl">
        <div className="">
            <h1 className="font-pixel text-5xl text-center text-slate-900 mb-8">
                My Pet App
            </h1>
        </div>
        {children}
      </div>
    </div>
  );
};

export default AuthLayout;

