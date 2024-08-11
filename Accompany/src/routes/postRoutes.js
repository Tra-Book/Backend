const express = require('express');
const router = express.Router();
const postController = require('../controllers/postController');
const commentController = require('../controllers/commentController');

// 게시글
router.post('/', postController.createPost);
router.get('/:id', postController.getPost);
router.patch('/:id', postController.updatePost);
router.delete('/:id', postController.deletePost);
router.get('/user/:author', postController.getUserPosts);

// 댓글
router.post('/:postId/comments', commentController.createComment);
router.patch('/:postId/comments/:commentId', commentController.updateComment);
router.delete('/:postId/comments/:commentId', commentController.deleteComment);

module.exports = router;