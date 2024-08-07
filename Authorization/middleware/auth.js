const jwt = require('jsonwebtoken');

const User = require('../models/user');
const token = require('../util/token');

// Access Token (AT), Refresh Token (RT) 확인해서 route 보호
// 정상적으로 확인된 유저를 req.user에 저장
const authenticate = (req, res, next) => {
    const accessToken = req.headers['authorization']
        ? req.headers['authorization'].split(' ')[1]
        : null;
    const refreshToken = req.cookies['refreshToken'];

    if (!accessToken || !refreshToken) {
        // AT나 RT가 없음
        return res.status(403).json({ message: 'Authentication required: no AT or RT' });
    }
    jwt.verify(
        accessToken,
        process.env.ACCESS_TOKEN_SECRET,
        {
            complete: true,
            algorithms: ['HS256'],
            clockTolerance: 0,
            ignoreExpiration: false,
            ignoreNotBefore: false,
        },
        (err, accessDecoded) => {
            if (err) {
                // AT invalid or expired
                jwt.verify(
                    accessToken,
                    process.env.ACCESS_TOKEN_SECRET,
                    {
                        complete: true,
                        algorithms: ['HS256'],
                        clockTolerance: 0,
                        ignoreExpiration: true,
                        ignoreNotBefore: false,
                    },
                    (err, accessDecoded) => {
                        if (err) {
                            // AT invalid
                            return res
                                .status(403)
                                .json({ message: 'Authentication required: AT invalid' });
                        }
                        // AT expired
                        jwt.verify(
                            refreshToken,
                            process.env.REFRESH_TOKEN_SECRET,
                            {
                                complete: true,
                                algorithms: ['HS256'],
                                clockTolerance: 0,
                                ignoreExpiration: false,
                                ignoreNotBefore: false,
                            },
                            (err, refreshDecoded) => {
                                if (err) {
                                    // AT expired && RT invalid or expired
                                    return res.status(403).json({
                                        message:
                                            'Authentication required: AT expired and RT invalid or expired',
                                    });
                                }
                                // AT expired && RT valid -> AT 재발급
                                const newAccessToken = token.genAccessToken(
                                    accessDecoded.payload.email
                                );
                                return res
                                    .status(403)
                                    .cookie('refreshToken', refreshToken, {
                                        expires: new Date(Date.now() + 259200000),
                                        httpOnly: true,
                                    })
                                    .header('Authorization', newAccessToken)
                                    .json({ message: 'Renewed expired access token' });
                            }
                        );
                    }
                );
            } else {
                // AT valid
                User.getUserByEmail(accessDecoded.payload.email).then((user) => {
                    if (!user) {
                        return res.status(404).json({ message: 'User not found' });
                    }
                    req.user = new User(user);
                    return next();
                });
            }
        }
    );
};

module.exports = authenticate;