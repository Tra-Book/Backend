const cors = require('cors');
require('dotenv').config();

const corsOptions = {
  origin: process.env.CORS_ORIGIN,
  methods: process.env.CORS_METHODS,
  allowedHeaders: process.env.CORS_ALLOWED_HEADERS,
  credentials: process.env.CORS_CREDENTIALS,
  optionsSuccessStatus: 200
};

module.exports = cors(corsOptions);
