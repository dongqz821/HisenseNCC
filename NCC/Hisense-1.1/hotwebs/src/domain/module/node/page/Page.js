/* eslint-disable import/no-unresolved */
/*
 * @Author: mikey.zhangchqf 价格审批单
 * @Date: 2018-06-26 15:34:46
 * @Last Modified by: CongKe
 * @Last Modified time: 2019-08-27 10:45:30
 */

import React, { Component } from 'react';
import { createPage, base } from 'nc-lightapp-front';

const { NCDiv: Div, NCAffix: Affix } = base;

class Base extends Component {
	constructor(props) {
		super(props);
		// 合并生命周期
		let { constructor, ...lifecycles } = props.getLifecycle();
		Object.entries(lifecycles).forEach(([lifecycleName, callback]) => {
			let origin = this[lifecycleName];
			if (origin) {
				this[lifecycleName] = (...rest) => {
					callback.call(this, ...rest);
					origin.call(this, ...rest);
				};
			} else {
				this[lifecycleName] = callback.bind(this);
			}
		});
		this.getEvents = props.getEvents.bind(this);
		typeof constructor === 'function' && constructor.call(this, props);
		this.state = this.state || {};
	}

	componentDidMount() {
		this._initTemplate();
	}

	// 根据pagecode请求模板数据
	_initTemplate = () => {
		let { pagecode, appcode } = this.props;
		this.props.createUIDom(
			{
				pagecode,
				appcode,
				// 扩展 mergeRequest
				reqData: []
			},
			data => {
				// this.initTemplate 来自 this.props.getLifecycle()
				// 自定义生命周期
				typeof this.initTemplate === 'function' && (data = this.initTemplate.call(this, data));
				data.template.layout = [
					// { id: 'header', type: 'button' },
					{ id: 'search', type: 'search' },
					{
						id: 'head',
						type: 'table',
						shoulderButton: { id: 'header', type: 'button' },
						operationButton: { id: 'header', type: 'button' }
					}
				];
				this.props.meta.setMeta(data.template);
				this.props.button.setButtons(data.button);
			}
		);
	};

	// 渲染标题区
	renderTitle = ({ hasButton = false }) => {
		console.log('渲染标题区');
		return null;
	};

	// 渲染按钮区
	renderButton = ({ id }) => {
		console.log('渲染按钮区');
		return null;
	};

	// 渲染查询区
	renderSearch = ({ id }) => {
		console.log('渲染查询区');
		return null;
	};

	// 渲染表格区
	renderTable = ({ id }) => {
		console.log('渲染表格区');
		return null;
	};

	// 渲染 SimpleTable
	renderSimpleTable = ({ id }) => {
		console.log('渲染 SimpleTable');
		return null;
	};

	// 渲染 EditTable
	renderEditTable = ({ id }) => {
		console.log('渲染 EditTable');
		return null;
	};

	// 渲染 CardTable
	renderCardTable = ({ id }) => {
		console.log('渲染 CardTable');
		return null;
	};

	render() {
		let { meta } = this.props,
			metaData = meta.getMeta(),
			{ layout } = metaData;
		let titleWithButton = false;
		try {
			titleWithButton = layout[0].type === 'button';
		} catch (e) {}
		// 第一个是按钮区时，和标题区绑定
		return [
			!titleWithButton && this.renderTitle(),
			Array.isArray(layout) &&
				layout.map(({ type, ...otherProps }) => {
					switch (type) {
						case 'button':
							if (titleWithButton) {
								// 带按钮的标题区
								return this.renderTitle({
									withButton: titleWithButton,
									buttonId: otherProps.id
								});
							} else {
								// 按钮区
								return this.renderButton(otherProps);
							}

						case 'search':
							// 查询区
							return this.renderSearch(otherProps);
						case 'table':
							// 表格区
							return this.renderTable(otherProps);
						case 'simpleTable':
							// 表格区 - simpleTable
							return this.renderSimpleTable(otherProps);
						case 'editTable':
							// 表格区 - editTable
							return this.renderEditTable(otherProps);
						case 'cardTable':
							// 表格区 - cardTable
							return this.renderCardTable(otherProps);
						default:
							return null;
					}
				})
		];
	}
}

class List extends Base {
	// 渲染标题区
	renderTitle = ({ withButton = false, buttonId } = {}) => {
		console.log('渲染按钮区');
		let { BillHeadInfo, button } = this.props;
		let { createButtonApp } = button;
		let { createBillHeadInfo } = BillHeadInfo;
		let props = this.getEvents()[buttonId] || {};
		let titleInfo = this.getEvents().TITLE_AREA || {};
		return (
			<Div areaCode={Div.config.HEADER} className="nc-bill-header-area">
				<div className="header-title-search-area">{createBillHeadInfo(titleInfo)}</div>
				{/* 按钮区 */}
				{withButton && (
					<div className="header-button-area">
						{createButtonApp({
							area: buttonId,
							...props
						})}
					</div>
				)}
			</Div>
		);
	};

