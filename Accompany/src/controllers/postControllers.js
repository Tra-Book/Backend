const Post = require('../models/postModel');

exports.createPost = async (req, res) => {
    const { title, author, content } = req.body;

    const dateObject = new Date();
    const date = dateObject.toISOString().split('T')[0].replace(/-/g, '.');
    const time = dateObject.getHours() + ':' + dateObject.getMinutes();
    const dateTime = date + ' ' + time

    const newPost = new Post({ title, author, content, dateTime });
    try {
        // save()
        await newPost.save();
        res.status(201).json(newPost);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.getPost = async (req, res) => {
    try {
        // findById() (구현 필요)
        const post = await Post.findById(req.params.id);
        res.json(post);
    } catch (error) {
        res.status(404).json({ message: error.message });
    }
};

exports.updatePost = async (req, res) => {
    try {
        // findByIdAndUpdate() (구현 필요)
        const post = await Post.findByIdAndUpdate(req.params.id, req.body, { new: true });
        res.json(post);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.deletePost = async (req, res) => {
    try {
        // findByIdAndDelete() (구현 필요)
        await Post.findByIdAndDelete(req.params.id);
        res.json({ message: 'Post deleted' });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.getUserPosts = async (req, res) => {
    try {
        // find() (구현 필요)
        const posts = await Post.find({ author: req.params.author });
        res.json(posts);
    } catch (error) {
        res.status(404).json({ message: error.message });
    }
};
