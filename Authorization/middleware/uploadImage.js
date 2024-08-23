const { upload } = require('../utils/multerUtil');

const uploadImage = (req, res, next) => {
    upload.single('image')(req, res, (err) => {
        if (err) {
            if (err instanceof multer.MulterError) {
                return res.status(400).json({ message: `Multer error: ${err.message}` });
            } else {
                return res.status(500).json({ message: 'Failed to upload image' });
            }
        }
        next();
    });
};

module.exports = uploadImage;
