Page({

    /**
     * 页面的初始数据
     */
    data: {
        bg: ''
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.setData({
            bg: options.bg
        });
    },

    confirm() {
        const pages = getCurrentPages();
        if (pages.length >= 3) {
            pages[pages.length - 3].setData({
                ['scene.bg']: this.data.bg,
                edit:true
            });
            wx.navigateBack({delta: 2});
        }
    }
})