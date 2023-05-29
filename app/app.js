const api = require('./utils/api');
App({
    onLaunch() {
        wx.login({
            success: res => {
                api.request(undefined, {
                    code: res.code
                }, '/user/login').then(res => {
                    this.globalData.userInfo = res.data;
                }).catch(err => {
                    console.log(err);
                });
            }
        });
    },
    globalData: {
        userInfo: null,
        code: null,
        robotImg: 'https://ts1.cn.mm.bing.net/th/id/R-C.4c0e592babbcc49ffa45839c34f6bd70?rik=f2Kjzz0Wa5HMnA&riu=http%3a%2f%2f14081512.s21i.faiusr.com%2f2%2fABUIABACGAAg4dm05wUos6uR-QEwkAM4kAM.jpg&ehk=pniY%2bvs87vsphqxT5kOhj%2bZwE7db56IVr4FTx%2bPC5Vw%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1'
    }
})