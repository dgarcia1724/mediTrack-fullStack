"use client";

import { useState } from "react";
import { auth } from "@/lib/firebase";
import {
  signInWithCustomToken,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  AuthError,
} from "firebase/auth";

export default function TestAuth() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState("");
  const [idToken, setIdToken] = useState("");
  const [adminResponse, setAdminResponse] = useState("");
  const [error, setError] = useState("");

  const handleSignIn = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const userCredential = await signInWithEmailAndPassword(
        auth,
        email,
        password
      );
      const token = await userCredential.user.getIdToken();
      setIdToken(token);
      setError("");
    } catch (error) {
      const authError = error as AuthError;
      setError(authError.message);
    }
  };

  const handleSignUp = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const userCredential = await createUserWithEmailAndPassword(
        auth,
        email,
        password
      );
      const token = await userCredential.user.getIdToken();
      setIdToken(token);
      setError("");
    } catch (error) {
      const authError = error as AuthError;
      setError(authError.message);
    }
  };

  const getCustomToken = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/users/get-test-token",
        {
          method: "POST",
        }
      );
      const data = await response.json();
      setToken(data.token);
    } catch (error) {
      console.error("Error getting custom token:", error);
    }
  };

  const exchangeForIdToken = async () => {
    try {
      const userCredential = await signInWithCustomToken(auth, token);
      const idToken = await userCredential.user.getIdToken();
      setIdToken(idToken);
    } catch (error) {
      console.error("Error exchanging token:", error);
    }
  };

  const testAdminEndpoint = async () => {
    try {
      const response = await fetch("http://localhost:8080/admin-only", {
        headers: {
          Authorization: `Bearer ${idToken}`,
        },
      });
      const data = await response.text();
      setAdminResponse(data);
    } catch (error) {
      console.error("Error testing admin endpoint:", error);
    }
  };

  return (
    <div className="p-4 max-w-md mx-auto">
      <h1 className="text-2xl font-bold mb-4 text-gray-800">
        Authentication Test
      </h1>

      {/* Sign In/Sign Up Form */}
      <div className="mb-6 p-4 bg-white border rounded-lg shadow-sm">
        <h2 className="text-xl font-semibold mb-4 text-gray-800">
          Email/Password Auth
        </h2>
        <form className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1 text-gray-700">
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full p-2 border rounded text-gray-800 bg-white"
              placeholder="Enter your email"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1 text-gray-700">
              Password
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-2 border rounded text-gray-800 bg-white"
              placeholder="Enter your password"
            />
          </div>
          <div className="flex space-x-2">
            <button
              onClick={handleSignIn}
              className="bg-blue-500 text-white px-4 py-2 rounded flex-1 hover:bg-blue-600"
            >
              Sign In
            </button>
            <button
              onClick={handleSignUp}
              className="bg-green-500 text-white px-4 py-2 rounded flex-1 hover:bg-green-600"
            >
              Sign Up
            </button>
          </div>
        </form>
      </div>

      {/* Admin Test Section */}
      <div className="mb-6 p-4 bg-white border rounded-lg shadow-sm">
        <h2 className="text-xl font-semibold mb-4 text-gray-800">Admin Test</h2>
        <div className="space-y-4">
          <button
            onClick={getCustomToken}
            className="bg-blue-500 text-white px-4 py-2 rounded w-full hover:bg-blue-600"
          >
            Get Custom Token
          </button>

          {token && (
            <button
              onClick={exchangeForIdToken}
              className="bg-green-500 text-white px-4 py-2 rounded w-full hover:bg-green-600"
            >
              Exchange for ID Token
            </button>
          )}

          {idToken && (
            <button
              onClick={testAdminEndpoint}
              className="bg-purple-500 text-white px-4 py-2 rounded w-full hover:bg-purple-600"
            >
              Test Admin Endpoint
            </button>
          )}
        </div>
      </div>

      {/* Display Section */}
      <div className="p-4 bg-white border rounded-lg shadow-sm space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">
          Authentication Results
        </h2>

        {error && (
          <div className="p-2 bg-red-50 border border-red-200 rounded">
            <p className="font-medium text-red-800">Error:</p>
            <p className="text-red-700">{error}</p>
          </div>
        )}

        {token && (
          <div className="p-2 bg-white border rounded">
            <p className="font-medium text-lg mb-2 text-gray-800">
              Custom Token (from backend):
            </p>
            <p className="text-sm break-all p-2 rounded border bg-gray-50 text-gray-800">
              {token}
            </p>
            <p className="text-xs text-gray-600 mt-1">
              This token needs to be exchanged for an ID token
            </p>
          </div>
        )}

        {idToken && (
          <div className="p-2 bg-white border rounded">
            <p className="font-medium text-lg mb-2 text-gray-800">
              ID Token (for API calls):
            </p>
            <p className="text-sm break-all p-2 rounded border bg-gray-50 text-gray-800">
              {idToken}
            </p>
            <p className="text-xs text-gray-600 mt-1">
              Use this token in the Authorization header for API calls
            </p>
          </div>
        )}

        {adminResponse && (
          <div className="p-2 bg-white border rounded">
            <p className="font-medium text-lg mb-2 text-gray-800">
              Admin Endpoint Response:
            </p>
            <p className="text-green-700 font-medium">{adminResponse}</p>
          </div>
        )}
      </div>
    </div>
  );
}
