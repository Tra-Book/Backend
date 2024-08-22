const authService = require('../services/authService');
const { sendErrorResponse, generateAuthResponse } = require('../utils/responseUtil');

exports.postLogin = async (req, res, next) => {
    const { email, password } = req.body;
    try {
        const result = await authService.login(email, password);
        return result.error 
            ? sendErrorResponse(res, result.statusCode, result.message)
            : generateAuthResponse(res, 200, result.accessToken, result.refreshToken, result.user);
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postSignup = async (req, res, next) => {
    const { email, password, username } = req.body;
    try {
        const result = await authService.signup(email, password, username);
        return result.error
            ? sendErrorResponse(res, result.statusCode, result.message)
            : generateAuthResponse(res, 201, result.accessToken, result.refreshToken, result.user);
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postSendVerificationCode = async (req, res) => {
    const { email } = req.body;
    try {
        const result = await authService.sendVerificationCode(email);
        return result.error
            ? sendErrorResponse(res, 500, result.message)
            : res.status(200).json({ message: 'Verification code sent' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postVerifyCode = async (req, res) => {
    const { email, code } = req.body;
    try {
        const result = await authService.verifyCode(email, code);
        return result.error
            ? sendErrorResponse(res, 400, result.message)
            : res.status(200).json({ message: 'Valid code' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postUpdateProfile = async (req, res, next) => {
    const { username, email, profilePhoto, statusMessage, newPassword } = req.body;
    try {
        await authService.updateProfile(username, email, profilePhoto, statusMessage, newPassword);
        return res.status(200).json({ message: 'Success' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.deleteUserData = async (req, res, next) => {
    try {
        await authService.deleteUser(req.user);
        return res.status(200).json({ message: 'Success signout' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postGoogleLogin = (req, res) => {
    authService.handleSocialLogin(req, res, 'Google');
};

exports.postKakaoAuth = (req, res) => {
    authService.handleSocialLogin(req, res, 'Kakao');
};

exports.postNaverLogin = (req, res) => {
    authService.handleSocialLogin(req, res, 'Naver');
};
