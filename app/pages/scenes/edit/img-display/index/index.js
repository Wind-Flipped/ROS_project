Page({

    data: {
        bgs: [
            '#F5EAC7',
            '#D8D2EA',
            '#ECDADA',
            '#D3E8F3',
            '#DEE3BE',
            '#EEDFD3',
            '#F1E1CD',
            '#DADDEA',
            '#D1DBF1'
        ]
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        const windowInfo = wx.getWindowInfo();
        const itemWidth = 200;
        let itemHeight = itemWidth / windowInfo.screenWidth * windowInfo.screenHeight;
        this.setData({itemHeight,itemWidth});
    },
    goApply(e) {
        let {idx} = e.currentTarget.dataset;
        wx.navigateTo({
          url: '/pages/scenes/edit/img-display/apply/index?bg=' + this.data.bgs[idx],
        });
    }
})