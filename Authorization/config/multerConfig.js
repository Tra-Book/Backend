require('dotenv').config();
const path = require('path');

module.exports = {
    bucketName: process.env.GCS_BUCKET_NAME,
    projectId: process.env.GOOGLE_CLOUD_PROJECT,
    keyFilename: path.join(__dirname, '..', '.secure', process.env.GOOGLE_APPLICATION_CREDENTIALS),
};
