//页面初始化完成后再创建Vue对象
window.onload=function () {
	//创建Vue对象
	var app = new Vue({
		//接管id为app的区域
		el:"#app",
		data:{
			//声明数据列表变量，供v-for使用
			list:[],
			//总页数
			pages:1,
			//当前页
			pageNo:1,
			//声明对象:{specification:{规格名称信息},specificationOptionList:[规格选项列表]}
			entity:{specification:{},specificationOptionList:[]},
			//将要删除的id列表
			ids:[],
			//搜索包装对象
			searchEntity:{},
			//商品状态
			status: ['未审核','审核中', '已审核', '审核未通过', '关闭'],

		},
		methods:{
			//查询所有
			findAll:function () {
				axios.get("../specification/findAll.do").then(function (response) {
					//vue把数据列表包装在data属性中
					app.list = response.data;
				}).catch(function (err) {
					console.log(err);
				});
			},
			//分页查询
			findPage:function (pageNo) {
				axios.post("../specification/findPage.do?pageNo="+pageNo+"&pageSize="+10,this.searchEntity)
					.then(function (response) {
						app.pages = response.data.pages;  //总页数
						app.list = response.data.rows;  //数据列表
						app.pageNo = pageNo;  //更新当前页
					});
			},
			//让分页插件跳转到指定页
			goPage:function (page) {
				app.$children[0].goPage(page);
			},
			//新增
			add:function () {
				var url = "../specification/add.do";
				if(this.entity.specification.id != null){
					url = "../specification/update.do";
				}
				axios.post(url, this.entity).then(function (response) {
					if (response.data.success) {
						//刷新数据，刷新当前页
						app.findPage(app.pageNo);
					} else {
						//失败时显示失败消息
						alert(response.data.message);
					}
				});
			},
			//跟据id查询
			getById:function (id) {
				axios.get("../specification/getById.do?id="+id).then(function (response) {
					app.entity = response.data;
				})
			},
			//批量删除数据
			dele:function () {
				axios.get("../specification/delete.do?ids="+this.ids).then(function (response) {
					if(response.data.success){
						//刷新数据
						app.findPage(app.pageNo);
						//清空勾选的ids
						app.ids = [];
					}else{
						alert(response.data.message);
					}
				})
			},
			//表格行添加逻辑
			addTableRow:function () {
				//向specificationOptionList数组追加一个空对象
				this.entity.specificationOptionList.push({});
			},
			/**
			 * 删除表格行
			 * @param index 删除的下标
			 */
			deleteTableRow:function (index) {
				//删除元素-splice(下标，删除的个数)
				this.entity.specificationOptionList.splice(index, 1);
			},
			//新增表格行逻辑
			addTableRow:function () {
				this.entity.specificationOptionList.push({});
				
			},
			//删除表格行逻辑
			deleteTableRow:function (index) {
				//splice(删除开始索引, 删除个数)
				this.entity.specificationOptionList.splice(index,1);

			},
			//审核操作
			updateStatus:function (status) {
				axios.get("/specification/updateStatus.do?ids=" + this.ids + "&status=" + status)
					.then(function (response) {
						if(response.data.success){
							//刷新数据
							app.findPage(app.pageNo);
							//清空勾选的ids
							app.ids = [];
						}else{
							alert(response.data.message);
						}
					});
			}


		},
		//Vue对象初始化后，调用此逻辑
		created:function () {
			//调用用分页查询，初始化时从第1页开始查询
			this.findPage(1);
		}
	});
}