	// 渲染按钮区
	renderButton = ({ id }) => {
		console.log('渲染按钮区');
		let { button } = this.props;
		let { createButtonApp } = button;
		let props = this.getEvents()[id] || {};
		return (
			<Div areaCode={Div.config.HEADER} className="nc-bill-header-area">
				{/* 按钮区 */}
				<div className="header-button-area">
					{createButtonApp({
						area: id,
						...props
					})}
				</div>
			</Div>
		);
	};

	// 渲染查询区
	renderSearch = ({ id }) => {
		console.log('渲染查询区');
		let { search } = this.props,
			{ NCCreateSearch: createSearch } = search,
			props = this.getEvents.call(this)[id] || {};
		return <div className="nc-bill-search-area">{createSearch(id, props)}</div>;
	};

	// 渲染列表表格
	renderTable = ({ id }) => {
		console.log('渲染列表表格');
		let { table } = this.props,
			{ createSimpleTable } = table,
			props = this.getEvents.call(this)[id] || {};
		return <div className="nc-bill-table-area">{createSimpleTable(id, props)}</div>;
	};
}

class Card extends Base {
	// 渲染标题区
	renderTitle = ({ withButton = false, buttonId } = {}) => {
		console.log('渲染按钮区');
		let { BillHeadInfo, button, cardPagination } = this.props;
		let { createButtonApp } = button;
		let { createBillHeadInfo } = BillHeadInfo;
		let { createCardPagination } = cardPagination;
		let props = this.getEvents()[buttonId] || {};
		let titleInfo = this.getEvents().TITLE_AREA || {};
		let cardPaginationInfo = this.getEvents().CARD_PAGINATION || {};
		return (
			<Affix>
				<Div areaCode={Div.config.HEADER} className="nc-bill-header-area">
					<div className="header-title-search-area">{createBillHeadInfo(titleInfo)}</div>
					<div className="header-button-area">
						{withButton &&
							createButtonApp({
								area: buttonId,
								...props
							})}
					</div>
					<div className="header-cardPagination-area">{createCardPagination(cardPaginationInfo)}</div>
				</Div>
			</Affix>
		);
	};

	// 渲染按钮区
	renderButton = ({ id }) => {
		console.log('渲染按钮区');
		let { button } = this.props;
		let { createButtonApp } = button;
		let props = this.getEvents()[id] || {};
		return (
			<Div areaCode={Div.config.HEADER} className="nc-bill-header-area">
				<div className="header-button-area">
					{createButtonApp({
						area: id,
						...props
					})}
				</div>
			</Div>
		);
	};

	// 渲染查询区
	renderForm = ({ id }) => {
		console.log('渲染表单区');
		let { form } = this.props,
			{ createForm } = form,
			props = this.getEvents.call(this)[id] || {};
		return <div className="nc-bill-form-area">{createForm(id, props)}</div>;
	};

	// 渲染卡片表格
	renderTable = ({ id, shoulderButton }) => {
		console.log('渲染卡片表格');
		let { cardTable, button } = this.props,
			{ createCardTable } = cardTable,
			{ createButtonApp } = button,
			props = this.getEvents.call(this)[id] || {};
		// 表格肩部按钮
		if (shoulderButton) {
			props = {
				tableHead: () => {
					return (
						<div className="shoulder-definition-area">
							<div className="definition-icons">
								{createButtonApp({
									area: shoulderButton.id,
									...(this.getEvents.call(this)[shoulderButton.id] || {})
								})}
							</div>
						</div>
					);
				},
				...props
			};
		}
		return <div className="nc-bill-table-area">{createCardTable(id, props)}</div>;
	};
}

List = createPage({})(List);
Card = createPage({})(Card);

class Page extends Component {
	getEvents = () => ({});

	getLifecycle = () => ({});

	layout = () => {
		let props = {
			getEvents: this.getEvents,
			getLifecycle: this.getLifecycle,
			pagecode: this.pagecode,
			appcode: this.appcode,
			billinfo: this.billinfo
		};
		switch (this.pageType) {
			case Page.LIST:
				return (
					<div className="nc-bill-list">
						<List {...props} />
					</div>
				);
			case Page.CARD:
				return (
					<div className="nc-bill-card">
						<Card {...props} />
					</div>
				);
			default:
				return null;
		}
	};

	render() {
		return this.layout();
	}
}

Page.LIST = 'list';
Page.CARD = 'card';

export default Page;
