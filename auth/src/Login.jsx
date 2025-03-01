import React, { useState } from "react";

const AuthForm = () => {
  const [isSignup, setIsSignup] = useState(false);
  const [name, setName] = useState(""); // For signup
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleGoogleAuth = () => {
    window.location.href = `https://accounts.google.com/o/oauth2/auth?client_id=YOUR_GOOGLE_CLIENT_ID&redirect_uri=${encodeURIComponent(
      "http://localhost:8000/auth/google/callback"
    )}&response_type=code&scope=openid%20email%20profile`;
  };

  const handleGitHubAuth = () => {
    window.location.href = `https://github.com/login/oauth/authorize?client_id=Ov23li4s00ZjAYcdwaGD&redirect_uri=${encodeURIComponent(
      "http://localhost:8000/auth/github/callback"
    )}&scope=user:email`;
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevents page reload
  
    const url = isSignup
      ? "http://localhost:8000/authv1/register"
      : "http://localhost:8000/authv1/login";
  
    const requestData = isSignup
      ? { username: name, password, gmail: email } 
      : { username: email, password }; 
  
    try {
      const response = await fetch(url, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestData),
      });
  
      const responseData = await response;
      console.log("API Response:", responseData.text);
    } catch (error) {
      console.error("Error:", error);
    }
  };
  

  const toggleMode = () => {
    setIsSignup((prev) => !prev);
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-lg max-w-sm w-full">
        <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
          {isSignup ? "Create an Account" : "Welcome Back"}
        </h2>

        {/* Form */}
        <form className="space-y-4" onSubmit={handleSubmit}>
          {isSignup && (
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Full Name
              </label>
              <input
                type="text"
                placeholder="John Doe"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="mt-1 w-full px-4 py-2 border rounded-lg focus:ring focus:ring-blue-300"
                required
              />
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700">
              Email
            </label>
            <input
              type="email"
              placeholder="example@gmail.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="mt-1 w-full px-4 py-2 border rounded-lg focus:ring focus:ring-blue-300"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">
              Password
            </label>
            <input
              type="password"
              placeholder="********"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-1 w-full px-4 py-2 border rounded-lg focus:ring focus:ring-blue-300"
              required
            />
          </div>

          {isSignup && (
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Confirm Password
              </label>
              <input
                type="password"
                placeholder="********"
                className="mt-1 w-full px-4 py-2 border rounded-lg focus:ring focus:ring-blue-300"
                required
              />
            </div>
          )}

          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition duration-300"
          >
            {isSignup ? "Sign Up" : "Login"}
          </button>
        </form>

        {/* Divider */}
        <div className="flex items-center my-4">
          <div className="flex-1 h-px bg-gray-300"></div>
          <span className="px-2 text-gray-500">OR</span>
          <div className="flex-1 h-px bg-gray-300"></div>
        </div>

        {/* Google & GitHub Auth */}
        <div className="space-y-2">
          <button
            className="w-full flex items-center justify-center bg-red-500 text-white py-2 rounded-lg hover:bg-red-600 transition"
            onClick={handleGoogleAuth}
          >
            <span className="mr-2">🔴</span> Continue with Google
          </button>

          <button
            className="w-full flex items-center justify-center bg-gray-800 text-white py-2 rounded-lg hover:bg-gray-900 transition"
            onClick={handleGitHubAuth}
          >
            <span className="mr-2">🐙</span> Continue with GitHub
          </button>
        </div>

        {/* Toggle Login/Signup */}
        <p className="mt-4 text-center text-gray-600">
          {isSignup ? "Already have an account?" : "Don't have an account?"}
          <button
            type="button"
            onClick={toggleMode}
            className="text-blue-600 hover:underline ml-1"
          >
            {isSignup ? "Login" : "Sign Up"}
          </button>
        </p>
      </div>
    </div>
  );
};

export default AuthForm;
