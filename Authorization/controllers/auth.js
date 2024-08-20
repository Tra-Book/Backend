const bcrypt = require('bcryptjs');
const { OAuth2Client } = require('google-auth-library');
const axios = require('axios');
const generator = require('generate-password');
const { v4: uuidv4 } = require('uuid');
const nodemailer = require('nodemailer');

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
    service: 'Gmail',
    auth: {
        user: 'oriquack0423@gmail.com',
        pass: 'your-email-password'
    }
});

function sendVerificationEmail(email, token) {
    const verificationLink = `http://localhost:3000/auth/verify-email?token=${token}`;

    const mailOptions = {
        from: 'no-reply@yourdomain.com',
        to: email,
        subject: 'Email Verification',
        text: `Please verify your email by clicking on this link: ${verificationLink}`,
    };

    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            console.log('Error sending email:', error);
        } else {
            console.log('Email sent:', info.response);
        }
    });
}

exports.postLogin = (req, res, next) => {
    const { email, password } = req.body;

    User.getUserByEmail(email)
        .then((user) => {
            if (!user) {
                return sendErrorResponse(res, 404, 'User not found');
            }

            return bcrypt.compare(password, user.password)
                .then((doMatch) => {
                    if (!doMatch) {
                        return sendErrorResponse(res, 401, 'Incorrect password');
                    }

                    const accessToken = generateToken.genAccessToken(user.userId);
                    const refreshToken = generateToken.genRefreshToken();
                    return generateAuthResponse(res, 200, accessToken, refreshToken, { userId: user.userId, username: user.username });
                });
        })
        .catch(() => sendErrorResponse(res, 500, 'Server error'));
};

exports.postSignup = (req, res, next) => {
    const { email, password, username } = req.body;

    User.getUserByEmail(email)
        .then((user) => {
            if (user) {
                return sendErrorResponse(res, 400, 'User already exists');
            }

            return bcrypt.hash(password, 12)
                .then((hashedPassword) => {
                    const newUser = new User({ username, email, password: hashedPassword, statusMessage: null });
                    return newUser.save();
                })
                .then((userId) => {
                    const verificationToken = uuidv4();
                    redisClient.setex(verificationToken, 900, email);
                    sendVerificationEmail(email, verificationToken);

                    const accessToken = generateToken.genAccessToken(userId);
                    const refreshToken = generateToken.genRefreshToken();
                    return generateAuthResponse(res, 201, accessToken, refreshToken, { userId, username });
                });
        })
        .catch(() => sendErrorResponse(res, 500, 'Server error'));
};

exports.verifyEmail = (req, res) => {
    const { token } = req.query;

    if (!token) {
        return res.status(400).json({ message: 'Token is required' });
    }

    return redisClient.get(token, (err, email) => {
        if (err) {
            return sendErrorResponse(res, 500, 'Server error');
        }

        if (!email) {
            return sendErrorResponse(res, 400, 'Invalid or expired token');
        }

        // TODO: verify update 
        console.log(`Email verified: ${email}`);

        redisClient.del(token);

        return res.status(200).json({ message: 'Email verified' });
    });
};

exports.postUpdateProfile = (req, res, next) => {
    const { username, email, statusMessage, newPsssword } = req.body;

    User.getUserByEmail(email)
        .then((user) => {
            return bcrypt.hash(newPsssword, 12)
                .then((hashedPassword) => user.updateProfile({ username, statusMessage, password: hashedPassword }))
                .then(() => res.status(200).json({ message: 'Success' }));
        })
        .catch(() => sendErrorResponse(res, 500, 'Server error'));
};

exports.deleteUserData = (req, res, next) => {
    req.user.deleteUser()
        .then(() => res.status(200).json({ message: 'Success signout' }))
        .catch(() => sendErrorResponse(res, 500, 'Server error'));
};

const handleSocialLogin = (req, res, tokenVerifier, tokenName) => {
    const { email, access_token } = req.body;

    tokenVerifier(access_token)
        .then((tokenEmail) => {
            if (email !== tokenEmail) {
                return sendErrorResponse(res, 400, 'Bad request');
            }

            return User.getUserByEmail(email)
                .then((user) => {
                    if (!user) {
                        const username = '여행자' + generator.generate({ length: 8, numbers: true });
                        const password = generator.generate({ length: 14, numbers: true, symbols: true, strict: true });

                        return bcrypt.hash(password, 12)
                            .then((hashedPassword) => {
                                const newUser = new User({ username, email, password: hashedPassword, statusMessage: null });
                                return newUser.save()
                                    .then((userId) => {
                                        const accessToken = generateToken.genAccessToken(userId);
                                        const refreshToken = generateToken.genRefreshToken();
                                        return generateAuthResponse(res, 201, accessToken, refreshToken, { userId: userId, username: username });
                                    });
                            });
                    }

                    const accessToken = generateToken.genAccessToken(user.userId);
                    const refreshToken = generateToken.genRefreshToken();
                    return generateAuthResponse(res, 200, accessToken, refreshToken, { userId: user.userId, username: user.username });
                });
        })
        .catch((err) => {
            console.log(err);
            return sendErrorResponse(res, 500, `${tokenName} API server error`);
        });
};

exports.postGoogleLogin = (req, res) => {
    const CLIENT_ID = '';
    const client = new OAuth2Client(CLIENT_ID);

    const verifyGoogleToken = (token) => {
        return client.verifyIdToken({ idToken: token, audience: CLIENT_ID })
            .then((ticket) => ticket.getPayload().email);
    };

    handleSocialLogin(req, res, verifyGoogleToken, 'Google');
};

exports.postKakaoAuth = (req, res, next) => {
    const verifyKakaoToken = (token) => {
        return axios.get('https://kapi.kakao.com/v2/user/me', {
            headers: { Authorization: `Bearer ${token}` },
        }).then((response) => response.data.kakao_account.email);
    };

    handleSocialLogin(req, res, verifyKakaoToken, 'Kakao');
};

exports.postNaverLogin = (req, res, next) => {
    const verifyNaverToken = (token) => {
        return axios.get('https://openapi.naver.com/v1/nid/me', {
            headers: { Authorization: `Bearer ${token}` },
        }).then((response) => response.data.response.email);
    };

    handleSocialLogin(req, res, verifyNaverToken, 'Naver');
};

