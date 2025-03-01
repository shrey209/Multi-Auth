import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();
  const [token, setToken] = useState(null); // Start as null, avoid undefined issues

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const jwtFromUrl = urlParams.get("token");
    const jwtFromStorage = localStorage.getItem("jwt");

    if (jwtFromUrl) {
      setToken(jwtFromUrl);
      localStorage.setItem("jwt", jwtFromUrl);
    } else if (jwtFromStorage) {
      setToken(jwtFromStorage);
    }
  }, []);

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
      <h1 className="text-2xl font-bold text-gray-800 mb-4">Hello World</h1>

      {/* Show token if present */}
      {token ? (
        <div className="text-sm text-gray-600">Token: {token}</div>
      ) : (
        <div className="text-sm text-red-600">No token found</div>
      )}

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
