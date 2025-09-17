rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "192.168.2.6:27017" },
    { _id: 1, host: "192.168.2.6:27018" },
    { _id: 2, host: "192.168.2.6:27019" }
  ]
});

print('Replica set initiated with host IP');