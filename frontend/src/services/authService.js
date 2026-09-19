import api from "./api";
import { jwtDecode } from "jwt-decode";

export async function login(userName, password) {
    const response = await api.post("/auth/login", {
        userName,
        password,
    });

    const token = response.data.token;

    localStorage.setItem("token", token);

    return token;
}

export function logout() {
    localStorage.removeItem("token");
}

export function getCurrentUsername() {
    const token = localStorage.getItem("token");

    if (!token) {
        return null;
    }

    try {
        const decoded = jwtDecode(token);
        return decoded.sub;
    } catch (error) {
        console.error("Unable to decode JWT:", error);
        return null;
    }
}

export function isAuthenticated() {
    return !!localStorage.getItem("token");
}