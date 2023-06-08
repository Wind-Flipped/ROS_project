const api = require('../../utils/api');
import config from '../../utils/config';
Page({

    /**
     * 页面的初始数据
     */
    data: {
        id: -1,
        map: '',
        saving: false,
        interval: null,
        mapping: false,
    },

    onLoad(options) {
        console.log(options);
        const id = options.id || 142;
        this.setData({
            id
        });
    },
    begin() {
        console.log("test");
        api.request('GET', {
            mapId: this.data.id,
            type: 1
        }, '/ros/changeMode').then(res => {
            console.log(res);
            this.setData({
                mapping: true
            });
            let interval = setInterval(() => {
                this.setData({map: null});
                api.request('GET', {}, '/map/getPicture').then(res => {
                    console.log(res);
                    this.setData({
                        map: [config.host, res.data].join('/'),
                    });
                });
            }, 2000);
            this.setData({
                interval
            });
        }).catch(err => {
            console.log(err);
        });
    },
    save() {
        this.setData({
            saving: true,
        })
        wx.showLoading({
            title: '保存中...',
            mask: true
        });
        const _ts = this;
        api.request('POST', {}, '/ros/endMode').then(res => {
            if (res.code === 200) {
                setTimeout(function () {
                    wx.hideLoading().then(() => {
                        wx.showToast({
                            title: '保存成功',
                            icon: 'none'
                        });
                        _ts.setData({
                            saving: false
                        });
                        clearInterval(_ts.data.interval);
                        setTimeout(() => {
                            if (getCurrentPages().length >= 2) {
                                wx.navigateBack();
                            }
                        }, 2000);
                    });
                }, 3000);
            }
        });
    }

})