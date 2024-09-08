const authService = require('../services/authService');
const { sendErrorResponse, generateAuthResponse } = require('../utils/responseUtil');

exports.postLogin = async (req, res, next) => {
    const { email, password } = req.body;
    try {
        const { error, statusCode, message, data } = await authService.login(email, password);
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        const { accessToken, refreshToken, user } = data;
        return generateAuthResponse(res, 200, accessToken, refreshToken, user);
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postSignup = async (req, res, next) => {
    const { email, password, username } = req.body;
    try {
        const { error, statusCode, message, data } = await authService.signup(
            email,
            password,
            username
        );
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        const { accessToken, refreshToken, user } = data;
        return generateAuthResponse(res, 201, accessToken, refreshToken, user);
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postSendVerificationCode = async (req, res) => {
    const { email } = req.body;
    try {
        const { error, statusCode, message } = await authService.sendVerificationCode(email);
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        return res.status(200).json({ message: 'Verification code sent' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postVerifyCode = async (req, res) => {
    const { email, code } = req.body;
    try {
        const { error, statusCode, message } = await authService.verifyCode(email, code);
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        return res.status(200).json({ message: 'Valid code' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postUpdateProfile = async (req, res, next) => {
    const { username, statusMessage, imageUrl } = req.body;
    const profilePhoto = req.file ? req.file : null;

    try {
        const { error, statusCode, message, data } = await authService.updateProfile(
            req.user,
            username,
            profilePhoto,
            imageUrl,
            statusMessage
        );
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        return res
            .status(200)
            .json({ message: 'Profile updated successfully', profilePhoto: data });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postUpdatePassword = async (req, res, next) => {
    const { password, newPassword } = req.body;

    try {
        const { error, statusCode, message } = await authService.updatePassword(
            req.user,
            password,
            newPassword
        );
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        return res.status(200).json({ message: 'Password updated successfully' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.deleteUserData = async (req, res, next) => {
    try {
        const { error, statusCode, message } = await authService.deleteUser(req.user);
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        return res.status(200).json({ message: 'User deleted successfully' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.getNewAccessToken = async (req, res, next) => {
    try {
        const userId = req.user.userId;
        const { error, statusCode, message, data } = await authService.renewToken(req.user);
        if (error) {
            return sendErrorResponse(res, statusCode, message);
        }
        const { accessToken } = data;
        return generateAuthResponse(res, 200, accessToken, refreshToken, { userId: userId });
    } catch (err) {
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
