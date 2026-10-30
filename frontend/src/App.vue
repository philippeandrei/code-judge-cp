<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { cpp } from "@codemirror/lang-cpp";
import { java } from "@codemirror/lang-java";
import { basicSetup } from "codemirror";
import { EditorState, StateEffect } from "@codemirror/state";
import { EditorView } from "@codemirror/view";
import { HighlightStyle, syntaxHighlighting } from "@codemirror/language";
import { tags } from "@lezer/highlight";
import {
  createContest,
  createProblem,
  createUser,
  deleteContest,
  deleteProblem,
  deleteUser,
  getContests,
  getProblem,
  getProblems,
  getSubmission,
  getUserSubmissions,
  getUsers,
  submitCode,
} from "./api/codeJudgeApi";

const users = ref([]);
const problems = ref([]);
const contests = ref([]);
const submissions = ref([]);
const selectedUserId = ref("");
const selectedProblemId = ref("");
const notice = ref({ message: "", error: false });
const apiOnline = ref(false);
const loading = ref(false);
const modal = ref("");
const userForm = ref({ username: "", email: "" });
const problemForm = ref({ title: "", statement: "", timeLimitMs: 2000, memoryLimitKb: 65536, testCases: "" });
const contestForm = ref({ name: "", startTime: "", endTime: "" });
const sourceCode = ref(`import java.util.Scanner;

public class Solution {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println(scanner.nextInt() + scanner.nextInt());
  }
}`);
const language = ref("java");
const queuedSubmission = ref(null);
const editorContainer = ref(null);
let editorView;

const selectedUser = computed(() => users.value.find((user) => user.id === selectedUserId.value));
const latestStatus = computed(() => submissions.value[0]?.status || "—");
const codeLineCount = computed(() => Math.max(1, sourceCode.value.split("\n").length));
const finalSubmissionStatuses = new Set([
  "ACCEPTED",
  "WRONG_ANSWER",
  "TIME_LIMIT_EXCEEDED",
  "RUNTIME_ERROR",
  "COMPILATION_ERROR",
]);
const submissionPolling = ref(false);

function codeLanguage() {
  return language.value === "java" ? java() : cpp();
}

const editorTheme = EditorView.theme({
  "&": {
    color: "#24292f",
    backgroundColor: "#ffffff",
  },
  ".cm-content": {
    caretColor: "#3454d1",
  },
  ".cm-cursor, .cm-dropCursor": {
    borderLeftColor: "#3454d1",
  },
  "&.cm-focused": {
    outline: "none",
  },
  ".cm-selectionBackground, .cm-content ::selection": {
    backgroundColor: "#cce5ff",
  },
  ".cm-activeLine": {
    backgroundColor: "#f6f8fa",
  },
  ".cm-gutters": {
    color: "#8c959f",
    backgroundColor: "#f6f8fa",
    borderRight: "1px solid #e1e4e8",
  },
  ".cm-activeLineGutter": {
    color: "#57606a",
    backgroundColor: "#eaf2ff",
  },
}, { dark: false });

const editorHighlightStyle = HighlightStyle.define([
  { tag: tags.comment, color: "#6e7781", fontStyle: "italic" },
  { tag: [tags.keyword, tags.controlKeyword, tags.definitionKeyword, tags.moduleKeyword], color: "#cf222e" },
  { tag: [tags.typeName, tags.className], color: "#8250df" },
  { tag: [tags.function(tags.variableName), tags.labelName], color: "#0550ae" },
  { tag: [tags.variableName, tags.propertyName], color: "#24292f" },
  { tag: [tags.string, tags.special(tags.string)], color: "#0a3069" },
  { tag: [tags.number, tags.bool, tags.atom], color: "#0550ae" },
  { tag: tags.operator, color: "#953800" },
  { tag: tags.meta, color: "#116329" },
  { tag: tags.invalid, color: "#82071e", backgroundColor: "#ffebe9" },
]);

function editorExtensions() {
  return [
    basicSetup,
    editorTheme,
    syntaxHighlighting(editorHighlightStyle),
    codeLanguage(),
    EditorView.updateListener.of((update) => {
      if (update.docChanged) sourceCode.value = update.state.doc.toString();
    }),
  ];
}

function showNotice(message, error = false) {
  notice.value = { message, error };
  window.clearTimeout(showNotice.timer);
  showNotice.timer = window.setTimeout(() => { notice.value.message = ""; }, 5000);
}

