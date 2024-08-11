const Comment = require('../models/commentModel');

exports.createComment = async (req, res) => {
    const postId = req.params.postId;
    const author = req.body.author;
    const content = req.body.content;

    const dateObject = new Date();
    const date = dateObject.toISOString().split('T')[0].replace(/-/g, '.');
    const time = dateObject.getHours() + ':' + dateObject.getMinutes();
    const dateTime = date + ' ' + time

    const newComment = new Comment({ postId, author, content, dateTime });
    try {
        // save()
        await newComment.save();
        res.status(201).json(newComment);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.updateComment = async (req, res) => {
    const { commentId } = req.params;
    try {
        // findByIdAndUpdate: mongoose method (구현 필요)
        const comment = await Comment.findByIdAndUpdate(commentId, req.body, { new: true });
        res.json(comment);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

exports.deleteComment = async (req, res) => {
    const { commentId } = req.params;
    try {
        // findByIdAndDelete: mongoose method (구현 필요)
        await Comment.findByIdAndDelete(commentId);
        res.json({ message: 'Comment deleted' });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};
