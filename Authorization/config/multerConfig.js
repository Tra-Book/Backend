require('dotenv').config();

module.exports = {
    bucketName: process.env.GCS_BUCKET_NAME,
    projectId: process.env.GOOGLE_CLOUD_PROJECT,
    keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
}