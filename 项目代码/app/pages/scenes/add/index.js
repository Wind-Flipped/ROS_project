const api = require('../../../utils/api');
Page({

    /**
     * 页面的初始数据
     */
    data: {
        scene: {
            name: '',
            bg: '#F5EAC7'
        },
        scenes: []
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        const data = JSON.parse(decodeURIComponent(options.data));
        const name = data.name;
        this.setData({
            ['scene.name']: name,
            scenes: data.scenes
        });
    },
    editImg() {
        wx.navigateTo({
            url: '/pages/scenes/edit/img-display/index/index',
        })
    },
    modifyName() {
        if (this.data.scene.name === '默认场景') {
            return;
        }
        const _ts = this;
        wx.showModal({
            title: '设置场景名称',
            editable: true,
            placeholderText: '请输入场景名称',
            success: res => {
                if (res.confirm && res.content.length > 0 && _ts.data.scenes.findIndex(item => item === _ts.data.scene.name) === -1) {
                    _ts.setData({
                        ['scene.name']: res.content,
                    });
                    console.log(res.content);
                }
            }
        })
    },
    add() {
        if (this.data.scenes.findIndex(item => item.name===this.data.scene.name) === -1) {
            api.request('GET', {mapName: this.data.scene.name, bg: this.data.scene.bg}, '/map/createMap').then(res => {
                console.log(res);
            })
            this.data.scenes.push({
                name: this.data.scene.name,
                bg: this.data.scene.bg
            });
            wx.navigateBack();
        } else {
            wx.showModal({
                content: '名称重复',
                showCancel: false,
            });
        }
    }
})