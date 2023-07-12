const app = getApp();
const api = require('../../utils/api');
import config from '../../utils/config';
Page({
    data: {
        //  窗口信息
        windowInfo: {},
        userInfo: null,
        scenes: [],
        active: 0,
        navOpacity: 1,
        showPopup: false,
        device: null
    },
    showPopup() {
        this.setData({
            showPopup: true
        });
    },
    onVisibleChange(e) {
        this.setData({
            showPopup: e.detail.visible
        });
    },

    onLoad() {
        const windowInfo = wx.getWindowInfo();
        this.setData({
            windowInfo,
            robotImg: app.globalData.robotImg
        });

    },
    onPageScroll(e) {
        const {
            scrollTop
        } = e;
        const _ts = this;
        wx.createSelectorQuery().select('.navbar').boundingClientRect().exec(res => {
            _ts.setData({
                scrollTop,
                sticky: scrollTop >= res[0].height,
                navOpacity: Math.max(1 - scrollTop * 0.03, 0)
            });
        })
    },
    onShow() {
        this.getTabBar().setData({
            value: 'index',
        });
        api.request(undefined, {}, '/map/getAllMaps').then(res => {
            let scenes = res.data;
            scenes.forEach(item => {
                item.map = [config.host, item.url].join('/');
            });
            this.setData({
                scenes,
                userInfo: getApp().globalData.userInfo,
            });
        });
        api.request('GET', {}, '/ros/isConnect').then(res => {
            console.log(res);
            if (res.code === 100) {
                this.setData({
                    device: 1
                });
            }
        }).catch(err => {
            console.log(err);
        })
    },
    tabChange(e) {
        this.setData({
            active: e.currentTarget.dataset.idx
        })
    },
    swiperChange(e) {
        this.setData({
            active: e.detail.current
        });
    },
    editScene(e) {
        const data = encodeURIComponent(JSON.stringify({
            scenes: this.data.scenes,
            scene: e.currentTarget.dataset.scene
        }));

        wx.navigateTo({
            url: '/pages/scenes/edit/index/index?data=' + data,
        }).then(res => {
            this.setData({
                showPopup: false
            });
        });
    },
    navigate(e) {
        const {
            page
        } = e.currentTarget.dataset;
        wx.navigateTo({
            url: '/pages/' + page + '/index',
        }).then(res => {
            this.setData({
                showPopup: false
            });
        });
    },
    navToFlagEdit(e) {
        let {
            scene
        } = e.currentTarget.dataset;
        let data = encodeURIComponent(JSON.stringify(scene));
        wx.navigateTo({
            url: '/pages/flag-edit/index?scene=' + data,
        });
    },
    addDevice(e) {
        wx.showActionSheet({
            itemList: ['扫描二维码', '手动输入'],
            success: res => {
                if (res.tapIndex === 0) {
                    //    扫码获取ip: 10.193.151.216
                    wx.scanCode().then(res => {
                        this.linkDevice(res.result);
                    }).catch(err => {
                        console.log(err);
                    })
                }
                //    手动输入
                else if (res.tapIndex === 1) {
                    wx.showModal({
                        editable: true,
                        placeholderText: '输入机器人ip地址',
                    }).then(res => {
                        if (res.confirm && res.content.length > 0) {
                            this.linkDevice(res.content);
                        }
                    }).catch(err => {
                        console.log(err);
                    })
                }
            },
            fail: err => {
                console.log(err);
            }
        })
    },
    linkDevice(ip) {
        api.request('GET', {
            rosIp: ip
        }, '/ros/connect').then(res => {
            if (res.code === 100) {
                this.setData({
                    device: 1
                });
            }
        }).catch(err => {
            console.log(err);
        })
    },
    toMap(e) {
        const id = e.currentTarget.dataset.id;
        wx.navigateTo({
            url: '/pages/map/index?id=' + id,
        });
    },
    navToWork(e) {
        let {
            scene
        } = e.currentTarget.dataset;
        let data = encodeURIComponent(JSON.stringify(scene));
        wx.navigateTo({
            url: '/pages/robot/index?scene=' + data,
        });
    },
    imgError(e) {
        this.setData({
            ['scenes[' + e.target.dataset.idx + '].map']: null
        })
    }
})