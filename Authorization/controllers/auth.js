const bcrypt = require('bcryptjs');
const { OAuth2Client } = require('google-auth-library');

const User = require('../models/user');
const generateToken = require('../util/token');


exports.postLogin = (req, res, next) => {
    const email = req.body.email;
    const password = req.body.password;
    User.getUserByEmail(email).then((user) => {
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        bcrypt
            .compare(password, user.password)
            .then((doMatch) => {
                if (doMatch) {
                    const accessToken = generateToken.genAccessToken(user.email);
                    const refreshToken = generateToken.genRefreshToken();
                    return res
                        .status(200)
                        .cookie('refreshToken', refreshToken, {
                            expires: new Date(Date.now() + 259200000),
                            httpOnly: true,
                        })
                        .header('Authorization', accessToken)
                        .json({ user_id: user.user_id, username: user.username });
                }
                return res.status(401).json({ message: 'Incorrect password' });
            });
    }).catch((err) => {
        return res.status(500).json({ message: 'Server error' });
    });
};

exports.postSignup = (req, res, next) => {
    const email = req.body.email;
    const password = req.body.password;
    const username = req.body.username;
    User.getUserByEmail(email)
        .then((user) => {
            if (user) {
                return res.status(400).json({ message: 'User already exists' });
            }
        })
        .catch((err) => {
            return res.status(500).json({ message: 'Server error' });
        });
    bcrypt
        .hash(password, 12)
        .then((hashedPassword) => {
            const user = new User({
                username: username,
                email: email,
                password: hashedPassword,
            });
            return user.save();
        })
        .then((result) => {
            const accessToken = generateToken.genAccessToken(email);
            const refreshToken = generateToken.genRefreshToken();
            return res
                .status(201)
                .cookie('refreshToken', refreshToken, {
                    expires: new Date(Date.now() + 259200000),
                    httpOnly: true,
                })
                .header('Authorization', accessToken)
                .json({ email: email, username: username });
        })
        .catch((err) => {
            console.log(err);
            return res.status(500).json({ message: 'Server error' });
        });
};

exports.postGoogleLogin = async (req, res) => {
    const client = new OAuth2Client();
    const CLIENT_ID = '';
    const email = req.body.email;
    const token = req.body.access_token;

    // verify token and email
    try {
        const ticket = await client
            .verifyIdToken({
                idToken: token,
                audience: CLIENT_ID,
            });

        const payload = ticket.getPayload();
        const tokenEmail = payload['email'];
        if (email != tokenEmail) {
            return res.status(400).json({ message: 'Bad request' });
        }
    } catch (err) {
        console.log(err);
        return res.status(500).json({ message: 'Google API server error' });
    }

    User.getUserByEmail(email).then((user) => {
        if (!user) {
            // signup
            const username = '여행자' +
                generator.generate({
                    length: 8,
                    numbers: true,
                });
            const password = generator.generate({
                length: 14,
                numbers: true,
                symbols: true,
                strict: true,
            });
            bcrypt.hash(password, 12).then((hashedPassword) => {
                const newUser = new User({
                    username: username,
                    email: email,
                    password: hashedPassword,
                });
                newUser
                    .save()
                    .then((result) => {
                        const accessToken =
                            token.genAccessToken(email);
                        const refreshToken = token.genRefreshToken();
                        return res
                            .status(201)
                            .cookie('refreshToken', refreshToken, {
                                expires: new Date(Date.now() + 259200000),
                                httpOnly: true,
                            })
                            .header('Authorization', accessToken)
                            // TODO: user_id
                            .json({ user_id: result.user_id, username: username });
                    });
            })
                .catch((err) => {
                    console.log(err);
                    return res
                        .status(500)
                        .json({ message: 'Server error' });
                });
        } else {
            // login
            const accessToken = generateToken.genAccessToken(user.email);
            const refreshToken = generateToken.genRefreshToken();
            return res
                .status(200)
                .cookie('refreshToken', refreshToken, {
                    expires: new Date(Date.now() + 259200000),
                    httpOnly: true,
                })
                .header('Authorization', accessToken)
                .json({ user_id: user.user_id, username: user.username });
        }
    }).catch((err) => {
        return res.status(500).json({ message: 'Server error' });
    });
};


