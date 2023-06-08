import config from './config';
const request = async (method = 'GET', data={}, url) => {
    return new Promise((resolve, reject) => {
        wx.request({
          url: config.host + url,
          method,
          data,
          success: res => {
              resolve(res.data);
          },
          fail: err => {
              reject(err);
          }
        });
    })
}

module.exports = {
    request,
}