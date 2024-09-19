const { OAuth2Client } = require('google-auth-library');
const axios = require('axios');
const generator = require('generate-password');
const bcryptUtil = require('../utils/bcryptUtil');
const User = require('../models/user');
const googleConfig = require('../config/googleConfig');

exports.getTokenVerifier = (provider) => {
    switch (provider) {
        case 'Google':
            return verifyGoogleToken;
        case 'Kakao':
            return verifyKakaoToken;
        case 'Naver':
            return verifyNaverToken;
        default:
            throw new Error('Invalid provider');
    }
};

const verifyGoogleToken = async (token, timeout = 5000) => {
    const client = new OAuth2Client(googleConfig.clientId);
    const verifyPromise = client
        .verifyIdToken({
            idToken: token,
            audience: googleConfig.clientId,
        })
        .then((ticket) => ticket.getPayload().email)
        .catch((err) => {
            console.error('Verification error:', err);
            throw err;
        });

    const timeoutPromise = new Promise((_, reject) =>
        setTimeout(() => reject(new Error('Request timed out')), timeout)
    );

    return Promise.race([verifyPromise, timeoutPromise]);
};

const verifyKakaoToken = async (token) => {
    const response = await axios.get('https://kapi.kakao.com/v2/user/me', {
        headers: { Authorization: `Bearer ${token}` },
    });
    return response.data.kakao_account.email;
};

const verifyNaverToken = async (token) => {
    const response = await axios.get('https://openapi.naver.com/v1/nid/me', {
        headers: { Authorization: `Bearer ${token}` },
    });
    return response.data.response.email;
};

exports.createUser = async (email, connection) => {
    const username = '여행자' + generator.generate({ length: 8, numbers: true });
    const password = generator.generate({
        length: 14,
        numbers: true,
        symbols: true,
        strict: true,
    });

    const hashedPassword = await bcryptUtil.hashPassword(password);
    const newUser = new User({
        username,
        email,
        password: hashedPassword,
        statusMessage: null,
        profilePhoto: null,
    });
    const userId = await newUser.save(connection);
    return { userId, username };
};
