const express = require('express');
const bodyParser = require('body-parser');
const postRoutes = require('./routes/postRoutes');
const commentRoutes = require('./routes/commentRoutes');
// const mongoose = require('mongoose');
require('dotenv').config(); //.env 파일에 정의된 환경 변수를 process.env 객체에 로드하여 애플리케이션이 실행될 때 이를 사용할 수 있도록

const app = express();

app.use(bodyParser.json());

// mongoose.connect(process.env.DATABASE_URL, { useNewUrlParser: true, useUnifiedTopology: true });

app.use('/posts', postRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});

