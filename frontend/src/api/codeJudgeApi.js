export const API_BASE_URL = "http://localhost:7070/api";

async function readResponse(response) {
  const text = await response.text();
  let data = null;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    const message = typeof data === "string"
      ? data
      : data?.message || data?.error || `Request failed with status ${response.status}`;
    throw new Error(message);
  }

  return data;
}

async function sendJson(path, payload) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  return readResponse(response);
}

export async function getUsers() {
  return readResponse(await fetch(`${API_BASE_URL}/users`));
}

export async function createUser(payload) {
  return sendJson("/users", payload);
}

export async function deleteUser(id) {
  return readResponse(await fetch(`${API_BASE_URL}/users/${encodeURIComponent(id)}`, {
    method: "DELETE",
  }));
}

export async function getProblems() {
  return readResponse(await fetch(`${API_BASE_URL}/problems`));
}

export async function getProblem(id) {
  return readResponse(await fetch(`${API_BASE_URL}/problems/${encodeURIComponent(id)}`));
}

export async function createProblem(payload) {
  return sendJson("/problems", payload);
}

export async function deleteProblem(id) {
  return readResponse(await fetch(`${API_BASE_URL}/problems/${encodeURIComponent(id)}`, {
    method: "DELETE",
  }));
}

export async function getContests() {
  return readResponse(await fetch(`${API_BASE_URL}/contests`));
}

export async function createContest(payload) {
  return sendJson("/contests", payload);
}

export async function deleteContest(id) {
  return readResponse(await fetch(`${API_BASE_URL}/contests/${encodeURIComponent(id)}`, {
    method: "DELETE",
  }));
}

export async function submitCode(payload) {
  return sendJson("/submit", payload);
}

export async function getSubmission(id) {
  return readResponse(await fetch(`${API_BASE_URL}/submissions/${encodeURIComponent(id)}`));
}

export async function getUserSubmissions(userId) {
  return readResponse(await fetch(
    `${API_BASE_URL}/submissions/?userId=${encodeURIComponent(userId)}`
  ));
}
