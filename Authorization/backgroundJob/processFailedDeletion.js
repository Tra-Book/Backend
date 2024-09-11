const fs = require('fs');
const readline = require('readline');

async function removeImage(url) {
    try {
        const fileName = url.split('/').pop();
        const file = storage.bucket(config.bucketName).file('profilePhoto/' + fileName);
        await file.delete();
    } catch (error) {
        console.error('Error deleting file:', error);
        throw error;
    }
}

async function processFailedDeletions() {
    const fileStream = fs.createReadStream(failedDeletionsLogFile);
    const rl = readline.createInterface({
        input: fileStream,
        crlfDelay: Infinity,
    });

    const successfulDeletions = [];
    for await (const line of rl) {
        const url = line.split(' - ')[1].trim();
        try {
            await removeImage(url);
            successfulDeletions.push(line);
        } catch (err) {
            console.error(`Failed to delete ${url} again:`, err);
        }
    }

    if (successfulDeletions.length > 0) {
        const logData = fs.readFileSync(failedDeletionsLogFile, 'utf-8');
        const updatedLogData = logData
            .split('\n')
            .filter((line) => !successfulDeletions.includes(line))
            .join('\n');
        fs.writeFileSync(failedDeletionsLogFile, updatedLogData);
    }
}

// Call this function in your background job or cron job
processFailedDeletions()
    .then(() => {
        console.log('Processing of failed deletions complete.');
    })
    .catch((err) => {
        console.error('Error during processing of failed deletions:', err);
    });
