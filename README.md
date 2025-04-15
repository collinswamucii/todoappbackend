To-Do Application

Core Features
1️⃣ User Authentication
Implemented JWT-based sign-up and sign-in.
Different roles: Users and Admins (Admins can manage all tasks).
Users can only manage their own tasks.
2️⃣ Task Creation & Management
Users can create new tasks with details:
Task Title
Description
Due Date
Priority Level (Low, Medium, High)
Optional Attachments (Images/PDFs stored as Base64)
3️⃣ Edit & Delete Tasks
Users can edit task details before completion.
Tasks can be deleted if no longer needed.
4️⃣ Task Status Management
When created, tasks start as "To Do."
Users can update the status to:
In Progress
Completed
Archived (Admins only)
5️⃣ Filter & Sort Tasks
Users can filter tasks by:
Status (To Do, In Progress, Completed, Archived)
Priority (Low, Medium, High)
Due Date
Sorting options: Newest First, Oldest First, Priority Level
✅ Technical Requirements
✔ MySQL database has been used.
✔ JWT authentication.
✔ Validate & sanitize input to prevent security vulnerabilities.
✔ Use a database to store tasks, users, and statuses.
✔ Convert images/PDFs to Base64 before storing them.
