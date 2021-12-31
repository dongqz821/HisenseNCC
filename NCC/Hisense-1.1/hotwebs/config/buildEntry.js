/*
 * @Author: liyxt
 * @Date: 2019-04-23 09:37:04
 * @LastEditors: liyxt
 * @LastEditTime: 2020-05-19 16:43:33
 * @Description: file content
 */
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const glob = require('glob');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const fs = require('fs');
const webpack = require('webpack');

module.exports = function buildEntry({ buildPath, buildWithoutHTML, hash, mode, client, fse }) {
	fse = fse === 'true' || fse === true || false;
	Array.isArray(buildWithoutHTML) && buildWithoutHTML.unshift('refer');
	let projects = [],
		plugins = [],
		entries = {},
		externals = {};
	// 遍历src下的js
	(function(callback) {
		if (Array.isArray(buildPath)) {
			buildPath.forEach(_buildPath => {
				callback(_buildPath);
			});
		} else {
			callback(buildPath);
		}
	})(function(buildPath) {
		getFiles(buildPath);
	});

	projects.forEach(e => {
		if (e === 'uapbd') {
			// guozhq让弄的，供应链特殊
			fs.existsSync('./src/uapbd/scmbase/public') &&
				plugins.push(
					new CopyWebpackPlugin([{ from: `./src/uapbd/scmbase/public`, to: `./uapbd/scmbase/public` }])
				);
			// wanghxm让弄的，hr特殊
			fs.existsSync('./src/uapbd/hrbase/public') &&
				plugins.push(
					new CopyWebpackPlugin([{ from: `./src/uapbd/hrbase/public`, to: `./uapbd/hrbase/public` }])
				);
		}
		fs.existsSync(`./src/${e}/public`) &&
			plugins.push(
				new CopyWebpackPlugin([
					// {output}/to/file.txt
					{ from: `./src/${e}/public`, to: `./${e}/public` }
				])
			);
	});

	function getFiles(buildPath) {
		glob.sync(buildPath).forEach(path => {
			//  path ---为加载的每个index.js文件：./src/reva_demo/module/apply/list/index.js
			// chunk = 节点+list/card: reva_demo/module/apply/list
			if (
				(client === 'mobile' && path.includes('/mobile_')) ||
				(client !== 'mobile' && !path.includes('/mobile_'))
			) {
				// 移动端 || web端
				let chunk = path.split('./src/')[1].split('/index.js')[0],
					project = chunk.split('/')[0]; // reva_demo

				//把src自定义命名下的文件层级减掉，更改第二层级，把领域名改为 extend_领域名  by bbqin
				if (fse) {
					let chunkarr = chunk.split('/');
					chunkarr[0] = 'NCCExtend';
					chunkarr[1] = `extend_${chunkarr[1]}`;
					chunk = chunkarr.join('/');
				}

				projects.includes(project) || projects.push(project);
				// 生成webpack.config.js的入口
				let configJSONPath = './src/' + chunk + '/config.json',
					isExists = fs.existsSync(configJSONPath),
					_hash;
				if (isExists) {
					// 特殊处理的
					_hash = require('.' + configJSONPath).hash;
				}

				if (hash === 'false') {
					hash = false;
				} else if (hash === 'true') {
					hash = true;
				}

				let _chunk = ('/' + chunk + '/').toLowerCase();
				if (mode === 'development') {
					entries[`${chunk}/index`] = path;
				} else {
					if (hash) {
						// 筛选出带hash的
						if (_hash) {
							// config.json里的hash优先级高
							entries[`${chunk}/index`] = path;
						} else if (_hash !== false) {
							// 非参照页面生成hash
							!(
								_chunk.includes('/refer/') ||
								_chunk.includes('/ref/') ||
								_chunk.includes('/refers/') ||
								_chunk.includes('/mobile_refer/') ||
								fse
							) && (entries[`${chunk}/index`] = path);
						}
					} else {
						// 筛选出不带hash的
						if (_hash === false) {
							// config.json里的hash优先级高
							entries[`${chunk}/index`] = path;
						} else if (_hash !== true) {
							// 参照页面不生成hash
							(_chunk.includes('/refer/') ||
								_chunk.includes('/ref/') ||
								_chunk.includes('/refers/') ||
								_chunk.includes('/mobile_refer/') ||
								_hash === false ||
								fse) &&
								(entries[`${chunk}/index`] = path);
						}
					}
				}
				// buildWithoutHTML中的页面不生成html
				if (entries[`${chunk}/index`]) {
					let templatePath = client === 'mobile' ? './template/mobileTemplate.html' : './template/index.html';
					let configjs = ''; //额外配置的js文件
					let configcss = ''; //额外配置的css文件
					if (isExists) {
						let {
							template,
							output,
							dependjs,
							dependcss,
							dependModuleName,
							report,
							echarts,
							prodProxy
						} = require('.' + configJSONPath);

						// template: HTML模板路径
						if (template) {
							templatePath = template;
						}

						// output: 单独输出的文件配置
						if (output) {
							entries[`${output}/index`] = path;
						}

						// report: 报表依赖
						if (report) {
							configjs += `<script src="../../../../lappreportrt/nc-report/public/vendor.js"></script>`;
							configjs += `<script src="../../../../lappreportrt/nc-report/index.js"></script>`;
							configcss += `<link rel="stylesheet" href="../../../../lappreportrt/nc-report/public/vendor.css" />`;
							configcss += `<link rel="stylesheet" href="../../../../lappreportrt/nc-report/index.css" />`;
						}
						if (echarts) {
							configjs += `<script src="../../../../platform/echarts.js"></script>`;
						}

						// dependjs: 依赖的js文件配置
						if (Array.isArray(dependjs)) {
							configjs += dependjs.map(src => `<script src="${src}?v=${Date.now()}"></script>`).join('');
						}

						// dependcss: 依赖的css文件配置
						if (Array.isArray(dependcss)) {
							configcss += dependcss
								.map(item => `<link rel="stylesheet" href=${item}?v=${Date.now()}>`)
								.join('');
						}

						// dependModuleName: 依赖的模块名
						if (Array.isArray(dependModuleName)) {
							// 打包时排除
							dependModuleName.forEach(item => (externals[`${item}`] = `${item}/index`));
						}

						plugins.push(
							new webpack.DefinePlugin({
								PROD_PROXY: JSON.stringify((mode !== 'development' && prodProxy) || '')
							})
						);
					}

					if (!(buildWithoutHTML || []).some(e => path.includes(e))) {
						const htmlConf = {
							filename: `${chunk}/index.html`, // 生成的html文件名，可加目录/.../.../index.html
							template: `${templatePath}`, // 模板html路径
							inject: true, //允许插件修改哪些内容，包括head与body
							chunks: [`${chunk}/index`], // 生成的html文件引入哪些js，不传的话引入所有js
							cache: true,
							templateParameters: {
								configjs: configjs, //为模板添加js
								configcss: configcss //为模板添加css
							}
						};
						plugins.push(new HtmlWebpackPlugin(htmlConf));
					}
				}
			}
		});
	}

	let cleanOnceBeforeBuildPatterns = Object.values(entries).map(e => e.replace('index.js', '').replace('./src/', ''));
	plugins.push(
		new CleanWebpackPlugin({
			cleanOnceBeforeBuildPatterns,
			cleanAfterEveryBuildPatterns: [],
			verbose: true
		})
	);
	return {
		plugins,
		entries,
		externals
	};
};
