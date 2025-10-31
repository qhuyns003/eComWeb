# MongoDB Replica Set với Transactions Support

Dự án MongoDB Replica Set 3-node hỗ trợ transactions cho ứng dụng Spring Boot.

## 📁 Cấu trúc Files

```
mongodb/
├── docker-compose-final.yml    # Docker Compose configuration
├── init-replica-final.js       # Replica set initialization script
└── README-SETUP.md            # Hướng dẫn này
```

## 🚀 Cách Sử Dụng

### 1. Khởi chạy MongoDB Cluster

```bash
# Di chuyển đến thư mục chứa docker-compose
cd path/to/mongodb/folder

# Khởi chạy tất cả services
docker-compose -f docker-compose-final.yml up -d
```

### 2. Kiểm tra trạng thái Replica Set

```bash
# Đợi 15-30 giây để containers khởi động hoàn tất
# Sau đó kiểm tra trạng thái
docker exec -it mongo1 mongosh --eval "rs.status()"
```

### 3. Connection Strings

**Từ ứng dụng trên host machine (Spring Boot):**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017,localhost:27018,localhost:27019/your-database?replicaSet=rs0
```

**Từ container khác trong cùng Docker network:**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://mongo1:27017,mongo2:27017,mongo3:27017/your-database?replicaSet=rs0
```

**Cho single-node connection (recommended cho development):**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/your-database?replicaSet=rs0
```

## ✅ Kiểm tra Transactions

Sau khi khởi chạy, test transactions:

```javascript
// Trong mongosh
session = db.getMongo().startSession();
session.startTransaction();
try {
  session.getDatabase('testdb').testCollection.insertOne({test: 'transaction'});
  session.commitTransaction();
  print('✅ Transactions hoạt động!');
} catch (e) {
  session.abortTransaction();
  print('❌ Lỗi:', e);
} finally {
  session.endSession();
}
```

## 🔧 Troubleshooting

### Container không khởi động được:
```bash
# Kiểm tra logs
docker logs mongo1
docker logs mongo2
docker logs mongo3
docker logs mongo-setup
```

### Replica Set không khởi tạo:
```bash
# Khởi tạo thủ công
docker exec -it mongo1 mongosh /init-replica-final.js
```

### Application không kết nối được:
1. **Kiểm tra ports**: 27017, 27018, 27019 có bị chiếm không
2. **Kiểm tra replica set**: `rs.status()` phải cho thấy 1 PRIMARY + 2 SECONDARY
3. **Connection string**: Phải có `?replicaSet=rs0`

### Reset toàn bộ (nếu cần):
```bash
# Dừng tất cả
docker-compose -f docker-compose-final.yml down

# Xóa data (MẤT TOÀN BỘ DỮ LIỆU!)
rm -rf ./data

# Khởi chạy lại
docker-compose -f docker-compose-final.yml up -d
```

## 📊 Monitoring

### Kiểm tra trạng thái các nodes:
```bash
docker exec -it mongo1 mongosh --eval "
rs.status().members.forEach(m => 
  print(m.name + ': ' + m.stateStr + ' (health: ' + m.health + ')')
)"
```

### Kiểm tra databases:
```bash
docker exec -it mongo1 mongosh --eval "show dbs"
```

## 🛡️ Backup & Restore

### Backup:
```bash
# Backup toàn bộ
docker exec mongo1 mongodump --host localhost:27017 --out /tmp/backup
docker cp mongo1:/tmp/backup ./backup-$(date +%Y%m%d)

# Hoặc backup specific database
docker exec mongo1 mongodump --host localhost:27017 --db your-database --out /tmp/backup
```

### Restore:
```bash
# Copy backup vào container
docker cp ./backup mongo1:/tmp/backup

# Restore
docker exec mongo1 mongorestore --host localhost:27017 /tmp/backup
```

## 🔐 Security Notes

- **Development only**: Cấu hình này không có authentication
- **Production**: Cần thêm username/password và SSL
- **Firewall**: Chỉ expose ports cần thiết

## 📈 Performance Tuning

Để production, cần điều chỉnh:
- Memory allocation cho WiredTiger
- Journal settings
- Read/Write concerns
- Index optimization

---

**Tác giả**: Your Team  
**Cập nhật**: October 2025  
**Version**: 1.0