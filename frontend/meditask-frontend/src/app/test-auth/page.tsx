"use client";

import { useState } from "react";
import { auth } from "@/lib/firebase";
import { signInWithCustomToken } from "firebase/auth";

export default function TestAuth() {
  const [token, setToken] = useState("");
  const [idToken, setIdToken] = useState("");
  const [adminResponse, setAdminResponse] = useState("");

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
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Firebase Auth Test</h1>

      <div className="space-y-4">
        <button
          onClick={getCustomToken}
          className="bg-blue-500 text-white px-4 py-2 rounded"
        >
          Get Custom Token
        </button>

        {token && (
          <button
            onClick={exchangeForIdToken}
            className="bg-green-500 text-white px-4 py-2 rounded ml-2"
          >
            Exchange for ID Token
          </button>
        )}

        {idToken && (
          <button
            onClick={testAdminEndpoint}
            className="bg-purple-500 text-white px-4 py-2 rounded ml-2"
          >
            Test Admin Endpoint
          </button>
        )}
      </div>

      <div className="mt-4 space-y-2">
        {token && <p>Custom Token: {token.substring(0, 20)}...</p>}
        {idToken && <p>ID Token: {idToken.substring(0, 20)}...</p>}
        {adminResponse && <p>Admin Response: {adminResponse}</p>}
      </div>
    </div>
  );
}
