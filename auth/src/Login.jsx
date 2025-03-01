import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const OtpVerification = ({ email }) => {
  const navigate = useNavigate();
  const [otp, setOtp] = useState(Array(6).fill(""));

  const handleChange = (index, value) => {
    if (/^\d?$/.test(value)) {
      const newOtp = [...otp];
      newOtp[index] = value;
      setOtp(newOtp);

      if (value && index < 5) {
        document.getElementById(`otp-${index + 1}`).focus();
      }
    }
  };

  const handleSubmitOtp = async () => {
    const otpCode = otp.join("");
    try {
        const response = await axios.post("http://localhost:8000/authv1/verify", {
            email,
            otp: otpCode
        }, {
            headers: { "Content-Type": "application/json" }
        });
        console.log("OTP Verified:", response.data);
        navigate(`/?token=${response.data}`);
    } catch (error) {
        console.error("OTP Verification Error:", error.response?.data || error.message);
    }
};
  return (
    <div className="flex flex-col items-center">
      <h2 className="text-2xl font-bold text-gray-800 mb-4">Enter OTP</h2>
      <p className="text-gray-600 mb-4">We've sent a 6-digit code to {email}</p>
      <div className="flex space-x-2 mb-4">
        {otp.map((digit, index) => (
          <input
            key={index}
            id={`otp-${index}`}
            type="text"
            value={digit}
            maxLength="1"
            onChange={(e) => handleChange(index, e.target.value)}
            className="w-12 h-12 text-center border border-gray-300 rounded-lg focus:ring focus:ring-blue-300"
          />
        ))}
      </div>
      <button
        className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition duration-300"
        onClick={handleSubmitOtp}
      >
        Verify OTP
      </button>
    </div>
  );
};

const AuthForm = () => {
  const [isSignup, setIsSignup] = useState(false);
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8000/authv1/register", {
        username: name,
        password,
        gmail: email,
      });
      console.log("Signup Success:", response.data);
      setIsOtpSent(true);
    } catch (error) {
      console.error("Signup Error:", error.response?.data || error.message);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8000/authv1/login", {
        username,
        password,
      });

      console.log("Login Success:", response.data);
      navigate(`/?token=${response.data}`);
    } catch (error) {
      console.error("Login Error:", error.response?.data || error.message);
    }
  };

  if (isOtpSent) {
    return <OtpVerification email={email} />;
  }

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-lg max-w-sm w-full">
        <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
          {isSignup ? "Create an Account" : "Welcome Back"}
        </h2>

        <form className="space-y-4" onSubmit={isSignup ? handleSignup : handleLogin}>
          {isSignup && (
            <div>
              <label className="block text-sm font-medium text-gray-700">Full Name</label>
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
              {isSignup ? "Email" : "Username"}
            </label>
            <input
              type="text"
              placeholder={isSignup ? "example@gmail.com" : "Enter your username"}
              value={isSignup ? email : username}
              onChange={(e) => (isSignup ? setEmail(e.target.value) : setUsername(e.target.value))}
              className="mt-1 w-full px-4 py-2 border rounded-lg focus:ring focus:ring-blue-300"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Password</label>
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
              <label className="block text-sm font-medium text-gray-700">Confirm Password</label>
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

        <div className="flex items-center my-4">
          <div className="flex-1 h-px bg-gray-300"></div>
          <span className="px-2 text-gray-500">OR</span>
          <div className="flex-1 h-px bg-gray-300"></div>
        </div>

        <p className="mt-4 text-center text-gray-600">
          {isSignup ? "Already have an account?" : "Don't have an account?"}
          <button
            type="button"
            onClick={() => setIsSignup(!isSignup)}
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
