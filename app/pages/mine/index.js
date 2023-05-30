const app = getApp();
const api = require('../../utils/api');
Page({

    /**
     * 页面的初始数据
     */
    data: {
        userInfo: null,
        menuList: [{
                title: '场景',
                icon: {
                    name: 'control-platform',
                    color: '#333333'
                },
                value: 'scenes/index',
                func: 'navigate',
                data: ''
            },
            {
                title: '用户测试',
                icon: {
                    name: 'tools',
                    color: '#333333'
                },
                value: 'test',
                func: 'navigate',
                data: ''
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
        this.setData({
            windowInfo
        });
    },

    editProfile() {
        let userInfo = encodeURIComponent(JSON.stringify(this.data.userInfo));
        wx.navigateTo({
            url: '/pages/user-info/index/index?userInfo=' + userInfo,
        });
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.getTabBar().setData({
            value: 'mine',
        });
        this.setData({
            userInfo: app.globalData.userInfo,
        });
    },
    navigate(e) {
        wx.navigateTo({
          url: '/pages/' + e.currentTarget.dataset.page + '/index?data=' + e.currentTarget.dataset.data,
        });
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