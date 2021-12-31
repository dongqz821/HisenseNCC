/*
 * @Author: liyxt
 * @Date: 2019-07-02 10:02:16
 * @LastEditors: liyxt
 * @LastEditTime: 2020-04-14 14:31:30
 * @Description: file content
 */
/**
 * 生产环境配置
 */
const webpack = require('webpack');
const common = require('./webpack.common');
const path = require('path');
const merge = require('webpack-merge');
const configJSON = require('../config.json');
const buildEntry = require('./buildEntry');
const TerserPlugin = require('terser-webpack-plugin');
// const Visualizer = require('webpack-visualizer-plugin');

let { patch } = configJSON;

let { project, branch, provider } = patch || {};

module.exports = function(env, argv) {
	let { mode, hash, client, fse, buildPath, outputPath } = env;
	if (client === 'mobile') {
		process.env.PROJECT_CLIENT = 'mobile';
	}
	buildPath = buildPath || configJSON.buildEntryPath || './src/*/*/*/*/index.js';
	let buildWithoutHTML = configJSON.buildWithoutHTML;
	buildWithoutHTML && typeof buildWithoutHTML === 'string' && (buildWithoutHTML = [buildWithoutHTML]);
	let prodConfig = {
		mode: 'production',
		entry: {},
		output: {
			path: path.resolve(__dirname, `../${outputPath || 'dist'}`),
			publicPath: '../../../../',
			library: '[name]',
			libraryTarget: 'umd',
			chunkFilename: '[name].js'
		},
		devtool: false,
		plugins: [
			// new FileListPlugin(),
			// new ChunkListPlugin(),
			new webpack.BannerPlugin({
				banner:
					'@ncctag ' +
					JSON.stringify({
						project,
						branch,
						provider,
						date: new Date().toLocaleString()
					}), // 其值为字符串，将作为注释存在
				raw: false, // 如果值为 true，将直出，不会被作为注释
				entryOnly: false // 如果值为 true，将只在入口 chunks 文件中添加
			}),
			new webpack.DefinePlugin({
				NODE_ENV: JSON.stringify(mode),
				ISMA: configJSON.isMA,
				LOGIN_INFO: JSON.stringify(configJSON.directConnectInfo),
				MA_INFO: JSON.stringify(configJSON.maInfo)
			})
			// new Visualizer()
		],
		optimization: {
			minimize: true, // 是否启用压缩
			splitChunks: {
				automaticNameDelimiter: '_'
			},
			minimizer: [
				new TerserPlugin({
					parallel: 4,
					sourceMap: true,
					extractComments: false,
					terserOptions: {
						// compress: {
						// 	drop_console: true
						// },
						output: {
							comments: /@ncctag/i
						}
					}
				})
			]
		}
	};
	if (hash === 'false') {
		hash = false;
	} else if (hash === 'true') {
		hash = true;
	}

	// test模式，加source-map调试
	mode === 'test' && (prodConfig.devtool = 'source-map');
	// 节点加hash，参照不加hash
	prodConfig.output.filename = hash ? '[name].[hash:8].js' : '[name].js';
	// 获取入口
	let { entries, plugins, externals } = buildEntry({ buildPath, buildWithoutHTML, hash, client, mode, fse });
	Object.assign(common.externals, externals);
	Object.assign(prodConfig.entry, entries);
	prodConfig.plugins.push(...plugins);

	prodConfig = merge(common, prodConfig);
	return prodConfig;
};
