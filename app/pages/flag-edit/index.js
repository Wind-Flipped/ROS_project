const api = require('../../utils/api');
Page({

    /**
     * 页面的初始数据
     */
    data: {
        //  当前点击项
        cur: null,
        //  站点簇
        list: [],
        types: ['迎宾点', '侯餐点', '餐桌点','餐桌点']
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        let scene = JSON.parse(decodeURIComponent(options.scene));
        api.request('GET', {mapId: scene.id}, '/point/mapToPoints').then(res => {
            this.setData({list: res.data.map(item => {
                return {
                    name: item.name,
                    imgX: item.xaxis,
                    imgY: item.yaxis,
                    img: '/static/image/normal.png',
                    scale: 0.125,
                    id: item.id,
                    type: item.status,
                }
            }),scene});
        });
    },
    setList() {
        let scene = this.data.scene;
        api.request('GET', {mapId: scene.id}, '/point/mapToPoints').then(res => {
            this.setData({list: res.data.map(item => {
                return {
                    name: item.name,
                    imgX: item.xaxis,
                    imgY: item.yaxis,
                    img: '/static/image/normal.png',
                    scale: 0.125,
                    id: item.id,
                    type: item.status,
                }
            }),scene});
        });
    },

    setCur(e) {
        let cur = e.detail;
        cur.data.imgX = cur.data.imgX;
        cur.data.imgY = cur.data.imgY;
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
                this.data.cur.data.type = 2;
                this.data.list.push(this.data.cur.data);
                api.request('GET', {x: this.data.cur.data.imgX, y: this.data.cur.data.imgY, name: this.data.cur.data.name, mapId: this.data.scene.id, type: 2}, '/point/createPoint').then(res => {
                    this.setList();
                })
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
                api.request('GET', {pointId: this.data.cur.data.id}, '/point/deletePoint').then(res => {
                    console.log(res);
                })
                this.setData({
                    list,
                    cur: null,
                });
                this.selectComponent('#map').updateFlags(list);
            }
        }).catch(err => {})
    },

    /**
     * 关闭菜单
    */
    closeMenu() {
        this.setData({cur:null});
        let map = this.selectComponent('#map');
        map.data.tmpFlag = null;
        map.resetScale();
        map.drawMap();
    },
    editFlag() {
        wx.showActionSheet({
          itemList: ['修改名称', '修改类型'],
          success: res => {
              if (res.tapIndex === 0) {

              }
              else if (res.tapIndex === 1) {
                  wx.showActionSheet({
                    itemList: ['迎宾点', '侯餐点', '餐桌点',], 
                    success: res => {
                        api.request('POST', {
                            axisx:this.data.cur.data.imgX,
                            axisy: this.data.cur.data.imgY,
                            name: this.data.cur.data.name,
                            id: this.data.cur.id,
                            status: res.tapIndex,
                            mapId: this.data.scene.id
                        }, '/point/updatePoint',);
                    }
                  })
              }
          }
        })
    }
})