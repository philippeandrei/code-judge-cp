// TODO: Set this to the URL where the Code Judge API is running.
export const API_BASE_URL = "";

// This file is intentionally incomplete.
// Implement each function by calling the matching API endpoint.
// You may create a shared request helper later, but it is not required.

export async function getUsers() {
  // TODO: Send GET /users and return the response data.
  return [];
}

export async function createUser(payload) {
  // TODO: Send POST /users with payload as the JSON request body.
  return payload;
}

export async function deleteUser(id) {
  // TODO: Send DELETE /users/{id}.
  return null;
}

export async function getProblems() {
  // TODO: Send GET /problems and return the response data.
  return [];
}

export async function getProblem(id) {
  // TODO: Send GET /problems/{id} and return the response data.
  return { id };
}

export async function createProblem(payload) {
  // TODO: Send POST /problems with payload as the JSON request body.
  return payload;
}

export async function deleteProblem(id) {
  // TODO: Send DELETE /problems/{id}.
  return null;
}

export async function getContests() {
  // TODO: Send GET /contests and return the response data.
  return [];
}

export async function createContest(payload) {
  // TODO: Send POST /contests with payload as the JSON request body.
  return payload;
}

export async function deleteContest(id) {
  // TODO: Send DELETE /contests/{id}.
  return null;
}

export async function submitCode(payload) {
  // TODO: Send POST /submit with payload as the JSON request body.
  return { status: "PENDING", ...payload };
}

export async function getSubmission(id) {
  // TODO: Send GET /submissions/{id} and return the response data.
  return { id, status: "PENDING" };
}

export async function getUserSubmissions(userId) {
  // TODO: Send GET /submissions/?userId={userId} and return the response data.
  return [];
}
