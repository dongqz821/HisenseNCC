(window.webpackJsonp=window.webpackJsonp||[]).push([[15],{1249:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.default={table:{moduletype:"table",pagination:!0,name:"事务错误中心",items:[{itemtype:"label",maxlength:"20",visible:!0,width:"200px",label:"错误原因",disabled:!0,attrcode:"billmaker"},{itemtype:"customer",maxlength:"20",visible:!0,width:"200px",label:"发起方单据号",disabled:!0,attrcode:"billmaker1"},{itemtype:"label",maxlength:"20",visible:!0,width:"200px",label:"单据类型",disabled:!0,attrcode:"billmaker2"},{itemtype:"label",maxlength:"20",visible:!0,width:"200px",label:"交易类型",disabled:!0,attrcode:"billmaker3"},{itemtype:"label",maxlength:"20",visible:!0,width:"200px",label:"操作类型",disabled:!0,attrcode:"billmaker4"},{itemtype:"label",maxlength:"20",visible:!0,width:"200px",label:"业务日期",disabled:!0,attrcode:"billmaker5"},{itemtype:"customer",maxlength:"20",visible:!0,width:"200px",label:"操作",disabled:!0,attrcode:"opr",className:"table-opr",fixed:"right"}]}}},1250:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.default=[{type:"button_main",key:"retry",title:"重试",area:"page_header,table_row"},{type:"button_main",key:"return",title:"回退",area:"page_header,table_row"},{type:"button_main",key:"orderback",title:"订单追溯",area:"page_header,table_row"}]},1251:function(e,t,a){var n=a(1252);"string"==typeof n&&(n=[[e.i,n,""]]);var l={hmr:!0,transform:void 0,insertInto:void 0};a(13)(n,l);n.locals&&(e.exports=n.locals)},1252:function(e,t,a){(e.exports=a(12)(!1)).push([e.i,".fresh-btn button.u-button.nc-button-wrapper.button-secondary.bt-icon.u-button-icon{padding:0;text-align:center}.page-title-Icon{margin-right:6px;width:25px;height:auto}.nc-workbench-ownpage{background:#fff;height:calc(100% - 54px)}.nc-workbench-ownpage .search-topline{display:block;width:100%;background:#f6f6f6}.nc-workbench-ownpage .search-topline .serach-btn{margin-top:5px;margin-left:13px;vertical-align:top}.nc-workbench-ownpage .search-topline .service-data{display:inline-block;font-size:13px;line-height:33px;vertical-align:top;color:#111;text-indent:16px;margin-right:27px;padding-top:4px}.nc-workbench-ownpage .search-topline .all-time,.nc-workbench-ownpage .search-topline .send-bill,.nc-workbench-ownpage .search-topline .seven-day,.nc-workbench-ownpage .search-topline .today-time{display:inline-block;font-size:13px;line-height:33px;margin-right:30px;vertical-align:top;color:#111;padding-top:4px}.nc-workbench-ownpage .search-topline .send-bill{margin-left:120px;margin-right:0}.nc-workbench-ownpage .search-topline .item{display:inline-block;vertical-align:top;padding-top:5px;width:220px}.nc-workbench-ownpage .search-topline .item .calendar-picker{width:220px}.nc-workbench-ownpage .search-topline .bill-inpt{display:inline-block;font-size:13px;height:33px;vertical-align:top;padding-top:5px}.nc-workbench-ownpage .search-topline .bill-inpt input{padding-left:12px}.nc-workbench-ownpage .search-topline .bill-typename{display:inline-block;font-size:13px;line-height:33px;vertical-align:top;color:#111;text-indent:16px}.nc-workbench-ownpage .search-topline .bill-item,.nc-workbench-ownpage .search-topline .bill-type{display:inline-block;font-size:13px;line-height:33px;margin-right:30px;vertical-align:top;color:#111;cursor:pointer}.nc-workbench-ownpage .search-topline .active-status{color:red}.nc-workbench-ownpage .search-topline .all-time,.nc-workbench-ownpage .search-topline .seven-day,.nc-workbench-ownpage .search-topline .today-time{cursor:pointer;padding-top:4px}.bill-type-table{width:100%;border-spacing:0;border-collapse:collapse}.bill-type-table .bill-type-td{width:120px;width:147px}.lg-done{display:none!important}.billnolistcontainer{position:relative;display:inline-block}.billnolistcontainer *{margin:0;padding:0}.billnolistcontainer .billnospan{position:relative;display:inline-block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;vertical-align:middle}.billnolistcontainer .billnonum{display:inline-block;vertical-align:middle;margin-left:10px;padding:3px 7px;border-radius:3px;background:#e6e8ed;line-height:1}.billnolistbox-innerbox{background:#fff;height:100%;padding:5px 0}.billnolistbox-innerbox .billnolistitem{line-height:16px;padding:2px;text-indent:5px;word-break:break-all;word-wrap:break-word;word-break:normal}.billnolistbox-innerbox .billnolistitem a{padding:0;word-break:break-all;word-wrap:break-word;word-break:normal}.body-dispaly-in-row .u-table-fieldtype,.body-dispaly-in-row td{overflow:inherit}.body-dispaly-in-row .u-table-fieldtype,.body-dispaly-in-row td:first-of-type{overflow:hidden}.lightapp-component-simpleTable .single-line-and-ellipsis{overflow:inherit}.lightapp-component-simpleTable .u-table-content .u-table-fixed-right{box-shadow:none}",""])},960:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=C(a(21)),l=C(a(5)),i=C(a(7)),o=C(a(4)),r=C(a(6)),c=C(a(171)),s=C(a(112)),d=a(1),p=C(d),u=C(a(26)),h=a(969),g=C(a(35)),b=a(19),f=a(32),m=a(365),k=C(a(1249)),w=C(a(1250));a(1251);var y=a(45),v=C(a(367)),x=a(37);function C(e){return e&&e.__esModule?e:{default:e}}var E=u.default.createPage,_=u.default.base,D=u.default.toast,N=u.default.pageTo,T=u.default.high,I=_.NCButton,M=_.NCDiv,S=_.NCRangePickerClient,P=_.NCFormControl,B=_.NCTooltip,L=T.BillErrorTrack,H=[];function z(e){return(0,s.default)(new c.default(e))}var O=function(e){function t(e){(0,l.default)(this,t);var a=(0,o.default)(this,(t.__proto__||(0,n.default)(t)).call(this,e));a.billCodeBlur=function(){var e=a.state.langJson;a.loadgrid((function(t){t>0&&D({content:(0,b.langCheck)("ErrorCenter-0000022","pages",e)+t+(0,b.langCheck)("ErrorCenter-0000023","pages",e)}),0==t&&D({content:(0,b.langCheck)("ErrorCenter-0000024","pages",e),color:"warning"})}),1)},a.componentWillMount=function(){},a.keydownHangle=function(e){116==e.keyCode&&(e.preventDefault(),a.show())},a.visibilitychangeHandle=function(){if("visible"===document.visibilityState){var e=(0,a.props.table.getTablePageInfo)("table").pageIndex+1;a.getBillTypeData(null,e)}},a.webstocketdatahandle=function(e){var t=(0,a.props.table.getTablePageInfo)("table").pageIndex+1;a.getBillTypeData(null,t)},e.use.table("table");var i=a,r=new Date,c=r.getFullYear(),s=r.getDate(),d=r.getMonth()+1,u=new Date(r.getTime()-2592e6),h=u.getFullYear(),g=u.getDate(),f=u.getMonth()+1;a.state={langJson:{},v:[h+"-"+f+"-"+g,c+"-"+d+"-"+s],serviceDateIndex:0,serviceDatearr:["all","today","7date"],billgroup:[],billgroupIndex:-1,sendbillcode:"",gridarr:[],totalPage:"",total:"",billpk:"",billtype:"",gtxid:"",showBillTrack:!1};return(0,m.getMulti)({moduleId:"ErrorCenter",domainName:"workbench",callback:function(t){a.state.langJson=t;k.default.table.items[1].render=function(e,a,n){var l=e&&e.value,i=z(l&&l.split(",")||[]),o=i[0],r=e&&(e.tradeType||e.billtype),c=z(r&&r.split(",")||[]),s=!0;"string"==typeof o&&"string"==typeof e.billtype&&(o.includes(";")||e.billtype.includes(";"))&&(s=!1);var d=e&&e.hyperlink-0,u=(e&&e.liststatus,e&&e.module);e&&e.updown;return i.length>1?p.default.createElement(B,{className:"high-table-tooltip",placement:"bottom",overlay:p.default.createElement("div",{className:"billnolistbox-innerbox"},i.map((function(a,n){if(c.length<2&&s&&d){var l=e&&e.billpk&&e.billpk.split(",");l=l&&l[n]||l[0]||"";var i=c[0];return p.default.createElement("div",{className:"billnolistitem"},p.default.createElement("a",{onClick:function(){N.openAppByBilltype({billpk:l,billtype:i,type:"open",pathModule:u})}},e&&e.value&&a))}return p.default.createElement("div",{className:"billnolistitem",onClick:function(){return D({content:(0,b.langCheck)("ErrorCenter-0000021","pages",t),color:"warning"})}},a)})))},p.default.createElement("div",{className:"billnolistcontainer"},c.length<2&&s&&d?p.default.createElement("a",{onClick:function(){var t=e&&e.billpk&&e.billpk.split(",")[0],a=c[0];N.openAppByBilltype({billpk:t,billtype:a,type:"open",pathModule:u})}},e&&e.value&&o):p.default.createElement("span",{className:"billnospan",onClick:function(){return D({content:(0,b.langCheck)("ErrorCenter-0000021","pages",t),color:"warning"})},alt:o},o),i.length>1?p.default.createElement("span",{className:"billnonum"},"+",i.length):null)):c.length<2&&s&&d?p.default.createElement("a",{onClick:function(){var t=e&&e.billpk,a=e&&(e.tradeType||e.billtype);N.openAppByBilltype({billpk:t,billtype:a,type:"open",pathModule:u})}},e&&e.value&&o):p.default.createElement("span",null,e&&e.value&&o)},k.default.table.items[6].render=function(t,a,n){var l=[];return 1==a.billmaker4.track&&l.push("orderback"),1==a.billmaker4.compensable&&l.push("return"),1==a.billmaker.retry&&l.push("retry"),p.default.createElement("div",null,e.button.createOprationButton(l,{rowIndex:n,area:"table_row",buttonLimit:3,onButtonClick:function(e,t,n,l){i.optButtonHandle(e,t,n,l,a)}}))},k.default.table.name=(0,b.langCheck)("ErrorCenter-000000","pages",t),e.button.setPopContent("return",(0,b.langCheck)("ErrorCenter-0000017","pages",t));for(var n=25;n<32;n++)k.default.table.items[n-25].label=(0,b.langCheck)("ErrorCenter-00000"+n,"pages",t);for(var l=14;l<17;l++)w.default[l-14].title=(0,b.langCheck)("ErrorCenter-00000"+l,"pages",t);e.button.setButtons(w.default),e.meta.setMeta(k.default,(function(){}))}}),a}return(0,r.default)(t,e),(0,i.default)(t,[{key:"getBillTypeData",value:function(e,t){var a=this,n=this.state.langJson;(0,g.default)({url:"/nccloud/platform/fixdata/ListFixDataBillTypeAction.do",data:{},info:{name:(0,b.langCheck)("ErrorCenter-000000","pages",n),action:(0,b.langCheck)("ErrorCenter-0000019","pages",n),appcode:"10220PLOGG"},success:function(n){var l=[],i=a.state.billgroupIndex;n&&n.data&&n.data.success&&((l=n.data.data||[]).length!==H.length&&(H=l,i=-1,t=1),a.setState({billgroup:l,billgroupIndex:i},(function(){a.props.table.updateTableHeight("table"),a.loadgrid(e,t)})))}})}},{key:"billtypehandle",value:function(e){var t=this;this.setState({billgroupIndex:e},(function(){t.loadgrid(null,1)}))}},{key:"seviceDateHandle",value:function(e,t){var a=this;if(e<3)this.setState({serviceDateIndex:e},(function(){a.loadgrid(null,1)}));else if(t){if(this.state.v&&t.toString()==this.state.v.toString())return;this.setState({serviceDateIndex:3,v:t},(function(){a.loadgrid(null,1)}))}else this.setState({serviceDateIndex:0,v:null},(function(){a.loadgrid(null,1)}))}},{key:"billCodeHandle",value:function(e){this.setState({sendbillcode:e})}},{key:"loadgrid",value:function(e,t){var a=this,n=this.state,l=n.serviceDateIndex,i=n.serviceDatearr,o=n.v,r=n.sendbillcode,c=n.billgroupIndex,s=n.billgroup,d=n.langJson,p={},u=(0,this.props.table.getTablePageInfo)("table");p.start=t+"",p.pageSize=u.pageSize+"",l<3&&(p.datetype=i[l]),3==l&&(p.startdate=o[0],p.enddate=o[1]),r&&(p.billno=r),-1==c||(p.billtype=s[c].billtypecode),(0,g.default)({url:"/nccloud/platform/fixdata/ListFixDataAction.do",data:p,info:{name:(0,b.langCheck)("ErrorCenter-000000","pages",d),action:(0,b.langCheck)("ErrorCenter-0000020","pages",d),appcode:"10220PLOGG"},success:function(n){n.data.success&&(a.setState({gridarr:n.data.data.data,totalPage:n.data.data.pageinfo.totalpage,total:n.data.data.pageinfo.totalnum}),t--,a.setSimpleTab(n.data.data.data,t,p.pageSize,n.data.data.pageinfo.totalpage,n.data.data.pageinfo.totalnum)),"[object Function]"==Object.prototype.toString.call(e)&&e(n.data.data.pageinfo.totalnum)}})}},{key:"setSimpleTab",value:function(e,t,a,n,l){var i=this.props.table.setAllTableData,o={success:!0,message:"",table:{allpks:[1,2,3,4,5,6,7],pageInfo:{pageIndex:t,pageSize:a,total:l,totalPage:n},rows:[{rowId:"3334555qqqqq555",status:"0",values:{billmaker:{value:"2016-05-20 14:51:48"},billmaker1:{value:"17"},billmaker2:{value:"2016-06-20 14:58:46"},billmaker3:{value:"0001K710000000004DSE"},billmaker4:{value:"2016-06-20 14:58:46"}}},{rowId:"3334555qqqqq555",status:"0",values:{billmaker:{value:"2016-05-20 14:51:48"},billmaker1:{value:"18"},billmaker2:{value:"2016-06-20 14:58:46"},billmaker3:{value:"0001K710000000004DSE",display:""},billmaker4:{value:"2016-06-20 14:58:46"}}}]}};o.table.rows.length=0;for(var r=0;r<e.length;r++){var c=e[r].businessPk,s=e[r].hyperlink,d=e[r].retry,p=e[r].businessDate&&e[r].businessDate.length&&e[r].businessDate.split(" ")[0];o.table.rows.push({status:"0",values:{billmaker:{value:e[r].errorMsg_desc,gtx_id:e[r].gtxid,billpkccc:c,hyperlink:s,retry:d,module:e[r].module},billmaker1:{value:e[r].billNo,billpk:c,billtype:e[r].billType,tradeType:e[r].tradeType,hyperlink:s,billtype_name:e[r].billtype_name,liststatus:e[r].liststatus,updown:e[r].updown,module:e[r].module},billmaker2:{value:e[r].billtype_name,module:e[r].module},billmaker3:{value:e[r].tradeType_name,module:e[r].module},billmaker4:{value:e[r].operation,compensable:e[r].compensable,track:e[r].track,retry:d,module:e[r].module},billmaker5:{value:p,compensable:e[r].compensable,track:e[r].track,module:e[r].module}}})}i("table",o.table)}},{key:"optButtonHandle",value:function(e,t,a,n,l){var i=this,o="",r={};if("retry"==t&&(o="/nccloud/platform/fixdata/ResetFixDataAction.do",r.gtxid=[l.billmaker.gtx_id]),"return"==t&&(o="/nccloud/platform/fixdata/BackFixDataAction.do",r.gtxid=l.billmaker.gtx_id),"orderback"==t){var c=l.billmaker.gtx_id,s=l.billmaker1.billpk,d=l.billmaker1.billtype,p=z(d&&String(d).split(",")||[]),u=z(s&&String(s).split(",")||[]),h=!0;return(p.length>1||u.length>1)&&(h="none"),void this.setState({showBillTrack:!0,gtxid:c,billpk:s,billtype:d,errorViewType:h})}(0,g.default)({url:o,data:r,info:{name:(0,b.langCheck)("OwnLog-000013","pages"),action:(0,b.langCheck)("OwnLog-000003","pages"),appcode:"10220PLOGG"},success:function(e){if("return"==t){var a=i.state.langJson,n=(0,b.langCheck)("ErrorCenter-0000012","pages",a);D({content:n})}if("retry"==t){var l=i.state.langJson,o=(0,b.langCheck)("ErrorCenter-0000011","pages",l);D({content:o})}}})}},{key:"show",value:function(){var e=this.state.langJson,t=(0,b.langCheck)("ErrorCenter-0000010","pages",e),a=(0,this.props.table.getTablePageInfo)("table").pageIndex+1;this.getBillTypeData((function(){D({content:t})}),a)}},{key:"componentDidMount",value:function(){this.getBillTypeData(null,1),document.addEventListener("visibilitychange",this.visibilitychangeHandle),document.addEventListener("keydown",this.keydownHangle,!1)}},{key:"componentWillUnmount",value:function(){document.removeEventListener("visibilitychange",this.visibilitychangeHandle),document.removeEventListener("keydown",this.keydownHangle)}},{key:"ClickPageInfo",value:function(e,t,a,n){var l=(0,this.props.table.getTablePageInfo)("table").pageIndex+1;this.loadgrid(null,l);var i=this.props,o=i.errorFlag;(0,i.changeError)(!o)}},{key:"componentWillReceiveProps",value:function(e){var t=this,a=new Date,n=a.getFullYear(),l=a.getDate(),i=a.getMonth()+1,o=new Date(a.getTime()-2592e6),r=o.getFullYear(),c=o.getDate(),s=o.getMonth()+1;this.setState({v:[r+"-"+s+"-"+c,n+"-"+i+"-"+l],serviceDateIndex:0,billgroupIndex:-1,sendbillcode:""},(function(e){var a=(0,t.props.table.getTablePageInfo)("table").pageIndex+1;t.loadgrid(null,a)}))}},{key:"render",value:function(){var e=this,t=this.state.langJson,a=this.props,n=a.table,l=a.socket,i=n.createSimpleTable,o=this.state.billgroup,r=this.props,c=r.errorFlag,s=r.changeError,d=this,u=(0,f.getStore)("nccIcon")||"";return p.default.createElement(h.PageLayout,{header:p.default.createElement(h.PageLayoutHeader,null,p.default.createElement("div",{className:"pageLayoutHeaderOwn"},l.connectMesg({onMessage:function(e,t){console.log(t),t&&(s(!c),d.webstocketdatahandle(t))}}),p.default.createElement("img",{className:"page-title-Icon",src:v.default[u]?v.default[u]:v.default.defaultIcon}),p.default.createElement(M,{fieldid:(0,b.langCheck)("ErrorCenter-000000","pages",t),areaCode:M.config.Title},(0,b.langCheck)("ErrorCenter-000000","pages",t))),p.default.createElement("div",{className:"fresh-btn"},p.default.createElement(B,{placement:"bottom",overlay:"F5"},p.default.createElement(I,{fieldid:"refresh",colors:"secondary",shape:"icon",onClick:this.show.bind(this)},p.default.createElement("i",{className:"iconfont icon-shuaxin1"})))))},p.default.createElement("div",null,p.default.createElement(L,{show:this.state.showBillTrack,close:function(){e.setState({showBillTrack:!1})},pk:this.state.billpk,type:this.state.billtype,gtxid:this.state.gtxid,errorViewType:this.state.errorViewType}),p.default.createElement("div",{className:"search-topline"},p.default.createElement("span",{className:"service-data"},(0,b.langCheck)("ErrorCenter-000001","pages",t),"："),p.default.createElement("span",{className:0==this.state.serviceDateIndex?"all-time active-status":"all-time",onClick:this.seviceDateHandle.bind(this,0)},(0,b.langCheck)("ErrorCenter-000002","pages",t)),p.default.createElement("span",{className:1==this.state.serviceDateIndex?"today-time active-status":"today-time",onClick:this.seviceDateHandle.bind(this,1)},(0,b.langCheck)("ErrorCenter-000003","pages",t)),p.default.createElement("span",{className:2==this.state.serviceDateIndex?"seven-day active-status":"seven-day",onClick:this.seviceDateHandle.bind(this,2)},(0,b.langCheck)("ErrorCenter-000004","pages",t)),p.default.createElement("div",{className:"item datepicker-box"},p.default.createElement(S,{placeholder:(0,b.langCheck)("ErrorCenter-000005","pages",t)+"~"+(0,b.langCheck)("ErrorCenter-000006","pages",t),format:"YYYY-MM-DD",showTimeFunction:!1,value:this.state.v,onChange:this.seviceDateHandle.bind(this,3),showClear:!0,fieldid:"dateRange"})),p.default.createElement("span",{className:"send-bill"},(0,b.langCheck)("ErrorCenter-000007","pages",t),"："),p.default.createElement("span",{className:"bill-inpt"},p.default.createElement(P,{fieldid:"billno",type:"search",placeholder:(0,b.langCheck)("ErrorCenter-000008","pages",t),value:this.state.sendbillcode,onChange:this.billCodeHandle.bind(this),onKeyDown:function(t){13==(t=t||window.event).keyCode&&e.billCodeBlur()}})),p.default.createElement(I,{className:"serach-btn",colors:"primary",onClick:this.billCodeBlur},(0,b.langCheck)("ErrorCenter-0000032","pages",t))),p.default.createElement("div",{className:"search-topline"},p.default.createElement("table",{className:"bill-type-table"},p.default.createElement("tr",null,p.default.createElement("td",{className:"bill-type-td"},p.default.createElement("span",{className:"bill-typename"},(0,b.langCheck)("ErrorCenter-000009","pages",t),"：")),p.default.createElement("td",null,p.default.createElement("span",{className:-1==this.state.billgroupIndex?"bill-type active-status":"bill-type",onClick:this.billtypehandle.bind(this,-1)},(0,b.langCheck)("ErrorCenter-000002","pages",t)),o.map((function(t,a){return p.default.createElement("span",{className:e.state.billgroupIndex==a?"bill-item active-status":"bill-item",onClick:e.billtypehandle.bind(e,a)},t&&t.billtypename+t.billtypecount)})))))),p.default.createElement("div",{className:"table-box"},p.default.createElement("div",{className:"nc-singleTable-table-area"},i("table",{showIndex:!1,showCheck:!1,fieldid:"error",cancelCustomRightMenu:!0,handlePageInfoChange:this.ClickPageInfo.bind(this)})))))}}]),t}(d.Component),F=E({})(O);t.default=(0,x.connect)((function(e){return{errorFlag:e.appData.errorFlag}}),{changeError:y.changeError})(F)},969:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.PageLayoutRight=t.PageLayoutLeft=t.PageLayoutHeader=t.PageScrollLayout=t.PageLayout=void 0;var n=p(a(21)),l=p(a(5)),i=p(a(7)),o=p(a(4)),r=p(a(6)),c=a(1),s=p(c),d=p(a(0));function p(e){return e&&e.__esModule?e:{default:e}}a(970);var u=p(a(26)).default.base.NCDiv,h=!1,g=function(e){function t(e){return(0,l.default)(this,t),(0,o.default)(this,(t.__proto__||(0,n.default)(t)).call(this,e))}return(0,r.default)(t,e),(0,i.default)(t,[{key:"handleMouseUp",value:function(){h=!1,console.log(this.test)}},{key:"handleMouseMove",value:function(e){if(h){var t=e.clientX,a=document.querySelector("#layoutLeft"),n=a.getBoundingClientRect().left,l=parseInt(t-n)+3;l<200||(a.style.width=l+"px")}}},{key:"render",value:function(){return s.default.createElement("div",{className:"nc-workbench-page"},this.props.header?this.props.header:null,s.default.createElement("div",{onMouseMove:this.handleMouseMove.bind(this),onMouseUp:this.handleMouseUp.bind(this),className:"nc-workbench-ownpage "+(2===this.props.children.length?"nc-workbench-ownpage-all":"")+" "+(this.props.className||"")},this.props.children))}}]),t}(c.Component),b=function(e){function t(e){(0,l.default)(this,t);var a=(0,o.default)(this,(t.__proto__||(0,n.default)(t)).call(this,e));return a.state={suck:!1},a.initScrollContainerTop,a}return(0,r.default)(t,e),(0,i.default)(t,[{key:"handleMouseUp",value:function(){h=!1}},{key:"handleMouseMove",value:function(e){if(h){var t=e.clientX,a=document.querySelector("#layoutLeft"),n=a.getBoundingClientRect().left,l=parseInt(t-n)+3;l<200||(a.style.width=l+"px")}}},{key:"handleScroll",value:function(e){var t=this.refs.ncWorkbenchPageScroll.getBoundingClientRect().y;if(t==this.initScrollContainerTop&&this.setState({suck:!1}),t<this.initScrollContainerTop){if(this.state.suck)return;this.containerWidthChange(),this.setState({suck:!0})}}},{key:"containerWidthChange",value:function(){var e=this.refs.ncWorkbenchPageOwnpage.getBoundingClientRect().width;document.getElementsByClassName("nc-workbench-page-header")[0].style.width=e+"px",document.getElementById("suckTableHeader")&&(document.getElementById("suckTableHeader").style.width=e+"px")}},{key:"componentDidMount",value:function(){window.addEventListener("resize",this.containerWidthChange);var e=this.refs.ncWorkbenchPageScroll.getBoundingClientRect().y;this.initScrollContainerTop=e}},{key:"componentWillUnmount",value:function(){window.removeEventListener("resize",this.containerWidthChange)}},{key:"render",value:function(){var e=this.state.suck;return s.default.createElement("div",{className:"nc-workbench-page-scroll",onScroll:this.handleScroll.bind(this)},s.default.createElement("div",{className:" nc-workbench-page "},s.default.createElement("div",{className:"nc-workbench-page-container "+(e?"nc-workbench-suck":""),ref:"ncWorkbenchPageScroll"},this.props.header?this.props.header:null,s.default.createElement("div",{onMouseMove:this.handleMouseMove.bind(this),onMouseUp:this.handleMouseUp.bind(this),ref:"ncWorkbenchPageOwnpage",className:"nc-workbench-ownpage "+(2===this.props.children.length?"nc-workbench-ownpage-all":"")+" "+(this.props.className||"")},this.props.children))))}}]),t}(c.Component),f=function(e){function t(e){return(0,l.default)(this,t),(0,o.default)(this,(t.__proto__||(0,n.default)(t)).call(this,e))}return(0,r.default)(t,e),(0,i.default)(t,[{key:"handleMouseDown",value:function(){h=!0}},{key:"render",value:function(){return s.default.createElement("div",{id:"layoutLeft",className:"nc-workbench-ownpage-left "+(this.props.className||""),style:this.props.style?this.props.style:{background:"#f6f6f6"}},s.default.createElement("span",{className:"layout-drag-block",onMouseDown:this.handleMouseDown.bind(this)}),this.props.children)}}]),t}(c.Component),m=function(e){function t(e){(0,l.default)(this,t);var a=(0,o.default)(this,(t.__proto__||(0,n.default)(t)).call(this,e));return a.handleBindKey=a.handleBindKey.bind(a),a}return(0,r.default)(t,e),(0,i.default)(t,[{key:"componentDidMount",value:function(){document.addEventListener("keydown",this.handleBindKey,!1)}},{key:"componentWillUnmount",value:function(){document.removeEventListener("keydown",this.handleBindKey,!1)}},{key:"handleBindKey",value:function(e){var t=e||window.event||arguments.callee.caller.arguments[0];t&&t.keyCode&&t.shiftKey&&27==t.keyCode&&this.props.pageback&&this.props.pageback()}},{key:"render",value:function(){return s.default.createElement(u,{areaCode:u.config.HEADER,className:"nc-workbench-page-header "+(this.props.className||"")},this.props.children)}}]),t}(c.Component),k=function(e){function t(){return(0,l.default)(this,t),(0,o.default)(this,(t.__proto__||(0,n.default)(t)).apply(this,arguments))}return(0,r.default)(t,e),(0,i.default)(t,[{key:"render",value:function(){return s.default.createElement("div",{className:"nc-workbench-ownpage-right "+(this.props.className||"")},this.props.children)}}]),t}(c.Component);g.propTypes={children:d.default.any.isRequired},f.propTypes={children:d.default.any.isRequired},k.propTypes={children:d.default.any.isRequired},t.PageLayout=g,t.PageScrollLayout=b,t.PageLayoutHeader=m,t.PageLayoutLeft=f,t.PageLayoutRight=k},970:function(e,t,a){var n=a(971);"string"==typeof n&&(n=[[e.i,n,""]]);var l={hmr:!0,transform:void 0,insertInto:void 0};a(13)(n,l);n.locals&&(e.exports=n.locals)},971:function(e,t,a){(e.exports=a(12)(!1)).push([e.i,'.nc-workbench-page{position:absolute;top:0;z-index:10;height:100%;width:100%;padding:8px 0 0;display:flex;flex-direction:column}.nc-workbench-page .nc-workbench-page-header{flex:0 0 45px;display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #d9d9d9;border-radius:3px 3px 0 0;background:#fff;padding:0 20px}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn{display:flex;align-items:center;font-size:16px;color:#111;font-weight:700}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn .iconfont{margin-right:16px;border-radius:50%;font-size:16px;padding:0 4px;font-weight:400;line-height:24px;cursor:pointer}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn .iconfont:hover{background:#e8e8e8}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn span[type=ncdiv]{font-size:inherit}.nc-workbench-page .nc-workbench-page-header .pageLayoutHeaderOwn img{margin-right:10px}.nc-workbench-page .nc-workbench-page-header .header_in_icon{display:flex;font-weight:700;font-size:16px;color:#111;line-height:24px}.nc-workbench-page .nc-workbench-page-header .header_in_icon span[type=ncdiv]{font-size:inherit}.nc-workbench-page .nc-workbench-ownpage{min-width:800px;height:calc(100% - 61px);border-radius:0 0 3px 3px}.nc-workbench-page .nc-workbench-ownpage-all{display:flex;justify-content:flex-start;background:#fff;-webkit-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none}.nc-workbench-page .nc-workbench-ownpage-left{position:relative;height:100%;min-width:280px}.nc-workbench-page .nc-workbench-ownpage-left .layout-drag-block{position:absolute;right:-3px;z-index:10;top:50%;-webkit-transform:translateY(-50%);transform:translateY(-50%);display:block;padding:0 3px;height:100%;border-radius:3px;cursor:w-resize}.nc-workbench-page .nc-workbench-ownpage-left .layout-drag-block:after{content:"";display:block;width:1px;height:100%;background:#d9d9d9}.nc-workbench-page .nc-workbench-ownpage-left .layout-drag-block:hover:after{width:2px;background:rgba(0,0,0,.54)}.nc-workbench-page .nc-workbench-ownpage-right{min-width:50%;flex:1}.nc-workbench-page-scroll{height:100%;width:100%;overflow-y:auto}.nc-workbench-page-scroll .nc-workbench-page-container{margin-top:0;min-height:100%;background:#fff;border-radius:3px}.nc-workbench-page-scroll .nc-workbench-page-container .nc-workbench-page-header{position:static;transition:top .7s ease;height:46px}.nc-workbench-page-scroll .nc-workbench-page-container .suck-table-header{position:static;transition:top .7s ease}.nc-workbench-page-scroll .nc-workbench-suck{padding-top:80px}.nc-workbench-page-scroll .nc-workbench-suck .nc-workbench-page-header{position:fixed;z-index:1;top:44px}.nc-workbench-page-scroll .nc-workbench-suck .suck-table-header{position:fixed;z-index:1;top:94px}@media screen and (min-width:1280px){.nc-workbench-page{padding:8px 16px}}@media screen and (min-width:1440px){.nc-workbench-page{padding:8px 16px}}@media screen and (min-width:1600px){.nc-workbench-page{padding:8px 16px}}',""])}}]);
//# sourceMappingURL=ErrorCenter.js.map