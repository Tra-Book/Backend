const request = require('supertest');
const express = require('express');
const bodyParser = require('body-parser');
const authController = require('../controllers/authController');
const authService = require('../services/authService');
const { sendErrorResponse, generateAuthResponse } = require('../utils/responseUtil');
const User = require('../models/user'); // Import the User model


jest.mock('../services/authService');
jest.mock('../utils/responseUtil');

const app = express();
app.use(bodyParser.json());

app.post('/login', authController.postLogin);
app.post('/signup', authController.postSignup);
app.post('/send-verification-code', authController.postSendVerificationCode);
app.post('/verify-code', authController.postVerifyCode);
app.post('/update-profile', authController.postUpdateProfile);
app.delete('/delete-user', authController.deleteUserData);
app.post('/google-login', authController.postGoogleLogin);
app.post('/kakao-auth', authController.postKakaoAuth);
app.post('/naver-login', authController.postNaverLogin);

describe('Auth Controller', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    describe('POST /login', () => {
        it('should return 200 and tokens if login is successful', async () => {
            const mockResponse = {
                error: false,
                user: { userId: 1, username: 'testuser' },
                accessToken: 'access-token',
                refreshToken: 'refresh-token',
            };
            authService.login.mockResolvedValue(mockResponse);

            generateAuthResponse.mockImplementation(
                (res, statusCode, accessToken, refreshToken, user) => {
                    return res.status(statusCode).json({ accessToken, refreshToken, user });
                }
            );

            const res = await request(app)
                .post('/login')
                .send({ email: 'test@test.com', password: 'password' });

            expect(res.status).toBe(200);
            expect(res.body).toEqual({
                accessToken: 'access-token',
                refreshToken: 'refresh-token',
                user: { userId: 1, username: 'testuser' },
            });
            expect(authService.login).toHaveBeenCalledWith('test@test.com', 'password');
        });

        it('should return error if login fails', async () => {
            const mockErrorResponse = {
                error: true,
                statusCode: 401,
                message: 'Incorrect password',
            };
            authService.login.mockResolvedValue(mockErrorResponse);

            sendErrorResponse.mockImplementation((res, statusCode, message) => {
                return res.status(statusCode).json({ message });
            });

            const res = await request(app)
                .post('/login')
                .send({ email: 'test@test.com', password: 'wrongpassword' });

            expect(res.status).toBe(401);
            expect(res.body.message).toBe('Incorrect password');
            expect(authService.login).toHaveBeenCalledWith('test@test.com', 'wrongpassword');
        });
    });

    describe('POST /signup', () => {
        it('should return 201 and tokens if signup is successful', async () => {
            const mockResponse = {
                error: false,
                user: { userId: 1, username: 'newuser' },
                accessToken: 'access-token',
                refreshToken: 'refresh-token',
            };
            authService.signup.mockResolvedValue(mockResponse);

            generateAuthResponse.mockImplementation(
                (res, statusCode, accessToken, refreshToken, user) => {
                    return res.status(statusCode).json({ accessToken, refreshToken, user });
                }
            );

            const res = await request(app)
                .post('/signup')
                .send({ email: 'new@test.com', password: 'password', username: 'newuser' });

            expect(res.status).toBe(201);
            expect(res.body).toEqual({
                accessToken: 'access-token',
                refreshToken: 'refresh-token',
                user: { userId: 1, username: 'newuser' },
            });
            expect(authService.signup).toHaveBeenCalledWith('new@test.com', 'password', 'newuser');
        });

        it('should return error if signup fails', async () => {
            const mockErrorResponse = {
                error: true,
                statusCode: 400,
                message: 'User already exists',
            };
            authService.signup.mockResolvedValue(mockErrorResponse);

            sendErrorResponse.mockImplementation((res, statusCode, message) => {
                return res.status(statusCode).json({ message });
            });

            const res = await request(app)
                .post('/signup')
                .send({
                    email: 'existing@test.com',
                    password: 'password',
                    username: 'existinguser',
                });

            expect(res.status).toBe(400);
            expect(res.body.message).toBe('User already exists');
            expect(authService.signup).toHaveBeenCalledWith(
                'existing@test.com',
                'password',
                'existinguser'
            );
        });
    });

    describe('POST /send-verification-code', () => {
        it('should return 200 if verification code is sent successfully', async () => {
            authService.sendVerificationCode.mockResolvedValue({ error: false });

            const res = await request(app)
                .post('/send-verification-code')
                .send({ email: 'test@test.com' });

            expect(res.status).toBe(200);
            expect(res.body.message).toBe('Verification code sent');
            expect(authService.sendVerificationCode).toHaveBeenCalledWith('test@test.com');
        });

        it('should return error if sending verification code fails', async () => {
            authService.sendVerificationCode.mockResolvedValue({
                error: true,
                message: 'Failed to send email',
            });

            const res = await request(app)
                .post('/send-verification-code')
                .send({ email: 'test@test.com' });

            expect(res.status).toBe(500);
            expect(res.body.message).toBe('Failed to send email');
            expect(authService.sendVerificationCode).toHaveBeenCalledWith('test@test.com');
        });
    });

    describe('POST /verify-code', () => {
        it('should return 200 if verification code is valid', async () => {
            authService.verifyCode.mockResolvedValue({ error: false });

            const res = await request(app)
                .post('/verify-code')
                .send({ email: 'test@test.com', code: '12345678' });

            expect(res.status).toBe(200);
            expect(res.body.message).toBe('Valid code');
            expect(authService.verifyCode).toHaveBeenCalledWith('test@test.com', '12345678');
        });

        it('should return error if verification code is invalid', async () => {
            authService.verifyCode.mockResolvedValue({ error: true, message: 'Invalid code' });

            const res = await request(app)
                .post('/verify-code')
                .send({ email: 'test@test.com', code: 'wrongcode' });

            expect(res.status).toBe(400);
            expect(res.body.message).toBe('Invalid code');
            expect(authService.verifyCode).toHaveBeenCalledWith('test@test.com', 'wrongcode');
        });
    });

    describe('POST /update-profile', () => {
        it('should return 200 if profile is updated successfully', async () => {
            authService.updateProfile.mockResolvedValue({ error: false });

            const res = await request(app).post('/update-profile').send({
                username: 'updateduser',
                email: 'test@test.com',
                profilePhoto: 'photo.jpg',
                statusMessage: 'Hello world',
                newPassword: 'newpassword',
            });

            expect(res.status).toBe(200);
            expect(res.body.message).toBe('Success');
            expect(authService.updateProfile).toHaveBeenCalledWith(
                'updateduser',
                'test@test.com',
                'photo.jpg',
                'Hello world',
                'newpassword'
            );
        });

        it('should return error if profile update fails', async () => {
            authService.updateProfile.mockResolvedValue({
                error: true,
                statusCode: 500,
                message: 'Server error',
            });

            const res = await request(app).post('/update-profile').send({
                username: 'updateduser',
                email: 'test@test.com',
                profilePhoto: 'photo.jpg',
                statusMessage: 'Hello world',
                newPassword: 'newpassword',
            });

            expect(res.status).toBe(500);
            expect(res.body.message).toBe('Server error');
            expect(authService.updateProfile).toHaveBeenCalledWith(
                'updateduser',
                'test@test.com',
                'photo.jpg',
                'Hello world',
                'newpassword'
            );
        });
    });

    describe('DELETE /delete-user', () => {
        it('should return 200 if user is deleted successfully', async () => {
            // Create a mock user object instance of the User class
            const mockUser = new User({ userId: 1, username: 'testuser' });

            authService.deleteUser.mockResolvedValue({ error: false });

            const res = await request(app).delete('/delete-user').send({ user: mockUser });

            expect(res.status).toBe(200);
            expect(res.body.message).toBe('Success signout');
            expect(authService.deleteUser).toHaveBeenCalledWith(mockUser);
        });

        it('should return error if user deletion fails', async () => {
            // Create a mock user object instance of the User class
            const mockUser = new User({ userId: 1, username: 'testuser' });

            authService.deleteUser.mockResolvedValue({
                error: true,
                statusCode: 500,
                message: 'Server error',
            });

            const res = await request(app).delete('/delete-user').send({ user: mockUser });

            expect(res.status).toBe(500);
            expect(res.body.message).toBe('Server error');
            expect(authService.deleteUser).toHaveBeenCalledWith(mockUser);
        });
    });

    describe('POST /google-login', () => {
        it('should handle Google login successfully', async () => {
            authService.handleSocialLogin.mockResolvedValue({ error: false });

            const res = await request(app)
                .post('/google-login')
                .send({ email: 'test@test.com', access_token: 'google-token' });

            expect(authService.handleSocialLogin).toHaveBeenCalledWith(
                expect.anything(),
                expect.anything(),
                'Google'
            );
        });
    });

    describe('POST /kakao-auth', () => {
        it('should handle Kakao login successfully', async () => {
            authService.handleSocialLogin.mockResolvedValue({ error: false });

            const res = await request(app)
                .post('/kakao-auth')
                .send({ email: 'test@test.com', access_token: 'kakao-token' });

            expect(authService.handleSocialLogin).toHaveBeenCalledWith(
                expect.anything(),
                expect.anything(),
                'Kakao'
            );
        });
    });

    describe('POST /naver-login', () => {
        it('should handle Naver login successfully', async () => {
            authService.handleSocialLogin.mockResolvedValue({ error: false });

            const res = await request(app)
                .post('/naver-login')
                .send({ email: 'test@test.com', access_token: 'naver-token' });

            expect(authService.handleSocialLogin).toHaveBeenCalledWith(
                expect.anything(),
                expect.anything(),
                'Naver'
            );
        });
    });
});
