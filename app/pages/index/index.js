const app = getApp();
const api = require('../../utils/api');
Page({
    data: {
        //  窗口信息
        windowInfo: {},
        userInfo: null,
        scenes: [],
        active: 0,
        navOpacity: 1,
        showPopup: false,
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
                item.map = item.url
            });
            this.setData({
                scenes,
                userInfo: getApp().globalData.userInfo,
            });
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
        let {scene} = e.currentTarget.dataset;
        let data = encodeURIComponent(JSON.stringify(scene));
        wx.navigateTo({
          url: '/pages/flag-edit/index?scene=' + data,
        });
    }
})