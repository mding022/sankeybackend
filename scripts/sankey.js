const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

function delay(time) {
    return new Promise((resolve) => { 
        setTimeout(resolve, time);
    });
}

const runSankeyAutomation = async (inputData, size) => {
    const downloadPath = __dirname;

    const browser = await puppeteer.launch({
        headless: true,
        args: [
            '--disable-extensions',
            '--no-sandbox',
            '--disable-setuid-sandbox',
        ],
        executablePath: '/usr/bin/chromium-browser'
    });

    const page = await browser.newPage();

    const client = await page.target().createCDPSession();
    await client.send('Page.setDownloadBehavior', {
        behavior: 'allow',
        downloadPath: downloadPath,
    });

    await page.goto('https://sankeymatic.com/build/');

    await page.evaluate((data) => {
        document.getElementById('flows_in').value = data;
    }, inputData);

    await page.evaluate(() => {
        const inputField = document.getElementById('flows_in');
        inputField.dispatchEvent(new Event('input', { bubbles: true }));
    });

    await page.type('#flows_in', ' ', { delay: 100 });

    await page.evaluate((w, h) => {
        document.getElementById('size_w').value = w;
        document.getElementById('size_h').value = h;
        document.getElementById('size_w').dispatchEvent(new Event('change'));
        document.getElementById('size_h').dispatchEvent(new Event('change'));
    }, size, size);

    const checkbox = await page.$('#bg_transparent');
    if (checkbox) {
        await checkbox.click();
    }

    const saveButton = await page.$('#save_as_png_2x');
    await saveButton.click();

    await delay(1500);

    await browser.close();
    return 'Success';
};

const uuid = process.argv[3] ? process.argv[3] : 'default-uuid';

const inputFilePath = path.join(__dirname, uuid +'.txt');

const size = process.argv[2] ? parseInt(process.argv[2], 10) : 400; 

fs.readFile(inputFilePath, 'utf8', (err, inputData) => {
    if (err) {
        console.error('Error reading the input file:', err);
        process.exit(1);
    }

    runSankeyAutomation(inputData, size).then(console.log).catch(console.error);
});
