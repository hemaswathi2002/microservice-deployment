# Demo Microservice App
### Spring Boot + PostgreSQL + Nginx Frontend + Docker + Kubernetes + Jenkins

---

## Project Structure

```
microservice-app/
├── backend/                        ← Spring Boot 3.2 (Java 17)
│   ├── src/main/java/com/demo/
│   │   ├── Application.java
│   │   ├── DataLoader.java         ← Seeds sample data on startup
│   │   ├── controller/
│   │   │   ├── UserController.java
│   │   │   ├── ProductController.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   └── Product.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── ProductRepository.java
│   │   └── service/
│   │       ├── UserService.java
│   │       └── ProductService.java
│   ├── src/main/resources/
│   │   └── application.yml         ← H2 (local) + PostgreSQL (docker profile)
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/                       ← Static HTML + Nginx
│   ├── index.html
│   ├── nginx.conf                  ← Proxies /api → backend:8080
│   └── Dockerfile
│
├── k8s/                            ← Kubernetes manifests
│   ├── 00-namespace.yaml
│   ├── 01-config.yaml              ← ConfigMap + Secret
│   ├── 02-postgres.yaml            ← StatefulSet + PVC
│   ├── 03-backend.yaml             ← Deployment + Service + HPA
│   ├── 04-frontend.yaml            ← Deployment + Service
│   └── 05-ingress.yaml
│
├── jenkins/
│   └── Jenkinsfile                 ← Full CI/CD pipeline
│
└── docker-compose.yml              ← One-command local stack
```

---

## REST API Endpoints

### Products  `http://localhost:8080/api/products`
| Method | Path               | Description         |
|--------|--------------------|---------------------|
| GET    | /api/products      | List all (filter by ?category= or ?search=) |
| GET    | /api/products/{id} | Get by ID           |
| POST   | /api/products      | Create product      |
| PUT    | /api/products/{id} | Update product      |
| DELETE | /api/products/{id} | Delete product      |
| GET    | /api/products/health | Health check      |

### Users  `http://localhost:8080/api/users`
| Method | Path            | Description   |
|--------|-----------------|---------------|
| GET    | /api/users      | List all      |
| GET    | /api/users/{id} | Get by ID     |
| POST   | /api/users      | Create user   |
| PUT    | /api/users/{id} | Update user   |
| DELETE | /api/users/{id} | Delete user   |

---

## Option 1 — Local Dev (No Docker, H2 in-memory DB)

```bash
cd backend
mvn spring-boot:run
# API: http://localhost:8080
# H2 console: http://localhost:8080/h2-console
```

Open `frontend/index.html` in your browser — change API base to `http://localhost:8080`.

---

## Option 2 — Docker Compose (Recommended for local full-stack)

```bash
docker-compose up --build
```

| Service  | URL                        |
|----------|----------------------------|
| Frontend | http://localhost           |
| Backend  | http://localhost:8080      |
| Postgres | localhost:5432             |

```bash
# Stop
docker-compose down

# Stop and remove DB volume
docker-compose down -v
```

---

## Option 3 — Kubernetes

### Prerequisites
- kubectl pointing to your cluster
- Nginx Ingress Controller installed
- Metrics Server installed (for HPA)

### Steps

**1. Update image names** in `k8s/03-backend.yaml` and `k8s/04-frontend.yaml`:
```yaml
image: YOUR_DOCKERHUB/demo-backend:latest
image: YOUR_DOCKERHUB/demo-frontend:latest
```

**2. Build & push images**
```bash
docker build -t YOUR_DOCKERHUB/demo-backend:1.0  ./backend
docker build -t YOUR_DOCKERHUB/demo-frontend:1.0 ./frontend
docker push YOUR_DOCKERHUB/demo-backend:1.0
docker push YOUR_DOCKERHUB/demo-frontend:1.0
```

**3. Deploy**
```bash
kubectl apply -f k8s/
```

**4. Verify**
```bash
kubectl get all -n demo-app
kubectl get pods -n demo-app
kubectl logs -l app=backend -n demo-app
```

**5. Access**  
Add `127.0.0.1 demo.local` to `/etc/hosts`, then open http://demo.local

---

## Option 4 — Jenkins CI/CD Pipeline

### Setup

1. Install Jenkins plugins: **Pipeline, Git, Docker Pipeline**
2. Configure Tools in Jenkins:
   - JDK: name = `JDK-17`, install from adoptium
   - Maven: name = `Maven-3.9`
3. Add Credentials:
   - `docker-hub-credentials` → Username/Password (Docker Hub)
   - `kubeconfig` → Secret File (your `~/.kube/config`)
4. Update `jenkins/Jenkinsfile`:
   - Change `REGISTRY = 'YOUR_DOCKERHUB'` to your Docker Hub username
5. Create a Pipeline job → SCM → Git → Script path: `jenkins/Jenkinsfile`

### Pipeline Flow
```
Checkout → Test → Package → Docker Build → Docker Push → K8s Deploy → Verify
```
