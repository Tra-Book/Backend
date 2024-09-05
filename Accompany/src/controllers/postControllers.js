const AccompanyPost = require('../models/postModel.js');

exports.createPost = async (req, res) => {
    const {
        userId,
        cityId,
        title,
        content = null,
        tag = null,
        scraps = 0,
        chatroom,
        maxParticipants = null,
        minParticipants = null,
        currentParticipants = 0,
        genderPreference = null,
        maxAge = null,
        minAge = null,
        purpose = null,
        planId = null,
        itinerary,
        placeList = null,
        status = 0
    } = req.body;

    if (!userId || !title || !cityId || !chatroom || !itinerary) {
        return res.status(400).json({ message: 'Not null fields are missing.' });
    }

    const createdAt = new Date().toISOString().slice(0, 19).replace('T', ' ');

    const newPost = new AccompanyPost({
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
    });

    try {
        await newPost.save();
        res.status(201).json(newPost);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};


exports.getPost = async (req, res) => {
    try {
        const post = await AccompanyPost.getPostByAccompanyId(req.params.accompanyId);
        if (!post) {
            return res.status(404).json({ message: 'Post not found' });
        }
        res.json(post);
    } catch (error) {
        res.status(404).json({ message: error.message });
    }
};

exports.updatePost = async (req, res) => {
    try {
        const post = await AccompanyPost.getPostByAccompanyId(req.params.accompanyId);
        if (!post) {
            return res.status(404).json({ message: 'Post not found' });
        }

        await post.updatePost(req.body);
        res.json(post);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.deletePost = async (req, res) => {
    try {
        const post = await AccompanyPost.getPostByAccompanyId(req.params.accompanyId);
        if (!post) {
            return res.status(404).json({ message: 'Post not found' });
        }

        await post.deletePost();
        res.json({ message: 'Post deleted' });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.getAccompanyPostsByUserId = async (req, res) => {
    try {
        const posts = await AccompanyPost.getPostsByUserId(req.params.userId);

        if (posts.length === 0) {
            return res.status(404).json({ message: 'No posts found for this user.' });
        }

        return res.json(posts);
    } catch (error) {
        return res.status(400).json({ message: error.message });
    }
};

exports.addScrap = async (req, res) => {
    const { userId, accompanyId } = req.params;

    try {
        await AccompanyPost.addScrap(userId, accompanyId);
        res.status(201).json({ message: 'Post scrapped successfully' });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.removeScrap = async (req, res) => {
    const { userId, accompanyId } = req.params;

    try {
        await AccompanyPost.removeScrap(userId, accompanyId);
        res.status(200).json({ message: 'Scrap removed successfully' });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.getScrappedPostsByUserId = async (req, res) => {
    try {
        const posts = await AccompanyPost.getScrappedPostsByUserId(req.params.userId);

        if (posts.length === 0) {
            return res.status(404).json({ message: 'No scrapped posts found for this user.' });
        }

        return res.json(posts);
    } catch (error) {
        return res.status(400).json({ message: error.message });
    }
};

exports.setStatus = async (req, res) => {
    // status field : INT type -> 0: open, 1: closed
    try {
        const { accompanyId, status } = req.params;
        await AccompanyPost.setStatus(accompanyId, status);
        res.status(201).json({ message: 'Status changed successfully' });
    } catch (error) {
        return res.status(400).json({ message: error.message });
    }
}
