const crypto = require('crypto');

// PayHere notification signature generation
function generateNotificationSignature(merchantId, orderId, amount, currency, statusCode, merchantSecret) {
    const merchantSecretHash = crypto.createHash('md5').update(merchantSecret).digest('hex').toUpperCase();
    const plain = merchantId + orderId + amount + currency + statusCode + merchantSecretHash;
    return crypto.createHash('md5').update(plain).digest('hex').toUpperCase();
}

// Your PayHere configuration
const merchantId = '1210001';
const orderId = 'MED-2024-002';
const amount = '2500.00';
const currency = 'LKR';
const statusCode = '2';
const merchantSecret = 'NDc3NjI5MzY5NDk3ODU4NzY1NTcwNjU1MjA4MDM2NzE4NjU1NzA=';

// Generate the signature
const signature = generateNotificationSignature(merchantId, orderId, amount, currency, statusCode, merchantSecret);

console.log('Generated Signature:', signature);
console.log('Parameters:', {
    merchantId,
    orderId,
    amount,
    currency,
    statusCode,
    merchantSecret
});
