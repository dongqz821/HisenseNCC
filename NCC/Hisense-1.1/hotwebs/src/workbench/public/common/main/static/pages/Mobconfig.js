(window.webpackJsonp=window.webpackJsonp||[]).push([[20],{1023:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.DynamicComponents=void 0,t.getDynamicComponent=function(e){window[e]||function(e){var t="/nccloud/resources/"+e+".js";(0,o.loadScript)(t)}(e);return window[e]&&window[e].default};var o=a(19);t.DynamicComponents={NCUploader:"uap/common/components/NCUploader/index"}},1253:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.urls=t.pagecode=t.tableid=void 0,t.getDatas=function(){var e=arguments[1];(0,o.ajax)({url:s.query,data:{},success:function(t){t.success&&null!=t.data&&e.editTable.setTableData("grid",t.data.grid)}})},t.modifierMeta=function(e,t,a){t[l].showindex=!0;var i={label:(0,n.langCheck)("Mobconfig-000011","pages",a),attrcode:"opr",key:"opr",itemtype:"customer",fixed:"right",className:"table-opr",visible:!0,render:function(t,i,d){var c=e.editTable.getStatus(l);return"add"==c||"edit"==c?React.createElement("div",{className:"currency-opr-col"}):e.button.createOprationButton(["tabledel"],{area:"opr",buttonLimit:3,onButtonClick:function(e,t){return function(e,t,a,i,d,c){switch(t){case"tabledel":(0,o.ajax)({url:s.delete,data:{pks:[i.values.pk.value]},method:"post",success:function(t){e.editTable.deleteTableRowsByIndex(l,d),(0,o.toast)({title:(0,n.langCheck)("Mobconfig-000000","pages",c),color:"success"})}})}}(e,t,0,i,d,a)}})}};return t[l].items.push(i),t};var o=a(919),n=a(19),l=t.tableid="grid",s=(t.pagecode="10181808_mobappconfig",t.urls={save:"/nccloud/platform/mobappconfig/save.do",query:"/nccloud/platform/mobappconfig/query.do",delete:"/nccloud/platform/mobappconfig/delete.do",upload:"/nccloud/platform/attachment/ucgupload.do"})},962:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o,n,l=m(a(21)),s=m(a(5)),i=m(a(7)),d=m(a(4)),c=m(a(6)),r=a(1),u=m(r),p=a(919),b=a(19),h=a(365),g=a(1253),f=a(1023);function m(e){return e&&e.__esModule?e:{default:e}}var v=p.base.NCMessage,C=p.base.NCUpload,k=(o=function(e){function t(e){(0,s.default)(this,t);var a=(0,d.default)(this,(t.__proto__||(0,l.default)(t)).call(this,e));return n.call(a),a.state={showUploader:!1,billId:"",langJson:{},uploadParams:{appcode:"",pk:""}},a.uploadRef=u.default.createRef(),a}return(0,c.default)(t,e),(0,i.default)(t,[{key:"componentDidMount",value:function(){var e=this;(0,h.getMulti)({moduleId:"Mobconfig",domainName:"workbench",callback:function(t){e.setState({langJson:t}),e.initTemplate(e.props,t)}}),(0,g.getDatas)(!1,this.props)}},{key:"updateButtonStatus",value:function(){var e=this.props.editTable.getCheckedRows(g.tableid).length;0===e?this.props.button.setDisabled({delete:!0,upload:!0}):1===e?this.props.button.setDisabled({delete:!1,upload:!1}):this.props.button.setDisabled({delete:!1,upload:!0}),"edit"===this.props.editTable.getStatus(g.tableid)?(this.props.button.setButtonsVisible({add:!0,edit:!1,save:!0,cancel:!0,delete:!0}),this.props.button.setMainButton(["add"],!1)):(this.props.button.setButtonsVisible({add:!0,edit:!0,delete:!0,save:!1,cancel:!1}),this.props.button.setMainButton(["add"],!0))}},{key:"cancelConfirmModal",value:function(e){this.props.editTable.cancelEdit(g.tableid),this.props.editTable.showColByKey(g.tableid,"opr"),this.updateButtonStatus(),window.onbeforeunload=null}},{key:"onDeleteSys",value:function(e){var t=this,a=this.state.langJson,o=this.props.editTable.getCheckedRows(g.tableid),n=[],l=[];o.forEach((function(e){l.push(e.data.values.pk.value),n.push(e.index)})),(0,p.ajax)({url:g.urls.delete,data:{pks:l},success:function(e){var o=e.success;if(o){for(var n=t.props.editTable.getAllData(g.tableid),l=[],s=0;s<n.rows.length;s++)1!=n.rows[s].selected&&l.push(n.rows[s]);n.rows=l,t.props.editTable.setTableData(g.tableid,n),(0,p.toast)({title:(0,b.langCheck)("Mobconfig-000000","pages",a),color:"success"})}}}),this.props.modal.close("delConfirmModal")}},{key:"onButtonClick",value:function(e,t){var a=this,o=this.state.langJson;switch(t){case"upload":var n=this.props.editTable.getCheckedRows(g.tableid)[0].data.values,l=n.appcode,s=n.pk;this.setState({uploadParams:{appcode:l.value,pk:s.value}}),this.uploadRef&&this.uploadRef.current.click();break;case"add":var i=this.props.editTable.getNumberOfRows(g.tableid);this.props.editTable.addRow(g.tableid,i,!0,{}),this.props.editTable.setStatus(g.tableid,"edit"),this.updateButtonStatus(),window.onbeforeunload=function(){return""},this.setState(this.state);break;case"edit":this.props.editTable.setStatus(g.tableid,"edit"),this.updateButtonStatus(),window.onbeforeunload=function(){return""},this.setState(this.state);break;case"cancel":(0,p.promptBox)({color:"warning",title:(0,b.langCheck)("Mobconfig-000002","pages",o),content:(0,b.langCheck)("Mobconfig-000003","pages",o),beSureBtnName:(0,b.langCheck)("Mobconfig-000004","pages",o),cancelBtnName:(0,b.langCheck)("Mobconfig-000005","pages",o),beSureBtnClick:this.cancelConfirmModal.bind(this),cancelBtnClick:function(){a.props.modal.close("cancelConfirmModal"),a.setState(a.state)}});break;case"save":this.btnSave();break;case"delete":var d=this.props.editTable.getCheckedRows(g.tableid);if(0==d.length)return void v.create({content:(0,b.langCheck)("Mobconfig-000012","pages",o),color:"error",position:"bottom"});if("edit"==e.editTable.getStatus(g.tableid)){for(var c=[],r=0;r<d.length;r++)d[r].data.selected=!1,c.push(d[r].index);return void this.props.editTable.deleteTableRowsByIndex(g.tableid,c)}(0,p.promptBox)({color:"warning",title:(0,b.langCheck)("Mobconfig-000002","pages",o),content:(0,b.langCheck)("Mobconfig-000007","pages",o),beSureBtnName:(0,b.langCheck)("Mobconfig-000004","pages",o),cancelBtnName:(0,b.langCheck)("Mobconfig-000005","pages",o),beSureBtnClick:this.onDeleteSys.bind(this)})}}},{key:"render",value:function(){var e=this,t=this.state,a=t.langJson,o=t.uploadParams,n=this.props,l=n.button,s=n.editTable,i=n.modal,d=n.BillHeadInfo.createBillHeadInfo,c=s.createEditTable,r=p.base.NCDiv,h=l.createButtonApp,m=i.createModal,v=(0,f.getDynamicComponent)(f.DynamicComponents.NCUploader);return u.default.createElement("div",{className:"nc-single-table"},u.default.createElement(r,{areaCode:r.config.HEADER,className:"nc-singleTable-header-area",style:{borderBottom:"none"}},u.default.createElement("div",{className:"header-title-search-area"},d({title:(0,b.langCheck)("Mobconfig-000008","pages",a),initShowBackBtn:!1})),u.default.createElement("div",{className:"header-button-area"},h({area:"grid",buttonLimit:1,onButtonClick:this.onButtonClick.bind(this),popContainer:document.querySelector(".header-button-area")})),u.default.createElement(C,{name:"file",action:g.urls.upload,showUploadList:!1,data:o,onChange:this.handleUploadChange},u.default.createElement("button",{style:{display:"none"},ref:this.uploadRef}))),u.default.createElement("div",{className:'nc-singleTable-table-area"'},c(g.tableid,{useFixedHeader:!0,isAddRow:!0,selectedChange:this.updateButtonStatus.bind(this),statusChange:this.updateButtonStatus.bind(this),showIndex:!0,showCheck:!0,adaptionHeight:!0}),m("delConfirmModal",{title:(0,b.langCheck)("Mobconfig-000009","pages",a),content:(0,b.langCheck)("Mobconfig-000007","pages",a),userControl:!0,beSureBtnClick:this.onDeleteSys.bind(this),cancelBtnClick:function(){e.props.modal.close("delConfirmModal")}}),m("cancelConfirmModal",{title:(0,b.langCheck)("Mobconfig-000002","pages",a),content:(0,b.langCheck)("Mobconfig-000010","pages",a),beSureBtnClick:this.cancelConfirmModal.bind(this),cancelBtnClick:function(){e.props.modal.close("cancelConfirmModal")}}),this.state.showUploader&&u.default.createElement(v,{billId:this.state.billId,noControlPermission:!0,onHide:function(){e.setState({showUploader:!1})}})))}}]),t}(r.Component),n=function(){var e=this;this.initTemplate=function(t,a){t.createUIDom({pagecode:g.pagecode},(function(o){var n=o.template;n=(0,g.modifierMeta)(t,n,a),t.meta.setMeta(n),o.button&&t.button.setButtons(o.button),(0,g.getDatas)(!1,t),t.button.setButtonsVisible({add:!0,edit:!0,delete:!0,save:!1,cancel:!1}),e.props.button.setDisabled({delete:!0,upload:!0})}))},this.btnSave=function(t){var a=e.state.langJson;e.props.editTable.filterEmptyRows(g.tableid);var o=e.props.editTable.getChangedRows(g.tableid);if(o.length<1)return e.props.editTable.cancelEdit(g.tableid),window.onbeforeunload=null,void(0,p.toast)({title:(0,b.langCheck)("Mobconfig-000001","pages",a),color:"success"});for(var n={pageid:g.pagecode,model:{areacode:g.tableid,areaType:"table",pageinfo:null,rows:[]}},l=new Array,s=e.props.editTable.getChangedRows(g.tableid),i=0;i<s.length;i++)(null==s[i].values.appcode||null==s[i].values.appcode.value||s[i].values.appcode.value.length<1)&&(null==s[i].values.appid||null==s[i].values.appid.value||s[i].values.appid.value.length<1)&&(null==s[i].values.appname||null==s[i].values.appname.value||s[i].values.appname.value.length<1)||l.push(s[i]);if(l.length<1)return e.props.editTable.cancelEdit(g.tableid),window.onbeforeunload=null,void(0,p.toast)({title:(0,b.langCheck)("Mobconfig-000001","pages",a),color:"success"});o=l,n.model.rows=o,(0,p.ajax)({url:g.urls.save,data:n,success:function(e){var o=e.success,n=e.data;o&&n&&(this.props.editTable.setTableData(g.tableid,n[g.tableid]),this.props.editTable.cancelEdit(g.tableid),window.onbeforeunload=null,this.setState(this.state),(0,p.toast)({title:(0,b.langCheck)("Mobconfig-000001","pages",a),color:"success"}),t&&t())}.bind(e)})},this.handleUploadChange=function(t){var a=t.file,o=e.state.langJson;if("done"===a.status){var n=a.response;if(n.success){var l=n.data;(0,p.toast)({title:(0,b.langCheck)("Mobconfig-000013","pages",o),color:"success"});var s=e.props.editTable.getCheckedRows(g.tableid)[0].index;e.props.editTable.setRowByIndex(g.tableid,l.grid.rows[0],s)}else{var i=n.error;i&&i.message&&(0,p.toast)({title:i.message,color:"danger"})}}}},o);t.default=k=(0,p.createPage)({billinfo:{billtype:"grid",pagecode:"10181808_mobappconfig",headcode:"head",bodycode:"currtype"},initTemplate:function(){},mutiLangCode:"10140CURTP"})(k)}}]);
//# sourceMappingURL=Mobconfig.js.map