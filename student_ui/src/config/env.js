// 配置编译环境和线上环境之间的切换

import { env } from "echarts/src/export";

// let baseUrl = 'http://139.9.233.240:81';
let baseUrl = "http://127.0.0.1:8101";
//通过变量动态获取baseurl
const API_PATH = process.env.VUE_APP_API_PATH;
if ("" != API_PATH) {
    baseUrl = API_PATH;
}
export {
    baseUrl // 客户端id
};