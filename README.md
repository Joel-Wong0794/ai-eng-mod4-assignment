# TaskFlow API

A simple task management REST API built with Spring Boot. This is the sample project for the **Module 4 - End of Module Assignment**.

## 1. Assignment submission Description

### 1.1 Live application URL

Your TaskFlow API deployed on Railway:

```
https://ai-eng-mod4-assignment-production.up.railway.app/api/tasks
```

### 1.2 Screenshot: Railway dashboard

This is the Railway dashboard showing the active deployment.

![Railway dashboard](screenshots/dashboard.png)

### 1.3 Screenshot: Live application

(a) This is the live Railway URL that should returning task data (in the terminal). This is before adding tasks.

![Live application](screenshots/application00_InitialView.png)

(b) Used POST requests to add everyday tasks, such as buying groceries, paying an electricity bill, and doing laundry.
![Live application](screenshots/application01_PostTasks.png)

(c) The live Railway API returns the newly created tasks as JSON.
![Live Railway API showing task data](screenshots/application02_TasksOnURL.png)

### 1.4 Stretch Goal: Option 2

Deployed a React frontend on Netlify that fetches tasks from
the Spring Boot API on Railway. Configured backend CORS to
allow requests from the Netlify domain.

Frontend URL: https://ai-eng-mod4-assignment.netlify.app

![Netlify frontend showing Railway tasks](screenshots/stretch02-01_ReactFrontEnd.png)

## 2. Brief Description About This Project

TaskFlow is a backend API for keeping track of everyday tasks, such as buying groceries, paying bills, and doing laundry. Each task has an automatically generated ID, a title, a description, a priority, a due date, and a completion status.

### 2.1 What the API Does

The API accepts and returns JSON data rather than displaying a graphical task management interface. Opening the [live tasks endpoint](https://ai-eng-mod4-assignment-production.up.railway.app/api/tasks) in a browser shows the tasks currently stored in the deployed application.

| Method | Endpoint          | Purpose                                       |
| ------ | ----------------- | --------------------------------------------- |
| GET    | `/api/tasks`      | Retrieve all tasks.                           |
| GET    | `/api/tasks/{id}` | Retrieve an existing task by its ID.          |
| POST   | `/api/tasks`      | Create a task by sending its details as JSON. |

New tasks are added using an HTTP client such as curl or Postman. Refreshing the live tasks endpoint then shows the updated list. If no tasks are stored, the API returns an empty JSON array (`[]`).

### 2.2 How It Runs

The application is built with Spring Boot and Java 21, with Maven managing its dependencies and build process. A multi-stage Dockerfile first builds the application JAR, then copies it into a smaller Java runtime image. Railway builds and runs the container to make the API publicly accessible.

### 2.3 Data Storage Limitation

Tasks are stored in an in-memory H2 database. They remain available while the application is running, but are lost whenever it restarts or redeploys. The sample tasks shown in the screenshots were added manually using POST requests; they are not automatically recreated at startup.

## 3. Simple Frontend: Learning and Deployment

### 3.1 How the Files Work Together

- `frontend/index.html` defines the page: its heading, Refresh button, and task list.
- `frontend/styles.css` controls spacing, colours, and mobile layout.
- `frontend/app.js` uses `fetch()` to send a GET request to Railway, reads the JSON response, and displays each task. It also handles loading, empty lists, and errors.
- `netlify.toml` tells Netlify to publish the `frontend` folder. No frontend build command or package installation is needed.

The request path is: browser on Netlify -> Railway API -> H2 database -> JSON response -> task list on the page.

Streamlit could also display tasks, but it needs a running Python server. Netlify does not host that server, so this project uses plain HTML, CSS, and JavaScript to meet Option 2 as written.

### 3.2 Preview Locally

From the project root, run:

```bash
python3 -m http.server 5500 --directory frontend
```

Open `http://localhost:5500`. Python only serves the static files; it is not part of the frontend or required on Netlify.

The page requests the live Railway API, not a local database. Until Railway has the updated CORS configuration allowing your preview origin, the page will show an error. VS Code may forward the preview to `http://127.0.0.1:5500`; that is a different origin from `http://localhost:5500`.

### 3.3 Publish on Netlify

1. Commit and push the frontend, `netlify.toml`, and backend CORS changes to GitHub.
2. In Netlify, add a project by importing the GitHub repository.
3. Select the `main` branch. Leave the base directory and build command empty, and set the publish directory to `frontend` (also configured in `netlify.toml`).
4. Deploy the site and copy its HTTPS URL.

### 3.4 Allow the Netlify Origin in Railway

CORS is a browser rule governing whether JavaScript can read a response from another origin. Although curl can read the API, a browser needs the API to allow the frontend's origin. CORS is not authentication and does not make this public API private.

The `@CrossOrigin` annotation in `TaskController` reads the `TASKFLOW_FRONTEND_ORIGIN` environment variable. Its local default is `http://localhost:5500`.

1. Open the backend service in Railway and go to Variables.
2. Add `TASKFLOW_FRONTEND_ORIGIN` with your actual Netlify origin, for example `https://your-site-name.netlify.app`. Include `https://`, but no path or trailing slash.
3. Apply the change and let Railway redeploy with the updated backend code.
4. Open the Netlify site and select Refresh. If the API has no tasks, add some using the POST requests demonstrated earlier, then refresh again.

For a local preview instead, set the variable to the exact preview origin. The current configuration allows one frontend origin at a time; changing it requires a redeployment.

### 3.5 Finish the Submission

Record the real Netlify URL in section 1.4 and add a screenshot of that site displaying tasks fetched from Railway. Save the screenshot in `screenshots/` and embed it in this README. Only mark the stretch goal complete after verifying the deployed connection.

If the page cannot load tasks, check the browser console and Network tab for the request status and CORS message. Confirm Railway has the latest code and that `TASKFLOW_FRONTEND_ORIGIN` exactly matches the site URL. Never use `mode: "no-cors"` as a fix: JavaScript cannot read that response's JSON.
