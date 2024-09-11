const { Storage } = require('@google-cloud/storage');
const multer = require('multer');
const config = require('../config/multerConfig');
const fs = require('fs');
const path = require('path');

const storage = new Storage({
    projectId: config.projectId,
    keyFilename: config.keyFilename,
});

const upload = multer({
    storage: multer.memoryStorage(),
    limits: {
        fileSize: 5 * 1024 * 1024,
    },
    // multerGoogleStorage.storageEngine({
    //     bucket: config.bucketName,
    //     projectId: config.projectId,
    //     keyFilename: config.keyFilename,
    //     filename: (req, file, cb) => {
    //         const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
    //         cb(null, 'profilePhoto/' + uniqueSuffix + '-' + file.originalname);
    //     },
    // }),
});

async function uploadToGCS(file) {
    return new Promise((resolve, reject) => {
        if (!file) {
            resolve(null);
        }

        const bucket = storage.bucket(config.bucketName);
        const gcsFileName = `profilePhoto/${Date.now()}-${file.originalname}`;
        const fileUpload = bucket.file(gcsFileName);

        const stream = fileUpload.createWriteStream({
            metadata: {
                contentType: file.mimetype,
            },
        });

        stream.on('error', (err) => {
            reject(err);
        });

        stream.on('finish', async () => {
            try {
                await fileUpload.makePublic();

                resolve(`https://storage.googleapis.com/${config.bucketName}/${gcsFileName}`);
            } catch (err) {
                reject(`Failed to make file public: ${err.message}`);
            }
        });

        stream.end(file.buffer);
    });
}

async function removeFromGCS(filePath) {
    try {
        const fileName = filePath.split('/').pop();
        const file = storage.bucket(config.bucketName).file('profilePhoto/' + fileName);
        await file.delete();
    } catch (error) {
        console.log(error);
        await logFailedDeletion(filePath);
    }
}

async function logFailedDeletion(url) {
    const failedDeletionsLogFile = path.join(__dirname, '..', 'logs', 'failed_deletions.log');
    const logEntry = `${new Date().toISOString()} - ${url}\n`;
    fs.appendFile(failedDeletionsLogFile, logEntry, (err) => {
        if (err) {
            console.error('Failed to log deletion error:', err);
        }
    });
}

async function generateSignedUrl(filename) {
    const options = {
        version: 'v4',
        action: 'read',
        expires: Date.now() + 1000 * 60 * 60,
    };

    const [url] = await storage.bucket(config.bucketName).file(filename).getSignedUrl(options);
    return url;
}

module.exports = {
    upload,
    storage,
    uploadToGCS,
    generateSignedUrl,
    removeFromGCS,
};
