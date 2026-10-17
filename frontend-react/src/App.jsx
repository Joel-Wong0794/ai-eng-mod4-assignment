import { useEffect, useState } from "react";
import "./App.css";

// React reads tasks from the existing backend; it does not store them itself.
const API_URL =
  "https://ai-eng-mod4-assignment-production.up.railway.app/api/tasks";

export default function App() {
  // Updating state makes React render the page again with the latest values.
  const [tasks, setTasks] = useState([]);
  const [status, setStatus] = useState("Loading tasks...");
  const [loading, setLoading] = useState(true);
  const [refreshCount, setRefreshCount] = useState(0);

  // Run the request on mount and whenever the Refresh button changes the count.
  useEffect(() => {
    const controller = new AbortController();

    async function loadTasks() {
      try {
        const response = await fetch(API_URL, { signal: controller.signal });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        // Convert the response body from JSON into a JavaScript array.
        const data = await response.json();
        if (!Array.isArray(data)) throw new Error("Expected a task list");
        if (controller.signal.aborted) return;

        setTasks(data);
        setStatus(
          data.length
            ? `${data.length} task${data.length === 1 ? "" : "s"}`
            : "No tasks yet.",
        );
      } catch (error) {
        if (controller.signal.aborted) return;
        setStatus("Unable to load tasks. Please try again.");
        console.error(error);
      } finally {
        if (!controller.signal.aborted) setLoading(false);
      }
    }

    loadTasks();

    // Cancel an old request if the component unmounts or the effect runs again.
    return () => controller.abort();
  }, [refreshCount]);

  // Set loading immediately; changing the count triggers the effect above.
  function refreshTasks() {
    setLoading(true);
    setStatus("Loading tasks...");
    setRefreshCount((count) => count + 1);
  }

  // JSX describes the page. React escapes task text before displaying it.
  return (
    <main className="task-page">
      <header className="page-header">
        <div>
          <h1>
            TaskFlow<span className="brand-dot">.</span>
          </h1>
          <p className="page-label">My tasks</p>
        </div>
        <button type="button" onClick={refreshTasks} disabled={loading}>
          {loading ? "Loading..." : "Refresh"}
        </button>
      </header>

      <p className="request-status" role="status">
        {status}
      </p>

      {/* map creates one card per task; key lets React identify each item. */}
      <ul className="task-list" aria-label="Tasks" aria-busy={loading}>
        {tasks.map((task) => (
          <li className="task-card" key={task.id}>
            {/* A description list pairs each JSON key (dt) with its value (dd). */}
            <dl className="task-fields">
              <div className="wide-field">
                <dt>id</dt>
                <dd className="task-id">{task.id}</dd>
              </div>
              <div className="wide-field">
                <dt>title</dt>
                <dd>
                  <h2>{task.title}</h2>
                </dd>
              </div>
              <div className="wide-field">
                <dt>description</dt>
                <dd className="description">{task.description || "Not set"}</dd>
              </div>
              <div>
                <dt>priority</dt>
                <dd>
                  <span className="priority">{task.priority || "Not set"}</span>
                </dd>
              </div>
              <div>
                <dt>dueDate</dt>
                <dd>{task.dueDate || "Not set"}</dd>
              </div>
              <div>
                <dt>completed</dt>
                {/* Display a readable label without changing the API's boolean value. */}
                <dd>
                  <span
                    className={
                      task.completed ? "completion done" : "completion"
                    }
                  >
                    {task.completed ? "YES" : "NO"}
                  </span>
                </dd>
              </div>
            </dl>
          </li>
        ))}
      </ul>
    </main>
  );
}
