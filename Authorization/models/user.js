const db = require('../utils/mysqlUtil');

class User {
    constructor({ userId, username, email, password, statusMessage, profilePhoto }) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.statusMessage = statusMessage;
        this.profilePhoto = profilePhoto;
    }

    async save(connection) {
        try {
            const query = `
                INSERT INTO User (username, email, password, statusMessage, profilePhoto)
                VALUES (?, ?, ?, ?, ?)
            `;
            const [result] = await connection.query(query, [
                this.username,
                this.email,
                this.password,
                this.statusMessage,
                this.profilePhoto,
            ]);
            const userId = result.insertId;
            this.userId = userId;
            return userId;
        } catch (err) {
            console.error('Error saving user:', err.message);
            throw new Error('Could not save user');
        }
    }

    async updateProfile(username, statusMessage, profilePhoto, connection) {
        try {
            const query = `
                UPDATE User 
                SET username = ?, statusMessage = ?, profilePhoto = ?
                WHERE userId = ?
            `;
            await connection.query(query, [username, statusMessage, profilePhoto, this.userId]);
        } catch (err) {
            console.error('Error updating user profile:', err.message);
            throw new Error('Could not update user profile');
        }
    }

    async updatePassword(newPassword, connection) {
        try {
            const query = `
                UPDATE User 
                SET password = ?
                WHERE userId = ?
            `;
            await connection.query(query, [newPassword, this.userId]);
        } catch (err) {
            console.error('Error updating user password:', err.message);
            throw new Error('Could not update user password');
        }
    }

    async deleteUser(connection) {
        try {
            const query = `
                DELETE FROM User 
                WHERE userId = ?
            `;
            await connection.query(query, [this.userId]);
        } catch (err) {
            console.error('Error deleting user:', err.message);
            throw new Error('Could not delete user');
        }
    }

    static async getUserByEmail(userEmail) {
        try {
            const query = `
                SELECT userId, username, email, password, statusMessage, profilePhoto
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
                profilePhoto: rows[0].profilePhoto,
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
                SELECT userId, username, email, password, statusMessage, profilePhoto
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
                profilePhoto: rows[0].profilePhoto,
            });
            return user;
        } catch (err) {
            console.error('Error retrieving user by userId:', err.message);
            throw new Error('Could not retrieve user by userId');
        }
    }
}

module.exports = User;
