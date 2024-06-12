# Backend Engineering Case Study

Java Version: 17.0.10

Important Note: Project works in local. Maven clear, install, test give no warning or error. Builds the project in docker but can not run tests. I am assuming the problem is about connecting to database because mysql-db-dump.sql file gives silly errors I could not resolve.

Implementation Choices

-Entity-Relationship Design
	--User: Represents a user in the system.
	--Tournament: Represents a tournament.
	--TournamentGroup: Represents a group within a tournament, containing users from different countries.
	--UserGroup: Represents the relationship between users and groups, including their scores and active status.
-Data Persistence
	--JPA/Hibernate: For object-relational mapping (ORM) to manage the database interactions.
	--Repositories: Defined for each entity to perform CRUD operations and custom queries
-RESTful API Design
	--Controllers: Handle HTTP requests and responses.
	--Service Layer: Contains business logic and interacts with repositories.
	--DTOs: Used to transfer data between the server and client, ensuring separation between internal data structures and API responses.

Design Choices

-Modular Architecture
	--Separation of Concerns: Divided the application into layers (Controller, Service, Repository) to promote modularity, readability, and maintainability.
	--DTOs (Data Transfer Objects): Used to ensure that the data exposed by the API is decoupled from the internal data models.
-Concurrency Handling
	--Optimistic Locking: Ensures data consistency when multiple users interact with the system concurrently.
	--Synchronized Methods: Where necessary to prevent race conditions.

The design and implementation choices for the Tournament Management System are focused on creating a scalable, maintainable, and user-friendly application. By using a modular architecture, consistent time handling, robust error handling, and clear separation of concerns, the system is designed to be resilient, easy to understand, and capable of handling concurrent interactions efficiently. The use of Docker ensures that the application can be easily deployed and scaled, making it suitable for production environments.

API Endpoints(for Postman use)

-Create User
Post Request
http://localhost:8080/api/user/createUser/{userName}
Example Usage: http://localhost:8080/api/user/createUser/user1

-Update Level
Post Request
http://localhost:8080/api/user/updateUser/{usedId}
Example Usage: http://localhost:8080/api/user/updateUser/USER1718018574855

-Get User
Get Request
http://localhost:8080/api/user/getUser/{usedId}
Example Usage: http://localhost:8080/api/user/getUser/USER1717890085282

-Get All Users
http://localhost:8080/api/user/getAllUsers
Example Usage: http://localhost:8080/api/user/getAllUsers

-Update All Users
Post Requests
http://localhost:8080/api/user/updateUser/{usedId}
Example Usage: http://localhost:8080/api/user/updateUser/USER1718018574855

-Create Tournament
Post Request
http://localhost:8080/api/tournament/createTournament/{tournamentName}
Example Usage: http://localhost:8080/api/tournament/createTournament/t1

-End Tournament
Post Request
http://localhost:8080/api/tournament/end
Example Usage: http://localhost:8080/api/tournament/end

-Enter Tournament
Post Request
http://localhost:8080/api/tournament/enterTournament/{userId}
Example Usage: http://localhost:8080/api/tournament/enterTournament/USER1718046358863

-Get Tournament
Get Request
http://localhost:8080/api/tournament/activeTournament
Example Usage: http://localhost:8080/api/tournament/activeTournament

-Get Tournament Groups
Get Request
http://localhost:8080/api/tournament/getGroups
Example Usage: http://localhost:8080/api/tournament/getGroups

-Complete Level
Post Request
http://localhost:8080/api/tournament/completeLevel/{userId}
Example Usage: http://localhost:8080/api/tournament/completeLevel/USER1718046352769

-Get Group Leaderboard
Get Request
http://localhost:8080/api/tournament/groupLeaderboard/{groupId}
Example Usage: http://localhost:8080/api/tournament/groupLeaderboard/2

-Get Country Leaderboard
Get Request
http://localhost:8080/api/tournament/countryLeaderboard
Example Usage: http://localhost:8080/api/tournament/countryLeaderboard

-Claim Tournament Reward
Post Request
http://localhost:8080/api/tournament/claimReward/{userId}
Example Usage: http://localhost:8080/api/tournament/claimReward/USER1718046358863

-Get User Rank
Get Request
http://localhost:8080/api/tournament/userRank/{userId}
Example Usage: http://localhost:8080/api/tournament/userRank/USER1718024617223