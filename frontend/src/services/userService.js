import api from "./api";

export async function getUserByUsername(userName) {
    const response = await api.get(
        `/empNtkt/by-username/${encodeURIComponent(userName)}`
    );

    return response.data;
}
export async function getEmployees() {
    const response = await api.get("/empNtkt/employees");
    return response.data;
}

export async function getUserById(id) {
    const response = await api.get(`/empNtkt/user/${id}`);

    return response.data;
}

export async function createManager(manager) {
    const response = await api.post("/admin/create-manager", manager);
    return response.data;
}