Component({

    /**
     * 组件的初始数据
     */
    data: {
        list: [{
                icon: 'home',
                value: 'index'
            },
            // {
            //     icon: {
            //         prefix: 'iconfont icon',
            //         name: 'add',
            //         color: '#0052d9'
            //     },
            //     value: 'add'
            // },
            {
                icon: 'user',
                value: 'mine'
            },
        ],
        value: 'index'
    },

    /**
     * 组件的方法列表
     */
    methods: {
        onChange(event) {
            let value = event.detail.value;
            switch (value) {
                case 'index':
                case 'mine':
                    this.setData({
                        value,
                    });
                    wx.switchTab({
                        url: '/pages/' + value + '/index',
                    })
                    break;
                // case 'add':
                //     wx.scanCode().then(res => {
                //         console.log(res);
                //     })
                //     break;
            }
        }
    }
})