// TODO: Set this to the URL where the Code Judge API is running.
export const API_BASE_URL = "http://localhost:7070";

// This file is intentionally incomplete.
// Implement each function by calling the matching API endpoint.
// You may create a shared request helper later, but it is not required.

export async function getUsers() {
    const response = await fetch(`${API_BASE_URL}/api/users`);
    if (!response.ok) {
        throw new Error(`Could not load users: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function createUser(payload) {
    const response = await fetch(`${API_BASE_URL}/api/users`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload)
    });
    if (!response.ok) {
        throw new Error(`Could not create user: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function deleteUser(id) {
    // TODO: Send DELETE /users/{id}.
    return null;
}

export async function getProblems() {
    const response = await fetch(`${API_BASE_URL}/api/problems`);
    if (!response.ok) {
        throw new Error(`Could not load problems: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function getProblem(id) {
    const response = await fetch(`${API_BASE_URL}/api/problems/${id}`);
    if (!response.ok) {
        throw new Error(`Could not load problem: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;

}

export async function createProblem(payload) {
    const response = await fetch(`${API_BASE_URL}/api/problems`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload)
    });
    if (!response.ok) {
        throw new Error(`Could not create problem: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function deleteProblem(id) {
    // TODO: Send DELETE /problems/{id}.
    return null;
}

export async function getContests() {
    const response = await fetch(`${API_BASE_URL}/api/contests`);
    if (!response.ok) {
        throw new Error(`Could not load contests: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function createContest(payload) {

    const response = await fetch(`${API_BASE_URL}/api/contests`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload)
    });
    if (!response.ok) {
        throw new Error(`Could not create contests: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function deleteContest(id) {
    // TODO: Send DELETE /contests/{id}.
    return null;
}

export async function submitCode(payload) {
    // TODO: Send POST /submit with payload as the JSON request body.
    const response = await fetch(`${API_BASE_URL}/api/submit`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload)
    });
    if (!response.ok) {
        throw new Error(`Could not create submission: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function getSubmission(id) {
    const response = await fetch(`${API_BASE_URL}/api/submissions/${id}`);
    if (!response.ok) {
        throw new Error(`Could not load submission: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}

export async function getUserSubmissions(userId) {
    const response = await fetch(`${API_BASE_URL}/api/submissions/?userId=${userId}`);
    if (!response.ok) {
        throw new Error(`Could not load submissions: ${response.status}`);
    }
    const result = await response.json();
    console.log(result);

    return result;
}