function displayDate(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.valueOf()) ? value : date.toLocaleString();
}

function parseTestCases(value) {
  return value.split("\n").filter(Boolean).map((line) => {
    const [input = "", expectedOutput = ""] = line.split("|");
    return { input: input.trim(), expectedOutput: expectedOutput.trim() };
  });
}

async function loadAll() {
  loading.value = true;
  try {
    const [loadedUsers, loadedProblems, loadedContests] = await Promise.all([
      getUsers(),
      getProblems(),
      getContests(),
    ]);
    users.value = loadedUsers || [];
    problems.value = loadedProblems || [];
    contests.value = loadedContests || [];
    apiOnline.value = true;
    if (!selectedUserId.value && users.value.length) selectedUserId.value = users.value[0].id;
    if (!selectedProblemId.value && problems.value.length) selectedProblemId.value = problems.value[0].id;
    await loadSubmissions();
  } catch (error) {
    apiOnline.value = false;
    showNotice(`Could not load the API: ${error.message}`, true);
  } finally {
    loading.value = false;
  }
}

async function loadSubmissions() {
  if (!selectedUserId.value) {
    submissions.value = [];
    return;
  }
  try {
    submissions.value = await getUserSubmissions(selectedUserId.value) || [];
  } catch (error) {
    showNotice(`Could not load submissions: ${error.message}`, true);
  }
}

function openModal(name) {
  modal.value = name;
}

function closeModal() {
  modal.value = "";
}

async function saveUser() {
  try {
    await createUser(userForm.value);
    userForm.value = { username: "", email: "" };
    closeModal();
    showNotice("User created.");
    await loadAll();
  } catch (error) { showNotice(error.message, true); }
}

async function saveProblem() {
  try {
    const payload = {
      title: problemForm.value.title,
      statement: problemForm.value.statement,
      timeLimitMs: Number(problemForm.value.timeLimitMs),
      memoryLimitKb: Number(problemForm.value.memoryLimitKb),
      testCases: parseTestCases(problemForm.value.testCases),
      categories: [],
    };
    await createProblem(payload);
    problemForm.value = { title: "", statement: "", timeLimitMs: 2000, memoryLimitKb: 65536, testCases: "" };
    closeModal();
    showNotice("Problem created.");
    await loadAll();
  } catch (error) { showNotice(error.message, true); }
}

async function saveContest() {
  try {
    await createContest({ ...contestForm.value, problems: [] });
    contestForm.value = { name: "", startTime: "", endTime: "" };
    closeModal();
    showNotice("Contest created.");
    await loadAll();
  } catch (error) { showNotice(error.message, true); }
}

async function remove(resource, id) {
  if (!window.confirm(`Delete this ${resource}?`)) return;
  try {
    if (resource === "user") await deleteUser(id);
    if (resource === "problem") await deleteProblem(id);
    if (resource === "contest") await deleteContest(id);
    showNotice(`${resource[0].toUpperCase()}${resource.slice(1)} deleted.`);
    await loadAll();
  } catch (error) { showNotice(`Could not delete ${resource}: ${error.message}`, true); }
}

async function viewProblem(id) {
  try {
    const problem = await getProblem(id);
    showNotice(`${problem.title}: ${problem.statement}`);
  } catch (error) { showNotice(error.message, true); }
}

async function refreshSubmission(id) {
  try {
    const updated = await getSubmission(id);
    const index = submissions.value.findIndex((submission) => submission.id === id);
    if (index >= 0) submissions.value[index] = updated;
    showNotice(`Submission status: ${updated.status}`);
  } catch (error) { showNotice(error.message, true); }
}

async function waitForSubmissionStatus(id) {
  const timeoutMs = 30000;
  const intervalMs = 2000;
  const startedAt = Date.now();

  submissionPolling.value = true;
  try {
    while (Date.now() - startedAt < timeoutMs) {
      const updated = await getSubmission(id);
      queuedSubmission.value = updated;
      const index = submissions.value.findIndex((submission) => submission.id === id);
      if (index >= 0) submissions.value[index] = updated;

      if (finalSubmissionStatuses.has(updated.status)) {
        return updated;
      }

      await new Promise((resolve) => window.setTimeout(resolve, intervalMs));
    }

    throw new Error("Submission status check timed out.");
  } finally {
    submissionPolling.value = false;
  }
}

