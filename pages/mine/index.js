Page({

    /**
     * 页面的初始数据
     */
    data: {
        userInfo: {
            avatar: 'https://7365-sesj-ui-8gj1qi9v0eda77e8-1309422520.tcb.qcloud.la/Avatar/oiO_P5f8Fgl6C6C90mYpW_uDh5X4.png?sign=2f4aec8f23815ba02cf894e89eed9c77&t=1683967247',
            name: '蔡徐坤',
            signature: '欢迎使用智能送餐系统'
        },
        menuList: [
            {
                title: '场景',
                icon: {
                    name: 'control-platform',
                    color: '#333333'
                },
                value: 'scene',
                func: 'goScene'
            },
            {
                title: '设置',
                icon: {
                    name: 'setting',
                    color: '#333333'
                },
                value: 'setting',
                func: 'opensetting'
            },
            {
                title: '清除缓存',
                icon: {
                    name: 'delete',
                    color: '#333333'
                },
                value: 'delCache',
                func: 'delCache'
            },
        ]
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        const windowInfo = wx.getWindowInfo();
        this.setData({windowInfo});
    },

    editProfile() {
        wx.navigateTo({
          url: '/pages/user-info/index/index',
        })
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.getTabBar().setData({
            value: 'mine',
        });
    },


    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    goScene() {
        wx.navigateTo({
          url: '/pages/scenes/index/index',
        })
    },
    opensetting() {
        wx.openSetting();
    },
    delCache() {
        wx.showModal({
            content: '确定要清除所有缓存吗？',
            success: res => {
                if (res.confirm) {
                    wx.clearStorage();
                    wx.showToast({
                      title: '清除完毕',
                      icon: 'none'
                    });
                }
            },
            fail: err => console.log,
        });
    }
})