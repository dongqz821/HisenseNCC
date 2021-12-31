/*
 * @Author: liyxt
 * @Date: 2020-01-04 14:21:39
 * @LastEditors  : liyxt
 * @LastEditTime : 2020-01-06 10:37:21
 * @Description: file content
 */

// 维度：区域、事件、时机
export function area1() {
	return {
		beforeRunning: {},
		running: {},
		afterRunning: {}
	};
}

export function area2() {
	return {
		onBeforeEvent,
		onAfterEvent,
		onRowClick
	};
}

registerProps('area1', function() {
	return {
		onBeforeEvent,
		before_onBeforeEvent,
		onAfterEvent,
		onRowClick,
		status: 'browse'
	};
});
