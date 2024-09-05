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
        scraps,
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
        status
    }) {
        this.accompanyId = accompanyId;
        this.userId = userId;
        this.cityId = cityId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.tag = tag;
        this.scraps = scraps;
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
        this.status = status;
    }

    async save() {
        try {
            const itinerary = Array.isArray(this.itinerary) ? this.itinerary : [];
            const placeList = Array.isArray(this.placeList) ? this.placeList : [];

            const query = `
                INSERT INTO Accompany (userId, cityId, title, content, createdAt, tag, scraps, chatroom, maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            `;
            const [result] = await db.query(query, [
                this.userId, this.cityId, this.title, this.content,
                this.createdAt, this.tag, this.scraps, this.chatroom, this.maxParticipants,
                this.minParticipants, this.currentParticipants, this.genderPreference, this.maxAge, this.minAge, this.purpose,
                this.planId, JSON.stringify(itinerary), JSON.stringify(placeList), this.status
            ]);
            this.accompanyId = result.insertId;
        } catch (err) {
            console.error('Error saving accompany post:', err);
            console.log('Database connection settings:', db.config);
            throw new Error('Could not save accompany post');
        }
    }

    async updatePost({
        cityId, title, content, createdAt, tag, scraps, chatroom,
        maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList, status
    }) {
        try {
            const query = `
                UPDATE Accompany 
                SET cityId = ?, title = ?, content = ?, createdAt = ?, tag = ?, scraps = ?, chatroom = ?, 
                    maxParticipants = ?, minParticipants = ?, currentParticipants = ?, genderPreference = ?, maxAge = ?, minAge = ?, purpose = ?, planId = ?, 
                    itinerary = ?, placeList = ?, status = ?
                WHERE accompanyId = ?
            `;
            await db.query(query, [
                cityId, title, content, createdAt, tag, scraps, chatroom,
                maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId,
                JSON.stringify(itinerary), JSON.stringify(placeList), status, this.accompanyId
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
                SELECT accompanyId, userId, cityId, title, content, createdAt, tag, scraps, chatroom, 
                       maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList, status
                FROM Accompany WHERE userId = ?
            `;
            const [rows] = await db.query(query, [userId]);

            if (rows.length === 0) {
                return [];
            }

            const accompanyPosts = rows.map(row => {
                // Check if itinerary and placeList are strings or arrays and handle accordingly
                const itineraryParsed = Array.isArray(row.itinerary)
                    ? row.itinerary
                    : (row.itinerary ? JSON.parse(row.itinerary) : []);

                const placeListParsed = Array.isArray(row.placeList)
                    ? row.placeList
                    : (row.placeList ? JSON.parse(row.placeList) : []);

                return new AccompanyPost({
                    accompanyId: row.accompanyId,
                    userId: row.userId,
                    cityId: row.cityId,
                    title: row.title,
                    content: row.content,
                    createdAt: row.createdAt,
                    tag: row.tag,
                    scraps: row.scraps,
                    chatroom: row.chatroom,
                    maxParticipants: row.maxParticipants,
                    minParticipants: row.minParticipants,
                    currentParticipants: row.currentParticipants,
                    genderPreference: row.genderPreference,
                    maxAge: row.maxAge,
                    minAge: row.minAge,
                    purpose: row.purpose,
                    planId: row.planId,
                    itinerary: itineraryParsed,
                    placeList: placeListParsed,
                    status: row.status
                });
            });

            return accompanyPosts;

        } catch (err) {
            console.error('Error retrieving accompany posts by user id:', err.message);
            throw new Error('Could not retrieve accompany posts by user id');
        }
    }

    static async getPostByAccompanyId(accompanyId) {
        try {
            const query = `
                SELECT accompanyId, userId, cityId, title, content, createdAt, tag, scraps, chatroom, 
                       maxParticipants, minParticipants, currentParticipants, genderPreference, maxAge, minAge, purpose, planId, itinerary, placeList, status
                FROM Accompany WHERE accompanyId = ?
            `;
            const [rows] = await db.query(query, [accompanyId]);

            if (rows.length === 0) {
                return null;
            }

            // Check if itinerary and placeList are strings or arrays and handle accordingly
            const itineraryParsed = Array.isArray(rows[0].itinerary)
                ? rows[0].itinerary
                : (rows[0].itinerary ? JSON.parse(rows[0].itinerary) : []);

            const placeListParsed = Array.isArray(rows[0].placeList)
                ? rows[0].placeList
                : (rows[0].placeList ? JSON.parse(rows[0].placeList) : []);

            const accompanyPost = new AccompanyPost({
                accompanyId: rows[0].accompanyId,
                userId: rows[0].userId,
                title: rows[0].title,
                content: rows[0].content,
                createdAt: rows[0].createdAt,
                tag: rows[0].tag,
                scraps: rows[0].scraps,
                chatroom: rows[0].chatroom,
                maxParticipants: rows[0].maxParticipants,
                minParticipants: rows[0].minParticipants,
                currentParticipants: rows[0].currentParticipants,
                genderPreference: rows[0].genderPreference,
                maxAge: rows[0].maxAge,
                minAge: rows[0].minAge,
                purpose: rows[0].purpose,
                planId: rows[0].planId,
                itinerary: itineraryParsed,
                placeList: placeListParsed,
                status: rows[0].status
            });

            return accompanyPost;

        } catch (err) {
            console.error('Error retrieving accompany post by accompany id:', err.message);
            throw new Error('Could not retrieve accompany post by accompany id');
        }
    }

    // 특정 user가 특정 post를 스크랩
    static async addScrap(userId, accompanyId) {
        const connection = await db.getConnection();
        try {
            await connection.beginTransaction();
            // Insert scrap into ScrapAccompanyPost table
            const insertScrapQuery = `
            INSERT INTO ScrapAccompanyPost (userId, accompanyId)
            VALUES (?, ?)
        `;
            await connection.query(insertScrapQuery, [userId, accompanyId]);

            // Increment scraps field in Accompany table
            const updateScrapsQuery = `
            UPDATE Accompany
            SET scraps = scraps + 1
            WHERE accompanyId = ?
        `;
            await connection.query(updateScrapsQuery, [accompanyId]);

            await connection.commit(); // Commit the transaction
        } catch (err) {
            await connection.rollback(); // Roll back on error
            console.error('Error adding scrap:', err.message);
            throw new Error('Could not add scrap');
        } finally {
            connection.release(); // Release the connection
        }
    }

    // 특정 user가 특정 post의 스크랩을 삭제
    static async removeScrap(userId, accompanyId) {
        const connection = await db.getConnection();
        try {
            await connection.beginTransaction();

            // Delete scrap from ScrapAccompanyPost table
            const deleteScrapQuery = `
            DELETE FROM ScrapAccompanyPost 
            WHERE userId = ? AND accompanyId = ?
        `;
            await connection.query(deleteScrapQuery, [userId, accompanyId]);

            // Decrement scraps field in Accompany table
            const updateScrapsQuery = `
            UPDATE Accompany
            SET scraps = scraps - 1
            WHERE accompanyId = ? AND scraps > 0
        `;
            await connection.query(updateScrapsQuery, [accompanyId]);

            await connection.commit(); // Commit transaction
        } catch (err) {
            await connection.rollback(); // Rollback on error
            console.error('Error removing scrap:', err.message);
            throw new Error('Could not remove scrap');
        } finally {
            connection.release(); // Release the connection
        }
    }

    // 특정 user가 스크랩한 모든 post를 조회
    static async getScrappedPostsByUserId(userId) {
        try {
            const query = `
                SELECT A.accompanyId, A.userId, A.cityId, A.title, A.content, A.createdAt, A.tag, A.scraps, A.chatroom, 
                       A.maxParticipants, A.minParticipants, A.currentParticipants, A.genderPreference, A.maxAge, A.minAge, A.purpose, A.planId, A.itinerary, A.placeList, A.status
                FROM Accompany A
                JOIN ScrapAccompanyPost S ON A.accompanyId = S.accompanyId
                WHERE S.userId = ?
            `;
            const [rows] = await db.query(query, [userId]);

            if (rows.length === 0) {
                return [];
            }

            const scrappedPosts = rows.map(row => new AccompanyPost({
                accompanyId: row.accompanyId,
                userId: row.userId,
                cityId: row.cityId,
                title: row.title,
                content: row.content,
                createdAt: row.createdAt,
                tag: row.tag,
                scraps: row.scraps,
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
                status: row.status
            }));

            return scrappedPosts;

        } catch (err) {
            console.error('Error retrieving scrapped posts by user id:', err.message);
            throw new Error('Could not retrieve scrapped posts by user id');
        }
    }

    // 모집 중 / 모집 완료
    static async setStatus(accompanyId, status) {
        try {
            const query = `
                UPDATE Accompany
                SET status = ?
                WHERE accompanyId = ?
            `;
            await db.query(query, [status, accompanyId]);
        } catch (err) {
            console.error('Error setting status:', err.message);
            throw new Error('Could not change status');
        }
    }
}

module.exports = AccompanyPost;
