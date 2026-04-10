const crypto = require('crypto');

// PayHere notification signature generation
function generateNotificationSignature(merchantId, orderId, amount, currency, statusCode, merchantSecret) {
    const merchantSecretHash = crypto.createHash('md5').update(merchantSecret).digest('hex').toUpperCase();
    const plain = merchantId + orderId + amount + currency + statusCode + merchantSecretHash;
    return crypto.createHash('md5').update(plain).digest('hex').toUpperCase();
}

// Your PayHere configuration
const merchantId = '1235079';
const orderId = 'MED-2024-002';
const amount = '2500.00';
const currency = 'LKR';
const statusCode = '2';
const merchantSecret = 'pw';

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
