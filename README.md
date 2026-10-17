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

## 2 Stretch Goal

### 2.1 Stretch Goal: Option 2

Deployed a React frontend on Netlify that fetches tasks from
the Spring Boot API on Railway. Configured backend CORS to
allow requests from the Netlify domain.

Frontend URL: https://ai-eng-mod4-assignment.netlify.app

![Netlify frontend showing Railway tasks](screenshots/stretch02-01_ReactFrontEnd.png)

## 3. Brief Description About This Project

TaskFlow is a backend API for keeping track of everyday tasks, such as buying groceries, paying bills, and doing laundry. Each task has an automatically generated ID, a title, a description, a priority, a due date, and a completion status.

### 3.1 What the API Does

The API accepts and returns JSON data rather than displaying a graphical task management interface. Opening the [live tasks endpoint](https://ai-eng-mod4-assignment-production.up.railway.app/api/tasks) in a browser shows the tasks currently stored in the deployed application.

| Method | Endpoint          | Purpose                                       |
| ------ | ----------------- | --------------------------------------------- |
| GET    | `/api/tasks`      | Retrieve all tasks.                           |
| GET    | `/api/tasks/{id}` | Retrieve an existing task by its ID.          |
| POST   | `/api/tasks`      | Create a task by sending its details as JSON. |

New tasks are added using an HTTP client such as curl or Postman. Refreshing the live tasks endpoint then shows the updated list. If no tasks are stored, the API returns an empty JSON array (`[]`).

### 3.2 How It Runs

The application is built with Spring Boot and Java 21, with Maven managing its dependencies and build process. A multi-stage Dockerfile first builds the application JAR, then copies it into a smaller Java runtime image. Railway builds and runs the container to make the API publicly accessible.

### 3.3 Data Storage Limitation

Tasks are stored in an in-memory H2 database. Manually added tasks are lost whenever the backend restarts or redeploys. To keep the presentation populated, a startup initializer now inserts three sample tasks if the database is empty: buying groceries, paying an electricity bill, and doing laundry. Laundry is marked completed so the frontend demonstrates both YES and NO labels.

The sample due dates are based on the backend's startup date. Seeding runs when the backend starts, not whenever someone visits Netlify, and leaves existing tasks unchanged. This is demo-data initialization, not persistent storage. The earlier screenshots show tasks that were added manually using POST requests.
