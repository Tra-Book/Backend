const db = require('../utils/mysqlUtil');

class User {
    constructor({
        userId,
        username,
        email,
        password,
        statusMessage,
    }) {
        this.userId = userId
        this.username = username;
        this.email = email;
        this.password = password;
        this.statusMessage = statusMessage;
    }

    async save() {
        try {
            const query = `
                INSERT INTO User (username, email, password, statusMessage)
                VALUES (?, ?, ?, ?)
            `;
            const [result] = await db.query(query, [this.username, this.email, this.password, this.statusMessage]);
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
                UPDATE User 
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
                DELETE FROM User 
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
                SELECT userId, username, email, password, statusMessage
                FROM User 
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
                FROM User 
                WHERE userId = ?
            `;
            const [rows] = await db.query(query, [userId]);

            if (rows.length === 0) {
                return null;
            }
            const user = new User({
                userId: rows[0].userId,
                username: rows[0].username,
                email: rows[0].email,
                password: rows[0].password,
                statusMessage: rows[0].statusMessage,
            });
            return user;
        } catch (err) {
            console.error('Error retrieving user by userId:', err.message);
            throw new Error('Could not retrieve user by userId');
        }
    }
}

module.exports = User;