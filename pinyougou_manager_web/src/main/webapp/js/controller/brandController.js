window.onload = function () {
//vue对象
    var app = new Vue({
        el: "#app",
        data: {
            //声明数据列表变量，供v-for使用
            entity: {},
            list: [],
            pageNo: 1,//当前页
            pages: 1,//返回的总页数先声明
            ids: [],
            searchEntity: {},
            //商品状态
            status: ['未审核','审核中', '已审核', '审核未通过', '关闭'],
        },
        methods: {
            findAll: function () {
                axios.get("/brand/findAll.do").then(function (response) {
                    app.list = response.data;

                }).catch(function (err) {
                    console.log(err);
                });

            },
            findPage: function (pageNo) {
                axios.post("/brand/findPage.do?pageNum=" + pageNo + "&pageSize=" + 10, this.searchEntity).then(function (response) {
                    app.pages = response.data.pages;
                    app.list = response.data.rows;
                    app.pageNo = pageNo
                })

            },
            add: function () {
                //console.log(this.entity);
                var url = "/brand/add.do";
                if (this.entity.id != null) {
                    url = "/brand/update.do";
                }

                axios.post(url, this.entity).then(function (response) {
                    if (response.data.success) {
                        vue.findPage(vue.pageNo);

                    } else {
                        alert(response.data.message);

                    }
                });

            },
            findById: function (id) {
                axios.get("/brand/getById.do?id=" + id).then(function (response) {
                    vue.entity = response.data;
                })

            },
            dele: function () {
                axios.get("/brand/delete.do?ids=" + this.ids).then(function (response) {
                    if (response.data.success) {
                        vue.findPage(vue.pageNo);
                        vue.ids = [];

                    } else {
                        alert(response.data.message);

                    }
                })
            },


            goPage: function (page) {
                vue.$children[0].goPage(page);
            },

            //审核操作
            updateStatus: function (status) {
                axios.get("/brand/updateStatus.do?ids=" + this.ids + "&status=" + status)
                    .then(function (response) {
                        if (response.data.success) {
                            //刷新数据
                            app.findPage(app.pageNo);
                            //清空勾选的ids
                            app.ids = [];
                        } else {
                            alert(response.data.message);
                        }
                    });
            }


        },
        //初始化调用
        created: function () {
            //this.findAll();
            this.findPage(1);

        }

    });
}