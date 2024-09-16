const bcrypt = require('bcryptjs');

exports.comparePassword = async (plainTextPassword, hashedPassword) => {
    return await bcrypt.compare(plainTextPassword, hashedPassword);
};

exports.hashPassword = async (password) => {
    return await bcrypt.hash(password, 12);
};
