Page({

    data: {
        userInfo: {
            avatar: 'https://7365-sesj-ui-8gj1qi9v0eda77e8-1309422520.tcb.qcloud.la/Avatar/oiO_P5f8Fgl6C6C90mYpW_uDh5X4.png?sign=2f4aec8f23815ba02cf894e89eed9c77&t=1683967247',
            name: '蔡徐坤',
            signature: '欢迎使用智能送餐系统'
        },
    },

    onLoad(options) {

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    editInfo(e) {
        const type = e.currentTarget.dataset.type;
        const _ts = this;
        switch (type) {
            case 'name':
                wx.showModal({
                    title: '修改昵称',
                    editable: true,
                    placeholderText: '输入昵称',
                    success: res => {
                        if (res.confirm && res.content.length > 0) {
                            _ts.setData({
                                ['userInfo.name']: res.content
                            });
                        }
                    },
                    fail: console.log
                });
                break;
            case 'signature':
                wx.showModal({
                    title: '修改签名',
                    editable: true,
                    placeholderText: '输入个性签名',
                    success: res => {
                        if (res.confirm && res.content.length > 0) {
                            _ts.setData({
                                ['userInfo.signature']: res.content
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
                        _ts.setData({
                            ['userInfo.avatar']: res.tempFiles[0].tempFilePath
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