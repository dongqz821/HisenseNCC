(window.webpackJsonp=window.webpackJsonp||[]).push([[23],{1136:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0;var o=p(n(21)),a=p(n(5)),r=p(n(7)),i=p(n(4)),l=p(n(6)),c=n(1),s=p(c),d=n(19);function p(e){return e&&e.__esModule?e:{default:e}}n(1137);var u=function(e){function t(e){(0,a.default)(this,t);var n=(0,i.default)(this,(t.__proto__||(0,o.default)(t)).call(this,e));return n.toogleSideBox=function(){n.setState({flag:!n.state.flag},(function(){return n.props.onChange(n.state.flag)}))},n.state={flag:!1,title:(0,d.langCheck)("OwnLog-000011","pages",e.langJson),showHeader:!0,showFooter:!1,bodyInnerHTML:(0,d.langCheck)("OwnLog-000012","pages",e.langJson)},n}return(0,l.default)(t,e),(0,r.default)(t,[{key:"componentWillReceiveProps",value:function(e){this.setState({flag:e.flag})}},{key:"render",value:function(){var e=this.props,t=e.headerInnerHTML,n=e.footerInnerHTML,o=e.record,a=this.state,r=a.flag,i=a.showHeader,l=a.showFooter,c=a.title,p=[];return r&&o&&o.logmsg&&o.logmsg.value&&(p=o.logmsg.value.split("^^")),s.default.createElement("div",{className:r?"side-box side-box-show":"side-box side-box-hide"},s.default.createElement("div",{className:r?"side-box-content content-show":"side-box-content content-hide"},i&&s.default.createElement("header",{className:"header"},s.default.createElement("span",{className:"title"},c),t,s.default.createElement("a",{href:"javaScript:void(0)",onClick:this.toogleSideBox},(0,d.langCheck)("OwnLog-000012","pages",this.props.langJson))),s.default.createElement("div",{className:"body"},r&&p.length>0&&s.default.createElement("div",null,p.map((function(e,t){return s.default.createElement("p",{key:t,style:{marginBottom:"10px"}},e)})))),l&&s.default.createElement("footer",{className:"footer"},n)))}}]),t}(c.Component);t.default=u},1137:function(e,t,n){var o=n(1138);"string"==typeof o&&(o=[[e.i,o,""]]);var a={hmr:!0,transform:void 0,insertInto:void 0};n(13)(o,a);o.locals&&(e.exports=o.locals)},1138:function(e,t,n){(e.exports=n(12)(!1)).push([e.i,".side-box{position:fixed;width:40%;height:80%;background-color:rgba(0,0,0,.2);border-radius:2px;top:0;right:0}.side-box.side-box-hide{display:none;z-index:-5;opacity:0;transition-delay:.3s}.side-box.side-box-show{opacity:1;z-index:300}.side-box .side-box-content{width:100%;height:100%;background-color:#fff;border:1px solid #ccc;transition:right .5s;position:relative}.side-box .side-box-content.content-show{transition-delay:.1s}.side-box .side-box-content.content-hide{right:-100%}.side-box .side-box-content footer,.side-box .side-box-content header{display:flex;align-items:center;padding:0 20px;height:50px;color:#666;font-size:13px;font-weight:700;justify-content:space-between}.side-box .side-box-content header{border-bottom:1px solid #ccc}.side-box .side-box-content footer{border-top:1px solid #ccc;position:absolute;bottom:0;width:100%}.side-box .side-box-content footer a{margin-right:0;margin-left:auto}.side-box .side-box-content .uf{color:#d9d9d9;font-size:16px;float:right;cursor:pointer;margin-right:0;margin-left:auto}.side-box .side-box-content .uf:hover{color:#666}.side-box .side-box-content .body{margin:20px;height:calc(100% - 140px);overflow:auto}",""])},1139:function(e,t,n){var o=n(1140);"string"==typeof o&&(o=[[e.i,o,""]]);var a={hmr:!0,transform:void 0,insertInto:void 0};n(13)(o,a);o.locals&&(e.exports=o.locals)},1140:function(e,t,n){(e.exports=n(12)(!1)).push([e.i,".workbench-ownlog-log{height:calc(100vh - 123px);background:#fff;width:100%}.workbench-ownlog-log .log-search-area{padding:5px 0 5px 20px;border-top:1px solid #d9d9d9}.workbench-ownlog-log .header{padding:0 20px;flex:0 0 54px;justify-content:space-between}.workbench-ownlog-log .header,.workbench-ownlog-log .header .header-left{display:flex;align-items:center}.workbench-ownlog-log .header .header-left .iconfont{margin-right:6px;border-radius:50%;font-size:16px;padding:4px;font-weight:400;cursor:pointer}.workbench-ownlog-log .header .header-left .iconfont:hover{background:#e8e8e8}.workbench-ownlog-log .header .header-left div{margin-bottom:0;font-weight:400;font-size:16px;font-weight:700}.workbench-ownlog-log .content .u-tabs-bar{margin-bottom:0!important;border-bottom:none!important}.workbench-ownlog-log .content .search-area-contant{padding:0;margin:0}",""])},950:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=v(n(111)),a=v(n(3)),r=v(n(2)),i=v(n(21)),l=v(n(5)),c=v(n(7)),s=v(n(4)),d=v(n(6)),p=n(1),u=v(p),g=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&(t[n]=e[n]);return t.default=e,t}(n(919)),h=n(969),f=v(n(1136)),b=v(n(35)),w=n(19),k=n(365);n(1139);var m=v(n(68)),y=n(37);function v(e){return e&&e.__esModule?e:{default:e}}var x=g.createPage,C=g.base,L=g.print,_=C.NCButton,O=C.NCTabs,P=C.NCDiv,E=O.NCTabPane,M={},S={"10220PLOG_enterlog":"enterlog","10220PLOG_operatelog":"operatelog","10220PLOG_busilog":"busilog"},N=function(e){function t(e){(0,l.default)(this,t);var n=(0,s.default)(this,(t.__proto__||(0,i.default)(t)).call(this,e));return n.componentWillMount=function(){(0,k.getMulti)({moduleId:"OwnLog",domainName:"workbench",callback:function(e){n.setState({langJson:e})}})},n.getData=function(){var e=n.state.key;(0,n.props.createUIDom)({appcode:"10220PLOGG",pagecode:e},n.callbackFun)},n.callbackFun=function(e){if(e){if(e.template){var t=e.template;t=n.modifier(t,n.props,n),n.props.meta.setMeta(t)}if(e.button){var o=e.button;n.props.button.setButtons(o)}}"10220PLOG_passwordlog"==n.state.key&&n.searchBtnClick()},n.modifier=function(e,t,o){var a=n.state,r=a.key,i=a.langJson,l={label:(0,w.langCheck)("OwnLog-000000","pages",i),attrcode:"opr",itemtype:"customer",width:"50px",visible:!0,render:function(e,t,n){return u.default.createElement("div",null,u.default.createElement("a",{className:"opr-col",onClick:o.watchDetails.bind(this,t)},(0,w.langCheck)("OwnLog-000009","pages",i)))}},c={label:(0,w.langCheck)("OwnLog-000001","pages",i),attrcode:"numberIndex",itemtype:"customer",width:"80px",fixed:"left",visible:!0,render:function(e,t,n){return u.default.createElement("div",null,n+1)}};return"10220PLOG_busilog"==r&&e.grid.items.push(l),e.grid.items.unshift(c),e.grid.pagination=!0,e},n.tabChange=function(e){var t=n.props.table.getTablePageInfo("grid");n.props.table.setAllTableData("grid",{rows:[],pageInfo:t}),n.props.meta.setMeta(""),n.closeSlider(),n.setState({key:e,printBtnDisabled:!0},(function(){return n.getData()}))},n.searchBtnClick=function(e,t){var o=n.state,i=o.key,l=o.langJson,c=n.props,s=c.search,d=c.table,p=c.meta,u=d.setAllTableData,g=void 0,h=void 0;switch(i){case"10220PLOG_enterlog":h="/nccloud/platform/log/queryenterlog.do";break;case"10220PLOG_operatelog":h="/nccloud/platform/log/queryoperatelog.do";break;case"10220PLOG_busilog":h="/nccloud/platform/log/querybusilog.do";break;case"10220PLOG_passwordlog":h="/nccloud/platform/log/queryuserpasswordlog.do"}if(p.getMeta().search){var f=s.getQueryInfo("search");if(0==f.querycondition||null==f.querycondition)return}(g="10220PLOG_passwordlog"==i?{}:s.getQueryInfo("search")).pageInfo=d.getTablePageInfo("grid"),"pageChange"==e?g.pageInfo.pagepks=t:g.pageInfo.pageIndex=0,(0,b.default)({url:h,data:g,info:{name:(0,w.langCheck)("OwnLog-000013","pages",l),action:(0,w.langCheck)("OwnLog-000003","pages",l),appcode:"10220PLOGG"},success:function(e){var t=e.data;if(null!==t.data&&t.data.grid){t.data.grid.pageInfo&&n.setState({total:t.data.grid.pageInfo.total}),t.data.grid.rows&&t.data.grid.rows.length>0&&n.setState({printBtnDisabled:!1});var o=(0,r.default)({pageInfo:g.pageInfo},t.data.grid);u("grid",o),M=(0,a.default)({},i,t.data.grid.allpks),"10220PLOG_passwordlog"!==n.state.key&&(0,m.default)({status:"success",msg:""+(0,w.langCheck)("OwnLog-000014","pages",l)+n.state.total+(0,w.langCheck)("OwnLog-000015","pages",l)})}else n.setState({printBtnDisabled:!0}),u("grid",{rows:[],pageInfo:{pageIndex:"0",pageSize:g.pageInfo.pageSize,total:"",totalPage:"1"}}),M=(0,a.default)({},i,[]),"10220PLOG_passwordlog"!==n.state.key&&(0,m.default)({status:"warning",msg:(0,w.langCheck)("OwnLog-000016","pages",l)})}})},n.handlePageInfoChange=function(e,t,o){n.searchBtnClick("pageChange",o)},n.onPrint=function(){var e=n.state.key,t=void 0,a=void 0,r=void 0,i=n.props.table.getTablePageInfo("grid");if(!(0,o.default)(M)[0]!=e){switch(r=n.pickOidsFun(M[e],i),e){case"10220PLOG_enterlog":t="enterlog",a="/nccloud/platform/log/printenterlog.do";break;case"10220PLOG_operatelog":a="/nccloud/platform/log/printoperatelog.do",t="operatelog";break;case"10220PLOG_busilog":t="busilog",a="/nccloud/platform/log/printbusilog.do";break;case"10220PLOG_passwordlog":t="userpasswordlog",a="/nccloud/platform/log/printuserpasswordlog.do"}L("pdf",""+a,{funcode:"10220PLOGG",nodekey:t,appcode:"10220PLOGG",oids:r})}},n.pickOidsFun=function(e,t){if(t.pageSize>=e.length)return e;var n=t.pageIndex*t.pageSize,o=(Number(t.pageIndex)+1)*t.pageSize;return e=e.slice(n,o)},n.onAfterEvent=function(e,t){var o=n.props.meta.getMeta();"typepk_busiobj"==e&&(o.search.items.find((function(e){return"pk_operation"==e.attrcode})).queryCondition=function(){return{metaId:t.refpk}}),n.props.meta.setMeta(o)},n.watchDetails=function(e){n.setState({flag:!0,record:e})},n.onRowClick=function(e,t,o,a,r){n.setState({record:o})},n.closeSlider=function(){n.setState({flag:!1})},n.pageback=function(){n.props.history.push("/")},e.use.table("grid"),e.use.search("search"),n.state={key:"10220PLOG_enterlog",flag:!1,record:null,printBtnDisabled:!0,langJson:{},total:0},n}return(0,d.default)(t,e),(0,c.default)(t,[{key:"componentDidMount",value:function(){this.getData(),window["nc-lightapp-front"]=g}},{key:"render",value:function(){var e=this,t=this.state,n=t.key,o=t.flag,a=t.record,r=t.printBtnDisabled,i=t.langJson,l=this.props,c=l.search,s=l.table,d=c.NCCreateSearch,p=s.createSimpleTable;return u.default.createElement(h.PageLayout,{header:u.default.createElement(h.PageLayoutHeader,null,u.default.createElement("div",{className:"pageLayoutHeaderOwn",pageback:this.pageback},u.default.createElement("i",{className:"iconfont icon-danjufanhuiicon",onClick:this.pageback}),u.default.createElement(P,{fieldid:(0,w.langCheck)("OwnLog-000002","pages",i),areaCode:P.config.Title},(0,w.langCheck)("OwnLog-000002","pages",i))),u.default.createElement(_,{style:{fontSize:"14px"},disabled:r,onClick:this.onPrint,fieldid:"print"},(0,w.langCheck)("OwnLog-000004","pages",i)))},u.default.createElement("div",{className:"workbench-ownlog-log"},u.default.createElement("div",{className:"content nc-bill-list"},u.default.createElement("div",{className:"tabs"},u.default.createElement(O,{navtype:"turn",contenttype:"moveleft",tabBarPosition:"top",defaultActiveKey:n,onChange:this.tabChange},u.default.createElement(E,{tab:(0,w.langCheck)("OwnLog-000005","pages",i),key:"10220PLOG_enterlog"}),u.default.createElement(E,{tab:(0,w.langCheck)("OwnLog-000006","pages",i),key:"10220PLOG_operatelog"}),u.default.createElement(E,{tab:(0,w.langCheck)("OwnLog-000007","pages",i),key:"10220PLOG_busilog"}),u.default.createElement(E,{tab:(0,w.langCheck)("OwnLog-000008","pages",i),key:"10220PLOG_passwordlog"}))),u.default.createElement("div",{className:"log-search-area"},d("search",{clickSearchBtn:this.searchBtnClick,showAdvBtn:!1,onAfterEvent:this.onAfterEvent,fieldid:S[n]})),u.default.createElement("div",{className:"nc-bill-table-area"},p("grid",{foldCacheId:"ownlog",cancelCustomRightMenu:!0,onRowClick:this.onRowClick,handlePageInfoChange:this.handlePageInfoChange})))),u.default.createElement(f.default,{langJson:i,record:a,flag:o,onChange:function(t){e.setState({flag:t})}}))}}]),t}(p.Component),I=x({})(N);t.default=(0,y.connect)((function(e){return{}}),{})(I)},969:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.PageLayoutRight=t.PageLayoutLeft=t.PageLayoutHeader=t.PageScrollLayout=t.PageLayout=void 0;var o=p(n(21)),a=p(n(5)),r=p(n(7)),i=p(n(4)),l=p(n(6)),c=n(1),s=p(c),d=p(n(0));function p(e){return e&&e.__esModule?e:{default:e}}n(970);var u=p(n(26)).default.base.NCDiv,g=!1,h=function(e){function t(e){return(0,a.default)(this,t),(0,i.default)(this,(t.__proto__||(0,o.default)(t)).call(this,e))}return(0,l.default)(t,e),(0,r.default)(t,[{key:"handleMouseUp",value:function(){g=!1,console.log(this.test)}},{key:"handleMouseMove",value:function(e){if(g){var t=e.clientX,n=document.querySelector("#layoutLeft"),o=n.getBoundingClientRect().left,a=parseInt(t-o)+3;a<200||(n.style.width=a+"px")}}},{key:"render",value:function(){return s.default.createElement("div",{className:"nc-workbench-page"},this.props.header?this.props.header:null,s.default.createElement("div",{onMouseMove:this.handleMouseMove.bind(this),onMouseUp:this.handleMouseUp.bind(this),className:"nc-workbench-ownpage "+(2===this.props.children.length?"nc-workbench-ownpage-all":"")+" "+(this.props.className||"")},this.props.children))}}]),t}(c.Component),f=function(e){function t(e){(0,a.default)(this,t);var n=(0,i.default)(this,(t.__proto__||(0,o.default)(t)).call(this,e));return n.state={suck:!1},n.initScrollContainerTop,n}return(0,l.default)(t,e),(0,r.default)(t,[{key:"handleMouseUp",value:function(){g=!1}},{key:"handleMouseMove",value:function(e){if(g){var t=e.clientX,n=document.querySelector("#layoutLeft"),o=n.getBoundingClientRect().left,a=parseInt(t-o)+3;a<200||(n.style.width=a+"px")}}},{key:"handleScroll",value:function(e){var t=this.refs.ncWorkbenchPageScroll.getBoundingClientRect().y;if(t==this.initScrollContainerTop&&this.setState({suck:!1}),t<this.initScrollContainerTop){if(this.state.suck)return;this.containerWidthChange(),this.setState({suck:!0})}}},{key:"containerWidthChange",value:function(){var e=this.refs.ncWorkbenchPageOwnpage.getBoundingClientRect().width;document.getElementsByClassName("nc-workbench-page-header")[0].style.width=e+"px",document.getElementById("suckTableHeader")&&(document.getElementById("suckTableHeader").style.width=e+"px")}},{key:"componentDidMount",value:function(){window.addEventListener("resize",this.containerWidthChange);var e=this.refs.ncWorkbenchPageScroll.getBoundingClientRect().y;this.initScrollContainerTop=e}},{key:"componentWillUnmount",value:function(){window.removeEventListener("resize",this.containerWidthChange)}},{key:"render",value:function(){var e=this.state.suck;return s.default.createElement("div",{className:"nc-workbench-page-scroll",onScroll:this.handleScroll.bind(this)},s.default.createElement("div",{className:" nc-workbench-page "},s.default.createElement("div",{className:"nc-workbench-page-container "+(e?"nc-workbench-suck":""),ref:"ncWorkbenchPageScroll"},this.props.header?this.props.header:null,s.default.createElement("div",{onMouseMove:this.handleMouseMove.bind(this),onMouseUp:this.handleMouseUp.bind(this),ref:"ncWorkbenchPageOwnpage",className:"nc-workbench-ownpage "+(2===this.props.children.length?"nc-workbench-ownpage-all":"")+" "+(this.props.className||"")},this.props.children))))}}]),t}(c.Component),b=function(e){function t(e){return(0,a.default)(this,t),(0,i.default)(this,(t.__proto__||(0,o.default)(t)).call(this,e))}return(0,l.default)(t,e),(0,r.default)(t,[{key:"handleMouseDown",value:function(){g=!0}},{key:"render",value:function(){return s.default.createElement("div",{id:"layoutLeft",className:"nc-workbench-ownpage-left "+(this.props.className||""),style:this.props.style?this.props.style:{background:"#f6f6f6"}},s.default.createElement("span",{className:"layout-drag-block",onMouseDown:this.handleMouseDown.bind(this)}),this.props.children)}}]),t}(c.Component),w=function(e){function t(e){(0,a.default)(this,t);var n=(0,i.default)(this,(t.__proto__||(0,o.default)(t)).call(this,e));return n.handleBindKey=n.handleBindKey.bind(n),n}return(0,l.default)(t,e),(0,r.default)(t,[{key:"componentDidMount",value:function(){document.addEventListener("keydown",this.handleBindKey,!1)}},{key:"componentWillUnmount",value:function(){document.removeEventListener("keydown",this.handleBindKey,!1)}},{key:"handleBindKey",value:function(e){var t=e||window.event||arguments.callee.caller.arguments[0];t&&t.keyCode&&t.shiftKey&&27==t.keyCode&&this.props.pageback&&this.props.pageback()}},{key:"render",value:function(){return s.default.createElement(u,{areaCode:u.config.HEADER,className:"nc-workbench-page-header "+(this.props.className||"")},this.props.children)}}]),t}(c.Component),k=function(e){function t(){return(0,a.default)(this,t),(0,i.default)(this,(t.__proto__||(0,o.default)(t)).apply(this,arguments))}return(0,l.default)(t,e),(0,r.default)(t,[{key:"render",value:function(){return s.default.createElement("div",{className:"nc-workbench-ownpage-right "+(this.props.className||"")},this.props.children)}}]),t}(c.Component);h.propTypes={children:d.default.any.isRequired},b.propTypes={children:d.default.any.isRequired},k.propTypes={children:d.default.any.isRequired},t.PageLayout=h,t.PageScrollLayout=f,t.PageLayoutHeader=w,t.PageLayoutLeft=b,t.PageLayoutRight=k},970:function(e,t,n){var o=n(971);"string"==typeof o&&(o=[[e.i,o,""]]);var a={hmr:!0,transform:void 0,insertInto:void 0};n(13)(o,a);o.locals&&(e.exports=o.locals)},971:function(e,t,n){(e.exports=n(12)(!1)).push([e.i,'.nc-workbench-page{position:absolute;top:0;z-index:10;height:100%;width:100%;padding:8px 0 0;display:flex;flex-direction:column}.nc-workbench-page .nc-workbench-page-header{flex:0 0 45px;display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #d9d9d9;border-radius:3px 3px 0 0;background:#fff;padding:0 20px}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn{display:flex;align-items:center;font-size:16px;color:#111;font-weight:700}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn .iconfont{margin-right:16px;border-radius:50%;font-size:16px;padding:0 4px;font-weight:400;line-height:24px;cursor:pointer}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn .iconfont:hover{background:#e8e8e8}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn span[type=ncdiv]{font-size:inherit}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn img{margin-right:10px}.nc-workbench-page .nc-workbench-page-header .header_in_icon{display:flex;font-weight:700;font-size:16px;color:#111;line-height:24px}.nc-workbench-page .nc-workbench-page-header .header_in_icon span[type=ncdiv]{font-size:inherit}.nc-workbench-page .nc-workbench-ownpage{min-width:800px;height:calc(100% - 61px);border-radius:0 0 3px 3px}.nc-workbench-page .nc-workbench-ownpage-all{display:flex;justify-content:flex-start;background:#fff;-webkit-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none}.nc-workbench-page .nc-workbench-ownpage-left{position:relative;height:100%;min-width:280px}.nc-workbench-page .nc-workbench-ownpage-left .layout-drag-block{position:absolute;right:-3px;z-index:10;top:50%;-webkit-transform:translateY(-50%);transform:translateY(-50%);display:block;padding:0 3px;height:100%;border-radius:3px;cursor:w-resize}.nc-workbench-page .nc-workbench-ownpage-left .layout-drag-block:after{content:"";display:block;width:1px;height:100%;background:#d9d9d9}.nc-workbench-page .nc-workbench-ownpage-left .layout-drag-block:hover:after{width:2px;background:rgba(0,0,0,.54)}.nc-workbench-page .nc-workbench-ownpage-right{min-width:50%;flex:1}.nc-workbench-page-scroll{height:100%;width:100%;overflow-y:auto}.nc-workbench-page-scroll .nc-workbench-page-container{margin-top:0;min-height:100%;background:#fff;border-radius:3px}.nc-workbench-page-scroll .nc-workbench-page-container .nc-workbench-page-header{position:static;transition:top .7s ease;height:46px}.nc-workbench-page-scroll .nc-workbench-page-container .suck-table-header{position:static;transition:top .7s ease}.nc-workbench-page-scroll .nc-workbench-suck{padding-top:80px}.nc-workbench-page-scroll .nc-workbench-suck .nc-workbench-page-header{position:fixed;z-index:1;top:44px}.nc-workbench-page-scroll .nc-workbench-suck .suck-table-header{position:fixed;z-index:1;top:94px}@media screen and (min-width:1280px){.nc-workbench-page{padding:8px 16px}}@media screen and (min-width:1440px){.nc-workbench-page{padding:8px 16px}}@media screen and (min-width:1600px){.nc-workbench-page{padding:8px 16px}}',""])}}]);
//# sourceMappingURL=OwnLog.js.map