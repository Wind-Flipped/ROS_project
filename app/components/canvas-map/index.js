//  最小缩放
const MINIMUM_SCALE = 0.2;
//  最大缩放
const MAXIMUM_SCALE = 1.5;
//  缩放速度
const DELTA = 0.003;
//  缩放补偿量
const COMPENSATION = 1;
//  普通锚点
const NORMAL = '/static/image/normal.png';
const FLAG_SIZE = 200;
const NORMAL_SCALE = 0.125;
const ACTIVE_SCALE = 0.25;
//  临时锚点
const TMP = '/static/image/anchor.png';
//  手指移动
let moving = false;
//  flag数据结构,不对外保存数据
let FLAG = {
    //  标点名
    name: '',
    //  标点图片
    img: null,
    //  标点坐标
    imgX: 0,
    imgY: 0,
    scale: 1,
}

Component({
    properties: {
        //  地图图片url
        map: {
            type: String,
            value: '/static/image/map.png'
        },
        //  禁用长按
        disableLongTap: {
            type: Boolean,
            value: false
        },
        //  禁用缩放
        disableScale: {
            type: Boolean,
            value: false,
        },
        //  禁用点击
        disableTouch: {
            type: Boolean,
            value: false
        },
        //  标点簇
        flags: {
            type: Array,
            value: []
        },
        //  机器人位置
        robotPos: {
            type: Object,
            value: undefined
        }
    },
    observers: {
        //  监听机器人位置变化
        'robotPos': function (pos) {
            this.setData({
                robot: {
                    //  标点名
                    name: '',
                    //  标点图片
                    img: '/static/image/robotPosition.png',
                    //  标点坐标
                    imgX: pos.x,
                    imgY: pos.y,
                    scale: 0.2,
                }
            });
            if (!this.data.ctx || !this.data.canvas) {
                return;
            }
            this.drawMap();
        }
    },

    data: {
        ctx: null,
        canvas: null,
        img: null,
        imgX: 0, // 图片在画布中渲染的起点x坐标
        imgY: 0,
        imgScale: 0.6,
        canvasWidth: 0,
        canvasHeight: 0,
        //  位置
        pos: {},
        posl: {},
        start: [],
        pageX: null,
        pageY: null,
        //  临时锚点
        tmpFlag: null,
        robot: undefined
    },


    methods: {
        /**
         * 绘制整张地图所有元素
         */
        drawMap() {
            const ctx = this.data.ctx;
            ctx.clearRect(0, 0, this.data.canvas.width, this.data.canvas.height);
            ctx.drawImage(
                this.data.img, // 规定要使用的图像、画布或视频。
                0, 0, // 开始剪切的 x 坐标位置。
                this.data.imgWidth, this.data.imgHeight, // 被剪切图像的高度。
                this.data.imgX, this.data.imgY, // 在画布上放置图像的 x 、y坐标位置。
                this.data.imgWidth * this.data.imgScale, this.data.imgHeight * this.data.imgScale // 要使用的图像的宽度、高度
            );
            this.data.flags.forEach(item => {
                this.drawFlag(item);
            })
            if (this.data.tmpFlag) {
                this.drawFlag(this.data.tmpFlag);
            }
            if (this.data.robot) {
                this.drawFlag(this.data.robot);
            }
        },
        // 缩放 勾股定理方法-求两点之间的距离
        getDistance(p1, p2) {
            var x = p2.pageX - p1.pageX
            var y = p2.pageY - p1.pageY
            return Math.sqrt((x * x) + (y * y))
        },
        /**
         * 判断点是否在矩形内部
         * x1,y1: 矩形左上角坐标
         * x,y: 查询点
         */
        isInRect(x1, y1, width, height, x, y) {
            if (x <= x1 || x >= x1 + width || y <= y1 || y >= y1 + height) {
                return false;
            }
            return true;
        },
        /**
         * 判断点是否在地图内
         */
        isInMap(x, y) {
            return this.isInRect(this.data.imgX, this.data.imgY, this.data.img.width * this.data.imgScale, this.data.img.height * this.data.imgScale, x, y)
        },
        /**
         * 判断是否在flag中
         */
        isInFlag(flag, x, y) {
            if (!flag) {
                return false;
            }
            let pos = this.mapToWindow(flag.imgX, flag.imgY);
            return this.isInRect(pos.x - FLAG_SIZE * flag.scale / 2, pos.y - FLAG_SIZE * flag.scale, FLAG_SIZE * flag.scale, FLAG_SIZE * flag.scale, x, y);
        },
        /**
         * 屏幕坐标 -> 地图坐标
         */
        windowToMap(x, y) {
            return {
                x: (x - this.data.imgX) / this.data.imgScale,
                y: (y - this.data.imgY) / this.data.imgScale,
            }
        },
        /**
         * 地图坐标 -> 屏幕坐标
         */
        mapToWindow(x, y) {
            return {
                x: x * this.data.imgScale + this.data.imgX,
                y: y * this.data.imgScale + this.data.imgY,
            }
        },
        /**
         * 将某个flag设置为scale状态
         * */
        setScale(tar, scale) {
            this.resetScale();
            let flag = this.data.flags.find(item => item.name === tar.name);
            flag.scale = scale;
            this.drawMap();
        },
        /**
         * 重置所有缩放比例
         */
        resetScale() {
            this.data.flags.forEach(item => {
                item.scale = NORMAL_SCALE;
            });
        },
        /**
         * 寻找有无锚点被选中
         */
        touchFlag(x, y) {
            let flag = this.data.flags.find(item => this.isInFlag(item, x, y));
            if (flag) {
                //  如果找到
                this.data.tmpFlag = null;
                this.setScale(flag, ACTIVE_SCALE);
                return {
                    data: flag,
                    tmp: false,
                };
            }
            if (this.isInFlag(this.data.tmpFlag, x, y)) {
                return {
                    data: this.data.tmpFlag,
                    tmp: true,
                };
            }
            return null;
        },
        /**
         * 绘制标点
         */
        drawFlag(flag) {
            const canvas = this.data.canvas;
            const ctx = this.data.ctx;
            let img = canvas.createImage();
            const _ts = this;
            img.onload = (e) => {
                let windowX = flag.imgX * _ts.data.imgScale + _ts.data.imgX - FLAG_SIZE * flag.scale / 2;
                let windowY = flag.imgY * _ts.data.imgScale + _ts.data.imgY - FLAG_SIZE * flag.scale;
                ctx.drawImage(
                    img,
                    windowX, windowY,
                    FLAG_SIZE * flag.scale, FLAG_SIZE * flag.scale
                );
            }
            img.src = flag.img;
        },
        /**
         * 更新锚点列表,所有删除编辑操作均在父组件完成，组件只负责渲染一件事
         */
        updateFlags(list) {
            this.data.flags = list.map(item => {
                item.scale = NORMAL_SCALE;
                //  如果置空，则为默认
                if (!item.img) {
                    item.img = NORMAL;
                }
                return item;
            });
            this.data.tmpFlag = null;
            this.drawMap();
        },
        /**
         * 响应触摸事件
         */
        handleTap(e) {
            if (this.data.disableTouch) {
                return;
            }
            let touch = e.detail;
            let item = this.touchFlag(touch.x, touch.y);
            if (item) {
                //  触摸锚点
                this.triggerEvent('touch-flag', item);
            } else {
                //  触摸空白
                this.triggerEvent('touch-empty', item);
            }
        },
        /**
         * 监听长按回调
         */
        handleLongpress(e) {
            //  如果禁用长按
            if (this.data.disableLongTap) {
                return;
            }
            const touch = e.detail;
            //  如果不在地图内
            if (!this.isInMap(touch.x, touch.y)) {
                return;
            }
            this.resetScale();
            let mapLoc = this.windowToMap(touch.x, touch.y);
            this.data.tmpFlag = {
                name: '临时站点',
                imgX: mapLoc.x,
                imgY: mapLoc.y,
                scale: ACTIVE_SCALE,
                img: TMP,
            };
            this.drawMap();
            this.triggerEvent('create-tmp', {
                data: this.data.tmpFlag,
                tmp: true
            });
        },
        /**
         * 触摸开始回调
         */
        touchStart(event) {
            moving = true;
            if (event.touches && event.touches.length < 2) {
                const touch = event.touches[0];
                // 坐标转换，将窗口坐标转换成canvas的坐标
                this.data.pos = {
                    x: touch.pageX,
                    y: touch.pageY
                };
            } else {
                const touches = event.touches;
                // 手指按下时的手指所在的X，Y坐标
                this.data.pageX = touches[0].pageX;
                this.data.pageY = touches[0].pageY;
                // 记录初始 一组数据 作为缩放使用
                this.data.start = touches // 得到第一组两个点
            }
        },
        /**
         * 手指移动回调
         */
        async touchMove(e) {
            if (!moving) {
                return;
            }
            if (e.touches && e.touches.length < 2) { // 移动
                const touch = e.touches[0];
                this.data.posl = {
                    x: touch.pageX,
                    y: touch.pageY
                };
                var deltaX = this.data.posl.x - this.data.pos.x;
                var deltaY = this.data.posl.y - this.data.pos.y;
                this.data.imgX += deltaX;
                this.data.imgY += deltaY;
                this.data.pos = this.data.posl;
            } else { // 缩放
                if (this.data.disableScale) {
                    return;
                }
                const touches = e.touches;
                // // 2 根 手指执行 目标元素放大操作
                // // 得到第二组两个点
                var now = touches;
                var pos = {
                    x: now[0].pageX,
                    y: now[0].pageY
                };

                var newPos = {
                    x: ((pos.x - this.data.imgX) / this.data.imgScale),
                    y: ((pos.y - this.data.imgY) / this.data.imgScale)
                }
                // 当前距离变小， getDistance 是勾股定理的一个方法
                let dn = this.getDistance(now[0], now[1]);
                let dp = this.getDistance(this.data.start[0], this.data.start[1]);
                let d = Math.abs(dp - dn);
                if (dn < dp && d >= COMPENSATION) {
                    // 缩小
                    this.data.imgScale = Math.max(this.data.imgScale - DELTA, MINIMUM_SCALE);
                    this.data.imgX = (1 - this.data.imgScale) * newPos.x + (pos.x - newPos.x);
                    this.data.imgY = (1 - this.data.imgScale) * newPos.y + (pos.y - newPos.y)
                } else if (dn > dp && d >= COMPENSATION) {
                    // 放大
                    if (this.data.imgScale < MAXIMUM_SCALE) {
                        this.data.imgScale += DELTA;
                        this.data.imgX = (1 - this.data.imgScale) * newPos.x + (pos.x - newPos.x);
                        this.data.imgY = (1 - this.data.imgScale) * newPos.y + (pos.y - newPos.y);
                    }
                }
                this.data.start = now;
            }
            this.drawMap();
        },
        /**
         * 触摸结束
         */
        touchEnd() {
            moving = false;
        },

    },
    lifetimes: {
        ready() {
            const _ts = this;
            wx.getImageInfo({
                src: this.data.map,
            }).then(res => {
                const imgWidth = res.width;
                const imgHeight = res.height;
                wx.createSelectorQuery().in(_ts).select('.map').fields({
                    node: true,
                    size: true,
                }).exec(res => {
                    // Canvas 对象
                    const canvas = res[0].node;
                    // 渲染上下文
                    const ctx = canvas.getContext('2d');
                    const img = canvas.createImage();

                    // Canvas 画布的实际绘制宽高
                    const width = res[0].width;
                    const height = res[0].height;

                    _ts.setData({
                        ctx,
                        canvas,
                        img,
                        canvasWidth: width,
                        canvasHeight: height,
                        imgWidth,
                        imgHeight,
                    });

                    // 初始化画布大小
                    const dpr = wx.getWindowInfo().pixelRatio;
                    canvas.width = width * dpr;
                    canvas.height = height * dpr;
                    ctx.scale(dpr, dpr);
                    img.onload = () => {
                        _ts.drawMap();
                    }
                    img.src = this.data.map;
                })
            })
        }
    }
})