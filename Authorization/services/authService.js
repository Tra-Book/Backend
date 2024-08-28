const emailService = require('./emailService');
const socialAuthService = require('./socialAuthService');
const tokenUtil = require('../utils/tokenUtil');
const responseUtil = require('../utils/responseUtil');
const bcryptUtil = require('../utils/bcryptUtil');
const redisUtil = require('../utils/redisUtil');
const multerUtil = require('../utils/multerUtil');
const db = require('../utils/mysqlUtil');
const User = require('../models/user');
const multer = require('multer');

exports.login = async (email, password) => {
    const user = await User.getUserByEmail(email);
    if (!user) {
        return { error: true, statusCode: 404, message: 'User not found', data: null };
    }

    const doMatch = await bcryptUtil.comparePassword(password, user.password);
    if (!doMatch) {
        return { error: true, statusCode: 401, message: 'Incorrect password', data: null };
    }

    const accessToken = tokenUtil.genAccessToken(user.userId);
    const refreshToken = tokenUtil.genRefreshToken();
    return {
        error: false,
        statusCode: 200,
        message: 'Login successful',
        data: {
            user: { userId: user.userId, username: user.username },
            accessToken,
            refreshToken,
        },
    };
};

exports.signup = async (email, password, username) => {
    const connection = await db.getConnection();
    await connection.beginTransaction();

    try {
        const existingUser = await User.getUserByEmail(email);
        if (existingUser) {
            await connection.rollback();
            return { error: true, statusCode: 400, message: 'User already exists', data: null };
        }

        const hashedPassword = await bcryptUtil.hashPassword(password);
        const newUser = new User({ username, email, password: hashedPassword });
        const userId = await newUser.save(connection);

        const accessToken = tokenUtil.genAccessToken(userId);
        const refreshToken = tokenUtil.genRefreshToken();

        await connection.commit();

        return {
            error: false,
            statusCode: 201,
            message: 'User created successfully',
            data: {
                user: { userId, username },
                accessToken,
                refreshToken,
            },
        };
    } catch (err) {
        await connection.rollback();
        return { error: true, statusCode: 500, message: 'Server error', data: null };
    } finally {
        connection.release();
    }
};

exports.sendVerificationCode = async (email) => {
    const verificationCode = Math.floor(Math.random() * 100000000)
        .toString()
        .padStart(8, '0');
    await redisUtil.set(email, verificationCode, { EX: 900 });

    const emailSent = await emailService.sendVerificationEmail(email, verificationCode);
    if (!emailSent) {
        return { error: true, statusCode: 500, message: 'Failed to send email', data: null };
    }
    return {
        error: false,
        statusCode: 200,
        message: 'Verification code sent successfully',
        data: null,
    };
};

exports.verifyCode = async (email, code) => {
    const storedCode = await redisUtil.get(email);
    if (!storedCode || storedCode !== code) {
        return { error: true, statusCode: 400, message: 'Invalid code', data: null };
    }
    await redisUtil.del(email);
    return { error: false, statusCode: 200, message: 'Code verified successfully', data: null };
};

exports.updateProfile = async (user, username, profilePhoto, statusMessage, newPassword) => {
    let profilePhotoUrl = null;
    const connection = await db.getConnection();
    await connection.beginTransaction();

    try {
        profilePhotoUrl = await multerUtil.uploadToGCS(profilePhoto);
        const oldProfilePhotoUrl = user.profilePhoto;
        const hashedPassword = await bcryptUtil.hashPassword(newPassword);
        await user.updateProfile(
            username,
            statusMessage,
            hashedPassword,
            profilePhotoUrl,
            connection
        );

        await connection.commit();

        if (oldProfilePhotoUrl) {
            await multerUtil.removeFromGCS(oldProfilePhotoUrl);
        }
        return {
            error: false,
            statusCode: 200,
            message: 'Profile updated successfully',
            data: null,
        };
    } catch (err) {
        console.log(err);
        await connection.rollback();

        if (profilePhotoUrl) {
            await multerUtil.removeFromGCS(profilePhotoUrl);
        }
        return { error: true, statusCode: 500, message: 'Server error', data: null };
    } finally {
        connection.release();
    }
};

exports.deleteUser = async (user) => {
    const connection = await db.getConnection();
    await connection.beginTransaction();
    try {
        await user.deleteUser(connection);
        connection.commit();
        return { error: false, statusCode: 200, message: 'User deleted successfully', data: null };
    } catch (err) {
        connection.rollback();
        return { error: true, statusCode: 500, message: 'Server error', data: null };
    } finally {
        connection.release();
    }
};

exports.handleSocialLogin = async (req, res, provider) => {
    const connection = await db.getConnection();
    await connection.beginTransaction();

    try {
        const tokenVerifier = socialAuthService.getTokenVerifier(provider);
        const { email, access_token } = req.body;
        const tokenEmail = await tokenVerifier(access_token);
        if (email !== tokenEmail) {
            return { error: true, statusCode: 400, message: 'Bad request' };
        }

        const user = await User.getUserByEmail(email);
        if (!user) {
            const newUserDetails = await socialAuthService.createUser(email, connection);
            const accessToken = tokenUtil.genAccessToken(newUserDetails.userId);
            const refreshToken = tokenUtil.genRefreshToken();
            return responseUtil.generateAuthResponse(
                res,
                201,
                accessToken,
                refreshToken,
                newUserDetails
            );
        }

        const accessToken = tokenUtil.genAccessToken(user.userId);
        const refreshToken = tokenUtil.genRefreshToken();

        await connection.commit();

        return responseUtil.generateAuthResponse(res, 200, accessToken, refreshToken, {
            userId: user.userId,
            username: user.username,
        });
    } catch (err) {
        await connection.rollback();
        return { error: true, statusCode: 500, message: `${provider} API Server error` };
    } finally {
        connection.release();
    }
};