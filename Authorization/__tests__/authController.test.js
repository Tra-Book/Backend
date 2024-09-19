const request = require('supertest');
const express = require('express');
const authController = require('../controllers/authController');
const authenticate = require('../middleware/auth');
const upload = require('../middleware/uploadImage');
const authService = require('../services/authService');
const { sendErrorResponse, generateAuthResponse } = require('../utils/responseUtil');

jest.mock('../services/authService');
jest.mock('../middleware/auth');
jest.mock('../middleware/uploadImage');
jest.mock('../utils/responseUtil');

const app = express();
app.use(express.json());

app.post('/login', authController.postLogin);
app.post('/signup', authController.postSignup);
app.post('/send-verification-code', authController.postSendVerificationCode);
app.post('/verify-code', authController.postVerifyCode);
app.post('/update-profile', authenticate, upload, authController.postUpdateProfile);
app.delete('/delete-user', authenticate, authController.deleteUserData);

describe('Auth API Tests', () => {
    describe('POST /login', () => {
        it('should return 200 and tokens when login is successful', async () => {
            const mockResponse = {
                error: false,
                statusCode: 200,
                message: 'Login successful',
                data: {
                    user: { userId: 1, username: 'testuser' },
                    accessToken: 'access-token',
                    refreshToken: 'refresh-token',
                },
            };
            authService.login.mockResolvedValue(mockResponse);
            generateAuthResponse.mockReturnValue({});

            const response = await request(app)
                .post('/login')
                .send({ email: 'test@example.com', password: 'password' });

            expect(response.status).toBe(200);
            expect(authService.login).toHaveBeenCalledWith('test@example.com', 'password');
        });

        it('should return 404 when user is not found', async () => {
            const mockResponse = {
                error: true,
                statusCode: 404,
                message: 'User not found',
            };
            authService.login.mockResolvedValue(mockResponse);

            const response = await request(app)
                .post('/login')
                .send({ email: 'nonexistent@example.com', password: 'password' });

            expect(response.status).toBe(404);
            expect(sendErrorResponse).toHaveBeenCalledWith(response.res, 404, 'User not found');
        });
    });

    describe('POST /signup', () => {
        it('should return 201 when signup is successful', async () => {
            const mockResponse = {
                error: false,
                statusCode: 201,
                message: 'User created successfully',
                data: {
                    user: { userId: 1, username: 'testuser' },
                    accessToken: 'access-token',
                    refreshToken: 'refresh-token',
                },
            };
            authService.signup.mockResolvedValue(mockResponse);
            generateAuthResponse.mockReturnValue({});

            const response = await request(app)
                .post('/signup')
                .send({ email: 'newuser@example.com', password: 'password', username: 'newuser' });

            expect(response.status).toBe(201);
            expect(authService.signup).toHaveBeenCalledWith(
                'newuser@example.com',
                'password',
                'newuser'
            );
        });

        it('should return 400 when user already exists', async () => {
            const mockResponse = {
                error: true,
                statusCode: 400,
                message: 'User already exists',
            };
            authService.signup.mockResolvedValue(mockResponse);

            const response = await request(app).post('/signup').send({
                email: 'existing@example.com',
                password: 'password',
                username: 'existinguser',
            });

            expect(response.status).toBe(400);
            expect(sendErrorResponse).toHaveBeenCalledWith(
                response.res,
                400,
                'User already exists'
            );
        });
    });

    describe('POST /send-verification-code', () => {
        it('should return 200 when verification code is sent successfully', async () => {
            const mockResponse = {
                error: false,
                statusCode: 200,
                message: 'Verification code sent successfully',
            };
            authService.sendVerificationCode.mockResolvedValue(mockResponse);

            const response = await request(app)
                .post('/send-verification-code')
                .send({ email: 'test@example.com' });

            expect(response.status).toBe(200);
            expect(authService.sendVerificationCode).toHaveBeenCalledWith('test@example.com');
        });
    });

    describe('POST /verify-code', () => {
        it('should return 200 when code is valid', async () => {
            const mockResponse = {
                error: false,
                statusCode: 200,
                message: 'Code verified successfully',
            };
            authService.verifyCode.mockResolvedValue(mockResponse);

            const response = await request(app)
                .post('/verify-code')
                .send({ email: 'test@example.com', code: '12345678' });

            expect(response.status).toBe(200);
            expect(authService.verifyCode).toHaveBeenCalledWith('test@example.com', '12345678');
        });

        it('should return 400 when code is invalid', async () => {
            const mockResponse = {
                error: true,
                statusCode: 400,
                message: 'Invalid code',
            };
            authService.verifyCode.mockResolvedValue(mockResponse);

            const response = await request(app)
                .post('/verify-code')
                .send({ email: 'test@example.com', code: 'wrongcode' });

            expect(response.status).toBe(400);
            expect(sendErrorResponse).toHaveBeenCalledWith(response.res, 400, 'Invalid code');
        });
    });

    describe('POST /update-profile', () => {
        beforeEach(() => {
            authenticate.mockImplementation((req, res, next) => next());
            upload.mockImplementation((req, res, next) => next());
        });

        it('should return 200 when profile is updated successfully', async () => {
            const mockResponse = {
                error: false,
                statusCode: 200,
                message: 'Profile updated successfully',
            };
            authService.updateProfile.mockResolvedValue(mockResponse);

            const response = await request(app)
                .post('/update-profile')
                .field('username', 'newusername')
                .field('statusMessage', 'newstatus')
                .field('newPassword', 'newpassword')
                .attach('profilePhoto', './cat.jpeg')
                .set('Authorization', 'Bearer access-token');

            expect(response.status).toBe(200);
            expect(authService.updateProfile).toHaveBeenCalled();
        });
    });

    describe('DELETE /delete-user', () => {
        beforeEach(() => {
            authenticate.mockImplementation((req, res, next) => next());
        });

        it('should return 200 when user is deleted successfully', async () => {
            const mockResponse = {
                error: false,
                statusCode: 200,
                message: 'User deleted successfully',
            };
            authService.deleteUser.mockResolvedValue(mockResponse);

            const response = await request(app)
                .delete('/delete-user')
                .set('Authorization', 'Bearer access-token');

            expect(response.status).toBe(200);
            expect(authService.deleteUser).toHaveBeenCalled();
        });
    });
});
