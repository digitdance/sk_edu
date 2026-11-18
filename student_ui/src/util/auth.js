import Cookies from 'js-cookie'
import website from '@/config/website'
const TokenKey = website.key +'x-access-token'
const SchoolIdKey = website.key +'schoolId'
export function getToken() {
    return Cookies.get(TokenKey)
}

export function setToken(token) {
  var inFifteenMinutes = new Date(new Date().getTime() + 90 * 60 * 1000);
  return Cookies.set(TokenKey, token, { expires: inFifteenMinutes })
}

export function removeToken() {
    return Cookies.remove(TokenKey)
}


export function getSchoolId() {
  return Cookies.get(SchoolIdKey)
}

export function setSchoolId(schoolId) {
var inFifteenMinutes = new Date(new Date().getTime() + 90000 * 600000 * 1000);
return Cookies.set(SchoolIdKey, schoolId, { expires: inFifteenMinutes })
}

export function removeSchoolId() {
  return Cookies.remove(TokenKey)
}
