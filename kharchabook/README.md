# KharchaBook
### Personal Finance Tracker & Financial Literacy System
Module: CS5054NT — Advanced Programming and Technologies
Team: L2C1 Boys

## 🚀 Quick Start with VS Code / Windsurf

### Prerequisites
- **Java 11** or higher
- **Apache Maven** installed
- **MySQL Database** (or any database you prefer)
- **VS Code** or **Windsurf** with the following extensions:

### Required VS Code Extensions
1. **Extension Pack for Java** (Microsoft)
2. **Maven for Java** (Microsoft)
3. **Tomcat for Java** (Adrien Piquerez) - Optional
4. **Database Client** - Optional for database management

### 📋 Setup Instructions

#### 1. Clone/Open the Project
```bash
# Open in VS Code/Windsurf
code d:\Advanced_Programming_Code\kharchabook
```

#### 2. Set Up Database
```bash
# Create the database using the SQL schema
mysql -u root -p < sql/schema.sql
```

#### 3. Configure Database Connection
Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/kharchabook
db.username=root
db.password=your_mysql_password
```

#### 4. Build and Run the Project

**Option A: Using VS Code Tasks (Recommended)**
1. Press `Ctrl+Shift+P` → "Tasks: Run Task"
2. Select "build-full" to compile and package
3. Select "maven-tomcat-run" to start the server

**Option B: Using Terminal**
```bash
# In VS Code terminal (Ctrl+`)
mvn clean compile
mvn tomcat7:run
```

**Option C: Using Maven View**
1. Open Maven view (`Ctrl+Shift+P` → "Maven: Open Maven View")
2. Expand `kharchabook` → `Plugins` → `tomcat7` → `tomcat7:run`
3. Right-click and "Run"

#### 5. Access the Application
- **Home**: `http://localhost:8080/kharchabook`
- **Login**: `http://localhost:8080/kharchabook/login.jsp`
- **Register**: `http://localhost:8080/kharchabook/register.jsp`

### 🐛 Debugging Setup

#### Debug Configuration
The project includes pre-configured debug settings in `.vscode/launch.json`:

1. **Debug Java (Attach)**: For attaching to running Tomcat
2. **Debug Maven Tomcat**: Combined debug and run

#### How to Debug
1. Run "maven-tomcat-run-debug" task
2. Press `F5` and select "Debug Java (Attach)"
3. Set breakpoints in your Java code

### 🛠️ Development Features

#### VS Code Configuration
- **Auto-format on save**: Enabled
- **Auto-import organization**: Enabled
- **Java 17 compatibility**: Configured
- **Maven integration**: Automatic build configuration

#### Available Tasks
- `build-full`: Clean, compile, and package
- `maven-clean`: Clean build artifacts
- `maven-compile`: Compile source code
- `maven-package`: Create WAR file
- `maven-tomcat-run`: Run on Tomcat
- `maven-tomcat-run-debug`: Run with debug enabled
- `maven-install`: Install to local repository

#### Hot Reload
For development convenience, use Jetty plugin:
```bash
mvn jetty:run
```

### 📁 Project Structure
```
kharchabook/
├── src/main/java/com/kharchabook/
│   ├── dao/           # Data Access Objects
│   ├── filter/        # Servlet Filters
│   ├── model/         # Data Models
│   ├── servlet/       # Controllers
│   └── util/          # Utilities
├── src/main/resources/
│   └── db.properties  # Database Configuration
├── src/main/webapp/
│   ├── admin/         # Admin JSP pages
│   ├── user/          # User JSP pages
│   ├── includes/      # Common JSP includes
│   ├── css/           # Stylesheets
│   └── WEB-INF/       # Configuration
├── sql/
│   └── schema.sql     # Database Schema
├── .vscode/           # VS Code Configuration
├── pom.xml           # Maven Configuration
└── README.md         # This file
```

### 🔧 Configuration Files

#### VS Code Settings (`.vscode/settings.json`)
- Java 17 configuration
- Maven integration
- File exclusions
- Auto-format settings

#### Maven Tasks (`.vscode/tasks.json`)
- Pre-configured Maven commands
- Debug configurations
- Build automation

#### Debug Configuration (`.vscode/launch.json`)
- Java debug settings
- Tomcat debugging
- Combined launch configurations

### 🌐 Running Different Servers

#### Using Tomcat (Default)
```bash
mvn tomcat7:run
# Access: http://localhost:8080/kharchabook
```

#### Using Jetty (Alternative)
```bash
mvn jetty:run
# Access: http://localhost:8080/kharchabook
```

#### Using External Tomcat
1. Build WAR: `mvn package`
2. Deploy `target/kharchabook.war` to your Tomcat
3. Start Tomcat server

### 📊 Database Setup

#### MySQL Setup
```sql
-- Create database
CREATE DATABASE kharchabook;

-- Import schema
mysql -u root -p kharchabook < sql/schema.sql
```

#### Alternative Databases
Update `db.properties` for your preferred database:
- PostgreSQL
- SQL Server
- Oracle
- H2 (for testing)

### 🚀 Deployment

#### Production Deployment
```bash
# Build for production
mvn clean package -Pproduction

# Deploy WAR file to application server
```

#### Docker Deployment (Future Enhancement)
```dockerfile
# Dockerfile can be added for containerization
```

### 🤝 Development Workflow

1. **Setup**: Install prerequisites and extensions
2. **Database**: Set up and configure database
3. **Code**: Implement features in skeleton files
4. **Test**: Run with `mvn test`
5. **Debug**: Use VS Code debugger
6. **Deploy**: Build and deploy WAR file

### 📝 Notes

- This is a **skeleton project** - implement actual functionality
- All Java classes are empty with package declarations only
- All JSP files have basic HTML structure only
- Add Maven dependencies as needed for features
- Configure servlet mappings in `web.xml` as needed

### 🔍 Troubleshooting

#### Common Issues
1. **Port 8080 in use**: Change port in pom.xml
2. **Database connection**: Check db.properties configuration
3. **Java version**: Ensure Java 11+ is installed
4. **Maven issues**: Verify Maven installation and PATH

#### VS Code Issues
1. **Java extension not working**: Reload VS Code
2. **Maven tasks not showing**: Check Maven installation
3. **Debug not working**: Install Java Extension Pack

### 📞 Support

For issues related to:
- **Project structure**: Check this README
- **VS Code setup**: Verify extensions installation
- **Maven issues**: Check Maven configuration
- **Database**: Verify database setup

---

**Happy Coding! 🎉**