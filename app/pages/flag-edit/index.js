Page({

    /**
     * 页面的初始数据
     */
    data: {
        //  当前点击项
        cur: null,
        //  站点簇
        list: []
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

    },

    setCur(e) {
        let cur = e.detail;
        cur.data.imgX = Math.floor(cur.data.imgX);
        cur.data.imgY = Math.floor(cur.data.imgY);
        this.setData({
            cur
        });
    },

    touchFlag(e) {
        this.setCur(e);
    },
    createTmp(e) {
        this.setCur(e);
    },
    checkName(name) {
        if (!name || name.length <= 0) {
            wx.showToast({
              title: '非法输入',
              icon: 'none'
            });
            return false;
        }
        if (this.data.list.find(item => item.name === name)) {
            wx.showToast({
              title: '命名重复',
              icon: 'none'
            });
            return false;
        }
        return true;
    },
    addFlag() {
        if (!this.data.cur || !this.data.cur.tmp) {
            return;
        }
        wx.showModal({
            title: '新建站点',
            editable: true,
            placeholderText: '请输入站点名称'
        }).then(res => {
            if (res.confirm && this.checkName(res.content)) {
                this.data.cur.data.name = res.content;
                this.data.cur.data.img = null;
                this.data.list.push(this.data.cur.data);
                this.selectComponent('#map').updateFlags(this.data.list);
                this.setData({
                    cur: null
                });
            }
        }).catch(err => {});
    },
    touchEmpty() {
        let map = this.selectComponent('#map');
        map.resetScale();
        map.data.tmpFlag = null;
        map.drawMap();
        this.setData({
            cur: null
        });
    },
    deleteFlag() {
        if (!this.data.cur || this.data.cur.tmp) {
            return;
        }
        wx.showModal({
            content: '确定要删除该站点吗？',
        }).then(res => {
            if (res.confirm) {
                let list = this.data.list.filter(item => {
                    return item.name !== this.data.cur.data.name;
                });
                this.setData({
                    list,
                    cur: null,
                });
                this.selectComponent('#map').updateFlags(list);
            }
        }).catch(err => {})
    },
    closeMenu() {
        this.setData({cur:null});
        let map = this.selectComponent('#map');
        map.data.tmpFlag = null;
        map.resetScale();
        map.drawMap();
    }
})