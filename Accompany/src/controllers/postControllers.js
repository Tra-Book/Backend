const AccompanyPost = require('../models/postModel.js');

exports.createPost = async (req, res) => {
    const {
        userId,
        cityId,
        title,
        content = null,
        tag = null,
        likes = 0,
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
        placeList = null
    } = req.body;

    if (!userId || !title || !cityId || !chatroom || !itinerary) {
        return res.status(400).json({ message: 'Not null fields are missing.' });
    }

    const createdAt = new Date().toISOString();

    const newPost = new AccompanyPost({
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
        placeList
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
        const post = await AccompanyPost.getPostByAccompanyId(req.params.accompanyPostId);
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
        const post = await AccompanyPost.getPostByAccompanyId(req.params.accompanyPostId);
        if (!post) {
            return res.status(404).json({ message: 'Post not found' });
        }

        await post.updateProfile(req.body);
        res.json(post);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.deletePost = async (req, res) => {
    try {
        const post = await AccompanyPost.getPostByAccompanyId(req.params.accompanyPostId);
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
