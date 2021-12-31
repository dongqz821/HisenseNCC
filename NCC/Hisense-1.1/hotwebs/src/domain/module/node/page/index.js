/*
 * @Author: mikey.zhangchqf 价格审批单
 * @Date: 2018-06-26 15:34:46
 * @Last Modified by: CongKe
 * @Last Modified time: 2019-08-27 10:45:30
 */
import React from 'react';
import { render } from 'react-dom';
import { Page } from 'nc-lightapp-front';
import { events, lifecycle } from './events';
// import Page from './Page';

class PriceAuditList extends Page {
	// pagecode = '400400800_list'; // 页面唯一标识，请求模板数据

	// appcode = '10140NAT'; // 页面唯一标识，请求模板数据

	intl = ''; // 多语标识，请求多语资源

	billinfo = {};

	pageType = Page.LIST;

	getEvents = events;

	getLifecycle = lifecycle;
}

render(<PriceAuditList />, document.getElementById('app'));
