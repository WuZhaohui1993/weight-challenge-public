const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

const tabbarDir = path.join(__dirname, 'src/static/tabbar');

// 图标配置
const icons = [
    {
        name: 'home',
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#999999" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`,
        activeSvg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#4A90E2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`
    },
    {
        name: 'record',
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#999999" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>`,
        activeSvg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#4A90E2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>`
    },
    {
        name: 'circle',
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#999999" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="2" y1="12" x2="22" y2="12"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></svg>`,
        activeSvg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#4A90E2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="2" y1="12" x2="22" y2="12"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></svg>`
    },
    {
        name: 'profile',
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#999999" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
        activeSvg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="81" height="81" fill="none" stroke="#4A90E2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`
    }
];

async function generateIcons() {
    // 确保目录存在
    if (!fs.existsSync(tabbarDir)) {
        fs.mkdirSync(tabbarDir, { recursive: true });
    }

    for (const icon of icons) {
        // 生成普通图标
        await sharp(Buffer.from(icon.svg))
            .png()
            .toFile(path.join(tabbarDir, `${icon.name}.png`));
        console.log(`Created ${icon.name}.png`);

        // 生成激活图标
        await sharp(Buffer.from(icon.activeSvg))
            .png()
            .toFile(path.join(tabbarDir, `${icon.name}_active.png`));
        console.log(`Created ${icon.name}_active.png`);
    }

    console.log('All icons generated successfully!');
}

generateIcons().catch(console.error);
