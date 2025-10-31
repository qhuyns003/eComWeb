// Initialize MongoDB Replica Set for Transactions Support
// This script sets up a 3-node replica set configuration

rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "mongo1:27017" },
    { _id: 1, host: "mongo2:27017" },
    { _id: 2, host: "mongo3:27017" }
  ]
});

print('✅ Replica set "rs0" has been initiated successfully!');
print('📋 Configuration:');
print('   - Primary: mongo1:27017');
print('   - Secondary: mongo2:27017');
print('   - Secondary: mongo3:27017');
print('🔗 Connection strings:');
print('   - Internal (Docker): mongodb://mongo1:27017,mongo2:27017,mongo3:27017/?replicaSet=rs0');
print('   - External (Host): mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0');
print('💾 Transactions are now supported!');