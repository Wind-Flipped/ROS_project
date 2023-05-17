Page({

    /**
     * 页面的初始数据
     */
    data: {
        navTitle: '默认场景',
        mode: '迎宾',
        modes: ['迎宾', '送餐'],
        working: false,
        battery: 98,
        flags: ['1号标记','2号标记'],
        welcomeAddr: '1号标记',
        from: '1号标记',
        to: '2号标记'
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

    },
    switchMode() {
        if (this.data.working) {
            wx.showToast({
              title: '请先停止工作',
              icon: 'none'
            });
            return;
        }

        const _ts = this;
        wx.showActionSheet({
          itemList: _ts.data.modes,
          success: res => {
              this.setData({mode: _ts.data.modes[res.tapIndex]});
          },
          fail: err => console.log,
        });
    },
    switchWorking(e) {
        let working = !this.data.working;
        this.setData({
            working,
        })
    }
})