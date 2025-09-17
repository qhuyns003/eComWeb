Local MongoDB replica set for transactions

Files:
- docker-compose.yml: 3-node MongoDB replica set + setup container
- init-replica.js: JS script to call rs.initiate()

How to run:
1. Open terminal at this folder

```powershell
cd c:\Users\Admin\Documents\WorkSpace\eComWeb\dev-mongo
docker-compose up -d
```

2. Wait ~10s and check replica set status:

```powershell
docker exec -it mongo1 mongo --eval "rs.status()"
```

3. Connect with DBeaver or your app using the connection string (no auth):

```
mongodb://mongo1:27017,mongo2:27017,mongo3:27017/?replicaSet=rs0
```

If you run the app outside Docker (on your host), replace hostnames with `localhost` and ports mapped in `docker-compose.yml`:

```
mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0
```

Spring Boot (application.yml) sample:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0
```

Notes & troubleshooting:
- Make sure ports 27017/27018/27019 are free
- If rs.status shows members as UNKNOWN, give containers some time to elect primary
- To enable authentication, create admin user in init-replica.js and add auth parameters to connection string
