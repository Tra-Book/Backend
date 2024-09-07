const jwt = require('jsonwebtoken');

const genAccessToken = (userId) => {
    const payload = { userId: userId };
    return jwt.sign(payload, process.env.ACCESS_TOKEN_SECRET, {
        expiresIn: '1h',
        notBefore: '0',
        algorithm: 'HS256',
        // issuer
        // audience
    });
};

const genRefreshToken = () => {
    return jwt.sign({}, process.env.REFRESH_TOKEN_SECRET, {
        expiresIn: '3d',
        notBefore: '0',
        algorithm: 'HS256',
        // issuer
        // audience
    });
};

exports.genAccessToken = genAccessToken;
exports.genRefreshToken = genRefreshToken;