const api = require('../../../utils/api');
const app = getApp();
Page({

    data: {
        userInfo: null,
    },

    onLoad(options) {
        this.setData({
            userInfo: JSON.parse(decodeURIComponent(options.userInfo))
        });
    },

    onShow() {

    },

    editInfo(e) {
        const type = e.currentTarget.dataset.type;
        const _ts = this;
        let userInfo = this.data.userInfo;
        switch (type) {
            case 'name':
                wx.showModal({
                    title: '修改昵称',
                    editable: true,
                    placeholderText: '输入昵称',
                    success: res => {
                        if (res.confirm && res.content.length > 0) {
                            userInfo.userName = res.content;
                            api.request('POST',{
                                user: userInfo
                            }, '/user/update').then(res => {
                                app.globalData.userInfo = res.data;
                                this.setData({userInfo: res.data});
                            }).catch(err => {
                                console.log(err);
                            });
                        }
                    },
                    fail: console.log
                });
                break;
            case 'avatar':
                wx.chooseMedia({
                    count: 1,
                    mediaType: ['image', ],
                    sourceType: ['album', 'camera'],
                    camera: 'back',
                    success: res => {
                        userInfo.avatar = res.tempFiles[0].tempFilePath;
                        api.request('POST',{
                            user: userInfo
                        }, '/user/update').then(res => {
                            app.globalData.userInfo = res.data;
                            this.setData({userInfo: res.data});
                        });
                    },
                    fail: console.log
                })
                break;
            default:
                break;
        }
    }

})