const api = require('./utils/api');
App({
    onLaunch() {
        wx.login({
            success: res => {
                api.request(undefined, {
                    code: res.code
                }, '/user/login').then(res => {
                    console.log(res);
                    this.globalData.userInfo = res.data;
                }).catch(err => {
                    console.log(err);
                });
            }
        });
        this.detectException(5000);
        this.returnMode();
    },
    detectException(duration) {
        return setInterval(() => {
            api.request('GET', {}, '/ros/getException').then(res => {
                if (res.code === 500) {
                    let msg = []
                    //  姿态
                    if (!res.data[0]) {
                        msg.push('姿态异常');
                    }
                    else if (!res.data[1]) {
                        msg.push('电量异常');
                    }
                    else if (!res.data[2]) {
                        msg.push('导航异常');
                    }
                    let msgStr = msg.join('!');
                    wx.showModal({
                      title: '警告',
                      content: msgStr + '!请检查机器人',
                      showCancel: false,
                      confirmColor: 'tomato'
                    });
                }
            })
        }, duration);
    },
    returnMode() {
        api.request('GET', {}, '/ros/getMode').then(res => {
            const mode = res.data.status;
            const id = res.data.mapId;
            switch (mode) {
                case 1:
                    //  建图模式
                    wx.navigateTo({
                        url: '/pages/map/index?id=' + id,
                    });
                    break;
                case 2:
                case 3:
                    let scene = encodeURIComponent(JSON.stringify({
                        id
                    }));
                    wx.navigateTo({
                        url: '/pages/robot/index?scene=' + scene,
                    });
                    break;
                default:
                    break;
            }
        });
    },
    globalData: {
        userInfo: null,
        code: null,
        robotImg: 'https://ts1.cn.mm.bing.net/th/id/R-C.4c0e592babbcc49ffa45839c34f6bd70?rik=f2Kjzz0Wa5HMnA&riu=http%3a%2f%2f14081512.s21i.faiusr.com%2f2%2fABUIABACGAAg4dm05wUos6uR-QEwkAM4kAM.jpg&ehk=pniY%2bvs87vsphqxT5kOhj%2bZwE7db56IVr4FTx%2bPC5Vw%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1',
    }
})