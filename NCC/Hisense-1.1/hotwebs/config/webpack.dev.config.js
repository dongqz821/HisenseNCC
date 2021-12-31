/*
 * @Author: liyxt
 * @Date: 2019-09-26 09:50:38
 * @LastEditors: liyxt
 * @LastEditTime: 2019-10-30 17:28:12
 * @Description: file content
 */
/**
 * 开发环境配置
 */
const OpenBrowserPlugin = require('open-browser-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const webpack = require('webpack');
const path = require('path');
const common = require('./webpack.common');
const merge = require('webpack-merge');
const configJSON = require('../config.json');
const buildEntry = require('./buildEntry');
const port = configJSON.devPort || 3006;
const host = 'localhost';
const fs = require('fs');

module.exports = function(env, argv) {
	let { mode, buildPath, client = 'pc' } = env;
	if (client === 'mobile') {
		process.env.PROJECT_CLIENT = 'mobile';
	}
	buildPath = buildPath || configJSON.buildEntryPath || './src/*/*/*/*/index.js';
	let buildWithoutHTML = configJSON.buildWithoutHTML;
	buildWithoutHTML && typeof buildWithoutHTML === 'string' && (buildWithoutHTML = [buildWithoutHTML]);
	let devConfig = {
		mode,
		entry: {},
		output: {
			filename: '[name].js',
			path: path.resolve(__dirname, './dist'),
			publicPath: '/',
			library: '[name]',
			libraryTarget: 'umd',
			chunkFilename: '[name].js'
		},
		devtool: 'source-map',
		devServer: {
			contentBase: path.join(__dirname, '../src'),
			port, // 端口号
			host: '0.0.0.0', // 主机地址
			inline: false, // 控制台是否显示构建信息
			clientLogLevel: 'error', // 控制台显示什么log信息
			open: false, // 开始构建时是否打开浏览器，使用OpenBrowserPlugin在构建完成时打开浏览器
			hot: true, // 是否启用热替换
			lazy: false, // 是否请求时才编译包
			historyApiFallback: {
				// 404时的页面
				rewrites: { from: /./, to: '/404.html' }
			},
			overlay: {
				// 报错时浏览器是否显示错误信息
				warnings: true,
				errors: true
			},
			stats: 'errors-only', // 开启报错提示
			proxy: {
				// 请求代理
				'/nccloud': {
					target: configJSON.proxy
				}
			}
		},
		plugins: [
			new webpack.DefinePlugin({
				NODE_ENV: JSON.stringify(mode),
				ISMA: configJSON.isMA,
				LOGIN_INFO: JSON.stringify(configJSON.directConnectInfo),
				MA_INFO: JSON.stringify(configJSON.maInfo)
			}),
			new webpack.NamedModulesPlugin(), // 当开启 HMR 的时候使用该插件会显示模块的相对路径
			new webpack.HotModuleReplacementPlugin(), // 模块热替换插件
			new OpenBrowserPlugin({ url: `http://${host}:${port}/nccloud` }) // 构建完成打开浏览器插件
		]
	};

	// 二开相关
	let extendBuildEntryPath = configJSON.extendBuildEntryPath || [];
	let { entries: extendEntries, plugins: extendPlugins } = buildEntry({
		buildPath: extendBuildEntryPath,
		buildWithoutHTML,
		hash: false,
		mode,
		client,
		fse: true
	});
	// console.log(extendEntries);
	// let { entries = [], plugins = [], externals = [] } = {};

	let { entries, plugins, externals } = buildEntry({
		buildPath,
		buildWithoutHTML,
		hash: true,
		mode,
		client
	});

	// 合并 二开
	Object.assign(entries, extendEntries);

	Object.assign(common.externals, externals);
	Object.assign(devConfig.entry, entries);
	devConfig.plugins.push(...plugins);
	devConfig = merge(common, devConfig);
	return devConfig;
};
