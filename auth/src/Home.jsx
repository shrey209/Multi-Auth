import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate(); 
  const [token, setToken] = useState(localStorage.getItem("jwt") || null);

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const jwt = urlParams.get("token");

    if (jwt) {
      setToken(jwt);
      localStorage.setItem("jwt", jwt);
      window.history.replaceState({}, document.title, "/"); 
    }
  }, []);

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
      <h1 className="text-2xl font-bold text-gray-800 mb-4">Hello World</h1>
      
      {/* Show token if present */}
      {token && <div className="text-sm text-gray-600">Token: {token}</div>}

      {/* Show login button only if no token is present */}
      {!token && (
        <button
          onClick={() => navigate("/login")}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg shadow-lg hover:bg-blue-700 transition duration-300"
        >
          Go to Login
        </button>
      )}
    </div>
  );
};

export default Home;
