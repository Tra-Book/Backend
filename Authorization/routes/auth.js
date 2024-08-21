const express = require('express');
const authController = require('../controllers/auth');
const authenticate = require('../middleware/auth')

const router = express.Router();

router.post('/login', authController.postLogin);
router.post('/signup', authController.postSignup);
router.post('/send-verify-email', authController.postSendVerificationCode);
router.post('/verify-code', authController.postVerifyCode);
router.post('/update-profile', authenticate, authController.postUpdateProfile);
router.delete('/delete-user', authenticate, authController.deleteUserData);
router.post('/google-login', authController.postGoogleLogin);
router.post('/kakao-login', authController.postKakaoAuth);
router.post('/naver-login', authController.postNaverLogin);

module.exports = router;
