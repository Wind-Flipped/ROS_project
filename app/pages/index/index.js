// index.js
// 获取应用实例
const app = getApp()
Page({
    data: {
        //  窗口信息
        windowInfo: {},
        userInfo: {
            avatar: 'https://7365-sesj-ui-8gj1qi9v0eda77e8-1309422520.tcb.qcloud.la/Avatar/oiO_P5f8Fgl6C6C90mYpW_uDh5X4.png?sign=2f4aec8f23815ba02cf894e89eed9c77&t=1683967247',
            name: '蔡徐坤',
            signature: '大家好，我是练习时长两年半的...'
        },
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
        let scenes = [{
                name: '默认场景',
                //  壁纸
                bg: '#F5EAC7',
                //  地图
                map: 'https://pic2.zhimg.com/v2-7b66fab15c14f376bb8db20aafefdca9_r.jpg',
                //  设备
                device: {
                    name: '',
                    addr: '',
                }

            },
            {
                name: '自定sfasdfasdfasdfasfda义场景1',
                //  壁纸
                bg: '#ECDADA',
                //  地图
                map: undefined,
                //  设备
                device: undefined
            },
        ];
        this.setData({
            scenes
        });
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
            scenes: this.data.scenes.map(item => item.name),
            name: e.currentTarget.dataset.name
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
        wx.navigateTo({
          url: '/pages/flag-edit/index',
        });
    }
})