const express = require('express');
const authController = require('../controllers/auth');

const router = express.Router();

router.post('/login', authController.postLogin);
router.post('/signup', authController.postSignup);
router.post('/verify-email', authController.postVerifyEmail);
router.post('/update-profile', authController.postUpdateProfile);
router.delete('/delete-user', authController.deleteUserData);
router.post('/google-login', authController.postGoogleLogin);
router.post('/kakao-login', authController.postKakaoAuth);
router.post('/naver-login', authController.postNaverLogin);

module.exports = router;
