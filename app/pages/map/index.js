const api = require('../../utils/api');
Page({

    /**
     * 页面的初始数据
     */
    data: {
        map: ''
    },

    onLoad(options) {
        api.request('GET',{mapId: 142, type: 1}, '/ros/changeMode').then(res => {
            console.log(res);
            setInterval(()=> {
                api.request('GET', {}, '/map/getPicture').then(res => {
                    console.log(res);
                });
            },1000);
        });
    },

    onReady() {

    },

})