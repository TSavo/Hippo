db = db.getSiblingDB('admin');
db.auth( { user: 'crypto-admin', pwd: 'crypto-admin' });

db = db.getSiblingDB('cryptomarket');
db.createUser({ user: 'crypto-rw', pwd: 'crypto-rw', roles: [ { role: "dbOwner", db: "cryptomarket" } ] });
