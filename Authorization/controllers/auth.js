const bcrypt = require('bcryptjs');
const { OAuth2Client } = require('google-auth-library');
const axios = require('axios');
const generator = require('generate-password');
const { v4: uuidv4 } = require('uuid');
const nodemailer = require('nodemailer');
require('dotenv').config();

const User = require('../models/user');
const generateToken = require('../utils/token');
const redisClient = require('../utils/redis');

const sendErrorResponse = (res, statusCode, message) => {
    return res.status(statusCode).json({ message });
};

const generateAuthResponse = (res, statusCode, accessToken, refreshToken, user) => {
    return res
        .status(statusCode)
        .cookie('refreshToken', refreshToken, {
            expires: new Date(Date.now() + 259200000),
            httpOnly: true,
        })
        .header('Authorization', accessToken)
        .json(user);
};

const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.GMAIL_USER,
        pass: process.env.GMAIL_PASS,
    },
});

const sendVerificationEmail = async (email, verificationCode) => {
    const mailOptions = {
        from: '"Trabook" <trabook24@gmail.com>',
        to: email,
        subject: '이메일 인증 코드',
        html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e1e1e1; border-radius: 10px; background-color: #f9f9f9;">
            <h2 style="text-align: center; color: #4CAF50;">이메일 인증</h2>
            <p>안녕하세요,</p>
            <p><strong>Trabook</strong> 계정의 이메일 인증을 요청하셨습니다.</p>
            <p>아래 인증 코드를 입력하셔서 이메일 주소를 확인해주세요:</p>
            <br>
            <p style="font-size: 24px; font-weight: bold; text-align: center; color: #333;">${verificationCode}</p>
            <br>
            <p>만약 이 요청을 본인이 하지 않으셨다면, 이 이메일을 무시하셔도 됩니다.</p>
            <p>감사합니다.<br><br>Trabook 팀 드림</p>
            <hr style="border: 0; border-top: 1px solid #e1e1e1; margin: 20px 0;">
            <p style="font-size: 12px; color: #888; text-align: center;">이 이메일에 회신하지 마세요. 도움이 필요하시면 trabook24@gmail.com으로 연락주시기 바랍니다.</p>
        </div>
        `,
        text: `안녕하세요,\n\nTrabook 계정의 이메일 인증을 요청하셨습니다.\n\n인증 코드는: ${verificationCode}\n\n만약 이 요청을 본인이 하지 않으셨다면, 이 이메일을 무시하셔도 됩니다.\n\n감사합니다.\nTrabook 팀 드림`,
    };

    try {
        await transporter.sendMail(mailOptions);
        return true;
    } catch (error) {
        console.log(error);
        return false;
    }
};

exports.postLogin = async (req, res, next) => {
    const { email, password } = req.body;

    try {
        const user = await User.getUserByEmail(email);
        if (!user) {
            return sendErrorResponse(res, 404, 'User not found');
        }

        const doMatch = await bcrypt.compare(password, user.password);
        if (!doMatch) {
            return sendErrorResponse(res, 401, 'Incorrect password');
        }

        const accessToken = generateToken.genAccessToken(user.userId);
        const refreshToken = generateToken.genRefreshToken();
        return generateAuthResponse(res, 200, accessToken, refreshToken, {
            userId: user.userId,
            username: user.username,
        });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postSignup = async (req, res, next) => {
    const { email, password, username } = req.body;

    try {
        const existingUser = await User.getUserByEmail(email);
        if (existingUser) {
            return sendErrorResponse(res, 400, 'User already exists');
        }

        const hashedPassword = await bcrypt.hash(password, 12);
        const newUser = new User({
            username,
            email,
            password: hashedPassword,
            statusMessage: null,
        });
        const userId = await newUser.save();
        const accessToken = generateToken.genAccessToken(userId);
        const refreshToken = generateToken.genRefreshToken();
        return generateAuthResponse(res, 201, accessToken, refreshToken, {
            userId,
            username,
        });
    } catch (error) {
        console.log(error);
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.postSendVerificationCode = async (req, res) => {
    const { email } = req.body;
    const verificationCode = Math.floor(Math.random() * 100000000)
        .toString()
        .padStart(8, '0');

    try {
        await redisClient.set(email, verificationCode, { EX: 900 });

        const emailSent = await sendVerificationEmail(email, verificationCode);
        if (emailSent) {
            return res.status(200).json({ message: 'Verification code sent' });
        } else {
            return res.status(500).json({ message: 'Failed to send email' });
        }
    } catch (error) {
        console.log(error);
        return res.status(500).json({ message: 'Server error' });
    }
};

exports.postVerifyCode = async (req, res) => {
    const { email, code } = req.body;
    const storedCode = await redisClient.get(email);

    if (!storedCode) {
        return res.status(400).json({ message: 'Invalid code' });
    }

    if (storedCode === code) {
        await redisClient.del(email);
        return res.status(200).json({ message: 'Valid code' });
    } else {
        return res.status(400).json({ message: 'Invalid code' });
    }
};

exports.postUpdateProfile = async (req, res, next) => {
    const { username, email, statusMessage, newPassword } = req.body;

    try {
        const user = await User.getUserByEmail(email);
        const hashedPassword = await bcrypt.hash(newPassword, 12);
        await user.updateProfile(username, statusMessage, hashedPassword);
        return res.status(200).json({ message: 'Success' });
    } catch (error) {
        console.log(error);
        return sendErrorResponse(res, 500, 'Server error');
    }
};

exports.deleteUserData = async (req, res, next) => {
    try {
        await req.user.deleteUser();
        return res.status(200).json({ message: 'Success signout' });
    } catch (error) {
        return sendErrorResponse(res, 500, 'Server error');
    }
};

const handleSocialLogin = async (req, res, tokenVerifier, tokenName) => {
    const { email, access_token } = req.body;

    try {
        const tokenEmail = await tokenVerifier(access_token);
        if (email !== tokenEmail) {
            return sendErrorResponse(res, 400, 'Bad request');
        }

        const user = await User.getUserByEmail(email);
        if (!user) {
            const username = '여행자' + generator.generate({ length: 8, numbers: true });
            const password = generator.generate({
                length: 14,
                numbers: true,
                symbols: true,
                strict: true,
            });

            const hashedPassword = await bcrypt.hash(password, 12);
            const newUser = new User({
                username,
                email,
                password: hashedPassword,
                statusMessage: null,
            });
            const userId = await newUser.save();
            const accessToken = generateToken.genAccessToken(userId);
            const refreshToken = generateToken.genRefreshToken();
            return generateAuthResponse(res, 201, accessToken, refreshToken, {
                userId: userId,
                username: username,
            });
        }

        const accessToken = generateToken.genAccessToken(user.userId);
        const refreshToken = generateToken.genRefreshToken();
        return generateAuthResponse(res, 200, accessToken, refreshToken, {
            userId: user.userId,
            username: user.username,
        });
    } catch (err) {
        console.log(err);
        return sendErrorResponse(res, 500, `${tokenName} API server error`);
    }
};

exports.postGoogleLogin = (req, res) => {
    const client = new OAuth2Client(process.env.GOOGLE_CLIENTID);

    const verifyGoogleToken = async (token) => {
        const ticket = await client.verifyIdToken({ idToken: token, audience: CLIENT_ID });
        return ticket.getPayload().email;
    };

    handleSocialLogin(req, res, verifyGoogleToken, 'Google');
};

exports.postKakaoAuth = (req, res, next) => {
    const verifyKakaoToken = async (token) => {
        const response = await axios.get('https://kapi.kakao.com/v2/user/me', {
            headers: { Authorization: `Bearer ${token}` },
        });
        return response.data.kakao_account.email;
    };

    handleSocialLogin(req, res, verifyKakaoToken, 'Kakao');
};

exports.postNaverLogin = (req, res, next) => {
    const verifyNaverToken = async (token) => {
        const response = await axios.get('https://openapi.naver.com/v1/nid/me', {
            headers: { Authorization: `Bearer ${token}` },
        });
        return response.data.response.email;
    };

    handleSocialLogin(req, res, verifyNaverToken, 'Naver');
};
