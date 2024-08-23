const { Storage } = require('@google-cloud/storage');
const multer = require('multer');
const multerGoogleStorage = require('multer-google-storage');
const config = require('../config/multerConfig');

const storage = new Storage({
    projectId: config.projectId,
    keyFilename: config.keyFilename,
});

const upload = multer({
    storage: multerGoogleStorage.storageEngine({
        bucket: config.bucketName,
        projectId: config.projectId,
        keyFilename: config.keyFilename,
        acl: 'publicRead',
        filename: (req, file, cb) => {
            const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
            cb(null, 'profilePhoto/' + uniqueSuffix + '-' + file.originalname);
        },
    }),
});

module.exports = {
    upload,
    storage,
};
