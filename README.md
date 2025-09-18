# Spring and Keycloak

**Outline**

Exercise 1: Setup Keycloak  
Exercise 2: Create Realm  
Exercise 3: Create User and assign role  
Exercise 4: Allow Users to login into my app  
Exercise 5: Resources and authorization  
Exercise 6: User, Group and Inherited Role  
Exercise 7: Assign Client Role to User  
Exercise 8: Allow Client to call API (no need User)  
Exercise 9: Add custom attribute to JWT  
Exercise 10: Call API from Browser  
Exercise 11: Make Google as Identity provider

---

### Exercise 1: Setup Keycloak

Create [docker-compose.yml](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/master/docker-compose.yml) file

Run command: `docker-compose up -d`

---

### Exercise 2: Create Realm

Go to [http://localhost:8080/](http://localhost:8080/)

- username: admin
- password: admin

Manage realms → Create realm

- Name: **MyRealm**
- Enabled: on

Switch to "MyRealm"

Realm settings → General → Require SSL: None

Authentication → Required actions → Disable: Update profile, Verify email, Verify profile (To avoid errors when user logs in later `{"error":"invalid_grant","error_description":"Account is not fully set up"}`)

---

### Exercise 3: Create Client, Create User and assign role

Clients → Create client

- Client ID: **task-app**
- Client type: OpenID Connect
- Client authentication: On
- Standard flow: On
- Direct access grants: On

Realm roles → Create 2 roles: USER, ADMIN 

Users → Add user

- Username: **alice**
- Email verified: On
- Create
- Credentials → Set password → Temporary: Off
- Role mapping → Assign role → Realm roles → select **USER**

Users → Add user (Do the same thing to create **bob** and assign Realm role: **ADMIN**)

Execute command:

```bash
curl -X POST \
  http://localhost:8080/realms/MyRealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=task-app" \
  -d "client_secret=zNjQD1sdE9QHbjxUoXpa2ALjcTV5B4ox" \
  -d "grant_type=password" \
  -d "username=alice" \
  -d "password=alice"
```

It should return an access token  
(Go to Clients → task-app → Credentials to get `client_secret`)

---

### Exercise 4: Allow User to login into my app

Create [task-management](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/master/task-management/pom.xml) Spring Boot project

Create [AuthController](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/master/task-management/src/main/java/tuan/keycloakexample/task_management/controller/AuthController.java), [KeycloakService](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/master/task-management/src/main/java/tuan/keycloakexample/task_management/service/KeycloakService.java) to handle user login

Add Keycloak config to [application.properties](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/resources/application.properties#L7-L14)

Config a [FilterChain](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/config/SecurityConfig.java#L29-L37) to permit (no need to provide credential) call to `/auth/**` API 

Call to `/auth/login` with username and password. It should return an access token:

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758187154679%2F43.jpg?alt=media&token=a9f514a2-16c3-4f46-b3bc-d1a20eb59060)

**Sample JWT Token:**
```
eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoSnQ2WTRzanlNaU0xOTZFSEx2MXdmdHRLTW5lRmpNV2RTRldnYVh6QmZNIn0.eyJleHAiOjE3NTgxOTAxMzQsImlhdCI6MTc1ODE4OTgzNCwianRpIjoib25ydHJvOjI1NjBjMjdmLWJkY2MtMTJkZS01ZDFkLTA3YjEyODQ5NTg0YyIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvTXlSZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI3NGFhODg0Yi0yMjIxLTQ4MGQtYmU4ZS05OWRjYjUzNzIxZGMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0YXNrLWFwcCIsInNpZCI6ImRhZmYzM2I2LTI2ZTYtNGExOS1hMTU2LTQ5NzU4Y2MzZjM5YSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbXlyZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJVU0VSIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsidGFzay1hcHAiOnsicm9sZXMiOlsiVEFTS19DUkVBVE9SIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IkFsaWNlIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWxpY2UiLCJnaXZlbl9uYW1lIjoiQWxpY2UifQ.PWsn_HCRxRD24v1I2e5oKsBZNqvQNofQQhZSjS4zlHNvyfihMLo1_9f5WtwpqhVZQwOsi7UXcn5RZ0v7enn5lSB2tw-uh2UL5b0S8ZDK_fdaK-C9PZGY1cxC-SjlSC_5isNmaW_KeybR5fC5JYR3-YwfcnH8Qv1OUF-nqDcCUGnSW8eQbi9MhZj7Jnd3gMSuk6F7Y0VOQiEI_ulZMq8P-hVQ1wTlsX_yBcd6vpynUrGGwKgbREU3uuOXkGxg3NvKjJeHMSk56cWI91iSU_TrxhSgJeepwFlPyov2MlpmtTYpTmh1_FJ-M7rq9-owia11bI3Z4bDCLT-DotRmybW7-g
```


---

### Exercise 5: Resources and authorization

Create [TaskController](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/controller/TaskController.java#L27-L59), [TaskService](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/service/TaskService.java) and implement these APIs:

- `GET /api/tasks` only allow users with role USER or ADMIN
- `POST /api/tasks` only allow users with role USER or ADMIN
- `DELETE /api/tasks` only allow users with role ADMIN

Config [FilterChain](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/config/SecurityConfig.java#L39-L58) to required credential when calling to `/api/tasks/**`

**Note:**
```java
.exceptionHandling(ex -> ex
  .authenticationEntryPoint(customHandler) // ← handle when user does not provide access token
  .accessDeniedHandler(customHandler))     // ← handle when user does not have appropriate role
.oauth2ResourceServer(oauth2 -> oauth2
  .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
  .authenticationEntryPoint(customHandler)); // ← handle when token is invalid or expired
```

Create [JwtAuthenticationConverter](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/config/SecurityConfig.java#L75-L82) to extract Realm role from JWT

Config to connect to PostgreSQL in [application.properties](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/resources/application.properties#L17-L24)

**Test:**

Alice Create Task: ✅ (Success - has USER role)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758189223651%2F26.jpg?alt=media&token=afd2dfcf-04a8-40fc-ab13-85a1b8048cd6)

Alice delete Task: ❌ (Access Denied - needs ADMIN role)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758189395334%2F11.jpg?alt=media&token=82de2d8d-6b6b-49ff-8ecf-feb6abee231c)

Bob delete Task: ✅ (Success - has ADMIN role)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758189440015%2F45.jpg?alt=media&token=2a5987f2-e343-41f3-9896-76bf7ae03471)

---

### Exercise 6: User, Group and Inherited Role

Go to Keycloak and create user **john** without any Realm role

John login and get task: ❌ (Access Denied - no USER role)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758191133357%2F46.jpg?alt=media&token=804f3747-7186-497b-8a82-93799f4d2297)

Go to Keycloak and create **user-group** Group

- Assign Realm role **USER**
- Add **john** as member

John re-login to get new access token and get task again: ✅ (Success - inherited USER role from group)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758191115351%2F43.jpg?alt=media&token=8ba66e75-1b67-41cd-ba47-8fef9089041f)

---

### Exercise 7: Assign Client Role to User

Go to Keycloak

- Clients → **task-app** → Roles → Create role **TASK_CREATOR**
- Users → **alice** → Role mapping → Assign role → Client roles → Choose **TASK_CREATOR**

Create a new [API](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/controller/TaskController.java#L61-L64) 

Config [FilterChain](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/config/SecurityConfig.java#L46) to only allow users with role **TASK_CREATOR** to call the new API

Edit [JwtAuthenticationConverter](https://github.com/tuannguyen511/Spring-and-Keycloak/blob/db04085520e68c414565847ba44705fb6b27748d/task-management/src/main/java/tuan/keycloakexample/task_management/config/SecurityConfig.java#L84-L98) to extract Client role from JWT

**Test:**

Alice calls the new API: ✅ (Success - has TASK_CREATOR client role)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758192869526%2F23.jpg?alt=media&token=7c99449a-d975-4392-adfd-11002917d58f)

Bob calls the new API: ❌ (Access Denied - doesn't have TASK_CREATOR client role)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758192899644%2F56.jpg?alt=media&token=50bd616f-f6a0-42b2-b023-dbdc26ace475)

---

### Exercise 8: Allow Client to call API (no need User)

Go to Keycloak → Clients → **task-app** → 

- Settings → Service accounts roles: **On**
- Service accounts roles → Assign role → Realm roles → choose **USER**

Run command to get access token:

```bash
curl -X POST \
  http://localhost:8080/realms/MyRealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=task-app" \
  -d "client_secret=zNjQD1sdE9QHbjxUoXpa2ALjcTV5B4ox" \
  -d "grant_type=client_credentials"
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoSnQ2WTRzanlNaU0xOTZFSEx2MXdmdHRLTW5lRmpNV2RTRldnYVh6QmZNIn0.eyJleHAiOjE3NTgxOTM1MTgsImlhdCI6MTc1ODE5MzIxOCwianRpIjoidHJydGNjOmE0NTg2MWIxLWFlYzMtYmYxNC1hYjE4LWI4NmMzMTY5ZmM2MyIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvTXlSZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIwZWNjNjk5Mi1iYjQzLTRjY2EtYmJmMS00OGI2YThiY2M2MTAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0YXNrLWFwcCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbXlyZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJVU0VSIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRIb3N0IjoiMTkyLjE2OC42NS4xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LXRhc2stYXBwIiwiY2xpZW50QWRkcmVzcyI6IjE5Mi4xNjguNjUuMSIsImNsaWVudF9pZCI6InRhc2stYXBwIn0.idmtyip4oZCmyVxF8VYfohA4hsC9NTvyWgVjq7McQJ7Iaz5m2UE8TogrZwNs-5HVpLEheAV1vyw3RzmYRaEGRcCl-XeW_t4JVMzR0UR0biuvhcCGDFAnk_Owy6uZ9OB-1Om1fEPUelADFEYU7Fa2tXi2gZ0WWEYjXwAm5PM6qvasICsrzM6El1sn81-BmA8wwism_0i_LOOzBdgkAbjwllD9AnaLIfS6gQ3mmEAPA3dhEFgPcHdSA3fooAey8PK-P4h_n3NkrgvHqAYDLOYnMPLJ3IGwF4KzTqKxDiDX8iD4N3ITQ5Jd0MQ-TVfCAxSyYbTTXKk15kNjrOkes1y5WA",
  "expires_in": 300,
  "refresh_expires_in": 0,
  "token_type": "Bearer",
  "not-before-policy": 0,
  "scope": "email profile"
}
```

Call create Task API: ✅ (Success - service account has USER role)

![image](https://firebasestorage.googleapis.com/v0/b/diary-b58b9.appspot.com/o/stnP0ISWyvSDZAahXoB9D883fNo1%2Ffiles%2F1758193490165%2F42.jpg?alt=media&token=4ce711f2-f87a-4678-b2ba-197949c7611c)