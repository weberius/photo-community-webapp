import { GoogleGenerativeAI } from '@google/generative-ai';

const apiKey = 'YOUR_API_KEY';
const genAI = new GoogleGenerativeAI(apiKey);

async function listModels() {
    try {
        const model = genAI.getGenerativeModel({ model: 'gemini-1.5-flash' });
        // There isn't a direct "listModels" on the client SDK easily accessible without using the model manager which might not be exposed in the same way in the web SDK vs node.
        // Actually, for the node SDK (which we are using here), we can use the GoogleAIFileManager or similar, but the core SDK might not have listModels directly on the entry point in older versions.
        // However, let's try to just run a simple generation with 'gemini-1.5-flash' to see if it works in Node.

        console.log('Testing gemini-1.5-flash...');
        const result = await model.generateContent('Hello');
        console.log('Success! Response:', result.response.text());
    } catch (error) {
        console.error('Error with gemini-1.5-flash:', error.message);
    }

    try {
        console.log('Testing gemini-pro...');
        const modelPro = genAI.getGenerativeModel({ model: 'gemini-pro' });
        const resultPro = await modelPro.generateContent('Hello');
        console.log('Success with gemini-pro! Response:', resultPro.response.text());
    } catch (error) {
        console.error('Error with gemini-pro:', error.message);
    }
}

listModels();
