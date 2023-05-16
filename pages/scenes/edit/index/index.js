Page({

    /**
     * 页面的初始数据
     */
    data: {
        scene: {
            name: '名称',
            bg: '#F5EAC7'
        },
        scenes: [],
        edit: false,
        idx: -1,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        const data = JSON.parse(decodeURIComponent(options.data));
        const name = data.name;
        const scenes = data.scenes;
        this.setData({
            ['scene.name']: name,
            scenes,
            idx: scenes.findIndex(item => item === name),
        });
        //  TODO:   网络请求
    },

    navback() {
        if (this.data.edit) {
            wx.showModal({
                title: '场景编辑信息尚未保存',
                content: '场景编辑信息还未保存，请确定是否要保存编辑信息。',
                success: res => {
                    if (res.confirm) {
                        this.save();
                    }
                    else {
                        wx.navigateBack();
                    }
                }
            })
        } else {
            wx.navigateBack()
        }
    },
    /**
     * 保存更改
     */
    save() {
        const pages = getCurrentPages();
        this.data.scenes[this.data.idx] = this.data.scene.name;
        pages[pages.length - 2].setData({
            scenes: this.data.scenes,
        });
        //  TODO：网络请求
        wx.navigateBack();
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
        wx.showModal({
            title: '设置场景名称',
            editable: true,
            placeholderText: '请输入场景名称',
            success: res => {
                if (res.confirm && res.content.length > 0 && this.data.scenes.findIndex(item => item === res.content) === -1) {
                    this.setData({
                        ['scene.name']: res.content,
                        edit: true
                    });
                }
            }
        })
    }
})