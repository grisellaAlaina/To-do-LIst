# TodoList-Api-With-Auth
A todolist api with user authentication and authorization using MongoDB database and Json Web Token(JWT) written with Play Framework


## About

Endpoints except, **/login**, **/verifyToken** can only be accessed with a JWT token(Sent via httpHeader) which is generated when the user **/login**.



| Endpoint                        |   | Functionality |   |   |
|---------------------------------|---|---------------|---|---|
| **POST** /login                 |   | Signup        |   |   |
| **POST** /tasks/create          |   | Create task   |   |   |
| **GET** /tasks/all              |   | Get all tasks |   |   |
| **GET**/tasks/{taskId}          |   | Get a task    |   |   |
| **DELETE**/tasks/{taskId}       |   | Delete task   |   |   |
| **POST**/tasks/{taskId} /update |   | Update task   |   |   |
| **POST**/login                  |   | Get JWT Token |   |   |
| **GET**/verifyToken             |   | Verify Token  |   |   |




# How To Run

**Step 1**
* Run MongoDB server on localhost:27017

**Step 2**
* To obtain an authorization code send POST request /login with json

````
{
  "username": "user",
  "password": "password"
}
````

Copy the "token"  code from response and paste as header in requests

            Key Authorization
            Value {JWT token}
 



 



