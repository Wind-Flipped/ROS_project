const api = require('../../utils/api');
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
        flags: ['1号标记', '2号标记'],
        to: undefined,
        robot: {},
        transforming: false
    },

    onLoad(options) {
        let scene = JSON.parse(decodeURIComponent(options.scene));
        this.setData({
            scene
        });
        this.setList();
        this.setPower();
        setInterval(function () {
            this.setPower();
        }, 60000);
        let robotInterval = setInterval(() => {
            this.setRobot();
        }, 2000);
        this.setData({
            robotInterval
        });

    },
    setPower() {
        api.request('GET', {}, '/ros/getPower').then(res => {
            if (res.code === 100) {
                this.setData({
                    battery: res.data
                })
            }
        })
    },
    setList() {
        const scene = this.data.scene;
        api.request('GET', {
            mapId: scene.id
        }, '/point/mapToPoints').then(res => {
            this.setData({
                list: res.data.map(item => {
                    return {
                        name: item.name,
                        imgX: item.xaxis,
                        imgY: item.yaxis,
                        img: '/static/image/normal.png',
                        scale: 0.125,
                        id: item.id,
                        type: item.status,
                    }
                }),
                scene
            });
        });
    },
    setRobot() {
        api.request('GET', {}, '/ros/getLocation').then(res => {
            if (res.code === 100) {
                this.setData({
                    robot: {
                        x: res.data.xAxis,
                        y: res.data.yAxis
                    }
                });
            }
        })
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
                this.setData({
                    mode: _ts.data.modes[res.tapIndex]
                });
            },
            fail: err => console.log,
        });
    },
    working(e) {
        wx.showLoading({
            title: '',
            mask: true
        });
        if (this.data.mode === '迎宾') {
            api.request('GET', {
                type: 2,
                mapId: this.data.scene.id
            }, '/ros/changeMode').then(res => {
                if (res.code === 200) {
                    this.setData({working:true})
                    wx.hideLoading().then(() => {
                        wx.showToast({
                            title: '启动成功',
                        });
                    });
                } else {
                    wx.showToast({
                        title: res.code,
                        icon: 'error'
                    })
                }
            })
        }
        else if (this.data.mode === '送餐') {
            api.request('GET', {
                type: 3,mapId: this.data.scene.id
            },'/ros/changeMode').then(res => {
                if (res.code === 200) {
                    wx.hideLoading();
                    this.setData({working:true, transforming: true});
                } else {
                    wx.showToast({
                        title: res.code,
                        icon: 'error'
                    })
                }
            })

        }
    },
    stop(e) {
        api.request('POST', {}, '/ros/endMode').then(res => {
            console.log(res);
            if (res.code === 200) {
                wx.showToast({
                  title: '关闭成功',
                });
                this.setData({working:false});
            }
        });
    },
    onUnload() {
        this.stop();
    },
    /**
     * 确认就餐
    */
    confirmEating() {
        api.request('POST', {}, '/ros/confirmEat').then(res => {
            console.log(res);
            wx.showToast({
              title: '用户确认就餐',
              icon: 'none'
            });
        }).catch(err => {
            console.log(err);
        });
    },
    chooseTar(e) {
        const flag = e.detail.data;
        wx.showModal({
          content: `将${flag.name}(${flag.imgX}, ${flag.imgY})设为送餐终点吗`,
          success: res => {
              if (res.confirm) {
                  this.setData({to: flag});
              }
          }
        });
    },
    confirmRecieve() {
        api.request('POST', {}, '/ros/confirmReceive').then(res => {
            if (res.code === 200) {
                wx.showToast({
                  title: '用户确认取餐',
                  icon: 'none'
                });
                this.setData({transforming:true, to: null});
            }
            else {
                wx.showModal({
                  content: res.msg,
                  showCancel: false
                });
            }
        })
    },
    /**
     * 确认送餐
    */
    confirmSend() {
        if (this.data.to) {
            api.request('GET', {pointId:this.data.to.id},'/ros/confirmSend').then(res => {
                console.log(res);
                this.setData({transforming:false});
          })
        }
        else {
            wx.showToast({
              title: '请选择目的地',
              icon: 'none',
            });
        }
    }
})