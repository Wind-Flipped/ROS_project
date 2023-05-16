
Page({

    /**
     * 页面的初始数据
     */
    data: {
        navTitle: '场景管理',
        //  是否正在编辑
        edit: false,
        scenes: [
            '默认场景',
            '自定义场景1'
        ]
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

    },

    handleEdit() {
        this.setData({
            edit: true,
            navTitle: '编辑'
        })
    },
    handleCloseEdit() {
        this.setData({
            edit: false,
            navTitle: '场景管理'
        })
    },
    editScene(e) {
        const data = encodeURIComponent(JSON.stringify({
            scenes: this.data.scenes,
            name: e.currentTarget.dataset.name
        }));

        wx.navigateTo({
          url: '/pages/scenes/edit/index/index?data='+ data,
        });
    },
    addScene(e) {
        wx.showModal({
          title: '添加场景',
          editable:true,
          placeholderText: '请输入场景名称',
          success: res => {
              if (res.confirm && res.content.length > 0) {
                const data = encodeURIComponent(JSON.stringify({
                    scenes: this.data.scenes,
                    name: res.content
                }));
        
                wx.navigateTo({
                  url: '/pages/scenes/add/index?data='+ data,
                });
              }
          }
        })
    },
    delete(e) {
        let {name} = e.currentTarget.dataset;
        wx.showModal({
          content: '删除场景后，设备与地图数据将同时被删除，是否确认删除？',
          success: res => {
              if (res.confirm) {
                  let scenes = this.data.scenes.filter(item => item != name);
                  this.setData({scenes});
              }
          }
        })
    }
})