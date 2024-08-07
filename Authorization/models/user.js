const { stat } = require("fs");

class User {
    constructor({
        user_id,
        username,
        email,
        password,
        status_message,
    }) {
        this.user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status_message = status_message;
    }

    // return 값으로 unique 한 user_id 받아와야 함
    save() {
        const db = getDb();
        if (this._id) {
            // User exists -> update user
            return db.collection('users').updateOne({ _id: this._id }, { $set: this });
        }
        return db.collection('users').insertOne(this);
    }

    updateProfile(username, status_message, hashedPassword) {
        this.username = username;
        this.status_message = status_message;
        this.password = hashedPassword
        return this.save();
    }

    deleteUser() {
        const db = getDb();
        return db.collection('users').deleteOne({ _id: new mongodb.ObjectId(this._id) });
    }

    static getUserByEmail(userEmail) {
        const db = getDb();
        return db.collection('users').findOne({ email: userEmail });
    }
}

module.exports = User;