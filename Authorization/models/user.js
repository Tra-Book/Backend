
class User {
    constructor({
        userId,
        username,
        email,
        password,
        statusMessage,
    }) {
        this.userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.statusMessage = statusMessage;
    }

    // return 값으로 unique 한 userId 받아와야 함
    save() {
        //
    }

    updateProfile(username, statusMessage, hashedPassword) {
        //
    }

    deleteUser() {
        //
    }

    static getUserByEmail(userEmail) {
        //
    }

    static getUserByUserId(userId) {
        //
    }
}

module.exports = User;