import Cookies from 'js-cookie'
import website from '@/config/website'
const TokenKey = website.key +'x-access-token'
const DeptKey = website.key +'deptId'
export function getToken() {
    return Cookies.get(TokenKey)
}
export function getDeptId() {
  return Cookies.get(DeptKey)
}
export function setToken(token) {
  var inFifteenMinutes = new Date(new Date().getTime() + 120 * 60 * 1000);
  return Cookies.set(TokenKey, token, { expires: inFifteenMinutes })
}
export function setDept(deptId) {
  var inFifteenMinutes = new Date(new Date().getTime() + 12000000 * 6000 * 1000);
  return Cookies.set(DeptKey, deptId, { expires: inFifteenMinutes })
}
export function removeToken() {
    return Cookies.remove(TokenKey)
}
export function removeDept() {
  return Cookies.remove(DeptKey)
}
