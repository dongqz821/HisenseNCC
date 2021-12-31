/*
 * @Author: liyxt
 * @Date: 2020-03-18 10:20:51
 * @LastEditors: liyxt
 * @LastEditTime: 2020-03-26 15:54:45
 * @Description: file content
 */
export function events() {
	// this即组件的this
	return {
		// 标题区
		TITLE_AREA: {
			title: this.state.title,
			initShowBackBtn: true
		},
		// 卡片分页区
		CARD_PAGINATION: {},
		// 按钮区
		header: {
			// ...others
			onButtonClick: (...rest) => {
				console.log(...rest, this);
				this.setState({
					title: '哈哈哈哈哈'
				});
			}
		},
		// 查询区
		search: {
			clickSearchBtn: (...rest) => {
				console.log(...rest, this);
			}
		},
		// 表格区
		head: {
			showIndex: true,
			showCheck: true
		},
		// 其他区域
		areaCode: {}
	};
}

export function lifecycle() {
	// this即组件的this
	return {
		constructor() {
			this.state = {
				title: 'hhhhh'
			};
		},
		// react生命周期
		componentDidMount() {
			console.log('componentDidMount', this);
		},
		componentDidUpdate(prevProps, prevState) {
			console.log('componentDidUpdate');
		},
		// 业务生命周期
		initTemplate(data) {
			console.log('initTemplate', data);
			return data;
		}
	};
}
