const b = require('bcryptjs');
const h = b.hashSync('Bhagya@123', 10);
const sql = "USE user_db;\nUPDATE users SET password='" + h + "' WHERE email LIKE '%omnihealth.com';\n";
require('fs').writeFileSync('../fix_passwords.sql', sql);
console.log('Done. Hash length: ' + h.length + ' hash: ' + h);
