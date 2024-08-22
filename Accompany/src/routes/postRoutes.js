const express = require('express');
const router = express.Router();
const postController = require('../controllers/postControllers.js');
const commentController = require('../controllers/commentControllers.js');

// 나중에 다 수정해야 함!
router.post('/', postController.createPost);
router.get('/:accompanyId', postController.getPost);
router.patch('/:accompanyId', postController.updatePost);
router.delete('/:accompanyId', postController.deletePost);
router.get('/:userId', postController.getAccompanyPostsByUserId);

// 댓글
// router.post('/:postId/comments', commentController.createComment);
// router.patch('/:postId/comments/:commentId', commentController.updateComment);
// router.delete('/:postId/comments/:commentId', commentController.deleteComment);

module.exports = router;