const sharp = require('sharp');

const inputPath = process.argv[2];
const cutOffHeight = 40;

if (!inputPath) {
    console.error('Please provide an input file path.');
    process.exit(1);
}

sharp("src/main/resources/public/images/" + inputPath + ".png")
    .metadata()
    .then(({ width, height }) => {
        const newHeight = height - cutOffHeight;
        return sharp("src/main/resources/public/images/" + inputPath + ".png")
            .extract({ left: 0, top: 0, width, height: newHeight })
            .toFile("src/main/resources/public/images/" + inputPath + "p.png");
    })
    .then(() => {
    })
    .catch(err => {
    });
