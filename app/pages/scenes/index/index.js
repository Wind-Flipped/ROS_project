const api = require('../../../utils/api');
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
    onShow() {
        api.request(undefined, {}, '/map/getAllMaps').then(res => {
            let scenes = res.data;
            console.log(res);
            scenes.forEach(item => {
                item.map = item.url
            });
            this.setData({
                scenes
            });
        })
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
            scene: e.currentTarget.dataset.scene
        }));

        wx.navigateTo({
            url: '/pages/scenes/edit/index/index?data=' + data,
        });
    },
    addScene(e) {
        wx.showModal({
            title: '添加场景',
            editable: true,
            placeholderText: '请输入场景名称',
            success: res => {
                if (res.confirm && res.content.length > 0) {
                    const data = encodeURIComponent(JSON.stringify({
                        scenes: this.data.scenes,
                        name: res.content
                    }));

                    wx.navigateTo({
                        url: '/pages/scenes/add/index?data=' + data,
                    });
                }
            }
        })
    },
    delete(e) {
        let {
            id
        } = e.currentTarget.dataset;
        wx.showModal({
            content: '删除场景后，设备与地图数据将同时被删除，是否确认删除？',
            success: res => {
                if (res.confirm) {
                    api.request('GET', {
                        mapId: id,
                    }, '/map/deleteMap').then(res => {
                        console.log(res);
                        let scenes = this.data.scenes.filter(item => item.id !== id);
                        this.setData({
                            scenes
                        });
                    })
                }
            }
        })
    }
})