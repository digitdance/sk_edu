/*
    npm i cos-js-sdk-v5 --save

    //文件对象上传:
    utils.cos_utils.putObject(
    {
        bucket:'spoongsb-1253997188',
        region:'ap-chengdu'
    },'lecturer-files',fileObj,(progerss)=>{
        console.log(progerss)
    },(data,err)=>{
        console.error(err)
        console.log(data)
    })

    //大文件对象上传:
    utils.cos_utils.sliceUploadFile(
        utils.cos_utils.getConfig()
    ,'lecturer-files',fileObj,(progress)=>{
        console.log(JSON.stringify(progress))
    },(data,err)=>{
        console.error(err)
        console.log(data)
    })
*/
import utils from '.';
import fns from 'date-fns';
const COS = require('cos-js-sdk-v5');
const gBucket = 'labure-1259817162'
const gRegion = 'ap-chengdu'
const gSecretId = 'AKID2IRN5bAjDVmyy8NUZVJ2jOv0vfJn6FBx'
const gSecretKey = 'UneIvVu64oJadT6Xl1HdupcR9h3C8t15'

const cosUpload = {
    //获取基本配置
    getConfig () {
        console.log('config cos ... ')
        return {
            bucket: gBucket,
            region: gRegion
        }
    },
    auth () {
        console.log('auth cos ... ')
        return new COS({
            SecretId: gSecretId,
            SecretKey: gSecretKey
        });
    },
    /*
        COS上传对象
        //基础配置
        let config = {
            bucket:'spoongsb-1253997188',
            region:'ap-chengdu'
        }
        //资源前缀
        let prefix = 'lecturer-files'
        //待上传对象
        let file = new file()
        //上传进度
        let progress = {"loaded":0,"total":49032,"speed":null,"percent":0}
        let progress = {"loaded":49032,"total":49032,"speed":75666.67,"percent":1}
        //回传结果
        callback
        data:
        {
            Location: "spoongsb-1253997188.cos.ap-chengdu.myqcloud.com/lecturer-files/2019-09-24/1569302205144_icon.png", 
            statusCode: 200, 
            headers: 
            {
                content-length: "0"
            }
        }
        err:
        {error: "missing param Bucket"}
    */
    putObject (config, prefix, file, progress, callback) {
        let cos = utils.cos_utils.auth()
        let path = prefix + '/' + fns.format(new Date(), 'YYYY-MM-DD') + '/' + new Date().getTime() + '_' + file.name;
        console.log('上传路径 :', path)
        cos.putObject({
            Bucket: config.bucket,
            Region: config.region,
            Key: path,
            Body: file,
            onProgress: (progressData) => {
                progress(JSON.stringify(progressData))
            }
        }, (err, data) => {
            callback(data, err)
        });
    },
     /*
        COS实现文件的分块上传，适用于大文件上传
        //基础配置
        let config = {
            bucket:'spoongsb-1253997188',
            region:'ap-chengdu'
        }
        //资源前缀
        let prefix = 'lecturer-files'
        //待上传对象
        let file = new file()
        //上传进度
        let progress = {"loaded":0,"total":49032,"speed":null,"percent":0}
        let progress = {"loaded":49032,"total":49032,"speed":75666.67,"percent":1}
        //回传结果
        callback
        data:
        {
            Location: "spoongsb-1253997188.cos.ap-chengdu.myqcloud.com/lecturer-files/2019-09-24/1569302205144_icon.png", 
            statusCode: 200, 
            headers: 
            {
                content-length: "0"
            }
        }
        err:
        {error: "missing param Bucket"}
    */
    sliceUploadFile (config, prefix, file, progress, callback) {
        let cos = utils.cos_utils.auth()
        let path = prefix + '/' + fns.format(new Date(), 'YYYY-MM-DD') + '/' + new Date().getTime() + '_' + file.name;
        console.log('上传路径 :', path)
        cos.sliceUploadFile({
            Bucket: config.bucket,
            Region: config.region,
            Key: path,
            Body: file,
            onTaskReady: (taskId) => {
                //可回传任务号非必须
                console.log(taskId);
            },
            onHashProgress: (progressData) => {
                console.log(JSON.stringify(progressData));
            },
            onProgress: (progressData) => {
                //回传进度
                progress(progressData);
            }
        }, (err, data) => {
            //回传结果
            callback(data, err)
        });
    }
}
export default cosUpload