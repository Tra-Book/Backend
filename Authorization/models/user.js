const db = require('../utils/db');

class User {
    constructor({
        userId,
        username,
        email,
        password,
        statusMessage,
        gender,
    }) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.statusMessage = statusMessage;
        this.gender = gender;
    }

    // return 값으로 unique 한 userId 받아와야 함
    async save() {
        try {
            const query = `
                INSERT INTO user (username, email, password, statusMessage, gender)
                VALUES (?, ?, ?, ?)
            `;
            const [result] = await db.query(query, [this.username, this.email, this.password, this.statusMessage, this.gender]);
            const userId = result.insertId;
            this.userId = userId;
            return userId;
        } catch (err) {
            console.error('Error saving user:', err.message);
            throw new Error('Could not save user');
        }
    }

    async updateProfile(username, statusMessage, hashedPassword) {
        try {
            const query = `
                UPDATE user 
                SET username = ?, statusMessage = ?, password = ?
                WHERE email = ?
            `;
            await db.query(query, [username, statusMessage, hashedPassword, this.email]);
        } catch (err) {
            console.error('Error updating user profile:', err.message);
            throw new Error('Could not update user profile');
        }
    }

    async deleteUser() {
        try {
            const query = `
                DELETE FROM user 
                WHERE email = ?
            `;
            await db.query(query, [this.email]);
        } catch (err) {
            console.error('Error deleting user:', err.message);
            throw new Error('Could not delete user');
        }
    }

    static async getUserByEmail(userEmail) {
        try {
            const query = `
                SELECT userId, username, email, password, statusMessage, gender
                FROM user 
                WHERE email = ?
            `;
            const [rows] = await db.query(query, [userEmail]);

            if (rows.length === 0) {
                return null;
            }
            const user = new User({
                userId: rows[0].userId,
                username: rows[0].username,
                email: rows[0].email,
                password: rows[0].password,
                statusMessage: rows[0].statusMessage,
                gender: rows[0].gender,
            });
            return user;
        } catch (err) {
            console.error('Error retrieving user by email:', err.message);
            throw new Error('Could not retrieve user by email');
        }
    }

    static async getUserByUserId(userId) {
        try {
            const query = `
                SELECT userId, username, email, password, statusMessage 
                FROM user 
                WHERE userId = ?
            `;
            if (rows.length === 0) {
                return null;
            }

            const user = new User({
                userId: rows[0].userId,
                username: rows[0].username,
                email: rows[0].email,
                password: rows[0].password,
                statusMessage: rows[0].statusMessage,
                gender: rows[0].gender,
            });
            return user;
        } catch (err) {
            console.error('Error retrieving user by userId:', err.message);
            throw new Error('Could not retrieve user by userId');
        }
    }
}

module.exports = User;