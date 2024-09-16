const nodemailer = require('nodemailer');
const emailConfig = require('../config/emailConfig');

const transporter = nodemailer.createTransport(emailConfig);

exports.sendVerificationEmail = async (email, verificationCode) => {
    const mailOptions = {
        from: '"Trabook" <trabook24@gmail.com>',
        to: email,
        subject: '이메일 인증 코드',
        html: getVerificationEmailHtml(verificationCode),
        text: getVerificationEmailText(verificationCode),
    };
    
    try {
        await transporter.sendMail(mailOptions);
        return true;
    } catch (error) {
        console.log(error);
        return false;
    }
};

const getVerificationEmailHtml = (code) => `
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e1e1e1; border-radius: 10px; background-color: #f9f9f9;">
        <h2 style="text-align: center; color: #4CAF50;">이메일 인증</h2>
        <p>안녕하세요,</p>
        <p><strong>Trabook</strong> 계정의 이메일 인증을 요청하셨습니다.</p>
        <p>아래 인증 코드를 입력하셔서 이메일 주소를 확인해주세요:</p>
        <br>
        <p style="font-size: 24px; font-weight: bold; text-align: center; color: #333;">${code}</p>
        <br>
        <p>만약 이 요청을 본인이 하지 않으셨다면, 이 이메일을 무시하셔도 됩니다.</p>
        <p>감사합니다.<br><br>Trabook 팀 드림</p>
        <hr style="border: 0; border-top: 1px solid #e1e1e1; margin: 20px 0;">
        <p style="font-size: 12px; color: #888; text-align: center;">이 이메일에 회신하지 마세요. 도움이 필요하시면 trabook24@gmail.com으로 연락주시기 바랍니다.</p>
    </div>
`;

const getVerificationEmailText = (code) => `
    안녕하세요,

    Trabook 계정의 이메일 인증을 요청하셨습니다.

    인증 코드: ${code}

    만약 이 요청을 본인이 하지 않으셨다면, 이 이메일을 무시하셔도 됩니다.

    감사합니다.
    Trabook 팀 드림
`;
