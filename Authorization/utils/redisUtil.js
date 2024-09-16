const redis = require('redis');

let redisClient;

(async () => {
    redisClient = redis.createClient();

    redisClient.on('connect', () => {
        console.log('Connected to Redis...');
    });

    redisClient.on('error', (err) => {
        console.error('Redis error:', err);
    });

    await redisClient.connect();
})();

module.exports = redisClient;
