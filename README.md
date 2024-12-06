
# Tukano Application Deployment Instructions

This guide provides instructions to deploy the Tukano application either **locally** using Docker Compose or on **Kubernetes**. 

### **Disclaimer**
The application is pre-configured with default values that work out of the box for deployment. You can skip the environment variables section if you don’t need to customize these configurations.

**Note:** The authentication feature (`USE_AUTH`) is turned off by default for testing purposes. If you want to enable authentication for blob uploads, downloads, and deletes, make sure to set `USE_AUTH` to `true` in the configuration.

---

## Environment Variables

Below are the environment variables grouped by their respective services. 

### **1. Tukano Microservice**
#### **Server Configuration**
| **Environment Variable** | **Description**                                   | **Default**   | **Where to Configure**       |
|--------------------------|---------------------------------------------------|---------------|-------------------------------|
| `HOST`                   | Host address the microservice will bind to.       | `0.0.0.0`     | Docker Compose/Kubernetes    |
| `PORT`                   | Port the microservice will bind to.               | `8080`        | Docker Compose/Kubernetes    |
| `BLOBS_TOKEN`            | Token to verify blob uploads and downloads.       | `secret`      | `.env`                       |

#### **Cache Configuration**
| **Environment Variable** | **Description**                                   | **Default**   | **Where to Configure**       |
|--------------------------|---------------------------------------------------|---------------|-------------------------------|
| `USE_CACHE`              | Enable or disable caching.                        | `TRUE`        | Docker Compose/Kubernetes    |
| `CACHE_TYPE`             | Cache type: `IN_MEMORY` or `REDIS`.               | `IN_MEMORY`   | Docker Compose/Kubernetes    |
| `REDIS_HOST`             | Hostname of the Redis cache.                      | `redis`       | Docker Compose/Kubernetes    |
| `REDIS_KEY`              | Password to access the Redis service (mandatory when using Redis). |               | `.env`                       |

#### **Database Configuration**
| **Environment Variable** | **Description**                                   | **Default**          | **Where to Configure**       |
|--------------------------|---------------------------------------------------|----------------------|-------------------------------|
| `HIBERNATE_CONFIG`       | Hibernate XML config file to use.                 | `in-memory.cfg.xml`          | Docker Compose/Kubernetes    |
| `DB_NAME`                | Postgres database name.                           | `postgres`           | Docker Compose/Kubernetes    |
| `DB_USER`                | Postgres database username.                       | `70624-71863`        | Docker Compose/Kubernetes    |
| `DB_PASSWORD`            | Password to access the Postgres service (mandatory when using Postgres).          |                      | `.env`                       |
| `DB_HOST`                | Hostname of the Postgres database.                | `postgres`           | Docker Compose/Kubernetes    |

#### **Blob Storage Configuration**
| **Environment Variable** | **Description**                                   | **Default**       | **Where to Configure**       |
|--------------------------|---------------------------------------------------|-------------------|-------------------------------|
| `BLOB_STORAGE_TYPE`      | Blob storage type: `LOCAL` or `REMOTE`.           | `LOCAL`           | Docker Compose/Kubernetes    |
| `BLOB_STORAGE_HOST`      | Hostname of the remote blob storage.              | `storage`         | Docker Compose/Kubernetes    |
| `BLOB_STORAGE_PORT`      | Port of the remote blob storage service.          | `8081`            | Docker Compose/Kubernetes    |
| `BLOB_STORAGE_TOKEN`     | Shared secret for Tukano and blob storage (mandatory when using remote blob storage).        |                   | `.env`                       |
| `USE_AUTH`               | Enable session authentication for blob storage.   | `false`           | Docker Compose/Kubernetes    |

---

### **2. Blob Storage Microservice**
| **Environment Variable** | **Description**                                   | **Default**   | **Where to Configure**       |
|--------------------------|---------------------------------------------------|---------------|-------------------------------|
| `PORT`                   | Port the microservice will bind to.               | `8081`        | Docker Compose/Kubernetes    |
| `HOST`                   | Host address the microservice will bind to.       | `0.0.0.0`     | Docker Compose/Kubernetes    |
| `STORAGE_TOKEN`          | Shared secret for Tukano and blob storage (mandatory).        |               | `.env`                       |

---

### **3. Redis Cache Microservice**
| **Environment Variable** | **Description**                                   | **Default**   | **Where to Configure**       |
|--------------------------|---------------------------------------------------|---------------|-------------------------------|
| `REDIS_KEY`              | Password to access the Redis service (mandatory).             |               | `.env`                       |

---

### **4. Postgres Microservice**
| **Environment Variable** | **Description**                                   | **Default**   | **Where to Configure**       |
|--------------------------|---------------------------------------------------|---------------|-------------------------------|
| `POSTGRES_USER`          | Postgres database username (mandatory).                       |               | `.env`                       |
| `POSTGRES_PASSWORD`      | Password to access the Postgres service (mandatory).          |               | `.env`                       |
| `POSTGRES_DB`            | Postgres database name (mandatory).                           |               | `.env`                       |

---

## Deployment Instructions

### **1. Deploying Locally with Docker Compose**
1. Set all required secrets in the `.env` file located in the root of the repository.
2. Configure any desired environment variables in the `docker-compose.yml` file.
3. Run the following command in the root directory of the repository:
   ```bash
   docker compose up -d
   ```
   This will start the application in detached mode.

---

### **2. Deploying with Kubernetes**
1. Ensure your Kubernetes cluster is set up.
2. Set all required secrets in the `.env` file located in the root of the repository.
3. Create a Kubernetes secret configuration in the cluster using the following command:
   ```bash
   kubectl create secret generic tukano-secrets --from-env-file={.env file path}
   ```
4. Configure any desired environment variables in the Kubernetes `.yaml` files located in the deployment folder.
5. Apply the configuration to the cluster using:
   ```bash
   kubectl apply -f {kubernetes folder path}
   ```
   This will deploy the application to the cluster.
