const db = require('../utils/db');

class AccompanyPost {
    constructor({
        accompanyId,
        userId,
        cityId,
        title,
        content,
        createdAt,
        tag,
        likes,
        chatroom,
        maxParticipants,
        minParticipants,
        currentParticipants,
        genderPreference,
        maxAge,
        minAge,
        purpose,
        planId,
        itinerary,
        placeList,
    }) {
        this.accompanyId = accompanyId;
        this.userId = userId;
        this.cityId = cityId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.tag = tag;
        this.likes = likes;
        this.chatroom = chatroom;
        this.maxParticipants = maxParticipants;
        this.minParticipants = minParticipants;
        this.currentParticipants = currentParticipants;
        this.genderPreference = genderPreference;
        this.maxAge = maxAge;
        this.minAge = minAge;
        this.purpose = purpose;
        this.planId = planId;
        this.itinerary = itinerary;
        this.placeList = placeList;
    }

    async save() {
        try {
            const query = `
                INSERT INTO Accompany (userId, cityId, title, content, createdAt, tag, likes, chatroom, maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            `;
            const [result] = await db.query(query, [
                this.userId, this.cityId, this.title, this.content,
                this.createdAt, this.tag, this.likes, this.chatroom, this.maxParticipants,
                this.minParticipants, this.currentParticipants, this.genderPreference, this.maxAge, this.minAge, this.purpose,
                this.planId, JSON.stringify(this.itinerary), JSON.stringify(this.placeList)
            ]);
            this.accompanyId = result.insertId;
        } catch (err) {
            console.error('Error saving accompany post:', err.message);
            throw new Error('Could not save accompany post');
        }
    }

    async updateProfile({
        cityId, title, content, createdAt, tag, likes, chatroom,
        maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList
    }) {
        try {
            const query = `
                UPDATE Accompany 
                SET cityId = ?, title = ?, content = ?, createdAt = ?, tag = ?, likes = ?, chatroom = ?, 
                    maxParticipants = ?, minParticipants = ?, currentParticipants = ?, genderPreference = ?, maxAge = ?, minAge = ?, purpose = ?, planId = ?, 
                    itinerary = ?, placeList = ? 
                WHERE accompanyId = ?
            `;
            await db.query(query, [
                cityId, title, content, createdAt, tag, likes, chatroom,
                maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId,
                JSON.stringify(itinerary), JSON.stringify(placeList), this.accompanyId
            ]);
        } catch (err) {
            console.error('Error updating accompany post:', err.message);
            throw new Error('Could not update accompany post');
        }
    }

    async deletePost() {
        try {
            const query = `DELETE FROM Accompany WHERE accompanyId = ?`;
            await db.query(query, [this.accompanyId]);
        } catch (err) {
            console.error('Error deleting accompany post:', err.message);
            throw new Error('Could not delete accompany post');
        }
    }

    static async getPostsByUserId(userId) {
        try {
            const query = `
                SELECT accompanyId, userId, cityId, title, content, createdAt, tag, likes, chatroom, 
                       maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList 
                FROM Accompany WHERE userId = ?
            `;
            const [rows] = await db.query(query, [userId]);

            if (rows.length === 0) {
                return [];
            }

            const accompanyPosts = rows.map(row => new AccompanyPost({
                accompanyId: row.accompanyId,
                userId: row.userId,
                cityId: row.cityId,
                title: row.title,
                content: row.content,
                createdAt: row.createdAt,
                tag: row.tag,
                likes: row.likes,
                chatroom: row.chatroom,
                maxParticipants: row.maxParticipants,
                minParticipants: row.minParticipants,
                currentParticipants: row.currentParticipants,
                genderPreference: row.genderPreference,
                maxAge: row.maxAge,
                minAge: row.minAge,
                purpose: row.purpose,
                planId: row.planId,
                itinerary: JSON.parse(row.itinerary),
                placeList: JSON.parse(row.placeList),
            }));

            return accompanyPosts;

        } catch (err) {
            console.error('Error retrieving accompany posts by user id:', err.message);
            throw new Error('Could not retrieve accompany posts by user id');
        }
    }

    static async getPostByAccompanyId(accompanyId) {
        try {
            const query = `
                SELECT accompanyId, userId, cityId, title, content, createdAt, tag, likes, chatroom, 
                       maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList 
                FROM Accompany WHERE accompanyId = ?
            `;
            const [rows] = await db.query(query, [accompanyId]);

            if (rows.length === 0) {
                return null;
            }

            const accompanyPost = new AccompanyPost({
                accompanyId: rows[0].accompanyId,
                userId: rows[0].userId,
                title: rows[0].title,
                content: rows[0].content,
                createdAt: rows[0].createdAt,
                tag: rows[0].tag,
                likes: rows[0].likes,
                chatroom: rows[0].chatroom,
                maxParticipants: rows[0].maxParticipants,
                minParticipants: rows[0].minParticipants,
                currentParticipants: rows[0].currentParticipants,
                genderPreference: rows[0].genderPreference,
                maxAge: rows[0].maxAge,
                minAge: rows[0].minAge,
                purpose: rows[0].purpose,
                planId: rows[0].planId,
                itinerary: JSON.parse(rows[0].itinerary),
                placeList: JSON.parse(rows[0].placeList),
            });

            return accompanyPost;

        } catch (err) {
            console.error('Error retrieving accompany post by accompany id:', err.message);
            throw new Error('Could not retrieve accompany post by accompany id');
        }
    }
}

module.exports = AccompanyPost;