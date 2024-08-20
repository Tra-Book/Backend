const bcrypt = require('bcryptjs');
const { OAuth2Client } = require('google-auth-library');
const axios = require('axios');
const generator = require('generate-password');
const { v4: uuidv4 } = require('uuid');
const nodemailer = require('nodemailer');

const User = require('../models/user');
const { postLogin, postSignup, postVerifyEmail, postUpdateProfile, deleteUserData, postGoogleLogin, postKakaoAuth, postNaverLogin } = require('../controllers/auth');
const generateToken = require('../utils/token');
const redisClient = require('../utils/redis');

jest.mock('bcryptjs');
jest.mock('google-auth-library');
jest.mock('axios');
jest.mock('generate-password');
jest.mock('uuid');
jest.mock('nodemailer');
jest.mock('../models/user');
jest.mock('../utils/token');
jest.mock('../utils/redis');

describe('Auth Controller', () => {
    let res;
    let req;
    let next;

    beforeEach(() => {
        res = {
            status: jest.fn().mockReturnThis(),
            json: jest.fn().mockReturnThis(),
            cookie: jest.fn().mockReturnThis(),
            header: jest.fn().mockReturnThis()
        };
        req = {
            body: {},
            query: {},
            user: {}
        };
        next = jest.fn();
    });

    describe('postLogin', () => {
        it('should return 404 if user is not found', async () => {
            req.body = { email: 'test@example.com', password: 'password' };
            User.getUserByEmail.mockResolvedValue(null);

            await postLogin(req, res, next);

            expect(res.status).toHaveBeenCalledWith(404);
            expect(res.json).toHaveBeenCalledWith({ message: 'User not found' });
        });

        it('should return 401 if password is incorrect', async () => {
            const user = { userId: 1, password: 'hashedPassword' };
            req.body = { email: 'test@example.com', password: 'password' };
            User.getUserByEmail.mockResolvedValue(user);
            bcrypt.compare.mockResolvedValue(false);

            await postLogin(req, res, next);

            expect(res.status).toHaveBeenCalledWith(401);
            expect(res.json).toHaveBeenCalledWith({ message: 'Incorrect password' });
        });

        it('should return 200 and tokens if login is successful', async () => {
            const user = { userId: 1, username: 'testuser', password: 'hashedPassword' };
            req.body = { email: 'test@example.com', password: 'password' };
            User.getUserByEmail.mockResolvedValue(user);
            bcrypt.compare.mockResolvedValue(true);
            generateToken.genAccessToken.mockReturnValue('accessToken');
            generateToken.genRefreshToken.mockReturnValue('refreshToken');

            await postLogin(req, res, next);

            expect(res.status).toHaveBeenCalledWith(200);
            expect(res.cookie).toHaveBeenCalledWith('refreshToken', 'refreshToken', expect.any(Object));
            expect(res.header).toHaveBeenCalledWith('Authorization', 'accessToken');
            expect(res.json).toHaveBeenCalledWith({ userId: user.userId, username: user.username });
        });
    });

    describe('postSignup', () => {
        it('should return 400 if user already exists', async () => {
            req.body = { email: 'test@example.com', password: 'password', username: 'testuser' };
            User.getUserByEmail.mockResolvedValue(true);

            await postSignup(req, res, next);

            expect(res.status).toHaveBeenCalledWith(400);
            expect(res.json).toHaveBeenCalledWith({ message: 'User already exists' });
        });

        it('should return 201 and tokens if signup is successful', async () => {
            req.body = { email: 'test@example.com', password: 'password', username: 'testuser' };
            User.getUserByEmail.mockResolvedValue(null);
            bcrypt.hash.mockResolvedValue('hashedPassword');
            User.prototype.save.mockResolvedValue('newUserId');
            uuidv4.mockReturnValue('verificationToken');
            generateToken.genAccessToken.mockReturnValue('accessToken');
            generateToken.genRefreshToken.mockReturnValue('refreshToken');
            redisClient.setex.mockResolvedValue(null);

            await postSignup(req, res, next);

            expect(res.status).toHaveBeenCalledWith(201);
            expect(res.cookie).toHaveBeenCalledWith('refreshToken', 'refreshToken', expect.any(Object));
            expect(res.header).toHaveBeenCalledWith('Authorization', 'accessToken');
            expect(res.json).toHaveBeenCalledWith({ userId: 'newUserId', username: 'testuser' });
        });
    });

    describe('postVerifyEmail', () => {
        it('should return 400 if token is missing', async () => {
            req.query = {};

            await postVerifyEmail(req, res);

            expect(res.status).toHaveBeenCalledWith(400);
            expect(res.json).toHaveBeenCalledWith({ message: 'Token is required' });
        });

        it('should return 400 if token is invalid or expired', async () => {
            req.query = { token: 'invalidToken' };
            redisClient.get.mockImplementation((_, cb) => cb(null, null));

            await postVerifyEmail(req, res);

            expect(res.status).toHaveBeenCalledWith(400);
            expect(res.json).toHaveBeenCalledWith({ message: 'Invalid or expired token' });
        });

        it('should return 200 if email is verified', async () => {
            req.query = { token: 'validToken' };
            redisClient.get.mockImplementation((_, cb) => cb(null, 'test@example.com'));

            await postVerifyEmail(req, res);

            expect(res.status).toHaveBeenCalledWith(200);
            expect(res.json).toHaveBeenCalledWith({ message: 'Email verified' });
        });
    });

    describe('postUpdateProfile', () => {
        it('should return 200 if profile is updated successfully', async () => {
            req.body = { username: 'newUser', email: 'test@example.com', statusMessage: 'new status', newPsssword: 'newPassword' };
            const user = { updateProfile: jest.fn().mockResolvedValue(true) };
            User.getUserByEmail.mockResolvedValue(user);
            bcrypt.hash.mockResolvedValue('hashedPassword');

            await postUpdateProfile(req, res, next);

            expect(res.status).toHaveBeenCalledWith(200);
            expect(res.json).toHaveBeenCalledWith({ message: 'Success' });
        });
    });

    describe('deleteUserData', () => {
        it('should return 200 if user data is deleted successfully', async () => {
            req.user.deleteUser = jest.fn().mockResolvedValue(true);

            await deleteUserData(req, res, next);

            expect(res.status).toHaveBeenCalledWith(200);
            expect(res.json).toHaveBeenCalledWith({ message: 'Success signout' });
        });
    });

    describe('Social Logins', () => {
        describe('postGoogleLogin', () => {
            it('should return 200 and tokens if Google login is successful', async () => {
                req.body = { email: 'test@example.com', access_token: 'validToken' };
                const client = {
                    verifyIdToken: jest.fn().mockResolvedValue({
                        getPayload: () => ({ email: 'test@example.com' })
                    })
                };
                OAuth2Client.mockReturnValue(client);
                const user = { userId: 'userId', username: 'testuser' };
                User.getUserByEmail.mockResolvedValue(user);
                generateToken.genAccessToken.mockReturnValue('accessToken');
                generateToken.genRefreshToken.mockReturnValue('refreshToken');

                await postGoogleLogin(req, res);

                expect(res.status).toHaveBeenCalledWith(200);
                expect(res.cookie).toHaveBeenCalledWith('refreshToken', 'refreshToken', expect.any(Object));
                expect(res.header).toHaveBeenCalledWith('Authorization', 'accessToken');
                expect(res.json).toHaveBeenCalledWith({ userId: 'userId', username: 'testuser' });
            });
        });

        describe('postKakaoAuth', () => {
            it('should return 200 and tokens if Kakao login is successful', async () => {
                req.body = { email: 'test@example.com', access_token: 'validToken' };
                axios.get.mockResolvedValue({ data: { kakao_account: { email: 'test@example.com' } } });
                const user = { userId: 'userId', username: 'testuser' };
                User.getUserByEmail.mockResolvedValue(user);
                generateToken.genAccessToken.mockReturnValue('accessToken');
                generateToken.genRefreshToken.mockReturnValue('refreshToken');

                await postKakaoAuth(req, res);

                expect(res.status).toHaveBeenCalledWith(200);
                expect(res.cookie).toHaveBeenCalledWith('refreshToken', 'refreshToken', expect.any(Object));
                expect(res.header).toHaveBeenCalledWith('Authorization', 'accessToken');
                expect(res.json).toHaveBeenCalledWith({ userId: 'userId', username: 'testuser' });
            });
        });

        describe('postNaverLogin', () => {
            it('should return 200 and tokens if Naver login is successful', async () => {
                req.body = { email: 'test@example.com', access_token: 'validToken' };
                axios.get.mockResolvedValue({ data: { response: { email: 'test@example.com' } } });
                const user = { userId: 'userId', username: 'testuser' };
                User.getUserByEmail.mockResolvedValue(user);
                generateToken.genAccessToken.mockReturnValue('accessToken');
                generateToken.genRefreshToken.mockReturnValue('refreshToken');

                await postNaverLogin(req, res);

                expect(res.status).toHaveBeenCalledWith(200);
                expect(res.cookie).toHaveBeenCalledWith('refreshToken', 'refreshToken', expect.any(Object));
                expect(res.header).toHaveBeenCalledWith('Authorization', 'accessToken');
                expect(res.json).toHaveBeenCalledWith({ userId: 'userId', username: 'testuser' });
            });
        });
    });
});
