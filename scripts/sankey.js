const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

function delay(time) {
    return new Promise((resolve) => { 
        setTimeout(resolve, time);
    });
}

const runSankeyAutomation = async (inputData) => {
    const downloadPath = __dirname;

    const browser = await puppeteer.launch({
        headless: false,
        args: [
            '--disable-extensions',
            '--no-sandbox',
            '--disable-setuid-sandbox',
        ],
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

    const saveButton = await page.$('#save_as_png_2x');
    await saveButton.click();

    await delay(1500);

    await browser.close();
    return 'Success';
};

const inputFilePath = path.join(__dirname, 'input.txt');

fs.readFile(inputFilePath, 'utf8', (err, inputData) => {
    if (err) {
        console.error('Error reading the input file:', err);
        process.exit(1);
    }

    runSankeyAutomation(inputData).then(console.log).catch(console.error);
});
