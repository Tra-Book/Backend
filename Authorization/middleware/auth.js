const jwt = require('jsonwebtoken');
const User = require('../models/user');

const authenticate = async (req, res, next) => {
    const accessToken = req.headers['authorization'];

    if (!accessToken) {
        return res.status(403).json({ message: 'Authentication required: no access token' });
    }

    try {
        const accessDecoded = jwt.verify(accessToken, process.env.ACCESS_TOKEN_SECRET, {
            complete: true,
            algorithms: ['HS256'],
            clockTolerance: 0,
            ignoreExpiration: false,
            ignoreNotBefore: false,
        });

        const user = await User.getUserByUserId(accessDecoded.payload.userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        req.user = new User(user);
        return next();
    } catch (err) {
        return res
            .status(403)
            .json({ message: 'Authentication required: invalid or expired access token' });
    }
};

module.exports = authenticate;
