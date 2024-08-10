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
        //
    }

    updateProfile(username, status_message, hashedPassword) {
        //
    }

    deleteUser() {
        //
    }

    static getUserByEmail(userEmail) {
        //
    }
}

module.exports = User;