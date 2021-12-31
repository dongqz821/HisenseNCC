/*
 * @Author: liyxt
 * @Date: 2019-12-05 14:56:05
 * @LastEditors: liyxt
 * @LastEditTime: 2019-12-06 17:37:02
 * @Description: file content
 */
// import { register } from 'nc-lightapp-front';

// 方案一：export方式导出
// 原理：依赖UMD格式，运行时根据命名空间获取变量
// 优点：业务组写法简单、标准，既可支持一开（import）又可支持二开（script）
// 缺点：
export const lifecycle = {
	constructor,
	componentDidMount,
	componentWillUnmount
};

// 将区域id作为导出的变量，以映射到具体区域
// 这就要求区域id不能带特殊符号如：- @
export const searchId = {
	onSearch,
	onAfterEvent
};

// 方案二：

export default function() {
	return {
		lifecycle: {
			constructor,
			componentDidMount,
			componentWillUnmount
		},
		searchId: {
			onSearch,
			onAfterEvent
		}
	};
}




export default function () {
    return <CustomerComponent type="text" value={this.state.text} onChange={this.handleChange} />
}

class CustomerComponent extends Component {
    constructor() {

    }
    render() {
        console.log(this);
        return <div></div>
    }
}