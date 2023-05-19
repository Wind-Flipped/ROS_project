Page({

    /**
     * 页面的初始数据
     */
    data: {
        scene: {
            name: '名称',
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
        //  TODO:   网络请求
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
        if (this.data.scenes.findIndex(item => item===this.data.scene.name) === -1) {
            const pages = getCurrentPages();
            this.data.scenes.push(this.data.scene.name);
            pages[pages.length - 2].setData({
                scenes: this.data.scenes
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