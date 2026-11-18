import request from '@/router/axios';
const COS = require('cos-js-sdk-v5');
const gBucket = 'labure-1259817162';
const gRegion = 'ap-chengdu';
const moment = require('moment');

const cos_utils = {
  getConfig () {
    return { bucket: gBucket, region: gRegion };
  },
  auth () {
    return new COS({
      getAuthorization (options, callback) {
        request({ url: '/api/common/cos/credentials', method: 'get' })
          .then((response) => {
            const data = response && response.data && response.data.data;
            const credentials = data && data.credentials;
            if (!credentials) {
              console.error('COS temporary credentials are unavailable');
              callback({});
              return;
            }
            callback({
              TmpSecretId: credentials.tmpSecretId,
              TmpSecretKey: credentials.tmpSecretKey,
              SecurityToken: credentials.sessionToken,
              StartTime: data.startTime,
              ExpiredTime: data.expiredTime
            });
          })
          .catch((error) => {
            console.error('Failed to request COS temporary credentials', error);
            callback({});
          });
      }
    });
  },
  putObject (config, prefix, file, progress, callback) {
    const cos = cos_utils.auth();
    const path = prefix + '/' + moment().format('YYYY-MM-DD') + '/' + Date.now() + '_' + file.name;
    cos.putObject({
      Bucket: config.bucket,
      Region: config.region,
      Key: path,
      Body: file,
      onProgress: (progressData) => progress(JSON.stringify(progressData))
    }, (err, data) => callback(data, err));
  },
  sliceUploadFile (config, prefix, file, progress, callback) {
    const cos = cos_utils.auth();
    const path = prefix + '/' + moment().format('YYYY-MM-DD') + '/' + Date.now() + '_' + file.name;
    cos.sliceUploadFile({
      Bucket: config.bucket,
      Region: config.region,
      Key: path,
      Body: file,
      onTaskReady: (taskId) => console.log(taskId),
      onHashProgress: (progressData) => console.log(JSON.stringify(progressData)),
      onProgress: (progressData) => progress(progressData)
    }, (err, data) => callback(data, err));
  }
};

export default cos_utils;