const express = require('express');
const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');

const corsConfig = require('./config/corsConfig');
const authRoutes = require('./routes/auth');

const app = express();

app.use(bodyParser.json({ limit: '10mb' }));
app.use(cookieParser());
app.use(corsConfig);
app.options('*', corsConfig);

app.use('/auth', authRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});

module.exports = app;
