const emailService = require('./emailService');
const socialAuthService = require('./socialAuthService');
const tokenUtil = require('../utils/tokenUtil');
const bcryptUtil = require('../utils/bcryptUtil');
const redisUtil = require('../utils/redisUtil');
const db = require('../utils/mysqlUtil');
const User = require('../models/user');

exports.login = async (email, password) => {
    const user = await User.getUserByEmail(email);
    if (!user) {
        return { error: true, statusCode: 404, message: 'User not found' };
    }

    const doMatch = await bcryptUtil.comparePassword(password, user.password);
    if (!doMatch) {
        return { error: true, statusCode: 401, message: 'Incorrect password' };
    }

    const accessToken = tokenUtil.genAccessToken(user.userId);
    const refreshToken = tokenUtil.genRefreshToken();

    return {
        error: false,
        user: { userId: user.userId, username: user.username },
        accessToken,
        refreshToken,
    };
};

exports.signup = async (email, password, username) => {
    const connection = await db.getConnection();
    await connection.beginTransaction();

    try {
        const existingUser = await User.getUserByEmail(email);
        if (existingUser) {
            await connection.rollback();
            return { error: true, statusCode: 400, message: 'User already exists' };
        }

        const hashedPassword = await bcryptUtil.hashPassword(password);
        const newUser = new User({ username, email, password: hashedPassword });
        const userId = await newUser.save(connection); // Pass the connection to the save method

        const accessToken = tokenUtil.genAccessToken(userId);
        const refreshToken = tokenUtil.genRefreshToken();

        await connection.commit();

        return { error: false, user: { userId, username }, accessToken, refreshToken };
    } catch (err) {
        await connection.rollback();
        return { error: true, statusCode: 500, message: 'Server Error' };
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
        return { error: true, message: 'Failed to send email' };
    }
    return { error: false };
};

exports.verifyCode = async (email, code) => {
    const storedCode = await redisUtil.get(email);
    if (!storedCode || storedCode !== code) {
        return { error: true, message: 'Invalid code' };
    }
    await redisUtil.del(email);
    return { error: false };
};

exports.updateProfile = async (
    username,
    email,
    profilePhoto = null,
    statusMessage,
    newPassword
) => {
    const connection = await db.getConnection();
    await connection.beginTransaction();

    try {
        const user = await User.getUserByEmail(email, connection);
        if (!user) {
            await connection.rollback();
            return { error: true, statusCode: 404, message: 'User not found' };
        }

        const hashedPassword = await bcryptUtil.hashPassword(newPassword);
        await user.updateProfile(username, statusMessage, hashedPassword, profilePhoto, connection);

        await connection.commit();
        return { error: false, message: 'Profile updated successfully' };
    } catch (err) {
        await connection.rollback();
        return { error: true, statusCode: 500, message: 'Server Error' };
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
    } catch (err) {
        connection.rollback();
        return { error: true, statusCode: 500, message: 'Server Error' };
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
            return generateAuthResponse(res, 201, accessToken, refreshToken, newUserDetails);
        }

        const accessToken = tokenUtil.genAccessToken(user.userId);
        const refreshToken = tokenUtil.genRefreshToken();

        await connection.commit();

        return generateAuthResponse(res, 200, accessToken, refreshToken, {
            userId: user.userId,
            username: user.username,
        });
    } catch (err) {
        await connection.rollback();
        return { error: true, statusCode: 500, message: `${provider} API server error` };
    } finally {
        connection.release();
    }
};