async function sendSubmission() {
  if (!selectedUserId.value || !selectedProblemId.value) {
    showNotice("Create a user and a problem before submitting.", true);
    return;
  }
  try {
    const createdSubmission = await submitCode({
      userId: selectedUserId.value,
      problemId: selectedProblemId.value,
      language: language.value,
      sourceCode: sourceCode.value,
    });
    queuedSubmission.value = createdSubmission;
    showNotice("Submission accepted and added to the judge queue.");
    await loadSubmissions();

    const finishedSubmission = await waitForSubmissionStatus(createdSubmission.id);
    showNotice(`Submission status: ${finishedSubmission.status}`);
    await loadSubmissions();
  } catch (error) { showNotice(`Submission failed: ${error.message}`, true); }
}

onMounted(() => {
  editorView = new EditorView({
    state: EditorState.create({
      doc: sourceCode.value,
      extensions: editorExtensions(),
    }),
    parent: editorContainer.value,
  });
  loadAll();
});

watch(language, () => {
  if (editorView) {
    editorView.dispatch({ effects: StateEffect.reconfigure.of(editorExtensions()) });
  }
});
</script>

<template>
  <header class="topbar">
    <div><p class="eyebrow">Online programming platform</p><h1>Code Judge</h1></div>
    <div class="api-status"><span class="status-dot" :class="{ online: apiOnline, offline: !apiOnline && !loading }"></span><span>{{ apiOnline ? "API connected" : loading ? "Checking API..." : "API unavailable" }}</span><button class="button button-ghost" @click="loadAll">Refresh</button></div>
  </header>

  <main class="shell">
    <div v-if="notice.message" class="notice" :class="{ error: notice.error }" role="status">{{ notice.message }}</div>
    <section class="summary-grid">
      <article class="summary-card"><span class="summary-icon">◎</span><div><span class="summary-label">Users</span><strong>{{ users.length }}</strong></div></article>
      <article class="summary-card"><span class="summary-icon">◇</span><div><span class="summary-label">Problems</span><strong>{{ problems.length }}</strong></div></article>
      <article class="summary-card"><span class="summary-icon">◷</span><div><span class="summary-label">Contests</span><strong>{{ contests.length }}</strong></div></article>
      <article class="summary-card"><span class="summary-icon">✓</span><div><span class="summary-label">Latest submission</span><strong>{{ latestStatus }}</strong></div></article>
    </section>

    <section class="panel resource-panel">
      <div class="section-heading"><div class="section-title"><span class="section-icon user-icon">♙</span><div><h2>Users</h2><p class="section-subtitle">People using the judge</p></div></div><button class="button" @click="openModal('user')">Add user</button></div>
      <div class="resource-grid"><p v-if="!users.length" class="muted">No users yet.</p><article v-for="user in users" :key="user.id" class="resource-card"><div class="resource-main"><span class="avatar">{{ user.username?.charAt(0).toUpperCase() }}</span><div><h3>{{ user.username }}</h3><p>{{ user.email }}</p></div></div><div class="item-actions"><button class="small-button" @click="selectedUserId = user.id; loadSubmissions()">Submissions</button><button class="small-button danger" @click="remove('user', user.id)" aria-label="Delete user">×</button></div></article></div>
    </section>

    <section class="panel resource-panel">
      <div class="section-heading"><div class="section-title"><span class="section-icon problem-icon">◇</span><div><h2>Problems</h2><p class="section-subtitle">Challenges to solve</p></div></div><button class="button" @click="openModal('problem')">Add problem</button></div>
      <div class="resource-grid"><p v-if="!problems.length" class="muted">No problems yet.</p><article v-for="problem in problems" :key="problem.id" class="resource-card"><div><h3>{{ problem.title }}</h3><p class="clamp">{{ problem.statement }}</p><span class="meta">{{ problem.timeLimitMs }} ms limit</span></div><div class="item-actions"><button class="small-button" @click="viewProblem(problem.id)">View</button><button class="small-button danger" @click="remove('problem', problem.id)" aria-label="Delete problem">×</button></div></article></div>
    </section>

    <section class="panel resource-panel">
      <div class="section-heading"><div class="section-title"><span class="section-icon contest-icon">◷</span><div><h2>Contests</h2><p class="section-subtitle">Timed events</p></div></div><button class="button" @click="openModal('contest')">Add contest</button></div>
      <div class="resource-grid"><p v-if="!contests.length" class="muted">No contests yet.</p><article v-for="contest in contests" :key="contest.id" class="resource-card"><div><h3>{{ contest.name }}</h3><p>{{ displayDate(contest.startTime) }}</p><span class="meta">Ends {{ displayDate(contest.endTime) }}</span></div><button class="small-button danger" @click="remove('contest', contest.id)" aria-label="Delete contest">×</button></article></div>
    </section>

    <section class="panel">
      <div class="section-heading"><div><p class="eyebrow">Judge</p><h2>Submit code</h2></div><span class="tag">Async queue</span></div>
      <div class="form-grid">
        <label>User<select v-model="selectedUserId" @change="loadSubmissions"><option v-for="user in users" :key="user.id" :value="user.id">{{ user.username }} ({{ user.id.slice(0, 8) }})</option></select></label>
        <label>Problem<select v-model="selectedProblemId"><option v-for="problem in problems" :key="problem.id" :value="problem.id">{{ problem.title }} ({{ problem.id.slice(0, 8) }})</option></select></label>
        <label>Language<select v-model="language"><option value="java">Java</option><option value="cpp">C++</option></select></label>
        <label class="full-width code-label"><span class="code-label-row"><span>Source code</span><span class="code-hint">{{ language === "java" ? "Java" : "C++" }} · {{ codeLineCount }} lines</span></span><div ref="editorContainer" class="code-editor" aria-label="Source code editor"></div></label>
        <div class="form-actions full-width"><button class="button" @click="sendSubmission">Submit to judge</button><span class="muted">Choose a user and problem first.</span></div>
      </div>
      <div v-if="queuedSubmission" class="submission-result"><strong>{{ submissionPolling ? "Submission is being judged" : "Submission finished" }}</strong><br><span class="muted">{{ queuedSubmission.id }} · {{ queuedSubmission.status }}</span></div>
    </section>

    <section class="panel">
      <div class="section-heading"><div><p class="eyebrow">History</p><h2>Submissions</h2></div></div>
      <div class="card-list"><p v-if="!submissions.length" class="muted">Select a user to view submissions.</p><article v-for="submission in submissions" :key="submission.id" class="item-card"><div><h3>{{ submission.status }}</h3><p>{{ submission.language || "java" }} · {{ displayDate(submission.submittedTime) }} · {{ submission.id }}</p></div><button class="small-button" @click="refreshSubmission(submission.id)">Refresh status</button></article></div>
    </section>
  </main>

  <div v-if="modal" class="modal-backdrop" @click.self="closeModal">
    <form v-if="modal === 'user'" class="modal-card" @submit.prevent="saveUser"><div class="section-heading"><h2>Add user</h2><button type="button" class="icon-button" @click="closeModal">×</button></div><label>Username<input v-model="userForm.username" maxlength="20" required /></label><label>Email<input v-model="userForm.email" type="email" maxlength="50" required /></label><div class="form-actions"><button type="button" class="button button-ghost" @click="closeModal">Cancel</button><button class="button">Create user</button></div></form>
    <form v-if="modal === 'problem'" class="modal-card" @submit.prevent="saveProblem"><div class="section-heading"><h2>Add problem</h2><button type="button" class="icon-button" @click="closeModal">×</button></div><label>Title<input v-model="problemForm.title" maxlength="50" required /></label><label>Statement<textarea v-model="problemForm.statement" rows="4" required></textarea></label><div class="form-grid compact"><label>Time limit (ms)<input v-model="problemForm.timeLimitMs" type="number" min="1" required /></label><label>Memory limit (KB)<input v-model="problemForm.memoryLimitKb" type="number" min="1" required /></label></div><label>Test cases <span class="hint">One input/output pair per line, separated by <code>|</code></span><textarea v-model="problemForm.testCases" rows="4" placeholder="1 2 | 3"></textarea></label><div class="form-actions"><button type="button" class="button button-ghost" @click="closeModal">Cancel</button><button class="button">Create problem</button></div></form>
    <form v-if="modal === 'contest'" class="modal-card" @submit.prevent="saveContest"><div class="section-heading"><h2>Add contest</h2><button type="button" class="icon-button" @click="closeModal">×</button></div><label>Name<input v-model="contestForm.name" maxlength="20" required /></label><div class="form-grid compact"><label>Starts<input v-model="contestForm.startTime" type="datetime-local" required /></label><label>Ends<input v-model="contestForm.endTime" type="datetime-local" required /></label></div><div class="form-actions"><button type="button" class="button button-ghost" @click="closeModal">Cancel</button><button class="button">Create contest</button></div></form>
  </div>
</template>
