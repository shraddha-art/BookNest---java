# BookNest 📚
A desktop-based Library Management System built with Java, Swing, JDBC, and MySQL.

## Features
- Secure login screen
- Manage Readers — add, edit, delete, track books read
- Manage Books — add, edit, delete
- Start tab — issue books to readers with a start date
- End tab — record book returns, auto-calculate days taken
- Logout with confirmation dialog

## Tech Stack
| Technology | Usage |
|---|---|
| Java | Core language |
| NetBeans | IDE + Swing GUI |
| JDBC | Database connectivity |
| MySQL | Backend database |

## Project Structure
```
BookNest/
├── src/
│   ├── LoginForm.java       # Login screen
│   ├── Dashboard.java       # Main dashboard with all tabs
│   └── DBConnection.java    # MySQL connection handler
├── library_db.sql           # Database schema
└── mysql-connector-j.jar    # JDBC driver
```

## Database Schema
- **readers** — reader_id, name
- **books** — book_id, title
- **issue** — issue_id, reader_id, book_id, issue_date
- **returns** — return_id, issue_id, end_date, days_taken

## Setup Instructions
1. Install MySQL and create the database:
```sql
source library_db.sql
```
2. Update credentials in `DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/library_db";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```
3. Add `mysql-connector-j.jar` to project dependencies
4. Run `LoginForm.java`
5. Login with `admin / admin`

## Screenshots
> Login Screen and Dashboard coming soon

## Author
Shraddha Paithankar