exports.postKakaoAuth = async (req, res, next) => {
    const email = req.body.email;
    const token = req.body.access_token;

    // verify token and email
    try {
        const userInfoResponse = await axios.get('https://kapi.kakao.com/v2/user/me', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        const tokenEmail = userInfoResponse.data.kakao_account.email;
        if (email != tokenEmail) {
            return res.status(400).json({ message: 'Bad request' });
        }
    } catch (err) {
        console.log(err);
        return res.status(500).json({ message: 'Naver API Server error' });
    }

    User.getUserByEmail(email).then((user) => {
        if (!user) {
            // signup
            const username = '여행자' +
                generator.generate({
                    length: 8,
                    numbers: true,
                });
            const password = generator.generate({
                length: 14,
                numbers: true,
                symbols: true,
                strict: true,
            });
            bcrypt.hash(password, 12).then((hashedPassword) => {
                const newUser = new User({
                    username: username,
                    email: email,
                    password: hashedPassword,
                });
                newUser
                    .save()
                    .then((result) => {
                        const accessToken = generateToken.genAccessToken(email);
                        const refreshToken = generateToken.genRefreshToken();
                        return res
                            .status(201)
                            .cookie('refreshToken', refreshToken, {
                                expires: new Date(Date.now() + 259200000),
                                httpOnly: true,
                            })
                            .header('Authorization', accessToken)
                            // TODO
                            .json({ user_id: result.user_id, username: username });
                    });
            })
                .catch((err) => {
                    console.log(err);
                    return res.status(500).json({ message: 'Server error' });
                });
        } else {
            // login
            const accessToken = generateToken.genAccessToken(user.email);
            const refreshToken = generateToken.genRefreshToken();
            return res
                .status(200)
                .cookie('refreshToken', refreshToken, {
                    expires: new Date(Date.now() + 259200000),
                    httpOnly: true,
                })
                .header('Authorization', accessToken)
                .json({ user_id: user.user_id, username: user.username });
        }
    }).catch((err) => {
        return res.status(500).json({ message: 'Server error' });
    });
};

exports.postNaverLogin = async (req, res, next) => {
    const email = req.body.email;
    const token = req.body.access_token;

    try {
        const info = await axios.get('https://openapi.naver.com/v1/nid/me', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        const tokenEmail = info.data.response.email;

        if (email != tokenEmail) {
            return res.status(400).json({ message: 'Bad request' });
        }
    } catch (err) {
        console.log(err);
        return res.status(500).json({ message: 'Naver API Server error' });
    }

    User.getUserByEmail(email).then((user) => {
        if (!user) {
            // signup
            const username = '여행자' +
                generator.generate({
                    length: 8,
                    numbers: true,
                });
            const password = generator.generate({
                length: 14,
                numbers: true,
                symbols: true,
                strict: true,
            });
            bcrypt.hash(password, 12).then((hashedPassword) => {
                const newUser = new User({
                    username: username,
                    email: email,
                    password: hashedPassword,
                });
                newUser.save().then((result) => {
                    const accessToken = generateToken.genAccessToken(email);
                    const refreshToken = generateToken.genRefreshToken();
                    return res
                        .status(201)
                        .cookie('refreshToken', refreshToken, {
                            expires: new Date(Date.now() + 259200000),
                            httpOnly: true,
                        })
                        .header('Authorization', accessToken)
                        // TODO
                        .json({ user_id: result.user_id, username: username });
                });
            });
        } else {
            // login
            const accessToken = generateToken.genAccessToken(user.email);
            const refreshToken = generateToken.genRefreshToken();
            return res
                .status(200)
                .cookie('refreshToken', refreshToken, {
                    expires: new Date(Date.now() + 259200000),
                    httpOnly: true,
                })
                .header('Authorization', accessToken)
                .json({ user_id: user.user_id, username: user.username });
        }
    }).catch((err) => {
        return res.status(500).json({ message: 'Server error' });
    });
};

exports.postUpdateProfile = (req, res, next) => {
    const username = req.body.username;
    const email = req.body.email;
    const status_message = req.body.status_message;
    const new_password = req.body.new_password;

    User.getUserByEmail(email).then((user) => {
        bcrypt.hash(new_password, 12).then((hashedPassword) => {
            user.updateProfile(
                username = username,
                status_message = status_message,
                hashedPassword = hashedPassword,
            ).then(() => {
                return res.status(200).json({ message: 'Success' });
            });
        });
    }).catch((err) => {
        return res.status(500).json({ message: 'Server error' });
    });
};

exports.deleteUserData = (req, res, next) => {
    req.user
        .deleteUser()
        .then((result) => {
            return res.status(200).json({ message: 'Success signout' });
        })
        .catch((err) => {
            return res.status(500).json({ message: 'Server error' });
        });
};