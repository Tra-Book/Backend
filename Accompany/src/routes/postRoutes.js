const express = require('express');
const router = express.Router();
const postController = require('../controllers/postControllers.js');
const commentController = require('../controllers/commentControllers.js');

// 나중에 다 수정해야 함!
router.post('/', postController.createPost);
router.get('/:accompanyId', postController.getPost);
router.post('/update/:accompanyId', postController.updatePost);
router.delete('/:accompanyId', postController.deletePost);
router.get('/user/:userId', postController.getAccompanyPostsByUserId);

router.post('/:accompanyId/:status', postController.setStatus);

router.post('/scrap/:userId/:accompanyId', postController.addScrap); // 게시물 스크랩
router.delete('/scrap/:userId/:accompanyId', postController.removeScrap); // 게시물 스크랩 취소
router.get('/scrap/:userId', postController.getScrappedPostsByUserId); // 사용자가 스크랩한 모든 게시물 조회

// 댓글
// router.post('/:postId/comments', commentController.createComment);
// router.patch('/:postId/comments/:commentId', commentController.updateComment);
// router.delete('/:postId/comments/:commentId', commentController.deleteComment);

module.exports = router;