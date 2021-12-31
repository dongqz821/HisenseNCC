/*
 * @Author: liyxt
 * @Date: 2019-12-09 19:54:41
 * @LastEditors  : liyxt
 * @LastEditTime : 2019-12-31 09:44:54
 * @Description: file content
 */
const configJSON = require('../config.json');
const fs = require('fs');
const { resolve, join, sep } = require('path');
const { spawn } = require('child_process');
const yazl = require('yazl');

var zipfile = new yazl.ZipFile();
var patchConfig = configJSON.patch || {};

// 先删除dist目录
delDir('./dist');
delDir('./patch');

let folderSet = new Set();

// windows下npm执行名不同
const ls = spawn(process.platform === 'win32' ? 'npm.cmd' : 'npm', [
	'run',
	'test',
	...(patchConfig.path || []).map(e => '--env.buildPath=' + e)
]);
ls.stdout.on('data', data => {
	if (data.includes('ERROR')) {
		throw new Error(data);
	} else {
		data && console.log(`${data}`);
	}
});

ls.stderr.on('data', data => {
	if (data.includes('ERROR')) {
		throw new Error(data);
	} else {
		data && console.log(`${data}`);
	}
});

ls.on('close', code => {
	folderSet.clear();
	// 加入到zip入口
	addEntry(resolve(__dirname, '../dist'));
	// 动态修改xml
	let xmlconfig = {
		id: uuid(),
		provider: patchConfig.provider,
		department: patchConfig.department,
		needRecreatedLoginJar: false,
		needDeploy: false,
		time: dateFormat('YYYY-mm-dd HH:MM:SS', new Date()),
		patchKey: [...folderSet].join(',')
	};
	let xml = fs.readFileSync(resolve(__dirname, '../config/packmetadata.xml'), 'utf-8');

	Object.entries(xmlconfig).forEach(([key, value]) => {
		xml = xml.replace(`<!-- ${key} -->`, `<${key}>${value}</${key}>`);
	});

	fs.writeFileSync(resolve(__dirname, '../dist/packmetadata.xml'), xml, 'utf-8');
	zipfile.addFile('./dist/packmetadata.xml', 'packmetadata.xml');

	zipfile.outputStream.pipe(fs.createWriteStream(`patch_${new Date().getTime()}.zip`)).on('close', function() {
		console.log('补丁已出！');
	});
	zipfile.end();
});

function delDir(path) {
	let files = [];
	if (fs.existsSync(path)) {
		files = fs.readdirSync(path);
		files.forEach(file => {
			let curPath = path + '/' + file;
			if (fs.statSync(curPath).isDirectory()) {
				delDir(curPath); //递归删除文件夹
			} else {
				fs.unlinkSync(curPath); //删除文件
			}
		});
		fs.rmdirSync(path);
	}
}

function uuid() {
	var s = [];
	var hexDigits = '0123456789abcdef';
	for (var i = 0; i < 36; i++) {
		s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
	}
	s[14] = '4'; // bits 12-15 of the time_hi_and_version field to 0010
	s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
	s[8] = s[13] = s[18] = s[23] = '-';

	var uuid = s.join('');
	return uuid;
}

function addEntry(prefix = './dist') {
	//读取目录
	var paths = fs.readdirSync(prefix);
	paths.forEach(function(path) {
		var from = join(prefix, path);
		var st = fs.statSync(from);
		if (st.isFile()) {
			let folder = '/hotwebs/nccloud/resources/' + prefix.split(`${sep}dist${sep}`)[1];
			folderSet.add(folder);
			folder = join('replacement', folder);
			zipfile.addFile(resolve(__dirname, 'ncc_patch'), join(folder, 'ncc_patch'));
			zipfile.addFile(from, join(folder, path));
		} else if (st.isDirectory()) {
			addEntry(from);
		}
	});
}

function dateFormat(fmt, date) {
	let ret;
	let opt = {
		'Y+': date.getFullYear().toString(), // 年
		'm+': (date.getMonth() + 1).toString(), // 月
		'd+': date.getDate().toString(), // 日
		'H+': date.getHours().toString(), // 时
		'M+': date.getMinutes().toString(), // 分
		'S+': date.getSeconds().toString() // 秒
		// 有其他格式化字符需求可以继续添加，必须转化成字符串
	};
	for (let k in opt) {
		ret = new RegExp('(' + k + ')').exec(fmt);
		if (ret) {
			fmt = fmt.replace(ret[1], ret[1].length == 1 ? opt[k] : opt[k].padStart(ret[1].length, '0'));
		}
	}
	return fmt;
}
