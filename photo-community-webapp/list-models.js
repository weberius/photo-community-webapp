const https = require('https');

const apiKey = 'YOUR_API_KEY';
const url = `https://generativelanguage.googleapis.com/v1beta/models?key=${apiKey}`;

https.get(url, (res) => {
    let data = '';

    res.on('data', (chunk) => {
        data += chunk;
    });

    res.on('end', () => {
        try {
            const response = JSON.parse(data);
            if (response.models) {
                console.log('Available Models:');
                response.models.forEach(model => {
                    console.log(`- ${model.name} (${model.supportedGenerationMethods.join(', ')})`);
                });
            } else {
                console.log('Response:', response);
            }
        } catch (e) {
            console.error('Error parsing response:', e);
        }
    });

}).on('error', (err) => {
    console.error('Error:', err.message);
});
