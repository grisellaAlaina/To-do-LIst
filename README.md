# TodoList-Api-With-Auth
A todolist api with user authentication and authorization using MongoDB database and Json Web Token(JWT) written with Play Framework


## About

Endpoints except, **/login**, **/verifyToken** can only be accessed with a JWT token(Sent via httpHeader) which is generated when the user **/login**.



| Endpoint                        | Method | Functionality                  |
|---------------------------------|--------|--------------------------------|
| /login                          | POST   | User Login/Get JWT Token       |
| /tasks/create                   | POST   | Create Task                    |
| /tasks/all                      | GET    | Get All Tasks                  |
| /tasks/{taskId}                 | GET    | Get Task By ID                 |
| /tasks/{taskId}                 | DELETE | Delete Task                    |
| /tasks/update                   | POST   | Update Task                    |
| /verifyToken                    | GET    | Verify JWT Token               |
| /tasks/{taskId}/export          | GET    | Export Task as Zip             |



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
 



 



