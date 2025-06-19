import React from 'react';

const AuthLayout = ({ children }) => {
  // A high-resolution placeholder image. You can replace this URL with your own.
  const backgroundImageUrl = '/background_tamagotchi.png';

  return (
    // This main div creates the full-screen background
    <div
      className="min-h-screen flex items-center justify-center bg-gray-100 p-4"

      style={{ backgroundImage: `url(${backgroundImageUrl})` }}
    >
      {/* The children prop will render whatever is placed inside the AuthLayout.
        In this case, it will be your AuthForm for either login or register.
        The white background and blur effect make the form highly readable.
      */}

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

